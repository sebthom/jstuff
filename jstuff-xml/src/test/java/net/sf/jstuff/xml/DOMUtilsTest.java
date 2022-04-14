/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.xml;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.util.Map;

import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import net.sf.jstuff.core.collection.Maps;
import net.sf.jstuff.core.collection.Maps.MapDiff;
import net.sf.jstuff.xml.DOMUtils.XPathNode;
import net.sf.jstuff.xml.DOMUtils.XPathNodeConfiguration;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DOMUtilsTest {

   @Test
   public void testCreateCommentBefore() {
      final Element elem = DOMUtils.parseString("<foo id='myid'><bar/></foo>", null).getDocumentElement();
      DOMUtils.createCommentBefore(DOMUtils.findNode(elem, "/foo/bar"), "MY_COMMENT");
      assertThat(DOMUtils.toXML(elem, false, false)).isEqualTo("<foo id=\"myid\"><!--MY_COMMENT--><bar/></foo>");
   }

   @Test
   public void testEvaluate() {
      final Element elem = DOMUtils.parseString("<foo id='myid'><bar name='a'/><bar name='b'/><bar /></foo>", null).getDocumentElement();

      assertThat(DOMUtils.evaluateAsString(elem, "/foo/@id")).isEqualTo("myid");
      assertThat(DOMUtils.evaluateAsString(elem, "count(/foo/bar)")).isEqualTo("3");
      assertThat(DOMUtils.evaluateAsNodes(elem, "/foo/bar/@name")).hasSize(2).hasOnlyElementsOfType(Attr.class);
      assertThat(DOMUtils.evaluateAsNodes(elem, "/foo/bar[@name]")).hasSize(2).hasOnlyElementsOfType(Element.class);
      assertThat(DOMUtils.evaluateAsNodes(elem, "/foo/bar[@name='a']")).hasSize(1);
   }

   @Test
   public void testFindTextContent() {
      final Element elem = DOMUtils.parseString("<foo id='1'>1111<bar name='name'>2222</bar></foo>", null).getDocumentElement();

      assertThat(DOMUtils.findTextContent(elem, "/foo", true)).isEqualTo("11112222");
      assertThat(DOMUtils.findTextContent(elem, "/foo", false)).isEqualTo("1111");
      assertThat(DOMUtils.findTextContent(elem, "/foo/bar", true)).isEqualTo("2222");
      assertThat(DOMUtils.findTextContent(elem, "/foo/dummy", false)).isNull();
      assertThat(DOMUtils.findTextContent(elem, "/foo/dummy", false)).isNull();
   }

   @Test
   public void testGetXPathNodes() throws XMLException {
      final Element elem = DOMUtils.parseString("<foo id='1'><bar name='name'>blabla</bar></foo>", null).getDocumentElement();

      final XPathNodeConfiguration cfg = new XPathNodeConfiguration();
      cfg.recursive = true;
      assertThat(DOMUtils.getXPathNodes(elem, cfg)).hasToString(
         "{/foo/@id=1, /foo/bar/@name=name, /foo/bar/text()=blabla, /foo/text()=null}");

      cfg.recursive = false;
      assertThat(DOMUtils.getXPathNodes(elem, cfg)).hasToString("{/foo/@id=1, /foo/text()=null}");

      cfg.recursive = true;
      cfg.idAttributesByXMLTagName.addAll("*", "id", "name");
      assertThat(DOMUtils.getXPathNodes(elem, cfg)).hasToString(
         "{/foo[@id='1']/@id=1, /foo[@id='1']/bar[@name='name']/@name=name, /foo[@id='1']/bar[@name='name']/text()=blabla, /foo[@id='1']/text()=null}");

      final Element elem1 = DOMUtils.parseString("<top><child name='foo' weight='1'>1234</child></top>", null).getDocumentElement();
      final Element elem2 = DOMUtils.parseString(
         "<top><child name='foo' weight='2'>ABCD</child><child name='bar' weight='2'>ABCD</child></top>", null).getDocumentElement();

      cfg.idAttributesByXMLTagName.put("*", "name");
      final Map<String, XPathNode> attrs1 = DOMUtils.getXPathNodes(elem1, cfg);
      final Map<String, XPathNode> attrs2 = DOMUtils.getXPathNodes(elem2, cfg);

      final MapDiff<String, XPathNode> diff = Maps.diff(attrs1, attrs2);
      assertThat(diff.isDifferent()).isTrue();
      assertThat(diff.leftOnlyEntries).isEmpty();
      assertThat(diff.rightOnlyEntries) //
         .hasSize(3) //
         .containsKey("/top/child[@name='bar']/text()") //
         .containsKey("/top/child[@name='bar']/@name") //
         .containsKey("/top/child[@name='bar']/@weight");
      assertThat(diff.rightOnlyEntries.get("/top/child[@name='bar']/text()").value).isEqualTo("ABCD");
      assertThat(diff.rightOnlyEntries.get("/top/child[@name='bar']/@name").value).isEqualTo("bar");
      assertThat(diff.rightOnlyEntries.get("/top/child[@name='bar']/@weight").value).isEqualTo("2");

      assertThat(diff.entryValueDiffs.get(0).key).isEqualTo("/top/child[@name='foo']/@weight");
      assertThat(diff.entryValueDiffs.get(0).leftValue.value).isEqualTo("1");
      assertThat(diff.entryValueDiffs.get(0).rightValue.value).isEqualTo("2");
      assertThat(diff.entryValueDiffs.get(1).key).isEqualTo("/top/child[@name='foo']/text()");
      assertThat(diff.entryValueDiffs.get(1).leftValue.value).isEqualTo("1234");
      assertThat(diff.entryValueDiffs.get(1).rightValue.value).isEqualTo("ABCD");
   }

   @Test
   @SuppressWarnings("resource")
   public void testParseFile() throws XMLException {
      assertThat(DOMUtils.parseInputSource( //
         new InputSource(DOMUtils.class.getResourceAsStream("wrong-dtd-location.xml")), "wrong-dtd-location.xml", null, (File[]) null //
      )).isNotNull();
   }

   @Test
   public void testSortNodes() throws XMLException {
      {
         final Document doc = DOMUtils.parseString("<foo><bar name='CC'/><bar name='AA'/><bar name='BB'/></foo>", null);
         DOMUtils.sortChildNodesByAttributes(doc.getDocumentElement(), true, "name");
         assertThat(DOMUtils.toXML(doc, false, false)).isEqualTo("<foo><bar name=\"AA\"/><bar name=\"BB\"/><bar name=\"CC\"/></foo>");
      }

      {
         // sort in reverse
         final Document doc = DOMUtils.parseString("<foo><bar name='CC'/><bar name='AA'/><bar name='BB'/></foo>", null);
         DOMUtils.sortChildNodesByAttributes(doc.getDocumentElement(), false, "name");
         assertThat(DOMUtils.toXML(doc, false, false)).isEqualTo("<foo><bar name=\"CC\"/><bar name=\"BB\"/><bar name=\"AA\"/></foo>");
      }

      {
         // numeric sort
         final Document doc = DOMUtils.parseString("<foo><bar name='9080'/><bar name='443'/><bar name='1100'/><bar name='80'/></foo>",
            null);
         DOMUtils.sortChildNodesByAttributes(doc.getDocumentElement(), true, "name");
         assertThat(DOMUtils.toXML(doc, false, false)).isEqualTo(
            "<foo><bar name=\"80\"/><bar name=\"443\"/><bar name=\"1100\"/><bar name=\"9080\"/></foo>");
      }

      {
         // sort on none-existing attribute
         final Document doc = DOMUtils.parseString("<foo><bar name='CC'/><bar name='AA'/><bar name='BB'/></foo>", null);
         DOMUtils.sortChildNodesByAttributes(doc.getDocumentElement(), true, "value");
         assertThat(DOMUtils.toXML(doc, false, false)).isEqualTo("<foo><bar name=\"CC\"/><bar name=\"AA\"/><bar name=\"BB\"/></foo>");
      }

      {
         // sort while omitting attribute name
         final Document doc = DOMUtils.parseString("<foo><bar name='CC'/><bar name='AA'/><bar name='BB'/></foo>", null);
         DOMUtils.sortChildNodesByAttributes(doc.getDocumentElement(), true);
         assertThat(DOMUtils.toXML(doc, false, false)).isEqualTo("<foo><bar name=\"CC\"/><bar name=\"AA\"/><bar name=\"BB\"/></foo>");
      }
      {
         // sort on two attribute
         final Document doc = DOMUtils.parseString(
            "<foo><bar name='bb' value='BB'/><bar name='bb' value='AA'/><bar name='aa' value='AA'/></foo>", null);
         DOMUtils.sortChildNodesByAttributes(doc.getDocumentElement(), true, "name", "value");
         assertThat(DOMUtils.toXML(doc, false, false)) //
            .isEqualTo("<foo><bar name=\"aa\" value=\"AA\"/><bar name=\"bb\" value=\"AA\"/><bar name=\"bb\" value=\"BB\"/></foo>");
      }
   }

   @Test
   public void testToXML() throws XMLException {
      final Document doc = DOMUtils.parseString("<foo id='1'><bar name='name'>blabla</bar></foo>", null);
      assertThat(DOMUtils.toXML(doc.getFirstChild().getFirstChild(), false, false)).isEqualTo("<bar name=\"name\">blabla</bar>");
   }
}
