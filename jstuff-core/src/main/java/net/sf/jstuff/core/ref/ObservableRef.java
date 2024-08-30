/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.ref;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Observable mutable reference holder.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface ObservableRef<V> extends Ref<V> {

   void await(V desiredValue) throws InterruptedException;

   boolean await(V desiredValue, long timeout, TimeUnit unit) throws InterruptedException;

   /**
    * @return if any observer is subscribed, does not take threads into account that are listening using {@link #await(Object)}
    */
   boolean isObserved();

   /**
    * @param observer a {@link Consumer}, {@link BiConsumer} or {@link Runnable}
    */
   boolean isObserving(Object observer);

   void subscribe(BiConsumer<V, V> observer);

   void subscribe(Consumer<V> observer);

   void subscribe(Runnable observer);

   void unsubscribe(BiConsumer<V, V> observer);

   void unsubscribe(Consumer<V> observer);

   void unsubscribe(Runnable observer);
}
