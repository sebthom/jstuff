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
 */
public class AtomPerson
{
	private String email;
	private String name;
	private String uri;

	public AtomPerson()
	{}

	/**
	 * @param name
	 */
	public AtomPerson(final String name)
	{
		this.name = name;
	}

	/**
	 * @param name
	 * @param email
	 * @param uri
	 */
	public AtomPerson(final String name, final String email, final String uri)
	{
		this.name = name;
		this.email = email;
		this.uri = uri;
	}

	/**
	 * @return the email
	 */
	public String getEmail()
	{
		return email;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the uri
	 */
	public String getUri()
	{
		return uri;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(final String email)
	{
		this.email = email;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final String name)
	{
		this.name = name;
	}

	/**
	 * @param uri the uri to set
	 */
	public void setUri(final String uri)
	{
		this.uri = uri;
	}
}
