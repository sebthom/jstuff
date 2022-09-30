/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

import net.sf.jstuff.core.reflection.Fields;
import net.sf.jstuff.core.reflection.Methods;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ResettableCountDownLatch extends CountDownLatch {

   private final int count;
   private final AbstractQueuedSynchronizer synchronizer;
   private final Method syncSetState;

   public ResettableCountDownLatch(final int count) {
      super(count);
      this.count = count;
      synchronizer = Fields.read(this, "sync"); // access CountDownLatch#sync field
      syncSetState = Methods.findAny(AbstractQueuedSynchronizer.class, "setState", int.class);
   }

   public void reset() {
      Methods.invoke(synchronizer, syncSetState, count);
   }
}
