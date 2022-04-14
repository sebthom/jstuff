/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.serviceregistry.support;

import static org.assertj.core.api.Assertions.*;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.sf.jstuff.integration.serviceregistry.ServiceProxy;
import net.sf.jstuff.integration.serviceregistry.ServiceRegistry;
import net.sf.jstuff.integration.serviceregistry.impl.DefaultServiceRegistry;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"spring-test-context.xml"})
// enforces closing of application context:
// http://stackoverflow.com/questions/7498202/springjunit4classrunner-does-not-close-the-application-context-at-the-end-of-jun
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SpringBeanAsServiceTest {
   public static class AlternativeGreetingInterceptor implements MethodInterceptor {

      @Override
      public Object invoke(final MethodInvocation invocation) throws Throwable {
         return "Hi!";
      }
   }

   public static class DefaultTestService implements TestService {

      @Override
      public String getGreeting() {
         return "Hello!";
      }
   }

   public interface TestService {
      String getGreeting();
   }

   public static class TestServiceAOPAdvice {
      public String getAlternativeGreeting() {
         return "Hi!";
      }
   }

   public static final ServiceRegistry REGISTRY = new DefaultServiceRegistry();

   @BeforeClass
   @AfterClass
   public static void testServiceNotRegistered() {
      final ServiceProxy<TestService> service = REGISTRY.getService(TestService.class.getName(), TestService.class);
      assertThat(service.isServiceAvailable()).isFalse();
   }

   @Test
   public void testSpringBeanRegistrator() throws SecurityException, IllegalArgumentException {
      final TestService service = REGISTRY.getService(TestService.class.getName(), TestService.class).get();
      // ensure we get the spring AOP adviced service that returns the alternative greeting instead the one implemented by DefaultTestService
      assertThat(service.getGreeting()).isEqualTo("Hi!");
   }
}
