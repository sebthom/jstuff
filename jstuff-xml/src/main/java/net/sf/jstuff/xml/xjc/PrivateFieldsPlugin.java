/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.xml.xjc;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class PrivateFieldsPlugin extends AbstractPlugin {

   private static final Logger LOG = Logger.create();

   public static final String OPTION_NAME = "Xprivate-fields";

   @Override
   public String getOptionName() {
      return OPTION_NAME;
   }

   @Override
   public String getUsage() {
      return "  -" + OPTION_NAME + "    : Changes the field visibility from protected to private";
   }

   @Override
   public boolean run(final Outline model, final Options options, final ErrorHandler errorHandler) throws SAXException {

      // scan all XSD classes
      for (final ClassOutline classDef : model.getClasses()) {

         for (final JFieldVar fieldDef : classDef.implClass.fields().values()) {
            // ignore static fields
            if ((fieldDef.mods().getValue() & JMod.STATIC) != 0) {
               continue;
            }

            // ignore private fields
            if ((fieldDef.mods().getValue() & JMod.PRIVATE) != 0) {
               continue;
            }

            LOG.info("Declaring [%s#%s] private.", classDef.implClass.name(), fieldDef.name());
            fieldDef.mods().setPrivate();
         }
      }
      return true;
   }

}
