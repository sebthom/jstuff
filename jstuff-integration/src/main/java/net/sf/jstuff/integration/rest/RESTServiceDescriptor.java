/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
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

import java.io.Serializable;
import java.util.Collection;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class RESTServiceDescriptor implements Serializable
{
	private static final long serialVersionUID = 1L;

	private Collection<RESTResourceActionDescriptor> methods;

	/**
	 * @return the methods
	 */
	public Collection<RESTResourceActionDescriptor> getMethods()
	{
		return methods;
	}

	/**
	 * @param methods the methods to set
	 */
	public void setMethods(final Collection<RESTResourceActionDescriptor> methods)
	{
		this.methods = methods;
	}

}