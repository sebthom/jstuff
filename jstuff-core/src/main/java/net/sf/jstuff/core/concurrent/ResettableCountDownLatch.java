/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.concurrent;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

import net.sf.jstuff.core.reflection.Fields;
import net.sf.jstuff.core.reflection.Methods;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ResettableCountDownLatch extends CountDownLatch {

   private final int count;
   private final AbstractQueuedSynchronizer sync;
   private final Method syncSetState;

   public ResettableCountDownLatch(final int count) {
      super(count);
      this.count = count;
      sync = Fields.read(this, "sync");
      syncSetState = Methods.findAny(AbstractQueuedSynchronizer.class, "setState", int.class);
   }

   public void reset() {
      Methods.invoke(sync, syncSetState, count);
   }
}
