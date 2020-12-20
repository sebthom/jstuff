/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class FieldsTest {

   static class Entity {
      final String name;

      Entity(final String name) {
         this.name = name;
      }
   }

   @Test
   public void testWriteIgnoringFinal() {
      final Entity e = new Entity("foo");
      assertThat(e.name).isEqualTo("foo");

      Fields.writeIgnoringFinal(e, "name", "bar");
      assertThat(e.name).isEqualTo("bar");
   }
}
