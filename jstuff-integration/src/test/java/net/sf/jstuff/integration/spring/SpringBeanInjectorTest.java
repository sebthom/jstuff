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

import junit.framework.TestCase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SpringBeanInjectorTest extends TestCase
{
	public static class Entity
	{
		@Autowired
		public Object springBean;
	}

	public void testSpringBeanInjector()
	{
		try
		{
			SpringBeanInjector.get(); // must fail, since the spring context is not yet opened
			fail();
		}
		catch (final IllegalStateException ex)
		{}

		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("SpringBeanInjectorTest.xml",
				SpringBeanInjectorTest.class);

		final SpringBeanInjector injector = SpringBeanInjector.get();
		final Entity e = new Entity();
		injector.inject(e);
		assertNotNull(e.springBean);

		ctx.close();

		try
		{
			SpringBeanInjector.get(); // must fail, since the spring context is closed
			fail();
		}
		catch (final IllegalStateException ex)
		{}
	}
}
