/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.future;

import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ConstantFuture<T> extends CompletableFuture<T> {

   public static <T> ConstantFuture<T> of(final T value) {
      return new ConstantFuture<>(value);
   }

   public ConstantFuture(final T value) {
      complete(value);
   }
}
