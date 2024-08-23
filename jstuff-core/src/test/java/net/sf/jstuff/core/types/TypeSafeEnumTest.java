/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.types;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import net.sf.jstuff.core.io.SerializationUtils;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class TypeSafeEnumTest {

   public static final class MyEnum extends TypeSafeEnum<String> {
      private static final long serialVersionUID = 1L;

      public static final MyEnum ITEM1 = new MyEnum("ITEM1");
      public static final MyEnum ITEM2 = new MyEnum("ITEM2");

      private MyEnum(final String name) {
         super(name);
      }
   }

   @Test
   public void testItemOrder() {
      assertThat(TypeSafeEnum.getEnums(MyEnum.class)).hasSize(2);
      assertThat(TypeSafeEnum.getEnums(MyEnum.class).get(0)).isSameAs(MyEnum.ITEM1);
      assertThat(TypeSafeEnum.getEnums(MyEnum.class).get(1)).isSameAs(MyEnum.ITEM2);
   }

   @Test
   public void testSerialization() {
      assertThat(MyEnum.ITEM1.ordinal).isNotEqualTo(MyEnum.ITEM2.ordinal);
      final MyEnum deserializedItem = (MyEnum) SerializationUtils.deserialize(SerializationUtils.serialize(MyEnum.ITEM1));
      assertThat(deserializedItem).isSameAs(MyEnum.ITEM1);
   }
}
