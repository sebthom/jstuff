/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.xml.stream;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.types.Decorator;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingXMLStreamReader extends Decorator.Default<XMLStreamReader> implements AutoCloseableXMLStreamReader {

   public DelegatingXMLStreamReader() {
   }

   public DelegatingXMLStreamReader(final XMLStreamReader wrapped) {
      super(wrapped);
   }

   @Override
   public void close() throws XMLStreamException {
      wrapped.close();
   }

   @Override
   public int getAttributeCount() {
      return wrapped.getAttributeCount();
   }

   @Override
   public String getAttributeLocalName(final int index) {
      return wrapped.getAttributeLocalName(index);
   }

   @Override
   public QName getAttributeName(final int index) {
      return wrapped.getAttributeName(index);
   }

   @Override
   public String getAttributeNamespace(final int index) {
      return wrapped.getAttributeNamespace(index);
   }

   @Override
   public String getAttributePrefix(final int index) {
      return wrapped.getAttributePrefix(index);
   }

   @Override
   public String getAttributeType(final int index) {
      return wrapped.getAttributeType(index);
   }

   @Override
   public String getAttributeValue(final int index) {
      return wrapped.getAttributeValue(index);
   }

   @Override
   public String getAttributeValue(final @Nullable String namespaceURI, final String localName) {
      return wrapped.getAttributeValue(namespaceURI, localName);
   }

   @Override
   public @Nullable String getCharacterEncodingScheme() {
      return wrapped.getCharacterEncodingScheme();
   }

   @Override
   public String getElementText() throws XMLStreamException {
      return wrapped.getElementText();
   }

   @Override
   public @Nullable String getEncoding() {
      return wrapped.getEncoding();
   }

   @Override
   public int getEventType() {
      return wrapped.getEventType();
   }

   @Override
   public String getLocalName() {
      return wrapped.getLocalName();
   }

   @Override
   public Location getLocation() {
      return wrapped.getLocation();
   }

   @Override
   public QName getName() {
      return wrapped.getName();
   }

   @Override
   public NamespaceContext getNamespaceContext() {
      return wrapped.getNamespaceContext();
   }

   @Override
   public int getNamespaceCount() {
      return wrapped.getNamespaceCount();
   }

   @Override
   public @Nullable String getNamespacePrefix(final int index) {
      return wrapped.getNamespacePrefix(index);
   }

   @Override
   public @Nullable String getNamespaceURI() {
      return wrapped.getNamespaceURI();
   }

   @Override
   public @Nullable String getNamespaceURI(final int index) {
      return wrapped.getNamespaceURI(index);
   }

   @Override
   public @Nullable String getNamespaceURI(final String prefix) {
      return wrapped.getNamespaceURI(prefix);
   }

   @Override
   public @Nullable String getPIData() {
      return wrapped.getPIData();
   }

   @Override
   public @Nullable String getPITarget() {
      return wrapped.getPITarget();
   }

   @Override
   public @Nullable String getPrefix() {
      return wrapped.getPrefix();
   }

   @Override
   public @Nullable Object getProperty(final String name) throws IllegalArgumentException {
      return wrapped.getProperty(name);
   }

   @Override
   public @Nullable String getText() {
      return wrapped.getText();
   }

   @Override
   public char[] getTextCharacters() {
      return wrapped.getTextCharacters();
   }

   @Override
   public int getTextCharacters(final int sourceStart, final char[] target, final int targetStart, final int length)
      throws XMLStreamException {
      return wrapped.getTextCharacters(sourceStart, target, targetStart, length);
   }

   @Override
   public int getTextLength() {
      return wrapped.getTextLength();
   }

   @Override
   public int getTextStart() {
      return wrapped.getTextStart();
   }

   @Override
   public @Nullable String getVersion() {
      return wrapped.getVersion();
   }

   @Override
   public boolean hasName() {
      return wrapped.hasName();
   }

   @Override
   public boolean hasNext() throws XMLStreamException {
      return wrapped.hasNext();
   }

   @Override
   public boolean hasText() {
      return wrapped.hasText();
   }

   @Override
   public boolean isAttributeSpecified(final int index) {
      return wrapped.isAttributeSpecified(index);
   }

   @Override
   public boolean isCharacters() {
      return wrapped.isCharacters();
   }

   @Override
   public boolean isEndElement() {
      return wrapped.isEndElement();
   }

   @Override
   public boolean isStandalone() {
      return wrapped.isStandalone();
   }

   @Override
   public boolean isStartElement() {
      return wrapped.isStartElement();
   }

   @Override
   public boolean isWhiteSpace() {
      return wrapped.isWhiteSpace();
   }

   @Override
   public int next() throws XMLStreamException {
      return wrapped.next();
   }

   @Override
   public int nextTag() throws XMLStreamException {
      return wrapped.nextTag();
   }

   @Override
   public void require(final int type, final @Nullable String namespaceURI, @Nullable final String localName) throws XMLStreamException {
      wrapped.require(type, namespaceURI, localName);
   }

   @Override
   public boolean standaloneSet() {
      return wrapped.standaloneSet();
   }

}
