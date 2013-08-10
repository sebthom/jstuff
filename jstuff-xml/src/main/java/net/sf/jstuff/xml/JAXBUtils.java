/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2013 Sebastian
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
package net.sf.jstuff.xml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import net.sf.jstuff.core.StringUtils;
import net.sf.jstuff.core.validation.Args;

/**
 * Interesting reading: Unofficial JAXB Guide http://jaxb.java.net/guide/
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class JAXBUtils
{
	public static String toXML(final Object obj) throws XMLException
	{
		Args.notNull("obj", obj);

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		toXML(obj, baos);
		return baos.toString();
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void toXML(final Object obj, final OutputStream out) throws XMLException
	{
		Args.notNull("obj", obj);
		Args.notNull("out", out);

		try
		{
			final JAXBContext ctx = JAXBContext.newInstance(obj.getClass());
			final Marshaller m = ctx.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			if (ctx.createJAXBIntrospector().getElementName(obj) == null)
				m.marshal(new JAXBElement(new QName(StringUtils.lowerCaseFirstChar(obj.getClass().getSimpleName())), obj.getClass(), obj),
						out);
			else
				m.marshal(obj, out);
		}
		catch (final JAXBException ex)
		{
			throw new XMLException(ex);
		}
	}

	public static String toXSD(final Class< ? >... xmlRootClasses) throws XMLException
	{
		Args.notNull("rootClasses", xmlRootClasses);

		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		try
		{
			toXSD(out, xmlRootClasses);
		}
		catch (final IOException ex)
		{
			// never happens
		}
		return out.toString();
	}

	public static void toXSD(final OutputStream out, final Class< ? >... xmlRootClasses) throws XMLException, IOException
	{
		Args.notNull("xmlRootClasses", xmlRootClasses);
		Args.notNull("out", out);

		try
		{
			final StreamResult result = new StreamResult(out);
			JAXBContext.newInstance(xmlRootClasses).generateSchema(new SchemaOutputResolver()
				{
					@Override
					public Result createOutput(final String namespaceURI, final String suggestedFileName) throws IOException
					{
						result.setSystemId(new File(suggestedFileName));
						return result;
					}
				});
		}
		catch (final JAXBException ex)
		{
			throw new XMLException(ex);
		}
	}

	protected JAXBUtils()
	{
		super();
	}
}
