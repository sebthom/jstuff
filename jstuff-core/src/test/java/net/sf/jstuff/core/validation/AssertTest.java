/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
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
package net.sf.jstuff.core.validation;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class AssertTest extends TestCase {
    public void testArgumentNotEmpty() {
        try {
            Args.notEmpty("password", (String) null);
            fail();
        } catch (final IllegalArgumentException ex) {
            assertEquals("[password] must not be null", ex.getMessage());
        }

        try {
            Args.notEmpty("password", "");
            fail();
        } catch (final IllegalArgumentException ex) {
            assertEquals("[password] must not be empty", ex.getMessage());
        }

        Args.notEmpty("password", "secret");

        try {
            Args.notEmpty("values", (String[]) null);
            fail();
        } catch (final IllegalArgumentException ex) {
            assertEquals("[values] must not be null", ex.getMessage());
        }

        try {
            Args.notEmpty("values", new String[0]);
            fail();
        } catch (final IllegalArgumentException ex) {
            assertEquals("[values] must not be empty", ex.getMessage());
        }

        Args.notEmpty("values", new String[] { "dfd" });

    }

    public void testArgumentNotNull() {
        try {
            Args.notNull("password", null);
            fail();
        } catch (final IllegalArgumentException ex) {
            assertEquals("[password] must not be null", ex.getMessage());
        }

        Args.notNull("password", "");
        Args.notNull("password", "secret");
    }

    public void testIsFalse() {
        try {
            Assert.isFalse(true, "foo");
            fail();
        } catch (final IllegalStateException ex) {
            assertEquals("foo", ex.getMessage());
        }

        Assert.isFalse(false, "foo");
    }

    public void testIsReadableFile() throws IOException {
        try {
            Assert.isFileReadable(new File("foo"));
            fail();
        } catch (final IllegalStateException ex) {
            assertTrue(ex.getMessage().contains("does not exist"));
        }

        try {
            Assert.isFileReadable(File.createTempFile("foo", "bar").getParentFile());
            fail();
        } catch (final IllegalStateException ex) {
            assertTrue(ex.getMessage().contains("is not a file"));
        }
    }

    public void testIsTrue() {
        try {
            Assert.isTrue(false, "foo");
            fail();
        } catch (final IllegalStateException ex) {
            assertEquals("foo", ex.getMessage());
        }

        Assert.isTrue(true, "foo");
    }

    public void testNotEmpty() {
        try {
            Assert.notEmpty("", "foo");
            fail();
        } catch (final IllegalStateException ex) {
            assertEquals("foo", ex.getMessage());
        }

        try {
            Assert.notEmpty((String) null, "foo");
            fail();
        } catch (final IllegalStateException ex) {
            assertEquals("foo", ex.getMessage());
        }

        Assert.notEmpty("value", "foo");

        try {
            Assert.notEmpty(new String[0], "foo");
            fail();
        } catch (final IllegalStateException ex) {
            assertEquals("foo", ex.getMessage());
        }

        try {
            Assert.notEmpty((String[]) null, "foo");
            fail();
        } catch (final IllegalStateException ex) {
            assertEquals("foo", ex.getMessage());
        }

        Assert.notEmpty(new String[] { "value" }, "foo");
    }

    public void testNotNull() {
        try {
            Assert.notNull(null, "foo");
            fail();
        } catch (final IllegalStateException ex) {
            assertEquals("foo", ex.getMessage());
        }

        Assert.notNull("value", "foo");
    }
}
