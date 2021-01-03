/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.rest;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.apache.commons.lang3.ArrayUtils;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.reflection.SerializableMethod;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class RestResourceAction implements Serializable {
   private static final long serialVersionUID = 1L;

   private final RestResourceAction fallbackFor;
   private final HttpRequestMethod httpRequestMethod;
   private final Class<?> httpRequestBodyType;
   private final SerializableMethod serviceMethod;
   private final String[] parameterNames;
   private final String resourceName;
   private final int requiredURLParameterCount;
   private final String requestURITemplate;

   public RestResourceAction(final String resource, final HttpRequestMethod httpRequestMethod, final Method serviceMethod, final String[] parameterNames) {
      this(resource, httpRequestMethod, serviceMethod, parameterNames, null);
   }

   public RestResourceAction(final String resourceName, final HttpRequestMethod httpRequestMethod, final Method serviceMethod, final String[] parameterNames,
      final RestResourceAction fallbackFor) {
      Args.notNull("resource", resourceName);
      Args.notNull("httpRequestMethod", httpRequestMethod);
      Args.notNull("method", serviceMethod);
      Args.notNull("parameterNames", parameterNames);

      this.serviceMethod = new SerializableMethod(serviceMethod);
      this.resourceName = resourceName;
      this.httpRequestMethod = httpRequestMethod;
      this.fallbackFor = fallbackFor;
      this.parameterNames = parameterNames;

      final int paramCount = serviceMethod.getParameterTypes().length;

      if (httpRequestMethod == HttpRequestMethod.PUT || httpRequestMethod == HttpRequestMethod.POST) {
         if (paramCount == 0)
            throw new IllegalArgumentException("HTTP Request Method [" + httpRequestMethod
               + "] requires a service method with at least one parameter. ResourceId= " + resourceName + ", Service Method=" + serviceMethod);

         if (paramCount < 2) {
            requestURITemplate = resourceName;
         } else {
            requestURITemplate = resourceName + "/${" + Strings.join(ArrayUtils.remove(parameterNames, paramCount - 1), "}/${") + "}";
         }

         // for POST and PUT the last parameter is supposed to be submitted as HTTP Body
         // Message
         requiredURLParameterCount = paramCount - 1 - (serviceMethod.isVarArgs() ? 1 : 0);
         httpRequestBodyType = serviceMethod.getParameterTypes()[paramCount - 1];
      } else {
         if (paramCount < 1) {
            requestURITemplate = resourceName;
         } else {
            requestURITemplate = resourceName + "/${" + Strings.join(parameterNames, "}/${") + "}";
         }

         requiredURLParameterCount = paramCount - (serviceMethod.isVarArgs() ? 1 : 0);
         httpRequestBodyType = null;
      }
   }

   public RestResourceAction getFallbackFor() {
      return fallbackFor;
   }

   public Class<?> getHttpRequestBodyType() {
      return httpRequestBodyType;
   }

   public HttpRequestMethod getHttpRequestMethod() {
      return httpRequestMethod;
   }

   public Class<?> getHttpResponseBodyType() {
      return serviceMethod.getMethod().getReturnType();
   }

   public String[] getParameterNames() {
      return parameterNames;
   }

   public String getRequestURITemplate() {
      return requestURITemplate;
   }

   public int getRequiredURLParameterCount() {
      return requiredURLParameterCount;
   }

   public String getResourceName() {
      return resourceName;
   }

   public Method getServiceMethod() {
      return serviceMethod.getMethod();
   }

   public String getServiceMethodSignature() {
      if (parameterNames.length == 0)
         return serviceMethod.getDeclaringClass().getSimpleName() + "." + serviceMethod.getName() + "()";
      return serviceMethod.getDeclaringClass().getSimpleName() + "." + serviceMethod.getName() + "(" + Strings.join(parameterNames, ",") + ")";
   }

   public boolean isFallback() {
      return fallbackFor != null;
   }

   public boolean isFallBackMethod() {
      return fallbackFor != null;
   }

   @Override
   public String toString() {
      return getClass().getSimpleName() + ": resource=" + resourceName + "; httpMethod = " + httpRequestMethod + "; method=" + serviceMethod;
   }
}
