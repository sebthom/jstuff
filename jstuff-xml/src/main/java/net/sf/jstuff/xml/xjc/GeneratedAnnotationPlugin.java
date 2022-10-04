/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.xml.xjc;

import org.eclipse.jdt.annotation.Nullable;
import org.xml.sax.ErrorHandler;

import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JClass;
import com.sun.tools.xjc.Driver;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.EnumOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.outline.PackageOutline;

/**
 * {@link com.sun.tools.xjc.Plugin} that marks the generated code by using JSR-250's '@Generated'.
 *
 * Simplified version of {@link com.sun.tools.xjc.addon.at_generated.PluginImpl} only annotating classes and not methods and fields.
 *
 * Additionally also annotates package-info.java.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class GeneratedAnnotationPlugin extends AbstractPlugin {

   public static final String OPTION_NAME = "Xmark-generated";

   @Override
   public String getOptionName() {
      return OPTION_NAME;
   }

   @Override
   public String getUsage() {
      return "  -" + OPTION_NAME + "    :  mark the generated types as @javax.annotation.Generated";
   }

   @Nullable
   private JClass generatedAnnotation;

   @Override
   public boolean run(final Outline model, final Options options, final ErrorHandler errorHandler) {
      // we want this to work without requiring JSR-250 jar.
      generatedAnnotation = model.getCodeModel().ref("javax.annotation.Generated");

      for (final PackageOutline pkgDef : model.getAllPackageContexts()) {
         annotate(pkgDef._package());
      }
      for (final ClassOutline classDef : model.getClasses()) {
         annotate(classDef.implClass);
      }
      for (final EnumOutline enumDef : model.getEnums()) {
         annotate(enumDef.clazz);
      }

      return true;
   }

   private void annotate(final JAnnotatable m) {
      m.annotate(generatedAnnotation) //
         .param("value", Driver.class.getName())//
         .param("comments", "JAXB RI v" + Options.getBuildID());
   }
}
