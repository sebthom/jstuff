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
    @Override
    @SuppressWarnings("rawtypes")
    public boolean canConvert(final Class type) {
        return type.equals(AtomText.class);
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final AtomText t = (AtomText) source;
        writer.addAttribute("type", t.getType());
        writer.setValue(t.getContent());
    }

    @Override
    public AtomText unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final AtomText t = new AtomText();
        t.setType(reader.getAttribute("type"));
        t.setContent(reader.getValue());
        return t;
    }
}