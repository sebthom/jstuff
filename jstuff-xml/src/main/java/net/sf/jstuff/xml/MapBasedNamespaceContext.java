/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
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

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.collection.iterator.Iterators;
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

      switch (prefix) {
         case XMLConstants.DEFAULT_NS_PREFIX:
            return defaultNamespaceURI;
         case XMLConstants.XML_NS_PREFIX:
            return XMLConstants.XML_NS_URI;
         case XMLConstants.XMLNS_ATTRIBUTE:
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
         default:
            break;
      }

      final var nsURI = namespaceURIsByPrefix.get(prefix);
      return nsURI == null ? XMLConstants.NULL_NS_URI : nsURI.get(0);
   }

   @Override
   public @Nullable String getPrefix(final String namespaceURI) {
      if (namespaceURI.equals(defaultNamespaceURI))
         return XMLConstants.DEFAULT_NS_PREFIX;
      if (namespaceURI.equals(XMLConstants.XML_NS_URI))
         return XMLConstants.XML_NS_PREFIX;
      if (namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI))
         return XMLConstants.XMLNS_ATTRIBUTE;

      final var prefix = prefixesByNamespaceURI.get(namespaceURI);
      return prefix == null ? null : prefix.get(0);
   }

   @Override
   public Iterator<String> getPrefixes(final String namespaceURI) {
      Args.notNull("namespaceURI", namespaceURI);

      final var prefix = prefixesByNamespaceURI.get(namespaceURI);
      return prefix == null ? Iterators.empty() : prefix.iterator();
   }
}
