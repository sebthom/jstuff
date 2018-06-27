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

import java.io.Serializable;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;
import net.sf.jstuff.core.io.SerializationUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SpringBeanRefTest extends TestCase {
   public static class Entity implements Serializable {
      private static final long serialVersionUID = 1L;

      public SpringBeanRef<Object> springBean = SpringBeanRef.of("springBean");
   }

   public void testSpringBeanRef() {

      final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("SpringBeanLocatorTest.xml", SpringBeanRefTest.class);

      final Entity e = new Entity();
      assertNotNull(e.springBean);
      assertNotNull(e.springBean.get());
      final Entity e2 = SerializationUtils.clone(e);
      assertNotNull(e2.springBean);
      assertNotNull(e2.springBean.get());

      ctx.close();
   }
}
