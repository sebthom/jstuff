/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.spring;

import static org.assertj.core.api.Assertions.*;

import java.io.Serializable;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import net.sf.jstuff.core.io.SerializationUtils;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SpringBeanRefTest {

   public static class Entity implements Serializable {
      private static final long serialVersionUID = 1L;

      public SpringBeanRef<Object> springBean = SpringBeanRef.of("springBean");
   }

   @Test
   public void testSpringBeanRef() {

      final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("SpringBeanLocatorTest.xml", SpringBeanRefTest.class);

      final Entity e = new Entity();
      assertThat(e.springBean).isNotNull();
      assertThat(e.springBean.get()).isNotNull();
      final Entity e2 = SerializationUtils.clone(e);
      assertThat(e2.springBean).isNotNull();
      assertThat(e2.springBean.get()).isNotNull();

      ctx.close();
   }
}
