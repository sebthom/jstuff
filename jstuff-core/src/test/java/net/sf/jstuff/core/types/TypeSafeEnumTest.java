/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.types;

import junit.framework.TestCase;
import net.sf.jstuff.core.io.SerializationUtils;
import net.sf.jstuff.core.types.TypeSafeEnum;

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
        assertTrue(MyEnum.getItems(MyEnum.class).get(1) == MyEnum.ITEM2);
        assertTrue(MyEnum.getItems(MyEnum.class).get(0) == MyEnum.ITEM1);
    }

    public void testSerialization() {
        assertFalse(MyEnum.ITEM1.getOrdinal() == MyEnum.ITEM2.getOrdinal());
        final MyEnum deserializedItem = (MyEnum) SerializationUtils.deserialize(SerializationUtils.serialize(MyEnum.ITEM1));
        assertSame(deserializedItem, MyEnum.ITEM1);
    }
}
