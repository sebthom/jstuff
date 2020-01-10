/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.types;

import static org.junit.Assert.*;

import junit.framework.TestCase;
import net.sf.jstuff.core.io.SerializationUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class TypeSafeEnumTest extends TestCase {
   public static final class MyEnum extends TypeSafeEnum<String> {
      private static final long serialVersionUID = 1L;

      public static final MyEnum ITEM1 = new MyEnum("ITEM1");
      public static final MyEnum ITEM2 = new MyEnum("ITEM2");

      private MyEnum(final String name) {
         super(name);
      }
   }

   public void testItemOrder() {
      assertEquals(2, TypeSafeEnum.getEnums(MyEnum.class).size());
      assertSame(MyEnum.ITEM1, TypeSafeEnum.getEnums(MyEnum.class).get(0));
      assertSame(MyEnum.ITEM2, TypeSafeEnum.getEnums(MyEnum.class).get(1));

   }

   public void testSerialization() {
      assertNotEquals(MyEnum.ITEM1.ordinal, MyEnum.ITEM2.ordinal);
      final MyEnum deserializedItem = (MyEnum) SerializationUtils.deserialize(SerializationUtils.serialize(MyEnum.ITEM1));
      assertSame(deserializedItem, MyEnum.ITEM1);
   }
}
