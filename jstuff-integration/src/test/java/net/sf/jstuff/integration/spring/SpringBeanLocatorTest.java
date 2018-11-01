/*********************************************************************
 * Copyright 2010-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SpringBeanLocatorTest extends TestCase {
   public void testSpringBeanLocator() {
      try {
         SpringBeanLocator.get().byClass(Object.class); // must fail, since the spring context is not yet opened
         fail();
      } catch (final IllegalStateException ex) {
         // expected
      }
      final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("SpringBeanLocatorTest.xml", SpringBeanLocatorTest.class);

      assertNotNull(SpringBeanLocator.get().byClass(SpringBeanLocator.class));
      assertNotNull(SpringBeanLocator.get().byName("springBean"));

      ctx.close();

      try {
         SpringBeanLocator.get().byClass(Object.class); // must fail, since the spring context is closed
         fail();
      } catch (final IllegalStateException ex) {
         // expected
      }
   }
}
