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
package net.sf.jstuff.integration.persistence.query;

import java.io.Serializable;

/**
 * Encapsulation class for setting sort field and sort direction.
 * 
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SortBy implements Serializable
{
	private static final long serialVersionUID = 1L;

	/** sorting direction */
	private SortDirection direction;

	/** sorting field */
	private String field;

	public SortBy()
	{}

	public SortBy(final String field, final SortDirection direction)
	{
		this.field = field;
		this.direction = direction;
	}

	/**
	 * @return the direction
	 */
	public SortDirection getDirection()
	{
		return direction;
	}

	/**
	 * @return the field
	 */
	public String getField()
	{
		return field;
	}

	/**
	 * @param direction the direction to set
	 */
	public void setDirection(final SortDirection direction)
	{
		this.direction = direction;
	}

	/**
	 * @param field the field to set
	 */
	public void setField(final String field)
	{
		this.field = field;
	}

	@Override
	public String toString()
	{
		return field + ' ' + direction;
	}
}
