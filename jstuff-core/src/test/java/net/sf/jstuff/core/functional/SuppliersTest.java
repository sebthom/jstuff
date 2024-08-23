/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Supplier;

import org.junit.Test;

import net.sf.jstuff.core.concurrent.Threads;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SuppliersTest {

   @Test
   public void testMemoize() {
      final Supplier<Object> supplier = Suppliers.memoize(Object::new);
      assertThat(supplier.get()).isSameAs(supplier.get());
   }

   @Test
   public void testMemoizeSoft() {
      final Supplier<Object> supplier = Suppliers.memoizeSoft(Object::new);
      assertThat(supplier.get()).isSameAs(supplier.get());
      final int hashCode = supplier.get().hashCode();
      System.gc();
      Threads.sleep(200);
      System.gc();
      Threads.sleep(200);
      assertThat(supplier.get().hashCode()).isEqualTo(hashCode);
   }

   @Test
   public void testMemoizeSoftWithTTL() {
      final Supplier<Object> supplier = Suppliers.memoizeSoft(Object::new, 500);
      assertThat(supplier.get()).isSameAs(supplier.get());
      final Object obj = supplier.get();
      Threads.sleep(501);
      assertThat(obj).isNotSameAs(supplier.get());
   }

   @Test
   public void testMemoizeWeak() {
      final Supplier<Object> supplier = Suppliers.memoizeWeak(Object::new);
      assertThat(supplier.get()).isSameAs(supplier.get());
      final int hashCode = supplier.get().hashCode();
      System.gc();
      Threads.sleep(200);
      System.gc();
      Threads.sleep(200);
      assertThat(supplier.get().hashCode()).isNotEqualTo(hashCode);
   }

   @Test
   public void testMemoizeWeakWithTTL() {
      final Supplier<Object> supplier = Suppliers.memoizeWeak(Object::new, 500);
      assertThat(supplier.get()).isSameAs(supplier.get());
      final Object obj = supplier.get();
      Threads.sleep(501);
      assertThat(obj).isNotSameAs(supplier.get());
   }

   @Test
   public void testMemoizeWithTTL() {
      final Supplier<Object> supplier = Suppliers.memoize(Object::new, 500);
      assertThat(supplier.get()).isSameAs(supplier.get());
      final Object obj = supplier.get();
      Threads.sleep(501);
      assertThat(obj).isNotSameAs(supplier.get());
   }

   @Test
   public void testOf() {
      final var obj = new Object();
      assertThat(Suppliers.of(obj).get()).isSameAs(obj);
   }
}
