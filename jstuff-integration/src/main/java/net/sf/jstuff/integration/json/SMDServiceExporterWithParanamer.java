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
package net.sf.jstuff.integration.json;

import java.lang.reflect.Method;

import net.sf.jstuff.core.Logger;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SMDServiceExporterWithParanamer extends SMDServiceExporter
{
	private static final Logger LOG = Logger.make();

	private final Paranamer paranamer = new CachingParanamer(new BytecodeReadingParanamer());

	@Override
	protected String[] getParameterNames(final Method method)
	{
		if (method.getParameterTypes().length == 0) return null;

		// try lookup the parameter based on the service interface's method declaration  
		String[] parameters = paranamer.lookupParameterNames(method, false);

		// check if the parameters could be found
		if (parameters != null) return parameters;

		// try lookup the parameter based on the service implementation's method declaration 
		try
		{
			final Method serviceImplMethod = getService().getClass().getMethod(method.getName(),
					method.getParameterTypes());
			parameters = paranamer.lookupParameterNames(serviceImplMethod);
			if (parameters != null) return parameters;
		}
		catch (final Exception ex)
		{
			LOG.trace("Unexpected exception", ex);
		}

		// as fallback use indexed parameter
		return super.getParameterNames(method);
	}
}
