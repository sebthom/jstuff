/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.logging;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.junit.Before;
import org.junit.Test;

import net.sf.jstuff.core.concurrent.Threads;
import net.sf.jstuff.core.logging.jul.Loggers;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class LoggingTest {
   private static class Entity implements InterfaceA, InterfaceB {
      @Override
      public Object[] methodA(final int a, final String b, final String... c) {
         Threads.sleep(1000);
         return new Object[] {b, c};
      }

      @Override
      public void methodB() {
         Threads.sleep(1000);
      }
   }

   private interface InterfaceA {
      Object[] methodA(int a, String b, String... c);
   }

   private interface InterfaceB {
      void methodB();
   }

   private static final Logger LOG = Logger.create();
   private static final Formatter ROOT_LOGGER_FORMATTER = new Formatter() {
      private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

      @Override
      public synchronized String format(final LogRecord record) {
         final StringBuilder sb = new StringBuilder(1000);
         sb.append(df.format(new Date(record.getMillis()))).append(" ");
         sb.append(record.getLevel().getName().charAt(0)).append(" ");
         {
            sb.append("[");
            if (record.getSourceClassName() != null) {
               sb.append(record.getSourceClassName());
            } else {
               sb.append(record.getLoggerName());
            }
            if (record.getSourceMethodName() != null) {
               sb.append("#").append(record.getSourceMethodName()).append("()");
            }
            sb.append("] ");
         }
         sb.append(formatMessage(record));
         if (record.getThrown() != null) {
            final StringWriter errors = new StringWriter();
            record.getThrown().printStackTrace(new PrintWriter(errors));
            sb.append(errors);
         }
         sb.append("\n");
         return sb.toString();
      }
   };

   private void coolMethod(final String label, final int number, final boolean flag, final Class<?> clazz, final Object obj) {
      LOG.entry();
      LOG.entry(label, number);
      LOG.entry(label, number, flag, clazz, obj);
      LOG.error("ERROR");
      try {
         throw new RuntimeException("Cannot process request.");
      } catch (final Exception ex) {
         LOG.error(ex);
      }
      try {
         throw new RuntimeException("Cannot initialize service.");
      } catch (final Exception ex) {
         LOG.fatal(ex);
      }
      LOG.warn("WARN");
      LOG.info("INFO");
      LOG.debug("DEBUG");
      LOG.trace("TRACE");
      LOG.exit();
      LOG.exit(1234);
   }

   private void genericLoggerTest() {
      assertThat(LOG).isNotNull();
      assertThat(LOG.getName()).isEqualTo(LoggingTest.class.getName());

      for (final Handler handler : Loggers.ROOT_LOGGER.getHandlers()) {
         handler.setLevel(java.util.logging.Level.ALL);
      }

      final int[] count = {0};
      final Handler h = new Handler() {
         @Override
         public void close() throws SecurityException {
         }

         @Override
         public void flush() {
         }

         @Override
         public void publish(final LogRecord record) {
            count[0]++;
         }
      };
      Loggers.ROOT_LOGGER.addHandler(h);
      try {
         System.out.println("LOGGER LEVEL = DEFAULT (INFO) ****************");
         assertThat(LOG.isInfoEnabled()).isTrue();
         assertThat(LOG.isDebugEnabled()).isFalse();
         count[0] = 0;
         coolMethod("hello", 5, true, Void.class, Integer.valueOf(42));
         assertThat(count[0]).isEqualTo(5);

         Threads.sleep(50);
         System.out.println("LOGGER LEVEL = INFO **************************");
         java.util.logging.Logger.getLogger(LOG.getName()).setLevel(java.util.logging.Level.INFO);
         assertThat(LOG.isInfoEnabled()).isTrue();
         assertThat(LOG.isDebugEnabled()).isFalse();
         count[0] = 0;
         coolMethod("hello", 5, true, Void.class, Integer.valueOf(42));
         assertThat(count[0]).isEqualTo(5);

         Threads.sleep(50);
         System.out.println("LOGGER LEVEL = FINEST **************************");
         java.util.logging.Logger.getLogger(LOG.getName()).setLevel(java.util.logging.Level.FINEST);
         assertThat(LOG.isTraceEnabled()).isTrue();
         count[0] = 0;
         coolMethod("hello", 5, true, Void.class, Integer.valueOf(42));
         assertThat(count[0]).isEqualTo(12);

         Threads.sleep(50);
         System.out.println("LOGGER LEVEL = SEVERE (INHERTIED) ************");
         Loggers.ROOT_LOGGER.setLevel(java.util.logging.Level.SEVERE);
         assertThat(LOG.isTraceEnabled()).isTrue();
         java.util.logging.Logger.getLogger(LOG.getName()).setLevel(null);
         assertThat(LOG.isWarnEnabled()).isFalse();
         assertThat(LOG.isErrorEnabled()).isTrue();
         count[0] = 0;
         coolMethod("hello", 5, true, Void.class, Integer.valueOf(42));
         assertThat(count[0]).isEqualTo(3);
      } finally {
         Loggers.ROOT_LOGGER.removeHandler(h);
      }
   }

   @Before
   public void setUp() throws Exception {
      System.out.println(LOG.isDebugEnabled());

      System.out.println("############################################");
      System.out.println("Resetting loggers...");
      System.out.println("############################################");
      Loggers.ROOT_LOGGER.setLevel(java.util.logging.Level.INFO);
      for (final Handler handler : Loggers.ROOT_LOGGER.getHandlers()) {
         handler.setLevel(java.util.logging.Level.INFO);
         if (handler instanceof ConsoleHandler) {
            ((ConsoleHandler) handler).setFormatter(ROOT_LOGGER_FORMATTER);
         }
      }

      java.util.logging.Logger.getLogger(LoggingTest.class.getName()).setLevel(null);
      Threads.sleep(50);
   }

   @Test
   public void test1LoggingJUL() {
      LoggerConfig.setPreferSLF4J(false);
      LoggerConfig.setCompactExceptionLogging(true);
      LoggerConfig.setAddLocationToDebugMessages(true);

      assertThat(((DelegatingLogger) LOG).getWrapped()).isInstanceOf(JULLogger.class);
      genericLoggerTest();
   }

   @Test
   public void test2LoggingSLF4J() {
      System.out.println(LOG.isDebugEnabled());

      LoggerConfig.setPreferSLF4J(true);
      LoggerConfig.setCompactExceptionLogging(false);
      LoggerConfig.setAddLocationToDebugMessages(false);

      assertThat(((DelegatingLogger) LOG).getWrapped()).isInstanceOf(SLF4JLogger.class);

      genericLoggerTest();
   }

   @Test
   public void testCreateLogged() {
      LoggerConfig.setPreferSLF4J(false);
      LoggerConfig.setCompactExceptionLogging(false);
      LoggerConfig.setAddLocationToDebugMessages(true);

      for (final Handler handler : Loggers.ROOT_LOGGER.getHandlers()) {
         handler.setLevel(java.util.logging.Level.ALL);
      }
      java.util.logging.Logger.getLogger(Entity.class.getName()).setLevel(java.util.logging.Level.FINEST);
      final int[] count = new int[1];
      final Handler h = new Handler() {
         @Override
         public void close() throws SecurityException {
         }

         @Override
         public void flush() {
         }

         @Override
         public void publish(final LogRecord record) {
            count[0]++;
            switch (count[0]) {
               case 1:
                  assertThat(record.getMessage()).isEqualTo("methodA():ENTRY >> (a: 1, b: \"foo\", c: [bar])");
                  break;
               case 2:
                  assertThat(record.getMessage()).startsWith("methodA():EXIT  << [foo, [bar]] ");
                  break;
               case 3:
                  assertThat(record.getMessage()).isEqualTo("methodB():ENTRY >> ()");
                  break;
               case 4:
                  assertThat(record.getMessage()).startsWith("methodB():EXIT  << *void* ");
                  break;
            }
         }
      };
      Loggers.ROOT_LOGGER.addHandler(h);
      try {
         final Entity entity = new Entity();
         final InterfaceA wrapped = Logger.createLogged(entity, InterfaceA.class, InterfaceB.class);
         wrapped.methodA(1, "foo", "bar");

         ((InterfaceB) wrapped).methodB();
      } finally {
         Loggers.ROOT_LOGGER.removeHandler(h);
      }
   }
}
