/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.json;

import static net.sf.jstuff.core.collection.CollectionUtils.*;
import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.util.NestedServletException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.reflection.Methods;
import net.sf.jstuff.integration.spring.SpringBeanParanamer;

/**
 * JSON-RPC Standard Method Definition
 *
 * https://dojotoolkit.org/reference-guide/1.10/dojox/rpc/smd.html
 * https://dojotoolkit.org/reference-guide/1.10/dojox/rpc/SMDLibrary.html
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SMDServiceExporter extends RemoteExporter implements HttpRequestHandler, InitializingBean {
   private static final Logger LOG = Logger.create();

   private static final ObjectMapper JSON = new ObjectMapper();
   private static final ObjectWriter JSON_PRETTY_WRITER = JSON.writerWithDefaultPrettyPrinter();

   public static Map<String, Method> buildExportedMethodsByName(final Class<?> serviceInterface) {
      final Map<String, Method> methodsByMethodName = new TreeMap<>();
      final Map<String, Class<?>[]> parameterTypesByMethodName = new HashMap<>();

      // loop through the service interface and all super interfaces to collect the public methods
      Class<?> clazz = serviceInterface;
      while (clazz != null) {
         // retrieve the public methods
         final Method[] publicMethods = serviceInterface.getMethods();
         for (final Method method : publicMethods) {
            final String name = method.getName();
            if (methodsByMethodName.containsKey(name)) {
               final Class<?>[] currentMethodParamTypes = method.getParameterTypes();
               final Class<?>[] registeredMethodParamTypes = parameterTypesByMethodName.get(name);

               if (!Arrays.equals(currentMethodParamTypes, registeredMethodParamTypes))
                  throw new IllegalStateException("Method overloading is not supported");
            } else {
               methodsByMethodName.put(name, method);
               parameterTypesByMethodName.put(name, method.getParameterTypes());
            }
         }
         clazz = clazz.getSuperclass();
      }
      return methodsByMethodName;
   }

   /**
    * JSON RPC1 Simple Method Description builder
    *
    * see http://dojo.jot.com/SMD
    * see http://manual.dojotoolkit.org/WikiHome/DojoDotBook/Book9
    */
   @SuppressWarnings("deprecation")
   public static String buildSMDTemplate(final Class<?> serviceInterface, final Object service,
      final Map<String, Method> exportedMethodsByName, final boolean pretty) throws JsonProcessingException {
      // build the method descriptors
      final Map<String, Object> methodDescriptions = new LinkedHashMap<>();
      for (final Method method : exportedMethodsByName.values()) {
         final Map<String, Object> methodDescriptor = new LinkedHashMap<>();
         if (method.getParameterTypes().length > 0) {
            // for some reason parameter names are not preserved in interfaces, therefore we look them up in the service implementing class instead
            final String[] names = SpringBeanParanamer.getParameterNames(method, service);

            final List<Object> parameters = newArrayList();
            for (int j = 0; j < method.getParameterTypes().length; j++) {
               final com.fasterxml.jackson.databind.jsonschema.JsonSchema parameterDescriptor = JSON.generateJsonSchema(method
                  .getParameterTypes()[j]);
               parameterDescriptor.getSchemaNode().put("name", names[0]);
               parameterDescriptor.getSchemaNode().put("java-type", method.getParameterTypes()[j].getName());
               parameters.add(parameterDescriptor);
            }
            methodDescriptor.put("parameters", parameters);
         }
         if (!Methods.isReturningVoid(method)) {
            final com.fasterxml.jackson.databind.jsonschema.JsonSchema returnTypeDescriptor = JSON.generateJsonSchema(method
               .getReturnType());
            returnTypeDescriptor.getSchemaNode().put("java-type", method.getReturnType().getName());
            methodDescriptor.put("returns", returnTypeDescriptor);
         }
         methodDescriptions.put(method.getName(), methodDescriptor);
      }

      // build the final SMD definition object
      final Map<String, Object> result = new LinkedHashMap<>(2);
      result.put("SMDVersion", "2.0");
      result.put("id", serviceInterface.getClass().getName());
      result.put("description", "");
      result.put("transport", "POST");
      result.put("envelope", "JSON-RPC-1.0");
      result.put("additionalParameters", false);
      result.put("target", "THE_SERVICE_URL");
      result.put("services", methodDescriptions);

      LOG.exit(result);
      if (pretty)
         return JSON_PRETTY_WRITER.writeValueAsString(result);
      return JSON.writeValueAsString(result);
   }

   /**
    * The exported methods by name.
    */
   private Map<String, Method> exportedMethodsByName = Collections.emptyMap();

   /**
    * Simple Method Description
    */
   private String smdTemplate = "";

   public SMDServiceExporter() {
      LOG.infoNew(this);
   }

   @Override
   public void afterPropertiesSet() throws Exception {
      final var srvIFace = asNonNullUnsafe(getServiceInterface());
      exportedMethodsByName = buildExportedMethodsByName(srvIFace);
      smdTemplate = buildSMDTemplate(srvIFace, asNonNullUnsafe(getService()), exportedMethodsByName, false);
   }

   /**
    * Processes the incoming JSON request and creates a JSON response..
    */
   @Override
   @SuppressWarnings("resource")
   public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
      // on POST requests we invoke a method and return the method value as a JSON string
      if ("POST".equals(request.getMethod())) {
         invokeMethod(request, response);
      } else if ("GET".equals(request.getMethod())) {
         // replace THE_SERVICE_URL place holder with the actual URL from the current request
         final String smd = smdTemplate.replaceFirst("THE_SERVICE_URL", request.getRequestURL().toString());

         response.getWriter().write(smd);
      }
   }

   @SuppressWarnings("resource")
   private void invokeMethod(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
      // deserializing the object
      final JsonNode requestObject = JSON.readTree(request.getReader());

      // looking up the method
      final String methodName = requestObject.get("method").asText();
      final Method method = exportedMethodsByName.get(methodName);

      // throw an exception if the method could not be found
      if (method == null)
         throw new ServletException("Method " + methodName + " not found");

      try {
         // converting method arguments
         final JsonNode paramsNode = requestObject.get("params");
         final Object[] methodArguments = new Object[paramsNode.size()];
         final Class<?>[] methodParameterTypes = method.getParameterTypes();
         for (int i = 0, l = paramsNode.size(); i < l; i++) {
            final JsonNode paramNode = paramsNode.get(i);
            if (paramNode != null) {
               methodArguments[i] = JSON.treeToValue(paramNode, methodParameterTypes[i]);
            }
         }

         // invoking the method
         final Object methodReturnValue = method.invoke(getProxyForService(), methodArguments);

         // creating a JSON object containing the method return value
         final var result = new LinkedHashMap<String, Object>(2);
         result.put("id", requestObject.get("id").asText());
         result.put("result", methodReturnValue);
         JSON.writeValue(response.getWriter(), result);

         LOG.exit(result);
      } catch (final Exception ex) {
         throw new NestedServletException("Invoking method " + methodName + " failed", ex);
      }
   }
}
