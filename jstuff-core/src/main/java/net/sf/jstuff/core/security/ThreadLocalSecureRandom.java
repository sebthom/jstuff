/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.security;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import net.sf.jstuff.core.builder.Builder;
import net.sf.jstuff.core.builder.BuilderFactory;
import net.sf.jstuff.core.fluent.Fluent;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ThreadLocalSecureRandom extends Random {

   @Builder.Property(required = true)
   public interface ThreadLocalSecureRandomBuilder extends Builder<ThreadLocalSecureRandom> {

      @Fluent
      ThreadLocalSecureRandomBuilder reseedEvery(Duration duration);

      @Fluent
      ThreadLocalSecureRandomBuilder useStrongInstances(boolean value);
   }

   private static final long serialVersionUID = 1L;

   public static ThreadLocalSecureRandomBuilder builder() {
      return BuilderFactory.of(ThreadLocalSecureRandomBuilder.class).create();
   }

   private final ThreadLocal<SecureRandom> instances = new ThreadLocal<SecureRandom>() {
      private long seedTimestamp;

      @Override
      public SecureRandom get() {
         if (reseedMS > 0) {
            final long now = System.currentTimeMillis();
            if (now - seedTimestamp > reseedMS) {
               seedTimestamp = now;
               set(new SecureRandom());
            }
         }
         return super.get();
      }

      @Override
      protected SecureRandom initialValue() {
         seedTimestamp = System.currentTimeMillis();
         try {
            return useStrongInstances ? SecureRandom.getInstanceStrong() : new SecureRandom();
         } catch (final NoSuchAlgorithmException ex) {
            throw new RuntimeSecurityException(ex);
         }
      }
   };

   private long reseedMS = -1;
   private boolean useStrongInstances = false;
   private boolean isInitialized = false;

   protected ThreadLocalSecureRandom() {
      super(0); // CHECKSTYLE:IGNORE .*
      isInitialized = true;
   }

   @Override
   public DoubleStream doubles() {
      return instances.get().doubles();
   }

   @Override
   public DoubleStream doubles(final double randomNumberOrigin, final double randomNumberBound) {
      return instances.get().doubles(randomNumberOrigin, randomNumberBound);
   }

   @Override
   public DoubleStream doubles(final long streamSize) {
      return instances.get().doubles(streamSize);
   }

   @Override
   public DoubleStream doubles(final long streamSize, final double randomNumberOrigin, final double randomNumberBound) {
      return instances.get().doubles(streamSize, randomNumberOrigin, randomNumberBound);
   }

   @Override
   public IntStream ints() {
      return instances.get().ints();
   }

   @Override
   public IntStream ints(final int randomNumberOrigin, final int randomNumberBound) {
      return instances.get().ints(randomNumberOrigin, randomNumberBound);
   }

   @Override
   public IntStream ints(final long streamSize) {
      return instances.get().ints(streamSize);
   }

   @Override
   public IntStream ints(final long streamSize, final int randomNumberOrigin, final int randomNumberBound) {
      return instances.get().ints(streamSize, randomNumberOrigin, randomNumberBound);
   }

   @Override
   public LongStream longs() {
      return instances.get().longs();
   }

   @Override
   public LongStream longs(final long streamSize) {
      return instances.get().longs(streamSize);
   }

   @Override
   public LongStream longs(final long randomNumberOrigin, final long randomNumberBound) {
      return instances.get().longs(randomNumberOrigin, randomNumberBound);
   }

   @Override
   public LongStream longs(final long streamSize, final long randomNumberOrigin, final long randomNumberBound) {
      return instances.get().longs(streamSize, randomNumberOrigin, randomNumberBound);
   }

   @Override
   public boolean nextBoolean() {
      return instances.get().nextBoolean();
   }

   @Override
   public void nextBytes(final byte[] bytes) {
      instances.get().nextBytes(bytes);
   }

   @Override
   public double nextDouble() {
      return instances.get().nextDouble();
   }

   @Override
   public float nextFloat() {
      return instances.get().nextFloat();
   }

   @Override
   public synchronized double nextGaussian() {
      return instances.get().nextGaussian();
   }

   @Override
   public int nextInt() {
      return instances.get().nextInt();
   }

   @Override
   public int nextInt(final int bound) {
      return instances.get().nextInt(bound);
   }

   @Override
   public long nextLong() {
      return instances.get().nextLong();
   }

   @SuppressWarnings("unused")
   private void readObject(final java.io.ObjectInputStream ois) throws java.io.IOException, ClassNotFoundException {
      throw new UnsupportedOperationException();
   }

   protected void setReseedEvery(final Duration value) {
      if (value == null || value.toMillis() < 1) {
         reseedMS = -1;
      } else {
         reseedMS = value.toMillis();
      }
   }

   @Override
   public void setSeed(final long seed) {
      if (isInitialized) {
         instances.get().setSeed(seed);
      }
   }

   protected void setUseStrongInstances(final boolean value) {
      useStrongInstances = value;
   }

   @SuppressWarnings("unused")
   private void writeObject(final ObjectOutputStream oos) throws IOException {
      throw new UnsupportedOperationException();
   }
}
