/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.types;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public final class Either<L, R> implements Serializable {

   private static final long serialVersionUID = 1L;

   public static <L, R> Either<L, R> left(final L left) {
      return new Either<>(left, null, true);
   }

   public static <L, R> Either<L, R> right(final R right) {
      return new Either<>(null, right, false);
   }

   private final boolean isLeft;

   private final L left;

   private final R right;

   private Either(final L left, final R right, final boolean isLeft) {
      this.left = left;
      this.right = right;
      this.isLeft = isLeft;
   }

   public void apply(final Consumer<? super L> leftConsumer, final Consumer<? super R> rightConsumer) {
      if (isLeft) {
         leftConsumer.accept(left);
      } else {
         rightConsumer.accept(right);
      }
   }

   @Override
   public boolean equals(final Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final Either<?, ?> other = (Either<?, ?>) obj;
      if (isLeft != other.isLeft)
         return false;
      if (!Objects.equals(left, other.left))
         return false;
      if (!Objects.equals(right, other.right))
         return false;
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (isLeft ? 1231 : 1237);
      result = prime * result + (left == null ? 0 : left.hashCode());
      result = prime * result + (right == null ? 0 : right.hashCode());
      return result;
   }

   public boolean isLeft() {
      return isLeft;
   }

   public boolean isRight() {
      return !isLeft;
   }

   /**
    * @throws IllegalStateException if left value not present
    */
   public L left() {
      if (!isLeft)
         throw new IllegalStateException("Left value not present");
      return left;
   }

   public L leftOrElse(final L fallback) {
      return isLeft ? left : fallback;
   }

   public <T> T map(final Function<? super L, ? extends T> leftMapper, final Function<? super R, ? extends T> rightMapper) {
      return isLeft ? leftMapper.apply(left) : rightMapper.apply(right);
   }

   @SuppressWarnings("unchecked")
   public <T> Either<T, R> mapLeft(final Function<? super L, ? extends T> mapper) {
      if (isLeft) {
         final T leftMapped = mapper.apply(left);
         return left(leftMapped);
      }
      return (Either<T, R>) this;
   }

   @SuppressWarnings("unchecked")
   public <T> Either<L, T> mapRight(final Function<? super R, ? extends T> mapper) {
      if (!isLeft) {
         final T rightMapped = mapper.apply(right);
         return right(rightMapped);
      }
      return (Either<L, T>) this;
   }

   /**
    * @throws IllegalStateException if right value not present
    */
   public R right() {
      if (isLeft)
         throw new IllegalStateException("Right value not present");
      return right;
   }

   public R rightOrElse(final R fallback) {
      return !isLeft ? right : fallback;
   }

   @Override
   public String toString() {
      return Objects.toString(isLeft ? left : right);
   }

   public Object value() {
      return isLeft ? left : right;
   }
}
