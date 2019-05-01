/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import net.sf.jstuff.core.collection.Maps;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class MapBasedNamespaceContext implements NamespaceContext {
   protected final Map<String, List<String>> namespaceURIsByPrefix = Maps.newHashMap(2);
   protected final Map<String, List<String>> prefixesByNamespaceURI = Maps.newHashMap(2);
   private String defaultNamespaceURI = XMLConstants.NULL_NS_URI;

   public void bindDefaultNameSpace(final String namespaceURI) {
      defaultNamespaceURI = namespaceURI;
   }

   public void bindNamespace(final String namespaceURI, final String prefix) {
      Args.notNull("namespaceURI", namespaceURI);
      Args.notNull("prefix", prefix);

      if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
         defaultNamespaceURI = namespaceURI;
      }

      if (!namespaceURIsByPrefix.containsKey(prefix)) {
         namespaceURIsByPrefix.put(prefix, new ArrayList<String>(2));
      }
      namespaceURIsByPrefix.get(prefix).add(namespaceURI);

      if (!prefixesByNamespaceURI.containsKey(namespaceURI)) {
         prefixesByNamespaceURI.put(namespaceURI, new ArrayList<String>(2));
      }
      prefixesByNamespaceURI.get(namespaceURI).add(prefix);
   }

   @Override
   public String getNamespaceURI(final String prefix) {
      Args.notNull("prefix", prefix);

      if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX))
         return defaultNamespaceURI;
      if (prefix.equals(XMLConstants.XML_NS_PREFIX))
         return XMLConstants.XML_NS_URI;
      if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE))
         return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;

      if (namespaceURIsByPrefix.containsKey(prefix))
         return namespaceURIsByPrefix.get(prefix).get(0);
      return XMLConstants.NULL_NS_URI;
   }

   @Override
   public String getPrefix(final String namespaceURI) {
      Args.notNull("namespaceURI", namespaceURI);

      if (namespaceURI.equals(defaultNamespaceURI))
         return XMLConstants.DEFAULT_NS_PREFIX;
      if (namespaceURI.equals(XMLConstants.XML_NS_URI))
         return XMLConstants.XML_NS_PREFIX;
      if (namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI))
         return XMLConstants.XMLNS_ATTRIBUTE;

      return prefixesByNamespaceURI.containsKey(namespaceURI) ? prefixesByNamespaceURI.get(namespaceURI).get(0) : null;
   }

   @Override
   public Iterator<String> getPrefixes(final String namespaceURI) {
      Args.notNull("namespaceURI", namespaceURI);

      return prefixesByNamespaceURI.containsKey(namespaceURI) ? prefixesByNamespaceURI.get(namespaceURI).iterator() : null;
   }
}
