/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
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
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import net.sf.jstuff.core.SystemUtils;
import net.sf.jstuff.core.builder.Builder;
import net.sf.jstuff.core.builder.BuilderFactory;
import net.sf.jstuff.core.fluent.Fluent;

/**
 * Tries to use /dev/urandom (NativePRNGNonBlocking) by default on Unix.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ThreadLocalSecureRandom extends SecureRandom {

   @Builder.Property(required = false)
   public interface ThreadLocalSecureRandomBuilder extends Builder<ThreadLocalSecureRandom> {

      @Fluent
      ThreadLocalSecureRandomBuilder algorithm(String algorithm);

      @Fluent
      ThreadLocalSecureRandomBuilder algorithm(String algorithm, Provider provider);

      @Fluent
      ThreadLocalSecureRandomBuilder algorithm(String algorithm, String provider);

      @Fluent
      ThreadLocalSecureRandomBuilder reseedEvery(Duration duration);

      /**
       * Specifies to use the strongest but probably blocking crypto source available (e.g. /dev/random on Unix).
       *
       * {@link ThreadLocalSecureRandomBuilder#useStrongInstances(boolean)} and {@link ThreadLocalSecureRandomBuilder#algorithm(String)} are mutually exclusive
       */
      @Fluent
      ThreadLocalSecureRandomBuilder useStrongInstances(boolean value);
   }

   private static final long serialVersionUID = 1L;

   public static ThreadLocalSecureRandomBuilder builder() {
      return BuilderFactory.of(ThreadLocalSecureRandomBuilder.class).create();
   }

   public static ThreadLocalSecureRandom getInstance(final String algorithm) throws NoSuchAlgorithmException {
      final ThreadLocalSecureRandom random = builder().algorithm(algorithm).build();
      try {
         random.instances.get();
      } catch (final RuntimeSecurityException ex) {
         if (ex.getCause() instanceof NoSuchAlgorithmException)
            throw (NoSuchAlgorithmException) ex.getCause();
         throw ex;
      }
      return random;
   }

   public static ThreadLocalSecureRandom getInstance(final String algorithm, final Provider provider) throws NoSuchAlgorithmException {
      final ThreadLocalSecureRandom random = builder().algorithm(algorithm, provider).build();
      try {
         random.instances.get();
      } catch (final RuntimeSecurityException ex) {
         if (ex.getCause() instanceof NoSuchAlgorithmException)
            throw (NoSuchAlgorithmException) ex.getCause();
         throw ex;
      }
      return random;
   }

   public static ThreadLocalSecureRandom getInstance(final String algorithm, final String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
      final ThreadLocalSecureRandom random = builder().algorithm(algorithm, provider).build();
      try {
         random.instances.get();
      } catch (final RuntimeSecurityException ex) {
         if (ex.getCause() instanceof NoSuchAlgorithmException)
            throw (NoSuchAlgorithmException) ex.getCause();
         if (ex.getCause() instanceof NoSuchProviderException)
            throw (NoSuchProviderException) ex.getCause();
         throw ex;
      }
      return random;
   }

   public static ThreadLocalSecureRandom getInstanceStrong() throws NoSuchAlgorithmException {
      final ThreadLocalSecureRandom random = builder().useStrongInstances(true).build();
      try {
         random.instances.get();
      } catch (final RuntimeSecurityException ex) {
         if (ex.getCause() instanceof NoSuchAlgorithmException)
            throw (NoSuchAlgorithmException) ex.getCause();
         throw ex;
      }
      return random;
   }

   @Deprecated
   public static byte[] getSeed(final int numBytes) {
      return SecureRandom.getSeed(numBytes);
   }

   private final ThreadLocal<SecureRandom> instances = new ThreadLocal<SecureRandom>() {
      private long seedTimestamp;

      @Override
      public SecureRandom get() {
         if (reseedMS > 0) {
            final long now = System.currentTimeMillis();
            if (now - seedTimestamp > reseedMS) {
               set(initialValue());
            }
         }
         return super.get();
      }

      @Override
      protected SecureRandom initialValue() {
         seedTimestamp = System.currentTimeMillis();
         try {
            if (algorithm != null) {
               if (algorithmProvider instanceof String)
                  return SecureRandom.getInstance(algorithm, (String) algorithmProvider);
               if (algorithmProvider instanceof Provider)
                  return SecureRandom.getInstance(algorithm, (Provider) algorithmProvider);
               return SecureRandom.getInstance(algorithm);
            }

            if (useStrongInstances)
               return SecureRandom.getInstanceStrong();

            if (SystemUtils.IS_OS_UNIX) {
               try {
                  // https://tersesystems.com/blog/2015/12/17/the-right-way-to-use-securerandom/
                  return SecureRandom.getInstance("NativePRNGNonBlocking");
               } catch (final NoSuchAlgorithmException ex) {
                  // ignore
               }
            }

            return new SecureRandom();

         } catch (final NoSuchAlgorithmException | NoSuchProviderException ex) {
            throw new RuntimeSecurityException(ex);
         }
      }
   };

   private long reseedMS = -1;

   private String algorithm;
   private Object algorithmProvider;
   private boolean useStrongInstances;
   private final boolean isInitialized;

   protected ThreadLocalSecureRandom() {
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
   public byte[] generateSeed(final int numBytes) {
      return instances.get().generateSeed(numBytes);
   }

   @Override
   public String getAlgorithm() {
      return instances.get().getAlgorithm();
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
   private void readObject(final java.io.ObjectInputStream ois) throws IOException, ClassNotFoundException {
      throw new UnsupportedOperationException();
   }

   protected void setAlgorithm(final String algorithm) {
      this.algorithm = algorithm;
      useStrongInstances = false;
   }

   protected void setAlgorithm(final String algorithm, final Provider provider) {
      this.algorithm = algorithm;
      algorithmProvider = provider;
      useStrongInstances = false;
   }

   protected void setAlgorithm(final String algorithm, final String provider) {
      this.algorithm = algorithm;
      algorithmProvider = provider;
      useStrongInstances = false;
   }

   protected void setReseedEvery(final Duration value) {
      if (value == null || value.toMillis() < 1) {
         reseedMS = -1;
      } else {
         reseedMS = value.toMillis();
      }
   }

   /**
    * @throws UnsupportedOperationException always
    */
   @Override
   public void setSeed(final byte[] seed) {
      throw new UnsupportedOperationException();
   }

   /**
    * @throws UnsupportedOperationException always
    */
   @Override
   public void setSeed(final long seed) {
      if (isInitialized)
         throw new UnsupportedOperationException();
   }

   protected void setUseStrongInstances(final boolean value) {
      useStrongInstances = value;
      algorithm = null;
      algorithmProvider = null;
   }

   @SuppressWarnings("unused")
   private void writeObject(final ObjectOutputStream oos) throws IOException {
      throw new UnsupportedOperationException();
   }
}
