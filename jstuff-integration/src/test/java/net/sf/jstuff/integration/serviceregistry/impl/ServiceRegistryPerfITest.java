/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.serviceregistry.impl;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.lateNonNull;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class ServiceRegistryPerfITest {

   public interface MyService {
      String getGreeting();
   }

   public static final MyService MY_SERVICE = new MyService() {
      @SuppressWarnings("unused")
      private long counter = 0;

      @Override
      public String getGreeting() {
         counter++;
         return "Hello";
      }
   };

   private ByteBuddyServiceRegistry byteBuddyRegistry = lateNonNull();
   private DefaultServiceRegistry jdkProxyRegistry = lateNonNull();

   void runPerfTest(final MyService service, final String label) {
      final var sw = new StopWatch();
      sw.start();
      for (int i = 0; i < 2_000_000; i++) {
         service.getGreeting();
      }
      sw.stop();
      System.out.println(label + ": " + sw.toString());
   }

   @BeforeEach
   void setup() throws Exception {
      byteBuddyRegistry = new ByteBuddyServiceRegistry();
      jdkProxyRegistry = new DefaultServiceRegistry();
   }

   @AfterEach
   void tearDown() throws Exception {
      byteBuddyRegistry = lateNonNull();
      jdkProxyRegistry = lateNonNull();
   }

   @Test
   void testPerformance() {
      byteBuddyRegistry.addService("MyService", MyService.class, MY_SERVICE);
      jdkProxyRegistry.addService("MyService", MyService.class, MY_SERVICE);

      // warm up
      System.out.println("-----------------------");
      runPerfTest(MY_SERVICE, "Direct    ");
      runPerfTest(byteBuddyRegistry.getService("MyService", MyService.class).get(), "Byte Buddy");
      runPerfTest(jdkProxyRegistry.getService("MyService", MyService.class).get(), "JDK Proxy ");

      System.out.println("-----------------------");
      runPerfTest(MY_SERVICE, "Direct    ");
      runPerfTest(byteBuddyRegistry.getService("MyService", MyService.class).get(), "Byte Buddy");
      runPerfTest(jdkProxyRegistry.getService("MyService", MyService.class).get(), "JDK Proxy ");

      System.out.println("-----------------------");
      runPerfTest(MY_SERVICE, "Direct    ");
      runPerfTest(byteBuddyRegistry.getService("MyService", MyService.class).get(), "Byte Buddy");
      runPerfTest(jdkProxyRegistry.getService("MyService", MyService.class).get(), "JDK Proxy ");
   }
}
