/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.logging.jul;

import java.util.logging.Level;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Levels {

   public static final int ALL_INT = Integer.MIN_VALUE;
   public static final int FINEST_INT = 300;
   public static final int FINER_INT = 400;
   public static final int FINE_INT = 500;
   public static final int CONFIG_INT = 700;
   public static final int INFO_INT = 800;
   public static final int WARNING_INT = 900;
   public static final int SEVERE_INT = 1000;
   public static final int OFF_INT = Integer.MAX_VALUE;

   public static Level getRootLevel() {
      synchronized (Loggers.ROOT_LOGGER) {
         final Level rootLevel = Loggers.ROOT_LOGGER.getLevel();
         if (rootLevel == null) {
            setRootLevel(Level.INFO);
            return Level.INFO;
         }
         return rootLevel;
      }
   }

   public static boolean isLoggable(final Level logRecordLevel, final @Nullable Level loggerLevel) {
      if (loggerLevel == null)
         return false;

      return logRecordLevel.intValue() >= loggerLevel.intValue();
   }

   /**
    * @return old log level
    */
   public static Level setRootLevel(final Level enabledLevel) {
      synchronized (Loggers.ROOT_LOGGER) {
         final Level oldLevel = Loggers.ROOT_LOGGER.getLevel();
         Loggers.ROOT_LOGGER.setLevel(enabledLevel);
         return oldLevel == null ? Level.INFO : oldLevel;
      }
   }
}
