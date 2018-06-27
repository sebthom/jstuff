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
package net.sf.jstuff.integration.serviceregistry.impl;

import org.apache.commons.lang3.time.StopWatch;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ServiceRegistryPerfITest extends TestCase {

   public interface MyService {
      String getGreeting();
   }

   public static final MyService MY_SERVICE = new MyService() {
      @SuppressWarnings("unused")
      private long counter = 0;

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
      for (int i = 0; i < 2000000; i++) {
         service.getGreeting();
      }
      sw.stop();
      System.out.println(label + ": " + sw.toString());
   }

   @Override
   protected void setUp() throws Exception {
      byteBuddyRegistry = new ByteBuddyServiceRegistry();
      jdkProxyRegistry = new DefaultServiceRegistry();
   }

   @Override
   protected void tearDown() throws Exception {
      byteBuddyRegistry = null;
      jdkProxyRegistry = null;
   }

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
