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
package net.sf.jstuff.core.functional;

import static net.sf.jstuff.core.functional.Accepts.*;

import junit.framework.TestCase;
import net.sf.jstuff.core.functional.Accepts.Contains;
import net.sf.jstuff.core.functional.Accepts.EndingWith;
import net.sf.jstuff.core.functional.Accepts.Property;
import net.sf.jstuff.core.functional.Accepts.StartingWith;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class AcceptsTest extends TestCase {
   public void testAcceptChaining() {
      {
         final Accept<String> a = startingWith("#").and(endingWith("#"));
         assertFalse(a.accept(null));
         assertFalse(a.accept("#foo"));
         assertFalse(a.accept("foo#"));
         assertTrue(a.accept("#foo#"));
      }

      {
         final Accept<String> a = startingWith("#").or(endingWith("#"));
         assertFalse(a.accept(null));
         assertTrue(a.accept("#foo"));
         assertTrue(a.accept("foo#"));
         assertTrue(a.accept("#foo#"));
      }

      {
         final Accept<Integer> a = notNull().and(greaterThan(10)).and(lessThan(20));
         assertFalse(a.accept(null));
         assertFalse(a.accept(2));
         assertTrue(a.accept(19));
         assertFalse(a.accept(21));
      }

      {
         final Accept<Long> a = notNull(). //
            and( //
               greaterThan(20L).or(lessThan(10L)).or(equalTo(12L)) //
         );
         assertFalse(a.accept(null));
         assertTrue(a.accept(21L));
         assertFalse(a.accept(19L));
         assertFalse(a.accept(11L));
         assertTrue(a.accept(12L));
         assertTrue(a.accept(9L));
      }
   }

   public void testContains() {
      final Contains<String> a = contains("oo");
      assertFalse(a.accept(null));
      assertFalse(a.accept(""));
      assertTrue(a.accept("foo"));

      final Accept<String> a2 = not(a);
      assertTrue(a2.accept(null));
      assertTrue(a2.accept(""));
      assertFalse(a2.accept("foo"));

      final Accept<String> a3 = a.ignoreCase();
      assertFalse(a3.accept(null));
      assertFalse(a3.accept(""));
      assertTrue(a3.accept("FOO"));
   }

   public void testEndingWith() {
      final EndingWith<String> a = endingWith("o#");
      assertFalse(a.accept(null));
      assertFalse(a.accept("#foo"));
      assertFalse(a.accept("foo"));
      assertTrue(a.accept("foo#"));

      final Accept<String> a2 = not(a);
      assertTrue(a2.accept(null));
      assertTrue(a2.accept("#foo"));
      assertTrue(a2.accept("foo"));
      assertFalse(a2.accept("foo#"));

      final Accept<String> a3 = a.ignoreCase();
      assertFalse(a3.accept(null));
      assertFalse(a3.accept("#FOO"));
      assertFalse(a3.accept("FOO"));
      assertTrue(a3.accept("FOO#"));
   }

   public void testEqualTo() {
      final Accept<String> a = equalTo("123");
      assertFalse(a.accept(null));
      assertFalse(a.accept("1234"));
      assertTrue(a.accept("123"));

      final Accept<String> a2 = not(a);
      assertTrue(a2.accept(null));
      assertTrue(a2.accept("1234"));
      assertFalse(a2.accept("123"));
   }

   public void testGreaterThan() {
      final Accept<Integer> a = greaterThan(10);
      assertFalse(a.accept(null));
      assertFalse(a.accept(9));
      assertFalse(a.accept(10));
      assertTrue(a.accept(11));

      final Accept<Integer> a2 = not(a);
      assertTrue(a2.accept(null));
      assertTrue(a2.accept(9));
      assertTrue(a2.accept(10));
      assertFalse(a2.accept(11));
   }

   public void testLessThan() {
      final Accept<Integer> a = lessThan(10);
      assertFalse(a.accept(null));
      assertTrue(a.accept(9));
      assertFalse(a.accept(10));
      assertFalse(a.accept(11));

      final Accept<Integer> a2 = not(a);
      assertTrue(a2.accept(null));
      assertFalse(a2.accept(9));
      assertTrue(a2.accept(10));
      assertTrue(a2.accept(11));
   }

   public void testNonNull() {
      final Accept<Object> a = notNull();
      assertFalse(a.accept(null));
      assertTrue(a.accept(""));

      final Accept<Object> a2 = not(a);
      assertTrue(a2.accept(null));
      assertFalse(a2.accept(""));
   }

   public void testNull() {
      final Accept<Object> a = isNull();
      assertTrue(a.accept(null));
      assertFalse(a.accept(""));

      final Accept<Object> a2 = not(a);
      assertFalse(a2.accept(null));
      assertTrue(a2.accept(""));
   }

   public void testProperty() {
      class Entity {
         @SuppressWarnings("unused")
         String name;
         Entity parent;
      }

      final Property<Entity, String> a = property("name", equalTo("foobar"));

      final Entity e = new Entity();

      e.name = "foobar";
      assertTrue(a.accept(e));

      e.name = "blub";
      assertFalse(a.accept(e));

      final Property<Entity, String> a2 = property("parent.name", equalTo("foobar"));

      e.parent = new Entity();

      e.parent.name = "blub";
      assertFalse(a2.accept(e));

      e.parent.name = "foobar";
      assertTrue(a2.accept(e));

      e.parent = null;
      assertFalse(a2.accept(e));
   }

   public void testStartingWith() {
      final StartingWith<String> a = startingWith("#f");
      assertFalse(a.accept(null));
      assertTrue(a.accept("#foo"));
      assertFalse(a.accept("foo"));
      assertFalse(a.accept("foo#"));

      final Accept<String> a2 = not(a);
      assertTrue(a2.accept(null));
      assertFalse(a2.accept("#foo"));
      assertTrue(a2.accept("foo"));
      assertTrue(a2.accept("foo#"));

      final Accept<String> a3 = a.ignoreCase();
      assertFalse(a3.accept(null));
      assertTrue(a3.accept("#FOO"));
      assertFalse(a3.accept("FOO"));
      assertFalse(a3.accept("FOO#"));
   }
}
