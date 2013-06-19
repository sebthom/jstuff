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
public class AtomCategory
{
	private String term;

	public AtomCategory()
	{}

	/**
	 * @param term
	 */
	public AtomCategory(final String term)
	{
		this.term = term;
	}

	/**
	 * @return the term
	 */
	public String getTerm()
	{
		return term;
	}

	/**
	 * @param term the term to set
	 */
	public void setTerm(final String term)
	{
		this.term = term;
	}
}
