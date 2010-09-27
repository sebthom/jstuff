/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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

import net.sf.jstuff.core.Logger;
import net.sf.jstuff.core.reflection.ReflectionUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class RESTServiceExporterXStreamImpl extends AbstractRESTServiceExporter
{
	private static final Logger LOG = Logger.get();

	private final XStream xStream;

	public RESTServiceExporterXStreamImpl()
	{
		super("UTF-8", "application/xml");

		final HierarchicalStreamDriver xmlDriver = //
		ReflectionUtils.isClassPresent("javax.xml.stream.XMLStreamReader") ? new StaxDriver() : //
				ReflectionUtils.isClassPresent("org.xmlpull.mxp1.MXParser") ? new XppDriver() : //
						new DomDriver();
		LOG.info("XML driver implementation: %s", xmlDriver);
		xStream = new XStream(xmlDriver);

		configureXStream(xStream);
	}

	protected void configureXStream(final XStream xStream)
	{
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
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <T> T deserializeRequestBody(final Class<T> targetType, final HttpServletRequest request)
			throws IOException
	{
		return (T) xStream.fromXML(request.getInputStream());
	}

	@Override
	protected String serializeResponse(final Object resultObject)
	{
		return xStream.toXML(resultObject);
	}
}
