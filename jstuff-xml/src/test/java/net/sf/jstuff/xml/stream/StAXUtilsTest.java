/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.xml.stream;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;
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

   private List<ElementInfo> findElements(final String xpath) throws XMLStreamException {
      try (AutoCloseableXMLStreamReader reader = STAX_FACTORY.createXMLStreamReader(getXml(), true)) {
         return StAXUtils.findElements(reader, xpath);
      }
   }

   @SuppressWarnings("resource")
   private InputStream getXml() {
      return asNonNull(StAXUtilsTest.class.getResourceAsStream("stax-utils-test.xml"));
   }

   private ElementInfo getElement(final String xpath) throws XMLStreamException {
      try (AutoCloseableXMLStreamReader reader = STAX_FACTORY.createXMLStreamReader(getXml(), true)) {
         return asNonNull(StAXUtils.findElement(reader, xpath));
      }
   }

   private String getElementText(final String xpath) throws XMLStreamException {
      return asNonNull(getElement(xpath).getText());
   }

   @Test
   public void testElementWithAttributeMatch() throws XMLStreamException {
      assertThat(getElementText("/root/group/item[@id='2']")).isEqualTo("<Cat>");
      assertThat(getElementText("root/group/item[@id='2']")).isEqualTo("<Cat>");
      assertThat(getElementText("group/item[@id='2']")).isEqualTo("<Cat>");
      assertThat(getElementText("item[@id='2']")).isEqualTo("<Cat>");

      assertThat(getElementText("item[@id='3']").trim()).isEqualTo("Beer\n\n         Bike");

      assertThat(getElementText("item[@id='5']").trim()).isEqualTo("Fast\n      <Steam>\n\n      Train");

      assertThat(getElementText("/root/group/item[@id='4']")).isEqualTo("Car");
      assertThat(getElementText("root/group/item[@id='4']")).isEqualTo("Car");
      assertThat(getElementText("group/item[@id='4']")).isEqualTo("Car");
      assertThat(getElementText("item[@id='4']")).isEqualTo("Car");

      assertThat(getElementText("/root/group/item[@anchor='#car' and @id='4']")).isEqualTo("Car");
      assertThat(getElementText("root/group/item[@anchor='#car' and @id='4']")).isEqualTo("Car");
      assertThat(getElementText("group/item[@anchor='#car' and @id='4']")).isEqualTo("Car");
      assertThat(getElementText("item[@anchor='#car' and @id='4']")).isEqualTo("Car");

      assertThat(getElementText("/root/group/item[@id='4' and @anchor='#car']")).isEqualTo("Car");
      assertThat(getElementText("root/group/item[@id='4' and @anchor='#car']")).isEqualTo("Car");
      assertThat(getElementText("group/item[@id='4' and @anchor='#car']")).isEqualTo("Car");
      assertThat(getElementText("item[@id='4' and @anchor='#car']")).isEqualTo("Car");

      final ElementInfo elem = getElement("item[@id='4' and @anchor='#car']");
      assertThat(elem.localName).isEqualTo("item");
      assertThat(elem.nsPrefix).isEqualTo("ns1");
      assertThat(elem.nsURI).isEqualTo("http://ns1");
      assertThat(elem.attrs.get("anchor")).isEqualTo("#car");
   }

   @Test
   public void testFindElement() throws XMLStreamException {
      assertThat(getElementText("/root/group/item")).isEqualTo("Dog");
      assertThat(getElementText("root/group/item")).isEqualTo("Dog");
      assertThat(getElementText("group/item")).isEqualTo("Dog");
      assertThat(getElementText("item")).isEqualTo("Dog");
      assertThat(getElementText("//item")).isEqualTo("Dog");
      assertThat(getElementText("/root//item")).isEqualTo("Dog");

      assertThat(asNonNull(getElement("/root").location).getLineNumber()).isEqualTo(2);
      assertThat(asNonNull(getElement("/root/group/item").location).getLineNumber()).isEqualTo(4);
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
