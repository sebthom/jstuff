/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.xml.stream;

import static net.sf.jstuff.xml.stream.StAXUtils.*;

import java.io.InputStream;
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

   static {
      for (final Handler handler : Logger.getLogger("").getHandlers()) {
         handler.setLevel(Level.FINE);
      }
      Logger.getLogger("net.sf.jstuff.xml.stream").setLevel(Level.FINE);
   }

   private InputStream getXml() {
      return StAXUtilsTest.class.getResourceAsStream("stax-utils-test.xml");
   }

   public void testElementWithAttributeMatch() throws XMLStreamException {
      assertEquals("<Cat>", findElement(getXml(), "/root/group/item[@id='2']").getText());
      assertEquals("<Cat>", findElement(getXml(), "root/group/item[@id='2']").getText());
      assertEquals("<Cat>", findElement(getXml(), "group/item[@id='2']").getText());
      assertEquals("<Cat>", findElement(getXml(), "item[@id='2']").getText());

      assertEquals("Beer\n\n         Bike", findElement(getXml(), "item[@id='3']").getText().trim());

      assertEquals("Fast\n      <Steam>\n\n      Train", findElement(getXml(), "item[@id='5']").getText().trim());

      assertEquals("Car", findElement(getXml(), "/root/group/item[@id='4']").getText());
      assertEquals("Car", findElement(getXml(), "root/group/item[@id='4']").getText());
      assertEquals("Car", findElement(getXml(), "group/item[@id='4']").getText());
      assertEquals("Car", findElement(getXml(), "item[@id='4']").getText());

      assertEquals("Car", findElement(getXml(), "/root/group/item[@anchor='#car' and @id='4']").getText());
      assertEquals("Car", findElement(getXml(), "root/group/item[@anchor='#car' and @id='4']").getText());
      assertEquals("Car", findElement(getXml(), "group/item[@anchor='#car' and @id='4']").getText());
      assertEquals("Car", findElement(getXml(), "item[@anchor='#car' and @id='4']").getText());

      assertEquals("Car", findElement(getXml(), "/root/group/item[@id='4' and @anchor='#car']").getText());
      assertEquals("Car", findElement(getXml(), "root/group/item[@id='4' and @anchor='#car']").getText());
      assertEquals("Car", findElement(getXml(), "group/item[@id='4' and @anchor='#car']").getText());
      assertEquals("Car", findElement(getXml(), "item[@id='4' and @anchor='#car']").getText());

      final ElementInfo elem = findElement(getXml(), "item[@id='4' and @anchor='#car']");
      assertEquals("item", elem.localName);
      assertEquals("ns1", elem.nsPrefix);
      assertEquals("http://ns1", elem.nsURI);
      assertEquals("#car", elem.attrs.get("anchor"));
   }

   public void testFindElement() throws XMLStreamException {
      assertEquals("Dog", findElement(getXml(), "/root/group/item").getText());
      assertEquals("Dog", findElement(getXml(), "root/group/item").getText());
      assertEquals("Dog", findElement(getXml(), "group/item").getText());
      assertEquals("Dog", findElement(getXml(), "item").getText());
      assertEquals("Dog", findElement(getXml(), "//item").getText());
      assertEquals("Dog", findElement(getXml(), "/root//item").getText());
   }

   public void testFindElements() throws XMLStreamException {
      assertEquals(5, findElements(getXml(), "/root/group/item").size());
      assertEquals(5, findElements(getXml(), "root/group/item").size());
      assertEquals(5, findElements(getXml(), "group/item").size());
      assertEquals(5, findElements(getXml(), "item").size());
      assertEquals(5, findElements(getXml(), "//item").size());
      assertEquals(5, findElements(getXml(), "/root//item").size());
   }
}
