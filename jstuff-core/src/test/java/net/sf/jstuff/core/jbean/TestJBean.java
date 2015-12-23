/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
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
package net.sf.jstuff.core.jbean;

import static net.sf.jstuff.core.collection.CollectionUtils.*;
import junit.framework.TestCase;
import net.sf.jstuff.core.jbean.changelog.PropertyChangelog;
import net.sf.jstuff.core.jbean.changelog.UndoMarker;
import net.sf.jstuff.core.jbean.meta.ClassDescriptor;
import net.sf.jstuff.core.jbean.meta.PropertyDescriptor;
import net.sf.jstuff.core.validation.Args;

import org.apache.commons.lang3.ObjectUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class TestJBean extends TestCase {
    private static final class MyEntity extends AbstractJBean {
        private static final long serialVersionUID = 1L;

        public static final ClassDescriptor<MyEntity> META = ClassDescriptor.of(MyEntity.class, "MyEntity", "", null);

        public static final PropertyDescriptor<String> PROP_comment = PropertyDescriptor.create(META, //
            "comment", String.class, 0, 1, false, false, true, //
            "the entity's comment", newHashMap( //
                "length", 64, //
                "min-length", 0 //
        ));

        private String comment;

        @Override
        @SuppressWarnings("unchecked")
        public <T> T _get(final PropertyDescriptor<T> property) {
            Args.notNull("property", property);
            if (property == PROP_comment)
                return (T) getComment();
            return super._get(property);
        }

        @Override
        public ClassDescriptor<?> _getMeta() {
            return META;
        }

        @Override
        public <T> MyEntity _set(final PropertyDescriptor<T> property, final T value) {
            Args.notNull("property", property);
            if (property == PROP_comment)
                setComment((String) value);
            else
                super._set(property, value);
            return this;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String newValue) {
            newValue = newValue == null ? null : newValue.trim();
            final String oldValue = getComment();
            if (!ObjectUtils.equals(oldValue, newValue)) {
                comment = newValue;
                onValueSet(PROP_comment, oldValue, newValue);
            }
        }
    }

    public void testJBean() {
        final PropertyChangelog changeLog = new PropertyChangelog();

        final MyEntity entity = new MyEntity();
        entity._subscribe(changeLog);

        assertFalse(changeLog.isDirty(entity));

        entity.setComment("STEP1");

        assertTrue(changeLog.isDirty(entity));

        final UndoMarker marker = changeLog.undoMarker();

        entity.setComment("STEP2");
        assertEquals("STEP2", entity.getComment());

        changeLog.undo(marker);
        assertEquals("STEP1", entity.getComment());
        assertTrue(changeLog.isDirty(entity));

        changeLog.undo();
        assertFalse(changeLog.isDirty(entity));
    }
}
