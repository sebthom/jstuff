/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.serviceregistry.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import net.sf.jstuff.integration.serviceregistry.ServiceProxy;
import net.sf.jstuff.integration.serviceregistry.ServiceRegistry;
import net.sf.jstuff.integration.serviceregistry.impl.DefaultServiceRegistry;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"spring-test-context.xml"})
// enforces closing of application context:
// http://stackoverflow.com/questions/7498202/springjunit4classrunner-does-not-close-the-application-context-at-the-end-of-jun
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class SpringBeanAsServiceTest {
   public static class AlternativeGreetingInterceptor implements MethodInterceptor {

      @Override
      public Object invoke(final @Nullable MethodInvocation invocation) throws Throwable {
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

   @BeforeTestClass
   @AfterTestClass
   static void testServiceNotRegistered() {
      final ServiceProxy<TestService> service = REGISTRY.getService(TestService.class.getName(), TestService.class);
      assertThat(service.isServiceAvailable()).isFalse();
   }

   @Test
   void testSpringBeanRegistrator() throws SecurityException, IllegalArgumentException {
      final TestService service = REGISTRY.getService(TestService.class.getName(), TestService.class).get();
      // ensure we get the spring AOP adviced service that returns the alternative greeting instead the one implemented by DefaultTestService
      assertThat(service.getGreeting()).isEqualTo("Hi!");
   }
}
