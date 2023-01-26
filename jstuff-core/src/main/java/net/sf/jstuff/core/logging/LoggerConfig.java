/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.logging;

import java.util.Set;

import net.sf.jstuff.core.collection.WeakIdentityHashSet;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public final class LoggerConfig {

   /**
    * weak set holding all instantiated loggers. required to switch the backing logger implementation during runtime if required.
    */
   private static final Set<DelegatingLogger> LOGGERS = new WeakIdentityHashSet<>(64);

   private static final Logger LOG = create(LoggerConfig.class.getName());

   private static final String PROPERTY_COMPACT_EXCEPTION_LOGGING = "net.sf.jstuff.core.logging.Logger.compactExceptionLogging";
   private static final String PROPERTY_ADD_LOCATION_TO_DEBUG_MESSAGES = "net.sf.jstuff.core.logging.Logger.addLocationToDebugMessages";
   private static final String PROPERTY_PREFER_SLF4J = "net.sf.jstuff.core.logging.Logger.preferSLF4J";
   private static final String PROPERTY_SANITIZE_STACK_TRACES = "net.sf.jstuff.core.logging.Logger.sanitizeStackTraces";

   private static boolean isSLF4JAvailable;
   private static boolean isPreferSLF4J = true;
   private static volatile boolean isUsingSLF4J;

   /**
    * If set to true, method name and line number are added to the log message.
    * This is esp. helpful in environments where you have no control over the used logger pattern by the underlying logger infrastructure
    * (e.g. in an JEE container).
    */
   static boolean isAddLocationToDebugMessages;
   static boolean isCompactExceptionLoggingEnabled;
   static boolean isSanitizeStrackTracesEnabled = true;

   static {
      setAddLocationToDebugMessages("true".equals(System.getProperty(PROPERTY_ADD_LOCATION_TO_DEBUG_MESSAGES, "false")));
      setCompactExceptionLogging("true".equals(System.getProperty(PROPERTY_COMPACT_EXCEPTION_LOGGING, "false")));
      setSanitizeStackTraces("true".equals(System.getProperty(PROPERTY_SANITIZE_STACK_TRACES, "true")));

      try {
         @SuppressWarnings("unused")
         final var test = new SLF4JLogger("");
         isSLF4JAvailable = true;
      } catch (final LinkageError err) {
         isSLF4JAvailable = false;
         LOG.trace(err);
      }

      setPreferSLF4J("true".equals(System.getProperty(PROPERTY_PREFER_SLF4J, "true")));
   }

   static Logger create(final String name) {
      Args.notNull("name", name);

      final var logger = new DelegatingLogger(isUsingSLF4J ? new SLF4JLogger(name) : new JULLogger(name));
      synchronized (LOGGERS) {
         LOGGERS.add(logger);
      }
      return logger;
   }

   public static boolean isAddLocationToDebugMessages() {
      return isAddLocationToDebugMessages;
   }

   public static boolean isCompactExceptionLoggingEnabled() {
      return isCompactExceptionLoggingEnabled;
   }

   public static boolean isPreferSLF4J() {
      return isPreferSLF4J;
   }

   public static boolean isSanitizeStrackTracesEnabled() {
      return isSanitizeStrackTracesEnabled;
   }

   public static boolean isSLF4JAvailable() {
      return isSLF4JAvailable;
   }

   public static boolean isUsingSLF4J() {
      return isUsingSLF4J;
   }

   public static void setAddLocationToDebugMessages(final boolean enabled) {
      LoggerConfig.isAddLocationToDebugMessages = enabled;
   }

   public static void setCompactExceptionLogging(final boolean enabled) {
      isCompactExceptionLoggingEnabled = enabled;
   }

   public static synchronized void setPreferSLF4J(final boolean value) {
      isPreferSLF4J = value;
      final boolean isUsingSLF4JNew = isSLF4JAvailable && isPreferSLF4J;
      if (isUsingSLF4JNew != isUsingSLF4J) {
         isUsingSLF4J = isUsingSLF4JNew;
         synchronized (LOGGERS) {
            // hot replacing the underlying logger infrastructure
            for (final DelegatingLogger logger : LOGGERS) {
               final String name = logger.getName();
               logger.setWrapped(isUsingSLF4J ? new SLF4JLogger(name) : new JULLogger(name));
            }
         }
      }
      if (isUsingSLF4J) {
         LOG.debug("Using SLF4J as logging infrastructure.");
      } else {
         LOG.debug("Using java.util.logging as logging infrastructure.");
      }
   }

   public static void setSanitizeStackTraces(final boolean enabled) {
      isSanitizeStrackTracesEnabled = enabled;
   }

   private LoggerConfig() {
   }
}
