/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
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
public interface Either<L, R> extends Serializable {

   static <L, R> Either<L, R> left(final L left) {
      return new Either<>() {
         private static final long serialVersionUID = 1L;

         @Override
         public void apply(final Consumer<? super L> leftConsumer, final Consumer<? super R> rightConsumer) {
            leftConsumer.accept(left);
         }

         @Override
         public boolean isLeft() {
            return true;
         }

         @Override
         public boolean isRight() {
            return false;
         }

         @Override
         public L left() {
            return left;
         }

         @Override
         public L leftOrElse(final L fallback) {
            return left;
         }

         @Override
         public <T> T map(final Function<? super L, ? extends T> leftMapper, final Function<? super R, ? extends T> rightMapper) {
            return leftMapper.apply(left);
         }

         @Override
         public R right() {
            throw new IllegalStateException("Right value not present");
         }

         @Override
         public R rightOrElse(final R fallback) {
            return fallback;
         }

         @Override
         public String toString() {
            return Objects.toString(left);
         }

         @SuppressWarnings("null")
         @Override
         public L value() {
            return left;
         }

         @Override
         public <T> Either<T, R> mapLeft(final Function<? super L, ? extends T> mapper) {
            final T leftMapped = mapper.apply(left());
            return Either.left(leftMapped);
         }

         @Override
         @SuppressWarnings("unchecked")
         public <T> Either<L, T> mapRight(final Function<? super R, ? extends T> mapper) {
            return (Either<L, T>) this;
         }

         @Override
         public boolean equals(final Object obj) {
            if (this == obj)
               return true;
            if (obj == null || getClass() != obj.getClass())
               return false;
            final var other = (Either<?, ?>) obj;
            if (other.isRight())
               return false;
            return Objects.equals(left, other.left());
         }

         @Override
         public int hashCode() {
            return Objects.hash(left);
         }
      };
   }

   static <L, R> Either<L, R> right(final R right) {
      return new Either<>() {
         private static final long serialVersionUID = 1L;

         @Override
         public void apply(final Consumer<? super L> leftConsumer, final Consumer<? super R> rightConsumer) {
            rightConsumer.accept(right);
         }

         @Override
         public boolean isLeft() {
            return false;
         }

         @Override
         public boolean isRight() {
            return true;
         }

         @Override
         public L left() {
            throw new IllegalStateException("Left value not present");
         }

         @Override
         public L leftOrElse(final L fallback) {
            return fallback;
         }

         @Override
         public <T> T map(final Function<? super L, ? extends T> leftMapper, final Function<? super R, ? extends T> rightMapper) {
            return rightMapper.apply(right);
         }

         @Override
         public R right() {
            return right;
         }

         @Override
         public R rightOrElse(final R fallback) {
            return right;
         }

         @Override
         public String toString() {
            return Objects.toString(right);
         }

         @SuppressWarnings("null")
         @Override
         public R value() {
            return right;
         }

         @Override
         @SuppressWarnings("unchecked")
         public <T> Either<T, R> mapLeft(final Function<? super L, ? extends T> mapper) {
            return (Either<T, R>) this;
         }

         @Override
         public <T> Either<L, T> mapRight(final Function<? super R, ? extends T> mapper) {
            final T rightMapped = mapper.apply(right());
            return Either.right(rightMapped);
         }

         @Override
         public boolean equals(final Object obj) {
            if (this == obj)
               return true;
            if (obj == null || getClass() != obj.getClass())
               return false;
            final var other = (Either<?, ?>) obj;
            if (other.isLeft())
               return false;
            return Objects.equals(right, other.right());
         }

         @Override
         public int hashCode() {
            return Objects.hash(right);
         }
      };
   }

   void apply(Consumer<? super L> leftConsumer, Consumer<? super R> rightConsumer);

   boolean isLeft();

   boolean isRight();

   /**
    * @throws IllegalStateException if left value not present
    */
   L left();

   L leftOrElse(L fallback);

   <T> T map(Function<? super L, ? extends T> leftMapper, Function<? super R, ? extends T> rightMapper);

   <T> Either<T, R> mapLeft(Function<? super L, ? extends T> mapper);

   <T> Either<L, T> mapRight(Function<? super R, ? extends T> mapper);

   /**
    * @throws IllegalStateException if right value not present
    */
   R right();

   R rightOrElse(R fallback);

   Object value();
}
