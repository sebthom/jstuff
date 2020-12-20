/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.serviceregistry.impl;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ServiceRegistryPerfITest {

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

   private ByteBuddyServiceRegistry byteBuddyRegistry;
   private DefaultServiceRegistry jdkProxyRegistry;

   public void runPerfTest(final MyService service, final String label) {
      final StopWatch sw = new StopWatch();
      sw.start();
      for (int i = 0; i < 2_000_000; i++) {
         service.getGreeting();
      }
      sw.stop();
      System.out.println(label + ": " + sw.toString());
   }

   @Before
   public void setup() throws Exception {
      byteBuddyRegistry = new ByteBuddyServiceRegistry();
      jdkProxyRegistry = new DefaultServiceRegistry();
   }

   @After
   public void tearDown() throws Exception {
      byteBuddyRegistry = null;
      jdkProxyRegistry = null;
   }

   @Test
   public void testPerformance() {
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
