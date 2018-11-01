/*********************************************************************
 * Copyright 2010-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.atom.feed;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class AtomTextConverter implements Converter {
   public boolean canConvert(final Class type) {
      return type.equals(AtomText.class);
   }

   public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
      final AtomText t = (AtomText) source;
      writer.addAttribute("type", t.getType());
      writer.setValue(t.getContent());
   }

   public AtomText unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
      final AtomText t = new AtomText();
      t.setType(reader.getAttribute("type"));
      t.setContent(reader.getValue());
      return t;
   }
}
