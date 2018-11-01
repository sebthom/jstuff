/*********************************************************************
 * Copyright 2010-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.xml.xjc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CCustomizations;
import com.sun.tools.xjc.model.CPluginCustomization;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class AbstractPlugin extends Plugin {

   protected String getCustomizationNS() {
      return null;
   }

   @Override
   public List<String> getCustomizationURIs() {
      if (getCustomizationNS() == null)
         return Collections.emptyList();
      return Arrays.asList(getCustomizationNS());
   }

   protected CCustomizations findCustomizations(final CCustomizations cc, final String name) {
      final CCustomizations result = new CCustomizations();
      for (final CPluginCustomization cpc : cc) {
         final Element e = cpc.element;
         if (StringUtils.equals(getCustomizationNS(), e.getNamespaceURI()) && e.getLocalName().equals(name)) {
            result.add(cpc);
         }
      }
      return result;
   }

}
