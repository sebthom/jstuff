/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2014 Sebastian
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

import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Assert;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Injects spring beans into unmanaged Java objects having {@link org.springframework.beans.factory.annotation.Autowired},
 * {@link org.springframework.beans.factory.annotation.Value} and  {@link javax.inject.Inject} annotations.
 *
 * <pre>
 * &lt;context:annotation-config /&gt;
 *
 * &lt;bean class="net.sf.jstuff.integration.spring.SpringBeanInjector" /&gt;
 * </pre>
 *
 * or
 *
 * <pre>
 * &lt;context:annotation-config /&gt;
 *
 * &lt;context:component-scan base-package="net.sf.jstuff.integration.spring" /&gt;
 * </pre>
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@Component
public class SpringBeanInjector implements DisposableBean
{
	private static final Logger LOG = Logger.create();

	private static SpringBeanInjector _INSTANCE;

	/**
	 * @return the default instance (the last instantiated one by any spring context)
	 */
	public static SpringBeanInjector get()
	{
		Assert.notNull(_INSTANCE, "No SpringBeanInjector instance created yet. Add  <bean class=\"" + SpringBeanInjector.class.getName()
				+ "\" /> to your spring configuration!");

		return _INSTANCE;
	}

	@Autowired
	private AutowiredAnnotationBeanPostProcessor processor;

	private SpringBeanInjector()
	{
		Assert.isNull(_INSTANCE, "A instance of " + this.getClass().getName() + " already exists.");

		LOG.infoNew(this);

		_INSTANCE = this;
	}

	/**
	 * <p><b>For use by Spring only!</b></p>
	 * {@inheritDoc}
	 */
	public void destroy()
	{
		_INSTANCE = null;
	}

	public void inject(final Object unmanagedBean)
	{
		LOG.entry(unmanagedBean);
		processor.processInjection(unmanagedBean);
		LOG.exit();
	}
}
