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

import java.lang.reflect.Method;

import net.sf.jstuff.core.Logger;

import org.springframework.aop.support.AopUtils;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class AbstractRESTServiceExporterWithParanamer extends AbstractRESTServiceExporter
{
	private static final Logger LOG = Logger.get();

	private final Paranamer paranamer = new CachingParanamer(new BytecodeReadingParanamer());

	/**
	 * @param characterEncoding
	 * @param contentType
	 */
	public AbstractRESTServiceExporterWithParanamer(final String characterEncoding, final String contentType)
	{
		super(characterEncoding, contentType);
	}

	@Override
	protected String[] getParameterNames(final Method method)
	{
		if (method.getParameterTypes().length == 0) return null;

		// try lookup the parameter based on the service interface's method declaration  
		String[] parameters = paranamer.lookupParameterNames(method);

		// check if the parameters could be found
		if (parameters != null) return parameters;

		// try lookup the parameter based on the service implementation's method declaration 
		try
		{
			final Method serviceImplMethod = AopUtils.getTargetClass(getService()).getMethod(method.getName(),
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
