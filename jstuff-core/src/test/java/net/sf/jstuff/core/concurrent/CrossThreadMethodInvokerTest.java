/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import static org.assertj.core.api.Assertions.*;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.Test;

import net.sf.jstuff.core.concurrent.CrossThreadMethodInvoker.CrossThreadProxy;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CrossThreadMethodInvokerTest {
   public interface IService {
      String work(String input);
   }

   public static class Service implements IService {
      private final Thread owner = Thread.currentThread();

      int invocations = 0;

      @Override
      public String work(@Nullable final String input) {
         Args.notNull("input", input);

         if (owner != Thread.currentThread())
            throw new RuntimeException("wrong thread!");

         LOG.info("Working on [" + input + "]...");
         invocations++;
         return input + input;
      }
   }

   private static final Logger LOG = Logger.create();

   @Test
   public void testCrossThreadMethodInvoker() {
      final Service service = new Service();

      final CrossThreadMethodInvoker methodInvoker = new CrossThreadMethodInvoker(2000);
      final CrossThreadProxy<IService> serviceProxy = methodInvoker.createProxy(service, IService.class);

      try {
         methodInvoker.waitForBackgroundThreads();
         failBecauseExceptionWasNotThrown(IllegalStateException.class);
      } catch (final IllegalStateException ex) {
         assertThat(ex.getMessage()).endsWith("is not started!");
      }

      methodInvoker.start(2);

      new Thread() {
         @Override
         public void run() {
            try {
               final IService srv = serviceProxy.get();
               assertThat(srv.work("foo")).isEqualTo("foofoo");
               try {
                  service.work(null);
                  failBecauseExceptionWasNotThrown(Exception.class);
               } catch (final Exception ex) {
                  assertThat(ex.getMessage()).isEqualTo("[input] must not be null");
               }
               try {
                  service.work("oops");
                  failBecauseExceptionWasNotThrown(Exception.class);
               } catch (final Exception ex) {
                  assertThat(ex.getMessage()).isEqualTo("wrong thread!");
               }
               assertThat(srv.work("bar")).isEqualTo("barbar");
            } finally {
               methodInvoker.backgroundThreadDone();
            }
         }
      }.start();

      new Thread() {
         @Override
         public void run() {
            try {
               final IService srv = serviceProxy.get();
               assertThat(srv.work("hey")).isEqualTo("heyhey");
               assertThat(srv.work("hoo")).isEqualTo("hoohoo");
            } finally {
               methodInvoker.backgroundThreadDone();
            }
         }
      }.start();

      assertThat(serviceProxy.getCrossThreadMethodInvoker()).isEqualTo(methodInvoker);

      methodInvoker.waitForBackgroundThreads();

      assertThat(service.invocations).isEqualTo(4);
   }
}
