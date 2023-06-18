/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.queue;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class UniquePriorityBlockingQueue<E> extends PriorityBlockingQueue<E> {

   private static final long serialVersionUID = 1L;

   @Override
   public boolean offer(final E e) {
      if (contains(e))
         return false;
      return super.offer(e);
   }
}
