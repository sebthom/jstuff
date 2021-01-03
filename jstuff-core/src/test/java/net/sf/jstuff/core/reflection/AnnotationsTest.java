/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import net.sf.jstuff.core.collection.Maps;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class AnnotationsTest {

   @Test
   public void testCreate() {
      final Map<String, Object> m1 = Maps.newHashMap("value", "hello!");
      final Map<String, Object> m2 = Maps.newHashMap("value", "hi!");

      final Ignore a1a = Annotations.create(Ignore.class, m1);
      final Ignore a1b = Annotations.create(Ignore.class, m1);
      final Ignore a2 = Annotations.create(Ignore.class, m2);

      assertThat(a1b) //
         .isEqualTo(a1a) //
         .isNotEqualTo(a2);
      assertThat(a1a).hasToString("@org.junit.Ignore(value=hello!)");
      assertThat(a2).hasToString("@org.junit.Ignore(value=hi!)");
   }
}
