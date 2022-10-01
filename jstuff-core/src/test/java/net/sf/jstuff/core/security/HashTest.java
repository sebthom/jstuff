/*
 * Copyright 2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class HashTest {

   @Test
   public void testHashCategories() {
      assertThat(Hash.ADLER32.getCategory()).isEqualTo(Hash.Category.CHECKSUM);
      assertThat(Hash.CRC32.getCategory()).isEqualTo(Hash.Category.CRC);
      assertThat(Hash.MD5.getCategory()).isEqualTo(Hash.Category.UNKEYED_CRYPTOGRAPHIC);
      assertThat(Hash.SHA1.getCategory()).isEqualTo(Hash.Category.UNKEYED_CRYPTOGRAPHIC);
      assertThat(Hash.SHA256.getCategory()).isEqualTo(Hash.Category.UNKEYED_CRYPTOGRAPHIC);
      assertThat(Hash.SHA384.getCategory()).isEqualTo(Hash.Category.UNKEYED_CRYPTOGRAPHIC);
      assertThat(Hash.SHA512.getCategory()).isEqualTo(Hash.Category.UNKEYED_CRYPTOGRAPHIC);
   }

   @Test
   public void testHash() {
      assertThat(Hash.ADLER32.hash("HelloWorld!")).isEqualTo(427_033_630L);
      assertThat(Hash.ADLER32.withSalt("foo".getBytes()).hash("HelloWorld!")).isEqualTo(702_678_370L);

      assertThat(Hash.CRC32.hash("HelloWorld!")).isEqualTo(3_083_157_831L);
      assertThat(Hash.CRC32.withSalt("foo".getBytes()).hash("HelloWorld!")).isEqualTo(178_500_065L);

      assertThat(Hash.MD5.hash("HelloWorld!")).isEqualTo("06e0e6637d27b2622ab52022db713ce2");
      assertThat(Hash.MD5.withSalt("foo".getBytes()).hash("HelloWorld!")).isEqualTo("6e368f642a96f22e6f33e7b6c57ea204");
      assertThat(Hash.MD5.hash("jk8ssl")).isEqualTo("0000000018e6137ac2caab16074784a6"); // test for leading zeros

      assertThat(Hash.SHA1.hash("HelloWorld!")).isEqualTo("d735871a64133ee062400659cf91b8234d1c1930");
      assertThat(Hash.SHA1.withSalt("foo".getBytes()).hash("HelloWorld!")).isEqualTo("776af7b601a002d2904ee4df37ed105ba61bb000");

      assertThat(Hash.SHA256.hash("HelloWorld!")).isEqualTo("729e344a01e52c822bdfdec61e28d6eda02658d2e7d2b80a9b9029f41e212dde");
      assertThat(Hash.SHA256.withSalt("foo".getBytes()).hash("HelloWorld!")).isEqualTo(
         "a334d87963a9bac92b1d7136db8679704ef336dd78f4bf20ec01bdd90fa844bf");

      assertThat(Hash.SHA384.hash("HelloWorld!")).isEqualTo(
         "03751ecfd90d6de2e28043baa0c6915eb723e89411b6c1bab006be7386e1459263b4eac6b023f5c7a2839eda96a74e53");
      assertThat(Hash.SHA384.withSalt("foo".getBytes()).hash("HelloWorld!")).isEqualTo(
         "561a7319dbd54a7689784d75d5b5062b19c8903410d5e97fb0cf38f92f9c7e2477746dd724966719a0cb26fbe97c102f");

      assertThat(Hash.SHA512.hash("HelloWorld!")).isEqualTo(
         "c5179301e5619c392fb9b8872b90156ea45e452ea7336d9ee727d5d4a2a95df6a973ddfdeb92bbc14e01aa923591d44835ac32ccd1a71f4681f6a731fae5c238");
      assertThat(Hash.SHA512.withSalt("foo".getBytes()).hash("HelloWorld!")).isEqualTo(
         "e4f870274af66ccb3a2e1e8f3c770e89265a4fffba594de2f9c0160036a0430a037dada3f2b045bef1f10b0c66a468ab409b41679490c98a5529ab5700a33245");
   }

   @Test
   public void testHasher() {
      assertThat(Hash.ADLER32.newHasher().update("HelloWorld!".getBytes()).hash()).isEqualTo(427_033_630L);
      assertThat(Hash.ADLER32.withSalt("foo".getBytes()).newHasher().update("HelloWorld!".getBytes()).hash()).isEqualTo(702_678_370L);
      assertThat(Hash.ADLER32.withSalt("foo".getBytes()).newHasher().update("Whatever".getBytes()).reset().update("HelloWorld!".getBytes())
         .hash()).isEqualTo(702_678_370L);

      assertThat(Hash.CRC32.newHasher().update("HelloWorld!".getBytes()).hash()).isEqualTo(3_083_157_831L);
      assertThat(Hash.CRC32.withSalt("foo".getBytes()).newHasher().update("HelloWorld!".getBytes()).hash()).isEqualTo(178_500_065L);
      assertThat(Hash.CRC32.withSalt("foo".getBytes()).newHasher().update("Whatever".getBytes()).reset().update("HelloWorld!".getBytes())
         .hash()).isEqualTo(178_500_065L);

      assertThat(Hash.MD5.newHasher().update("HelloWorld!".getBytes()).hash()).isEqualTo("06e0e6637d27b2622ab52022db713ce2");
      assertThat(Hash.MD5.withSalt("foo".getBytes()).newHasher().update("HelloWorld!".getBytes()).hash()).isEqualTo(
         "6e368f642a96f22e6f33e7b6c57ea204");
      assertThat(Hash.SHA1.newHasher().update("HelloWorld!".getBytes()).hash()).isEqualTo("d735871a64133ee062400659cf91b8234d1c1930");

   }
}
