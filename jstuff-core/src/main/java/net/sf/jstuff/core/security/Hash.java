/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.concurrent.NotThreadSafe;
import net.sf.jstuff.core.concurrent.ThreadSafe;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public interface Hash<T> {

   abstract class AbstractHash<T> implements Hash<T> {
      protected final String name;
      protected final byte[] salt;
      protected final Category category;

      protected AbstractHash(final Category category, final String name, final byte @Nullable [] salt) {
         this.category = category;
         this.name = name;
         this.salt = salt == null ? ArrayUtils.EMPTY_BYTE_ARRAY : salt;
      }

      @Override
      public Category getCategory() {
         return category;
      }

      @Override
      public String getName() {
         return name;
      }

      @Override
      public byte @Nullable [] getSalt() {
         return salt.length == 0 ? null : salt;
      }
   }

   /**
    * See https://en.wikipedia.org/wiki/List_of_hash_functions
    */
   enum Category {
      /** Cyclic redundancy check */
      CRC,
      CHECKSUM,
      /** Keyed cryptographic hash function **/
      KEYED_CRYPTOGRAPHIC,
      /** Unkeyed cryptographic hash function **/
      UNKEYED_CRYPTOGRAPHIC
   }

   abstract class ChecksumHash extends AbstractHash<Long> {

      protected ChecksumHash(final Category category, final String name, final byte @Nullable [] salt) {
         super(category, name, salt);
      }

      protected long compute(final InputStream is, final Checksum cs) throws IOException {
         if (salt.length > 0) {
            cs.update(salt);
         }

         final var buf = new byte[4096];
         int read;
         while ((read = is.read(buf)) > -1) {
            cs.update(buf, 0, read);
         }
         return cs.getValue();
      }

      protected long compute(final Path file, final Checksum cs) throws IOException {
         try (var fis = Files.newInputStream(file)) {
            return compute(fis, cs);
         }
      }

      protected long compute(final ReadableByteChannel ch, final Checksum cs) throws IOException {
         if (salt.length > 0) {
            cs.update(salt);
         }

         final var buf = ByteBuffer.allocate(4096);
         while (ch.read(buf) > -1) {
            buf.flip();
            cs.update(buf);
            buf.clear();
         }
         return cs.getValue();
      }

      @Override
      public Long hash(final byte[] bytes) {
         Args.notNull("bytes", bytes);

         final var cs = newInstance();
         if (salt.length > 0) {
            cs.update(salt);
         }
         cs.update(bytes);
         return cs.getValue();
      }

      @SuppressWarnings("resource")
      @Override
      public Long hash(final InputStream is) throws IOException {
         Args.notNull("is", is);

         return compute(is, newInstance());
      }

      @Override
      public Long hash(final Path file) throws IOException {
         Args.isFileReadable("file", file);

         return compute(file, newInstance());
      }

      @SuppressWarnings("resource")
      @Override
      public Long hash(final ReadableByteChannel ch) throws IOException {
         Args.notNull("ch", ch);

         return compute(ch, newInstance());
      }

      @Override
      public Long hash(final String text) {
         Args.notNull("text", text);

         return hash(text.getBytes(StandardCharsets.UTF_8));
      }

      @Override
      public Hasher<Long> newHasher() {
         final var hashImpl = this;
         final var cs = newInstance();
         return new Hasher<>() {
            {
               reset();
            }

            @Override
            public Hash<Long> getHash() {
               return hashImpl;
            }

            @Override
            public Long hash() {
               return cs.getValue();
            }

            @Override
            public Hasher<Long> reset() {
               cs.reset();
               if (salt.length > 0) {
                  cs.update(salt);
               }
               return this;
            }

            @Override
            public Hasher<Long> update(final byte b) {
               cs.update(b);
               return this;
            }

            @Override
            public Hasher<Long> update(final byte[] bytes) {
               cs.update(bytes);
               return this;
            }

            @Override
            public Hasher<Long> update(final byte[] bytes, final int offset, final int len) {
               cs.update(bytes, offset, len);
               return this;
            }

            @Override
            public Hasher<Long> update(final ByteBuffer bytes) {
               cs.update(bytes);
               return this;
            }
         };
      }

      protected abstract Checksum newInstance();

      @Override
      public Hash<Long> withSalt(final byte[] salt) {
         final var thisInstance = this;
         return new ChecksumHash(category, name, salt) {
            @Override
            protected Checksum newInstance() {
               return thisInstance.newInstance();
            }
         };
      }
   }

   /**
    * Incremental hasher
    */
   @NotThreadSafe
   interface Hasher<T> {

      Hash<T> getHash();

      /**
       * @return the computed hash and resets this instance
       */
      T hash();

      Hasher<T> reset();

      Hasher<T> update(byte b);

      Hasher<T> update(byte[] bytes);

      Hasher<T> update(byte[] bytes, int offset, int len);

      Hasher<T> update(ByteBuffer bytes);
   }

   class MessageDigestHash extends AbstractHash<String> {

      protected static MessageDigest lookup(final String name) throws SecurityException {
         try {
            return MessageDigest.getInstance(name);
         } catch (final NoSuchAlgorithmException ex) {
            throw new SecurityException(ex);
         }
      }

      protected static String toHexString(final byte[] bytes) {
         //same as:
         // return Strings.leftPad(new BigInteger(1, bytes).toString(16), hashCharLength, '0');
         final var sb = new StringBuilder();
         for (final byte b : bytes) {
            final int x = b & 0xff;
            if (x < 0x10) {
               sb.append('0');
            }
            sb.append(Integer.toHexString(x));
         }
         return sb.toString();
      }

      protected MessageDigestHash(final Category category, final String name, final byte @Nullable [] salt) {
         super(category, name, salt);
      }

      protected byte[] compute(final InputStream is, final MessageDigest md) throws IOException {
         if (salt.length > 0) {
            md.update(salt);
         }

         final var buf = new byte[4096];
         int read;
         while ((read = is.read(buf)) > -1) {
            md.update(buf, 0, read);
         }
         return md.digest();
      }

      protected byte[] compute(final Path file, final MessageDigest md) throws IOException {
         try (var fis = Files.newInputStream(file)) {
            return compute(fis, md);
         }
      }

      protected byte[] compute(final ReadableByteChannel ch, final MessageDigest md) throws IOException {
         if (salt.length > 0) {
            md.update(salt);
         }

         final var buf = ByteBuffer.allocate(4096);
         while (ch.read(buf) > -1) {
            buf.flip();
            md.update(buf);
            buf.clear();
         }
         return md.digest();
      }

      @Override
      public String hash(final byte[] bytes) {
         Args.notNull("bytes", bytes);

         final var md = newInstance();
         if (salt.length > 0) {
            md.update(salt);
         }
         md.update(bytes);
         return toHexString(md.digest());
      }

      @SuppressWarnings("resource")
      @Override
      public String hash(final InputStream is) throws IOException {
         Args.notNull("is", is);

         final var hash = compute(is, newInstance());
         return toHexString(hash);
      }

      @Override
      public String hash(final Path file) throws IOException {
         Args.isFileReadable("file", file);

         final var hash = compute(file, newInstance());
         return toHexString(hash);
      }

      @SuppressWarnings("resource")
      @Override
      public String hash(final ReadableByteChannel ch) throws IOException {
         Args.notNull("ch", ch);

         final var hash = compute(ch, newInstance());
         return toHexString(hash);
      }

      @Override
      public String hash(final String text) {
         Args.notNull("text", text);

         return hash(text.getBytes(StandardCharsets.UTF_8));
      }

      @Override
      public Hasher<String> newHasher() {
         final var hashImpl = this;
         final var md = newInstance();
         return new Hasher<>() {
            {
               reset();
            }

            @Override
            public Hash<String> getHash() {
               return hashImpl;
            }

            @Override
            public String hash() {
               return toHexString(md.digest());
            }

            @Override
            public Hasher<String> reset() {
               md.reset();
               if (salt.length > 0) {
                  md.update(salt);
               }
               return this;
            }

            @Override
            public Hasher<String> update(final byte b) {
               md.update(b);
               return this;
            }

            @Override
            public Hasher<String> update(final byte[] bytes) {
               md.update(bytes);
               return this;
            }

            @Override
            public Hasher<String> update(final byte[] bytes, final int offset, final int len) {
               md.update(bytes, offset, len);
               return this;
            }

            @Override
            public Hasher<String> update(final ByteBuffer bytes) {
               md.update(bytes);
               return this;
            }
         };
      }

      protected MessageDigest newInstance() throws SecurityException {
         return lookup(name);
      }

      @Override
      public Hash<String> withSalt(final byte[] salt) {
         final var thisInstance = this;
         return new MessageDigestHash(category, name, salt) {
            @Override
            protected MessageDigest newInstance() {
               return thisInstance.newInstance();
            }
         };
      }
   }

   Hash<Long> ADLER32 = new ChecksumHash(Category.CHECKSUM, "Adler-32", null) {
      @Override
      protected Checksum newInstance() {
         return new Adler32();
      }
   };

   Hash<Long> CRC32 = new ChecksumHash(Category.CRC, "CRC-32", null) {
      @Override
      protected Checksum newInstance() {
         return new CRC32();
      }
   };

   Hash<String> MD5 = new MessageDigestHash(Category.UNKEYED_CRYPTOGRAPHIC, "MD5", null);
   Hash<String> SHA1 = new MessageDigestHash(Category.UNKEYED_CRYPTOGRAPHIC, "SHA-1", null);
   Hash<String> SHA256 = new MessageDigestHash(Category.UNKEYED_CRYPTOGRAPHIC, "SHA-256", null);
   Hash<String> SHA384 = new MessageDigestHash(Category.UNKEYED_CRYPTOGRAPHIC, "SHA-384", null);
   Hash<String> SHA512 = new MessageDigestHash(Category.UNKEYED_CRYPTOGRAPHIC, "SHA-512", null);

   Category getCategory();

   String getName();

   byte @Nullable [] getSalt();

   T hash(byte[] bytes);

   T hash(InputStream is) throws IOException;

   T hash(Path file) throws IOException;

   T hash(ReadableByteChannel ch) throws IOException;

   T hash(String text);

   /**
    * @return a new not-thread safe hasher instance for incremental hashing
    */
   Hasher<T> newHasher();

   /**
    * @return a new instance configured with the given salt
    */
   Hash<T> withSalt(byte[] salt);
}
