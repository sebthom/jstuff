/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.xml.stream;

import java.io.InputStream;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;

import junit.framework.TestCase;
import net.sf.jstuff.xml.stream.StAXUtils.ElementInfo;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class StAXUtilsTest extends TestCase {

   private static final StAXFactory STAX_FACTORY = new StAXFactory();

   static {
      for (final Handler handler : Logger.getLogger("").getHandlers()) {
         handler.setLevel(Level.FINE);
      }
      Logger.getLogger("net.sf.jstuff.xml.stream").setLevel(Level.FINE);
   }

   private InputStream getXml() {
      return StAXUtilsTest.class.getResourceAsStream("stax-utils-test.xml");
   }

   private ElementInfo findElement(final String xpath) throws XMLStreamException {
      try (AutoCloseableXMLStreamReader reader = STAX_FACTORY.createXMLStreamReader(getXml(), true)) {
         return StAXUtils.findElement(reader, xpath);
      }
   }

   private List<ElementInfo> findElements(final String xpath) throws XMLStreamException {
      try (AutoCloseableXMLStreamReader reader = STAX_FACTORY.createXMLStreamReader(getXml(), true)) {
         return StAXUtils.findElements(reader, xpath);
      }
   }

   private String findElementText(final String xpath) throws XMLStreamException {
      return findElement(xpath).getText();
   }

   public void testElementWithAttributeMatch() throws XMLStreamException {
      assertEquals("<Cat>", findElementText("/root/group/item[@id='2']"));
      assertEquals("<Cat>", findElementText("root/group/item[@id='2']"));
      assertEquals("<Cat>", findElementText("group/item[@id='2']"));
      assertEquals("<Cat>", findElementText("item[@id='2']"));

      assertEquals("Beer\n\n         Bike", findElementText("item[@id='3']").trim());

      assertEquals("Fast\n      <Steam>\n\n      Train", findElementText("item[@id='5']").trim());

      assertEquals("Car", findElementText("/root/group/item[@id='4']"));
      assertEquals("Car", findElementText("root/group/item[@id='4']"));
      assertEquals("Car", findElementText("group/item[@id='4']"));
      assertEquals("Car", findElementText("item[@id='4']"));

      assertEquals("Car", findElementText("/root/group/item[@anchor='#car' and @id='4']"));
      assertEquals("Car", findElementText("root/group/item[@anchor='#car' and @id='4']"));
      assertEquals("Car", findElementText("group/item[@anchor='#car' and @id='4']"));
      assertEquals("Car", findElementText("item[@anchor='#car' and @id='4']"));

      assertEquals("Car", findElementText("/root/group/item[@id='4' and @anchor='#car']"));
      assertEquals("Car", findElementText("root/group/item[@id='4' and @anchor='#car']"));
      assertEquals("Car", findElementText("group/item[@id='4' and @anchor='#car']"));
      assertEquals("Car", findElementText("item[@id='4' and @anchor='#car']"));

      final ElementInfo elem = findElement("item[@id='4' and @anchor='#car']");
      assertEquals("item", elem.localName);
      assertEquals("ns1", elem.nsPrefix);
      assertEquals("http://ns1", elem.nsURI);
      assertEquals("#car", elem.attrs.get("anchor"));
   }

   public void testFindElement() throws XMLStreamException {
      assertEquals("Dog", findElementText("/root/group/item"));
      assertEquals("Dog", findElementText("root/group/item"));
      assertEquals("Dog", findElementText("group/item"));
      assertEquals("Dog", findElementText("item"));
      assertEquals("Dog", findElementText("//item"));
      assertEquals("Dog", findElementText("/root//item"));
   }

   public void testFindElements() throws XMLStreamException {
      assertEquals(5, findElements("/root/group/item").size());
      assertEquals(5, findElements("root/group/item").size());
      assertEquals(5, findElements("group/item").size());
      assertEquals(5, findElements("item").size());
      assertEquals(5, findElements("//item").size());
      assertEquals(5, findElements("/root//item").size());
   }
}
