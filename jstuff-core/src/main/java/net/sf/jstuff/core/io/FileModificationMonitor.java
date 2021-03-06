/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io;

import java.io.File;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import net.sf.jstuff.core.validation.Args;

/**
 * Monitors the modification date of the given file
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class FileModificationMonitor extends Observable {
   private static long getModificationDate(final File file) {
      return file == null || !file.exists() ? -1 : file.lastModified();
   }

   private final File file;
   private long pollingInterval = 1000;
   private boolean isMonitoring;

   private long lastModified;
   private final Timer timer = new Timer();

   private final TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
         if (isMonitoring) {
            final long currentLastModified = getModificationDate(file);
            if (lastModified != currentLastModified) {
               lastModified = currentLastModified;
               setChanged();
               notifyObservers();
            }
         }
      }
   };

   public FileModificationMonitor(final File file) {
      Args.notNull("file", file);

      this.file = file;
   }

   public FileModificationMonitor(final File file, final long pollingInterval) {
      Args.notNull("file", file);

      this.file = file;
      this.pollingInterval = pollingInterval;
   }

   /**
    * @return Returns the file being monitored
    */
   public File getFile() {
      return file;
   }

   /**
    * @return Returns the monitoring interval.
    */
   public long getPollingInterval() {
      return pollingInterval;
   }

   /**
    * @return Determines if the monitor is currently running.
    */
   public boolean isMonitoring() {
      return isMonitoring;
   }

   public synchronized void startMonitoring() {
      if (!isMonitoring) {
         lastModified = getModificationDate(file);
         timer.schedule(timerTask, 0, pollingInterval);
         isMonitoring = true;
      }
   }

   public synchronized void stopMonitoring() {
      timer.cancel();
      isMonitoring = false;
   }
}
