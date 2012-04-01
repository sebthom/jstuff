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
package net.sf.jstuff.integration.auth;

import net.sf.jstuff.integration.userregistry.UserDetails;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface AuthListener
{
	void afterLogin(Authentication authentication);

	void afterLogout(UserDetails userDetails);
}
