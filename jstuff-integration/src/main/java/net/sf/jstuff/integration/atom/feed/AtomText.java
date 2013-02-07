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
package net.sf.jstuff.integration.atom.feed;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 *
 */
public class AtomText
{
	public static final String TYPE_HTML = "html";
	public static final String TYPE_TEXT = "text";

	private String content;

	private String type = TYPE_TEXT;

	public AtomText()
	{}

	/**
	 * @param content
	 */
	public AtomText(final String content)
	{
		this.content = content;
	}

	/**
	 * @param content
	 * @param type
	 */
	public AtomText(final String content, final String type)
	{
		this.content = content;
		this.type = type;
	}

	/**
	 * @return the value
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @param value the value to set
	 */
	public void setContent(final String value)
	{
		this.content = value;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(final String type)
	{
		this.type = type;
	}
}
