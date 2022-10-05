/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.serviceregistry.impl;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;
import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Test;

import net.sf.jstuff.core.concurrent.Threads;
import net.sf.jstuff.integration.serviceregistry.ServiceListener;
import net.sf.jstuff.integration.serviceregistry.ServiceProxy;
import net.sf.jstuff.integration.serviceregistry.ServiceRegistry;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class AbstractServiceRegistryTest<R extends ServiceRegistry> {
   public static final class CountingListener<T> implements ServiceListener<T> {
      private final AtomicInteger count;

      public CountingListener(final AtomicInteger count) {
         this.count = count;
      }

      @Override
      public void onServiceAvailable(final ServiceProxy<T> service) {
         count.incrementAndGet();
      }

      @Override
      public void onServiceUnavailable(final ServiceProxy<T> service) {
         count.incrementAndGet();
      }
   }

   public static class DefaultService1 implements Service1 {

      @Override
      public String getGreeting() {
         return "Hello";
      }

      @Override
      public boolean validate() {
         return true;
      }
   }

   public static class DefaultService2 implements Service2 {

      @Override
      public String getGreeting() {
         return "Hola";
      }

      @Override
      public boolean validate() {
         return true;
      }
   }

   public static class DefaultService2Extended extends DefaultService2 implements Service2Extended {

      @Override
      public String getGoodbye() {
         return "Adios";
      }
   }

   public interface Service1 extends TestService {
      String ENDPOINT_ID = Service1.class.getName();

      String getGreeting();
   }

   public interface Service2 extends TestService {
      String ENDPOINT_ID = Service2.class.getName();

      String getGreeting();
   }

   public interface Service2Extended extends Service2 {
      String getGoodbye();
   }

   public interface TestService {
      boolean validate();
   }

   protected R registry = eventuallyNonNull();

   @After
   public void tearDown() throws Exception {
      registry = eventuallyNonNull();
   }

   @Test
   public void testServiceInheritance() {
      {
         final Service2 srv2 = new DefaultService2();
         assertThat(registry.getService(Service2.ENDPOINT_ID, Service2.class).isServiceAvailable()).isFalse();

         registry.addService(Service2.ENDPOINT_ID, Service2.class, srv2);
         final ServiceProxy<Service2> srv2Proxy = registry.getService(Service2.ENDPOINT_ID, Service2.class);
         assertThat(srv2Proxy.isServiceAvailable()).isTrue();
         assertThat(srv2Proxy.getServiceImplementationClass()).isEqualTo(DefaultService2.class);
         assertThat(srv2Proxy.get().validate()).isTrue(); // calling a method from the root-interface
         assertThat(registry.getService(Service2.ENDPOINT_ID, Service2Extended.class).isServiceAvailable()).isFalse();
         registry.removeService(Service2.ENDPOINT_ID, srv2);
      }

      {
         final Service2Extended srv2Ext = new DefaultService2Extended();
         assertThat(registry.getService(Service2.ENDPOINT_ID, Service2.class).isServiceAvailable()).isFalse();

         registry.addService(Service2.ENDPOINT_ID, Service2Extended.class, srv2Ext);
         final ServiceProxy<Service2> srv2Proxy = registry.getService(Service2.ENDPOINT_ID, Service2.class);
         assertThat(srv2Proxy.isServiceAvailable()).isTrue();
         assertThat(srv2Proxy.getServiceImplementationClass()).isEqualTo(DefaultService2Extended.class);
         assertThat(srv2Proxy.get().validate()).isTrue(); // calling a method from the root-interface
         assertThat(registry.getService(Service2.ENDPOINT_ID, Service2.class).isServiceAvailable()).isTrue();
         registry.removeService(Service2.ENDPOINT_ID, srv2Ext);
      }
   }

   @Test
   public void testServiceListener() {
      final ServiceProxy<Service1> srv1Proxy = registry.getService(Service1.ENDPOINT_ID, Service1.class);
      final AtomicInteger count = new AtomicInteger();
      final CountingListener<AbstractServiceRegistryTest.Service1> listener = new CountingListener<>(count);
      srv1Proxy.addServiceListener(listener);

      final DefaultService1 srv1Impl = new DefaultService1();

      count.set(0);
      registry.addService(Service1.ENDPOINT_ID, Service1.class, srv1Impl);
      registry.addService(Service1.ENDPOINT_ID, Service1.class, srv1Impl);
      assertThat(count.get()).isEqualTo(1);

      count.set(0);
      registry.removeService(Service1.ENDPOINT_ID, srv1Impl);
      registry.removeService(Service1.ENDPOINT_ID, srv1Impl);
      assertThat(count.get()).isEqualTo(1);

      count.set(0);
      srv1Proxy.removeServiceListener(listener);
      registry.addService(Service1.ENDPOINT_ID, Service1.class, srv1Impl);
      assertThat(count.get()).isZero();
   }

   @Test
   public void testServiceListenerGC() {
      ServiceProxy<Runnable> srv1Proxy = registry.getService(Runnable.class.getName(), Runnable.class);
      final AtomicInteger count = new AtomicInteger();
      CountingListener<Runnable> listener = new CountingListener<>(count);
      assertThat(srv1Proxy.addServiceListener(listener)).isTrue();
      assertThat(srv1Proxy.addServiceListener(listener)).isFalse();

      srv1Proxy = null; // remove ref to service proxy, but still hold ref to listener
      System.gc();
      Threads.sleep(500);

      final Runnable service = () -> { /**/ };

      count.set(0);
      registry.addService(Runnable.class.getName(), Runnable.class, service);
      registry.removeService(Runnable.class.getName(), service);
      assertThat(count.get()).isEqualTo(2);

      listener.toString(); // this call ensures that the listener is not GCed before by some JIT optimization
      listener = null; // also remove ref to listener
      System.gc();
      Threads.sleep(500);

      count.set(0);
      registry.addService(Runnable.class.getName(), Runnable.class, service);
      registry.removeService(Runnable.class.getName(), service);
      assertThat(count.get()).isZero();
   }

   @Test
   public void testServiceRegistry() {
      assertThat(registry.getService(Service1.ENDPOINT_ID, Service1.class)).isNotNull();
      assertThat(registry.getService(Service2.ENDPOINT_ID, Service2.class)).isNotNull();
      assertThat(registry.getService(Service1.ENDPOINT_ID, Service1.class).isServiceAvailable()).isFalse();
      assertThat(registry.getService(Service2.ENDPOINT_ID, Service2.class).isServiceAvailable()).isFalse();

      // test adding one service
      DefaultService1 srv1Impl = new DefaultService1();
      registry.addService(Service1.ENDPOINT_ID, Service1.class, srv1Impl);
      final ServiceProxy<Service1> srv1Proxy = registry.getService(Service1.ENDPOINT_ID, Service1.class);
      assertThat(srv1Proxy.isServiceAvailable()).isTrue();
      final Service1 srv1 = srv1Proxy.get();
      assertThat(srv1).isNotNull();
      assertThat(srv1.getGreeting()).isEqualTo("Hello");
      assertThat(srv1Impl).isNotSameAs(srv1);
      assertThat(srv1).isInstanceOf(ServiceProxy.class);
      assertThat(srv1Proxy).isSameAs(srv1);
      assertThat(srv1Proxy.getServiceEndpointId()).isEqualTo(Service1.ENDPOINT_ID);
      assertThat(srv1Proxy.getServiceInterface()).isSameAs(Service1.class);
      assertThat(registry.getService(Service1.ENDPOINT_ID, Service1.class)).isSameAs(srv1Proxy);

      // test adding another service instance to the same endpoint
      try {
         registry.addService(Service1.ENDPOINT_ID, Service1.class, new DefaultService1());
         failBecauseExceptionWasNotThrown(IllegalStateException.class);
      } catch (final IllegalStateException ex) {
         // expected
      }

      // test removing service1
      registry.removeService(Service1.ENDPOINT_ID, srv1Impl);
      assertThat(registry.getService(Service1.ENDPOINT_ID, Service1.class)).isSameAs(srv1Proxy);
      assertThat(srv1Proxy.isServiceAvailable()).isFalse();

      // test loading service2
      final DefaultService2 srv2Impl = new DefaultService2();
      registry.addService(Service2.ENDPOINT_ID, Service2.class, srv2Impl);
      assertThat(registry.getService(Service1.ENDPOINT_ID, Service1.class)).isNotNull();
      assertThat(registry.getService(Service2.ENDPOINT_ID, Service2.class)).isNotNull();

      // test reloading service1
      assertThat(srv1Proxy.isServiceAvailable()).isFalse();
      srv1Impl = new DefaultService1();
      registry.addService(Service1.ENDPOINT_ID, Service1.class, srv1Impl);
      assertThat(srv1Proxy.isServiceAvailable()).isTrue();
      assertThat(registry.getService(Service1.ENDPOINT_ID, Service1.class)).isSameAs(srv1Proxy);

      assertThat(registry.getActiveServiceEndpoints()).hasSize(2);

      // test replacing service at endpoint1 with a service with a different interface (e.g. same class from a different classloader)
      registry.removeService(Service1.ENDPOINT_ID, srv1Impl);
      assertThat(registry.getActiveServiceEndpoints()).hasSize(1);
      registry.addService(Service1.ENDPOINT_ID, Service2.class, srv2Impl);
      assertThat(srv1Proxy.isServiceAvailable()).isFalse();
      assertThat(registry.getService(Service1.ENDPOINT_ID, Service1.class)).isNotNull();
      assertThat(registry.getService(Service1.ENDPOINT_ID, Service2.class)).isNotNull();
   }
}
