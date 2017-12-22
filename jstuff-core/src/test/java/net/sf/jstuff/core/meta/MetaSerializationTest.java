/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.meta;

import java.io.Serializable;

import junit.framework.TestCase;
import net.sf.jstuff.core.collection.Maps;
import net.sf.jstuff.core.io.SerializationUtils;
import net.sf.jstuff.core.jbean.meta.ClassDescriptor;
import net.sf.jstuff.core.jbean.meta.PropertyDescriptor;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class MetaSerializationTest extends TestCase {
    public static class Entity<SELF_TYPE extends Entity<SELF_TYPE>> {
        private String comment;

        public static final ClassDescriptor<EntityMeta> META_CLASS = EntityMeta.META_CLASS;

        @SuppressWarnings("unchecked")
        public <T> T _get(final PropertyDescriptor<T> property) {
            Args.notNull("property", property);

            if (property.equals(EntityMeta.PROP_comment))
                return (T) getComment();
            throw new UnsupportedOperationException();
        }

        public ClassDescriptor<EntityMeta> _getMetaClass() {
            return EntityMeta.META_CLASS;
        }

        public <T> SELF_TYPE _set(final PropertyDescriptor<T> property, final T value) {
            Args.notNull("property", property);

            if (property.equals(EntityMeta.PROP_comment)) {
                setComment((String) value);
            }
            throw new UnsupportedOperationException();
        }

        public String getComment() {
            return comment;
        }

        public void setComment(final String comment) {
            this.comment = comment;
        }
    }

    public static class EntityMeta {
        public static final ClassDescriptor<EntityMeta> META_CLASS = ClassDescriptor.of(EntityMeta.class, "Entity", null, null);

        public static final PropertyDescriptor<String> PROP_comment = PropertyDescriptor.create(META_CLASS, //
            "comment", String.class, 0, 1, false, false, true, //
            "", Maps.newHashMap( //
                "descr", (Serializable) "this entity's comment" //
            ));
    }

    public void testSerialization() {
        assertNotNull(Entity.META_CLASS);
        assertSame(Entity.META_CLASS, SerializationUtils.clone(Entity.META_CLASS));
        assertSame(Entity.META_CLASS, SerializationUtils.clone(EntityMeta.PROP_comment.getMetaClass()));
        assertSame(EntityMeta.PROP_comment, SerializationUtils.clone(EntityMeta.PROP_comment));
    }
}
