/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.ref.Reference;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

import net.sf.jstuff.core.concurrent.Threads;

/**
 * Verifies identity-based weak-key lookup and snapshots, including cleared references and explicit null keys.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class WeakIdentityHashMapTest {
   private static final class Entity {
      private @Nullable String name;

      Entity setName(final String name) {
         this.name = name;
         return this;
      }

      @Override
      public int hashCode() {
         return Objects.hash(name);
      }

      @Override
      public boolean equals(final @Nullable Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         final Entity other = (Entity) obj;
         if (!Objects.equals(name, other.name))
            return false;
         return true;
      }
   }

   private static Reference<?> findWeakKeyReference(final WeakIdentityHashMap<?, ?> identityMap, final Object key)
         throws ReflectiveOperationException {
      final var mapField = WeakIdentityHashMap.class.getDeclaredField("map");
      mapField.setAccessible(true);
      final var backingMap = (Map<?, ?>) mapField.get(identityMap);
      if (backingMap != null) {
         for (final Object wrapper : backingMap.keySet()) {
            if (wrapper instanceof Reference<?> reference && reference.get() == key)
               return reference;
         }
      }
      throw new AssertionError("No weak reference found for the key");
   }

   @Test
   void testSnapshotsSkipClearedWeakKeys() throws ReflectiveOperationException {
      final var identityMap = new WeakIdentityHashMap<Object, String>();
      final var liveKey = new Object();
      final var clearedKey = new Object();
      identityMap.put(liveKey, "live");
      identityMap.put(clearedKey, "cleared");

      // clear() does not enqueue, so queue cleanup cannot hide the stale reference from this test.
      findWeakKeyReference(identityMap, clearedKey).clear();

      assertThat(identityMap.keySet()).containsExactly(liveKey);
      assertThat(identityMap.entrySet()).singleElement().satisfies(entry -> {
         assertThat(entry.getKey()).isSameAs(liveKey);
         assertThat(entry.getValue()).isEqualTo("live");
      });
      Reference.reachabilityFence(liveKey);
   }

   @Test
   void testSnapshotsPreserveNullKeyWhenWeakKeyClears() throws ReflectiveOperationException {
      final var identityMap = new WeakIdentityHashMap<@Nullable Object, String>();
      final var clearedKey = new Object();
      identityMap.put(null, "null key");
      identityMap.put(clearedKey, "cleared");

      findWeakKeyReference(identityMap, clearedKey).clear();

      assertThat(identityMap.keySet()).hasSize(1).containsNull();
      assertThat(identityMap.entrySet()).singleElement().satisfies(entry -> {
         assertThat(entry.getKey()).isNull();
         assertThat(entry.getValue()).isEqualTo("null key");
      });
   }

   @Test
   void testWeakIdentityHashMap() {
      final var identityMap = new WeakIdentityHashMap<@Nullable Entity, Object>();

      @Nullable
      Entity e1 = new Entity().setName("aa");
      @Nullable
      Entity e2 = new Entity().setName("aa");

      assertThat(e2).isEqualTo(e1).isNotSameAs(e1);

      identityMap.put(e1, Boolean.TRUE);
      identityMap.put(e2, Boolean.TRUE);
      identityMap.put(null, Boolean.TRUE);

      assertThat(identityMap) //
         .hasSize(3).containsKey(e1) //
         .containsKey(e2) //
         .containsKey(null);
      assertThat(identityMap.entrySet()).hasSize(3);
      assertThat(identityMap.keySet()).hasSize(3);
      assertThat(identityMap.values()).hasSize(3);

      System.gc();
      Threads.sleep(1000);

      assertThat(identityMap) //
         .hasSize(3) //
         .containsKey(e1) //
         .containsKey(e2) //
         .containsKey(null);
      assertThat(identityMap.entrySet()).hasSize(3);
      assertThat(identityMap.keySet()).hasSize(3);
      assertThat(identityMap.values()).hasSize(3);

      final var identityMap2 = new WeakIdentityHashMap<@Nullable Entity, Object>();

      identityMap2.put(e1, Boolean.TRUE);
      identityMap2.put(e2, Boolean.TRUE);
      identityMap2.put(null, Boolean.TRUE);

      assertThat(identityMap2).isEqualTo(identityMap);

      identityMap2.remove(e2);
      assertThat(identityMap2) //
         .containsKey(e1) //
         .doesNotContainKey(e2);
      assertThat(identityMap).isNotEqualTo(identityMap2);

      e1 = null;
      e2 = null;

      System.gc();
      Threads.sleep(1000);

      assertThat(identityMap).hasSize(1);
      assertThat(identityMap2).hasSize(1);

      identityMap.remove(null);

      assertThat(identityMap).isEmpty();
   }
}
