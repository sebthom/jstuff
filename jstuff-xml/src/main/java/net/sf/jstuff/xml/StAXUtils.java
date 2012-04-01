/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2012 Sebastian
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

import javax.xml.stream.XMLStreamReader;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class StAXUtils
{
	public static String getAttributeValue(final XMLStreamReader xmlr, final String attrLocalName)
	{
		for (int i = 0; i < xmlr.getAttributeCount(); i++)
		{
			final String localName = xmlr.getAttributeLocalName(i);
			if (localName.equals(attrLocalName)) return xmlr.getAttributeValue(i);
		}
		return null;
	}

	protected StAXUtils()
	{
		super();
	}
}
