/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 * 
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.integration.spring;

import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SpringBeanLocatorTest extends TestCase {
    public void testSpringBeanLocator() {
        try {
            SpringBeanLocator.get().byClass(Object.class); // must fail, since the spring context is not yet opened
            fail();
        } catch (final IllegalStateException ex) {}

        final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("SpringBeanLocatorTest.xml", SpringBeanLocatorTest.class);

        assertNotNull(SpringBeanLocator.get().byClass(SpringBeanLocator.class));
        assertNotNull(SpringBeanLocator.get().byName("springBean"));

        ctx.close();

        try {
            SpringBeanLocator.get().byClass(Object.class); // must fail, since the spring context is closed
            fail();
        } catch (final IllegalStateException ex) {}
    }
}
