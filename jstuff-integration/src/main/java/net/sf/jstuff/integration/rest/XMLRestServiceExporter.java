/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.FactoryConfigurationError;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;

import net.sf.jstuff.core.collection.PagedListWithSortBy;
import net.sf.jstuff.core.comparator.SortBy;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.reflection.Types;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class XMLRestServiceExporter extends AbstractRestServiceExporter {
   private static final Logger LOG = Logger.create();

   private final XStream xStream;

   public XMLRestServiceExporter() {
      super("UTF-8", "application/xml");

      final HierarchicalStreamDriver xmlDriver = getXStreamDriver();
      LOG.info("XML driver implementation: %s", xmlDriver);
      xStream = new XStream(xmlDriver);

      configureXStream(xStream);
   }

   protected void configureXStream(final XStream xStream) {
      xStream.useAttributeFor(Class.class);
      xStream.useAttributeFor(boolean.class);
      xStream.useAttributeFor(byte.class);
      xStream.useAttributeFor(char.class);
      xStream.useAttributeFor(double.class);
      xStream.useAttributeFor(float.class);
      xStream.useAttributeFor(int.class);
      xStream.useAttributeFor(long.class);
      xStream.useAttributeFor(Boolean.class);
      xStream.useAttributeFor(Byte.class);
      xStream.useAttributeFor(Character.class);
      xStream.useAttributeFor(Double.class);
      xStream.useAttributeFor(Float.class);
      xStream.useAttributeFor(Integer.class);
      xStream.useAttributeFor(Long.class);
      xStream.useAttributeFor(String.class);

      xStream.alias("pagedList", PagedListWithSortBy.class);
      xStream.alias("sortBy", SortBy.class);
      xStream.alias("action", RestResourceAction.class);
      xStream.alias(RestServiceDescriptor.class.getSimpleName(), RestServiceDescriptor.class);
      xStream.addImplicitCollection(RestServiceDescriptor.class, "actions");
   }

   @Override
   @SuppressWarnings({"unchecked", "resource"})
   protected <T> T deserializeRequestBody(final Class<T> targetType, final HttpServletRequest request) throws IOException {
      return (T) xStream.fromXML(request.getInputStream());
   }

   protected HierarchicalStreamDriver getXStreamDriver() {
      try {
         final StaxDriver xmlDriver = new StaxDriver();
         xmlDriver.getInputFactory();
         xmlDriver.getOutputFactory();
         return xmlDriver;
      } catch (final Exception | FactoryConfigurationError ex) {
         LOG.warn(ex, "Failed to use StaxDriver.");
      }

      return Types.isAvailable("org.xmlpull.mxp1.MXParser") ? new XppDriver() : new DomDriver();
   }

   @Override
   protected String serializeResponse(final Object resultObject) {
      return xStream.toXML(resultObject);
   }
}
