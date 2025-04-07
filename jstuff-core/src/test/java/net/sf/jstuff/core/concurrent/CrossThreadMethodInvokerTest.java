/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import static org.assertj.core.api.Assertions.*;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

import net.sf.jstuff.core.concurrent.CrossThreadMethodInvoker.CrossThreadProxy;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class CrossThreadMethodInvokerTest {
   interface IService {
      String work(String input);
   }

   static class Service implements IService {
      private final Thread owner = Thread.currentThread();

      int invocations = 0;

      @Override
      public String work(final @Nullable String input) {
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
   void testCrossThreadMethodInvoker() {
      final var service = new Service();

      final var methodInvoker = new CrossThreadMethodInvoker(2000);
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
