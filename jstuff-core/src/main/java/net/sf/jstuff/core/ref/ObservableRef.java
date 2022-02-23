/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.ref;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Observable mutable reference holder.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface ObservableRef<V> extends Ref<V> {

   boolean isObserved();

   boolean isObserving(Object observer);

   void subscribe(BiConsumer<V, V> observer);

   void subscribe(Consumer<V> observer);

   void subscribe(Runnable observer);

   void unsubscribe(BiConsumer<V, V> observer);

   void unsubscribe(Consumer<V> observer);

   void unsubscribe(Runnable observer);
}
