/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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
package net.sf.jstuff.integration.spring;

import net.sf.jstuff.core.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Injects spring beans into unmanaged Java objects having {@link org.springframework.beans.factory.annotation.Autowired},
 * {@link org.springframework.beans.factory.annotation.Value} and  {@link javax.inject.Inject} annotations.
 * 
 * <pre>
 * &lt;bean class="net.sf.jstuff.integration.spring.SpringInjector" /&gt;
 * </pre>
 * 
 * or 
 * 
 * <pre>
 * &lt;context:component-scan base-package="net.sf.jstuff.integration.spring" /&gt;
 * </pre>
 * 
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@Component
public class SpringInjector
{
	private static final Logger LOG = Logger.get();

	private static SpringInjector INSTANCE;

	public static SpringInjector get()
	{
		Assert.notNull(INSTANCE,
				"No SpringInjector instance created yet. Add  <bean class=\"" + SpringInjector.class.getName()
						+ "\" /> to your spring configuration!");

		return INSTANCE;
	}

	@Autowired
	private AutowiredAnnotationBeanPostProcessor processor;

	private SpringInjector()
	{
		LOG.info("Instantiated.");

		INSTANCE = this;
	}

	public void inject(final Object unmanagedBean)
	{
		LOG.traceMethodEntry(unmanagedBean);
		processor.processInjection(unmanagedBean);
		LOG.traceMethodExit();
	}
}
