/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class MapBasedNamespaceContext implements NamespaceContext {
   protected final Map<String, List<String>> namespaceURIsByPrefix = new HashMap<>(2);
   protected final Map<String, List<String>> prefixesByNamespaceURI = new HashMap<>(2);
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

      namespaceURIsByPrefix //
         .computeIfAbsent(prefix, unused -> new ArrayList<>(2)) //
         .add(namespaceURI);

      prefixesByNamespaceURI //
         .computeIfAbsent(namespaceURI, unused -> new ArrayList<>(2)) //
         .add(prefix);
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
