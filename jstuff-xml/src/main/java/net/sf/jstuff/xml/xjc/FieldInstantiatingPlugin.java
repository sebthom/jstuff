/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.xml.xjc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationValue;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;

import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.reflection.Fields;

/**
 * This plug-in finds fields of types that are defined in the schema and instantiates them even if not specified in the XML.
 *
 * Roughly based on https://github.com/walmir/xjc-inst/blob/master/xjc-plugin/src/main/java/de/jaxbnstuff/xjcplugin/FieldInstantiator.java
 *
 * To enable auto-instantiation use:
 *
 * <pre>
 * &lt;schema ... xmlns:FIELD_INST="FieldInstantiatingPlugin" jaxb:extensionBindingPrefixes="FIELD_INST"&gt;
 * ...
 *   &lt;element name="connection" minOccurs="0" maxOccurs="1"&gt;
 *     &lt;annotation&gt;
 *       &lt;appinfo&gt;
 *         &lt;FIELD_INST:enabled /&gt;
 *       &lt;/appinfo&gt;
 *     &lt;/annotation&gt;
 *     &lt;complexType&gt;
 *     ...
 * ...
 * </pre>
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class FieldInstantiatingPlugin extends AbstractPlugin {

   private static final Logger LOG = Logger.create();

   public static final String OPTION_NAME = "Xinst-fields";

   private static final String CUSTOMIZATION_NAMESPACE = "FieldInstantiatingPlugin";
   private static final String CUSTOMIZATION_ENABLED_TAG = "enabled";

   @Override
   protected String getCustomizationNS() {
      return CUSTOMIZATION_NAMESPACE;
   }

   @Override
   public String getOptionName() {
      return OPTION_NAME;
   }

   @Override
   public String getUsage() {
      return "  -" + OPTION_NAME + "       : Automatically instantiate fields with types defined in the schema.";
   }

   @Override
   public boolean run(final Outline outline, final Options options, final ErrorHandler errorHandler) throws SAXException {

      // collect all types defined in the XSD
      final List<JType> typeDefs = new ArrayList<>();
      for (final ClassOutline classDef : outline.getClasses()) {
         typeDefs.add(classDef.implClass);
      }

      // scan all XSD based classes for field references to other XSD based classes
      for (final ClassOutline classDef : outline.getClasses()) {

         for (final JFieldVar fieldDecl : classDef.implClass.fields().values()) {

            /*
             * @XmlElementRefs({
             *    @XmlElementRef(name = "bike", namespace = "my-config", type = JAXBElement.class, required = false),
             *    @XmlElementRef(name = "car", namespace = "my-config", type = JAXBElement.class, required = false)
             * })
             * private List<JAXBElement<?>> bikesAndCars;
             */
            final Field memberValueFields = Fields.find(JAnnotationUse.class, "memberValues");
            for (final JAnnotationUse a : fieldDecl.annotations()) {
               if ("javax.xml.bind.annotation.XmlElementRefs".equals(a.getAnnotationClass().binaryName()) || //
                  jakarta.xml.bind.annotation.XmlElementRefs.class.getName().equals(a.getAnnotationClass().binaryName())) {
                  for (final JAnnotationUse xmlElementRefAnno : ((JAnnotationArrayMember) a.getAnnotationMembers().get("value")).annotations()) {
                     final JAnnotationValue requiredAttribute = xmlElementRefAnno.getAnnotationMembers().get("required");
                     if (requiredAttribute != null) {
                        ((Map<?, ?>) Fields.read(xmlElementRefAnno, memberValueFields)).remove("required");
                     }
                  }
               }
            }
            if (!typeDefs.contains(fieldDecl.type())) {
               continue;
            }

            FieldOutline fieldDef = null;
            for (final FieldOutline f : classDef.getDeclaredFields()) {
               if (f.getPropertyInfo().getName(false).equals(fieldDecl.name())) {
                  fieldDef = f;
               }
            }
            if (fieldDef == null)
               throw new IllegalStateException("FieldOutline not found for " + fieldDecl.name());

            boolean doInstantiate = false;
            for (final CPluginCustomization pc : findCustomizations(fieldDef.getPropertyInfo().getCustomizations(), CUSTOMIZATION_ENABLED_TAG)) {
               pc.markAsAcknowledged();
               doInstantiate = true;
            }

            // initialize field
            if (doInstantiate) {
               LOG.info("%s#%s = new %s()", classDef.implClass.name(), fieldDecl.name(), fieldDecl.type().name());
               fieldDecl.init(JExpr._new(fieldDecl.type()));
            } else {
               LOG.info("Not instantiating %s#%s", classDef.implClass.name(), fieldDecl.name());
            }
         }
      }

      return true;
   }

   @Override
   public boolean isCustomizationTagName(final String nsURI, final String localName) {
      if (!CUSTOMIZATION_NAMESPACE.equals(nsURI))
         return false;

      return CUSTOMIZATION_ENABLED_TAG.equals(localName);
   }
}
