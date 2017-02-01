/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.integration.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import net.sf.jstuff.core.collection.PagedListWithSortBy;
import net.sf.jstuff.core.comparator.SortBy;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.reflection.Types;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
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

        LOG.infoNew(this);
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
    @SuppressWarnings("unchecked")
    protected <T> T deserializeRequestBody(final Class<T> targetType, final HttpServletRequest request) throws IOException {
        return (T) xStream.fromXML(request.getInputStream());
    }

    protected HierarchicalStreamDriver getXStreamDriver() {
        if (Types.isAvailable("javax.xml.stream.XMLStreamReader"))
            try {

            final StaxDriver xmlDriver = new StaxDriver();
            xmlDriver.getInputFactory();
            xmlDriver.getOutputFactory();
            return xmlDriver;
        } catch (final Exception ex) {
            LOG.warn(ex, "Failed to use StaxDriver.");
        } catch (final Error er) {
            if ("javax.xml.stream.FactoryConfigurationError".equals(er.getClass().getName()))
                LOG.warn(er, "Failed to use StaxDriver.");
            else
                throw er;
        }

        return Types.isAvailable("org.xmlpull.mxp1.MXParser") ? new XppDriver()
                : //
                new DomDriver();
    }

    @Override
    protected String serializeResponse(final Object resultObject) {
        return xStream.toXML(resultObject);
    }
}
