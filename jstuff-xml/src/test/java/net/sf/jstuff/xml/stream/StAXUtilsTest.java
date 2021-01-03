/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.xml.stream;

import static org.assertj.core.api.Assertions.*;

import java.io.InputStream;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import net.sf.jstuff.xml.stream.StAXUtils.ElementInfo;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class StAXUtilsTest {

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

   @Test
   public void testElementWithAttributeMatch() throws XMLStreamException {
      assertThat(findElementText("/root/group/item[@id='2']")).isEqualTo("<Cat>");
      assertThat(findElementText("root/group/item[@id='2']")).isEqualTo("<Cat>");
      assertThat(findElementText("group/item[@id='2']")).isEqualTo("<Cat>");
      assertThat(findElementText("item[@id='2']")).isEqualTo("<Cat>");

      assertThat(findElementText("item[@id='3']").trim()).isEqualTo("Beer\n\n         Bike");

      assertThat(findElementText("item[@id='5']").trim()).isEqualTo("Fast\n      <Steam>\n\n      Train");

      assertThat(findElementText("/root/group/item[@id='4']")).isEqualTo("Car");
      assertThat(findElementText("root/group/item[@id='4']")).isEqualTo("Car");
      assertThat(findElementText("group/item[@id='4']")).isEqualTo("Car");
      assertThat(findElementText("item[@id='4']")).isEqualTo("Car");

      assertThat(findElementText("/root/group/item[@anchor='#car' and @id='4']")).isEqualTo("Car");
      assertThat(findElementText("root/group/item[@anchor='#car' and @id='4']")).isEqualTo("Car");
      assertThat(findElementText("group/item[@anchor='#car' and @id='4']")).isEqualTo("Car");
      assertThat(findElementText("item[@anchor='#car' and @id='4']")).isEqualTo("Car");

      assertThat(findElementText("/root/group/item[@id='4' and @anchor='#car']")).isEqualTo("Car");
      assertThat(findElementText("root/group/item[@id='4' and @anchor='#car']")).isEqualTo("Car");
      assertThat(findElementText("group/item[@id='4' and @anchor='#car']")).isEqualTo("Car");
      assertThat(findElementText("item[@id='4' and @anchor='#car']")).isEqualTo("Car");

      final ElementInfo elem = findElement("item[@id='4' and @anchor='#car']");
      assertThat(elem.localName).isEqualTo("item");
      assertThat(elem.nsPrefix).isEqualTo("ns1");
      assertThat(elem.nsURI).isEqualTo("http://ns1");
      assertThat(elem.attrs.get("anchor")).isEqualTo("#car");
   }

   @Test
   public void testFindElement() throws XMLStreamException {
      assertThat(findElementText("/root/group/item")).isEqualTo("Dog");
      assertThat(findElementText("root/group/item")).isEqualTo("Dog");
      assertThat(findElementText("group/item")).isEqualTo("Dog");
      assertThat(findElementText("item")).isEqualTo("Dog");
      assertThat(findElementText("//item")).isEqualTo("Dog");
      assertThat(findElementText("/root//item")).isEqualTo("Dog");
   }

   @Test
   public void testFindElements() throws XMLStreamException {
      assertThat(findElements("/root/group/item")).hasSize(5);
      assertThat(findElements("root/group/item")).hasSize(5);
      assertThat(findElements("group/item")).hasSize(5);
      assertThat(findElements("item")).hasSize(5);
      assertThat(findElements("//item")).hasSize(5);
      assertThat(findElements("/root//item")).hasSize(5);
   }
}
