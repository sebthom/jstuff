/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.xml;

import java.io.File;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import junit.framework.TestCase;
import net.sf.jstuff.core.collection.Maps;
import net.sf.jstuff.core.collection.Maps.MapDiff;
import net.sf.jstuff.xml.DOMUtils.XPathNode;
import net.sf.jstuff.xml.DOMUtils.XPathNodeConfiguration;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DOMUtilsTests extends TestCase {

   public void testCreateCommentBefore() {
      final Element elem = DOMUtils.parseString("<foo id='myid'><bar/></foo>", null).getDocumentElement();
      DOMUtils.createCommentBefore(DOMUtils.findNode(elem, "/foo/bar"), "MY_COMMENT");
      assertEquals("<foo id=\"myid\"><!--MY_COMMENT--><bar/></foo>", DOMUtils.toXML(elem, false, false));
   }

   public void testEvaluate() {
      final Element elem = DOMUtils.parseString("<foo id='myid'><bar/><bar/><bar/></foo>", null).getDocumentElement();

      assertEquals("myid", DOMUtils.evaluate(elem, "/foo/@id"));
      assertEquals("3", DOMUtils.evaluate(elem, "count(/foo/bar)"));
   }

   public void testFindTextContent() {
      final Element elem = DOMUtils.parseString("<foo id='1'>1111<bar name='name'>2222</bar></foo>", null).getDocumentElement();

      assertEquals("11112222", DOMUtils.findTextContent(elem, "/foo", true));
      assertEquals("1111", DOMUtils.findTextContent(elem, "/foo", false));
      assertEquals("2222", DOMUtils.findTextContent(elem, "/foo/bar", true));
      assertEquals(null, DOMUtils.findTextContent(elem, "/foo/dummy", false));
      assertEquals(null, DOMUtils.findTextContent(elem, "/foo/dummy", false));
   }

   public void testGetXPathNodes() throws XMLException {
      final Element elem = DOMUtils.parseString("<foo id='1'><bar name='name'>blabla</bar></foo>", null).getDocumentElement();

      final XPathNodeConfiguration cfg = new XPathNodeConfiguration();
      cfg.recursive = true;
      assertEquals("{/foo/@id=1, /foo/bar/@name=name, /foo/bar/text()=blabla, /foo/text()=null}", DOMUtils.getXPathNodes(elem, cfg).toString());

      cfg.recursive = false;
      assertEquals("{/foo/@id=1, /foo/text()=null}", DOMUtils.getXPathNodes(elem, cfg).toString());

      cfg.recursive = true;
      cfg.idAttributesByXMLTagName.addAll("*", "id", "name");
      assertEquals(
         "{/foo[@id='1']/@id=1, /foo[@id='1']/bar[@name='name']/@name=name, /foo[@id='1']/bar[@name='name']/text()=blabla, /foo[@id='1']/text()=null}", DOMUtils
            .getXPathNodes(elem, cfg).toString());

      final Element elem1 = DOMUtils.parseString("<top><child name='foo' weight='1'>1234</child></top>", null).getDocumentElement();
      final Element elem2 = DOMUtils.parseString("<top><child name='foo' weight='2'>ABCD</child><child name='bar' weight='2'>ABCD</child></top>", null)
         .getDocumentElement();

      cfg.idAttributesByXMLTagName.put("*", "name");
      final Map<String, XPathNode> attrs1 = DOMUtils.getXPathNodes(elem1, cfg);
      final Map<String, XPathNode> attrs2 = DOMUtils.getXPathNodes(elem2, cfg);

      final MapDiff<String, XPathNode> diff = Maps.diff(attrs1, attrs2);
      assertTrue(diff.isDifferent());
      assertEquals(0, diff.leftOnlyEntries.size());
      assertEquals(3, diff.rightOnlyEntries.size());
      assertTrue(diff.rightOnlyEntries.containsKey("/top/child[@name='bar']/text()"));
      assertTrue(diff.rightOnlyEntries.containsKey("/top/child[@name='bar']/@name"));
      assertTrue(diff.rightOnlyEntries.containsKey("/top/child[@name='bar']/@weight"));
      assertEquals(diff.rightOnlyEntries.get("/top/child[@name='bar']/text()").value, "ABCD");
      assertEquals(diff.rightOnlyEntries.get("/top/child[@name='bar']/@name").value, "bar");
      assertEquals(diff.rightOnlyEntries.get("/top/child[@name='bar']/@weight").value, "2");

      assertEquals(diff.entryValueDiffs.get(0).key, "/top/child[@name='foo']/@weight");
      assertEquals(diff.entryValueDiffs.get(0).leftValue.value, "1");
      assertEquals(diff.entryValueDiffs.get(0).rightValue.value, "2");
      assertEquals(diff.entryValueDiffs.get(1).key, "/top/child[@name='foo']/text()");
      assertEquals(diff.entryValueDiffs.get(1).leftValue.value, "1234");
      assertEquals(diff.entryValueDiffs.get(1).rightValue.value, "ABCD");
   }

   public void testParseFile() throws XMLException {
      DOMUtils.parseInputSource(new InputSource(DOMUtils.class.getResourceAsStream("wrong-dtd-location.xml")), "wrong-dtd-location.xml", null, (File[]) null);
   }

   public void testSortNodes() throws XMLException {

      {
         final Document doc = DOMUtils.parseString("<foo><bar name='CC'/><bar name='AA'/><bar name='BB'/></foo>", null);
         DOMUtils.sortChildNodesByAttributes(doc.getDocumentElement(), true, "name");
         assertEquals("<foo><bar name=\"AA\"/><bar name=\"BB\"/><bar name=\"CC\"/></foo>", DOMUtils.toXML(doc, false, false));
      }

      {
         // sort in reverse
         final Document doc = DOMUtils.parseString("<foo><bar name='CC'/><bar name='AA'/><bar name='BB'/></foo>", null);
         DOMUtils.sortChildNodesByAttributes(doc.getDocumentElement(), false, "name");
         assertEquals("<foo><bar name=\"CC\"/><bar name=\"BB\"/><bar name=\"AA\"/></foo>", DOMUtils.toXML(doc, false, false));
      }

      {
         // numeric sort
         final Document doc = DOMUtils.parseString("<foo><bar name='9080'/><bar name='443'/><bar name='1100'/><bar name='80'/></foo>", null);
         DOMUtils.sortChildNodesByAttributes(doc.getDocumentElement(), true, "name");
         assertEquals("<foo><bar name=\"80\"/><bar name=\"443\"/><bar name=\"1100\"/><bar name=\"9080\"/></foo>", DOMUtils.toXML(doc, false, false));
      }

      {
         // sort on none-existing attribute
         final Document doc = DOMUtils.parseString("<foo><bar name='CC'/><bar name='AA'/><bar name='BB'/></foo>", null);
         DOMUtils.sortChildNodesByAttributes(doc.getDocumentElement(), true, "value");
         assertEquals("<foo><bar name=\"CC\"/><bar name=\"AA\"/><bar name=\"BB\"/></foo>", DOMUtils.toXML(doc, false, false));
      }

      {
         // sort while omitting attribute name
         final Document doc = DOMUtils.parseString("<foo><bar name='CC'/><bar name='AA'/><bar name='BB'/></foo>", null);
         DOMUtils.sortChildNodesByAttributes(doc.getDocumentElement(), true);
         assertEquals("<foo><bar name=\"CC\"/><bar name=\"AA\"/><bar name=\"BB\"/></foo>", DOMUtils.toXML(doc, false, false));
      }

      {
         // sort on two attribute
         final Document doc = DOMUtils.parseString("<foo><bar name='bb' value='BB'/><bar name='bb' value='AA'/><bar name='aa' value='AA'/></foo>", null);
         DOMUtils.sortChildNodesByAttributes(doc.getDocumentElement(), true, "name", "value");
         assertEquals("<foo><bar name=\"aa\" value=\"AA\"/><bar name=\"bb\" value=\"AA\"/><bar name=\"bb\" value=\"BB\"/></foo>", DOMUtils.toXML(doc, false,
            false));
      }

   }

   public void testToXML() throws XMLException {
      final Document doc = DOMUtils.parseString("<foo id='1'><bar name='name'>blabla</bar></foo>", null);
      assertEquals("<bar name=\"name\">blabla</bar>", DOMUtils.toXML(doc.getFirstChild().getFirstChild(), false, false));
   }
}
