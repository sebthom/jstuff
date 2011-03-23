/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
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
package net.sf.jstuff.xml;

import java.io.IOException;
import java.util.Map;

import junit.framework.TestCase;
import net.sf.jstuff.core.collection.CollectionUtils;

import org.w3c.dom.Element;

public class DOMUtilsTests extends TestCase
{
	public void testGetAttributesXPathAndValue() throws XMLException, IOException
	{
		final Element elem = DOMUtils.parseString("<foo id='1'><bar name='name'>blabla</bar></foo>", null)
				.getDocumentElement();

		assertEquals("{/foo/@id=1, /foo/bar/@name=name, /foo/bar/text()=blabla}",
				DOMUtils.getAttributesXPathAndValue(elem, true).toString());

		assertEquals("{/foo/@id=1}", DOMUtils.getAttributesXPathAndValue(elem, false).toString());

		assertEquals(
				"{/foo[@id='1']/@id=1, /foo[@id='1']/bar[@name='name']/@name=name, /foo[@id='1']/bar[@name='name']/text()=blabla}",
				DOMUtils.getAttributesXPathAndValue(elem, true, CollectionUtils.newArrayList("id", "name")).toString());

		final Element elem1 = DOMUtils.parseString("<top><child name='foo' weight='1'>1234</child></top>", null)
				.getDocumentElement();
		final Element elem2 = DOMUtils.parseString(
				"<top><child name='foo' weight='2'>ABCD</child><child name='bar' weight='2'>ABCD</child></top>", null)
				.getDocumentElement();

		final Map<String, String> attrs1 = DOMUtils.getAttributesXPathAndValue(elem1, true,
				CollectionUtils.newArrayList("name"));
		final Map<String, String> attrs2 = DOMUtils.getAttributesXPathAndValue(elem2, true,
				CollectionUtils.newArrayList("name"));

		assertEquals(
				"MapDiffMapDiff [entryValueDiffs=[EntryValueDiff [key=/top/child[@name='foo']/@weight, leftValue=1, rightValue=2], EntryValueDiff [key=/top/child[@name='foo']/text(), leftValue=1234, rightValue=ABCD]], leftOnlyEntries={}, rightOnlyEntries={/top/child[@name='bar']/@name=bar, /top/child[@name='bar']/text()=ABCD, /top/child[@name='bar']/@weight=2}]",
				CollectionUtils.diff(attrs1, attrs2).toString());
	}
}
