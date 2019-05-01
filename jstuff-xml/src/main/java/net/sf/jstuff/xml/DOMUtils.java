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
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.CollectionUtils;
import net.sf.jstuff.core.collection.MapWithLists;
import net.sf.jstuff.core.io.CharSequenceReader;
import net.sf.jstuff.core.io.FileUtils;
import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.reflection.Types;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class DOMUtils {
   private static final class SAXParseExceptionHandler extends org.xml.sax.helpers.DefaultHandler {
      private final List<SAXParseException> violations = new ArrayList<>();

      @Override
      public void error(final SAXParseException ex) throws SAXException {
         violations.add(ex);
      }

      @Override
      public void warning(final SAXParseException ex) throws SAXException {
         violations.add(ex);
      }
   }

   public static class XPathNode implements Serializable {
      private static final long serialVersionUID = 1L;

      public final String name;
      public final String value;
      public final String xPath;

      public XPathNode(final String name, final String value, final String xPath) {
         this.name = name;
         this.value = value;
         this.xPath = xPath;
      }

      @Override
      public boolean equals(final Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         final XPathNode other = (XPathNode) obj;

         if (!Objects.equals(name, other.name))
            return false;
         if (!Objects.equals(value, other.value))
            return false;
         if (!Objects.equals(xPath, other.xPath))
            return false;
         return true;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + (name == null ? 0 : name.hashCode());
         result = prime * result + (value == null ? 0 : value.hashCode());
         result = prime * result + (xPath == null ? 0 : xPath.hashCode());
         return result;
      }

      @Override
      public String toString() {
         return value;
      }
   }

   public static class XPathNodeConfiguration implements Serializable, Cloneable {
      private static final long serialVersionUID = 1L;
      private static final XPathNodeConfiguration INTERNAL_SHARED_INSTANCE = new XPathNodeConfiguration();

      public boolean recursive = true;
      public boolean useSchemaIdAttributes = true;
      public final MapWithLists<String, String> idAttributesByXMLTagName = new MapWithLists<>();

      @Override
      protected XPathNodeConfiguration clone() throws CloneNotSupportedException {
         final XPathNodeConfiguration clone = new XPathNodeConfiguration();
         clone.recursive = recursive;
         clone.useSchemaIdAttributes = useSchemaIdAttributes;
         clone.idAttributesByXMLTagName.putAll(idAttributesByXMLTagName);
         return clone;
      }

      public List<String> getIdAttributesForXMLTagName(final String xmlTagName) {
         return idAttributesByXMLTagName.getNullSafe(xmlTagName);
      }
   }

   private static final Logger LOG = Logger.create();

   protected static final MapBasedNamespaceContext NAMESPACE_CONTEXT = new MapBasedNamespaceContext();

   private static final EntityResolver NOREMOTE_DTD_RESOLVER = new EntityResolver() {
      private final Logger log = Logger.create();

      @Override
      public InputSource resolveEntity(final String schemaId, final String schemaLocation) throws SAXException, IOException {
         if (!schemaLocation.startsWith("file://")) {
            log.debug("Ignoring DTD [%s] [%s]", schemaId, schemaLocation);
            return new InputSource(new StringReader(""));
         }
         return null;
      }
   };

   protected static final ThreadLocal<TransformerFactory> TRANSFORMER_FACTORY = new ThreadLocal<TransformerFactory>() {
      @Override
      protected TransformerFactory initialValue() {
         return TransformerFactory.newInstance();
      }
   };

   protected static final ThreadLocal<XPath> XPATH = new ThreadLocal<XPath>() {
      @Override
      protected XPath initialValue() {
         final XPath xpath = XPathFactory.newInstance().newXPath();
         xpath.setNamespaceContext(NAMESPACE_CONTEXT);
         return xpath;
      }
   };

   private static List<Attr> _getIdAttributes(final Element elem, final XPathNodeConfiguration cfg) {
      final NamedNodeMap nodeMap = elem.getAttributes();
      final List<Attr> result = CollectionUtils.newArrayList(nodeMap.getLength());
      if (cfg.useSchemaIdAttributes) {
         for (int i = 0, l = nodeMap.getLength(); i < l; i++) {
            final Attr attr = (Attr) nodeMap.item(i);
            if (attr.isId()) {
               result.add((Attr) nodeMap.item(i));
            }
         }
      }
      if (result.size() == 0 && cfg.idAttributesByXMLTagName.size() > 0) {
         for (final String idAttrName : cfg.getIdAttributesForXMLTagName(elem.getTagName()))
            if (elem.hasAttribute(idAttrName)) {
               result.add(elem.getAttributeNode(idAttrName));
               break;
            }
         if (result.size() == 0) {
            for (final String idAttrName : cfg.getIdAttributesForXMLTagName("*"))
               if (elem.hasAttribute(idAttrName)) {
                  result.add(elem.getAttributeNode(idAttrName));
                  break;
               }
         }
      }
      return result;
   }

   static Document _getOwnerDocument(final Node node) {
      final Document doc = node.getOwnerDocument();
      if (doc == null && node instanceof Document)
         return (Document) node;
      return doc;
   }

   private static void _getXPathNodes(final Element elem, final XPathNodeConfiguration cfg, final CharSequence parentXPath,
      final Map<String, XPathNode> valuesByXPath) {
      /*
       * build the xPath of the current element
       */
      final StringBuilder xPath = new StringBuilder(parentXPath);
      xPath.append('/');
      xPath.append(elem.getTagName());
      final List<Attr> attrs = _getIdAttributes(elem, cfg);
      if (attrs.size() > 0) {
         xPath.append('[');
         boolean isFirst = true;
         for (final Attr idAttribute : _getIdAttributes(elem, cfg)) {
            if (isFirst) {
               isFirst = false;
            } else {
               xPath.append(" and ");
            }
            xPath.append('@');
            xPath.append(idAttribute.getName());
            xPath.append("='");
            xPath.append(idAttribute.getValue());
            xPath.append('\'');
         }
         xPath.append(']');
      }

      /*
       * iterate attributes
       */
      for (final Attr attr : getAttributes(elem)) {
         final String attrXPath = xPath + "/@" + attr.getName();
         valuesByXPath.put(attrXPath, new XPathNode(attr.getName(), attr.getValue(), attrXPath));
      }
      /*
       * iterate child elements
       */
      boolean foundTextNode = false;
      for (final Node child : DOMUtils.nodeListToList(elem.getChildNodes()))
         if (cfg.recursive && child instanceof Element) {
            _getXPathNodes((Element) child, cfg, xPath, valuesByXPath);
         } else if ("#text".equals(child.getNodeName())) {
            foundTextNode = true;
            final String nodeValue = child.getNodeValue().trim();
            if (nodeValue.length() > 0) {
               final String attrXPath = xPath + "/text()";
               valuesByXPath.put(attrXPath, new XPathNode("#text", nodeValue, attrXPath));
            }
         }
      if (!foundTextNode) {
         final String attrXPath = xPath + "/text()";
         valuesByXPath.put(attrXPath, new XPathNode("#text", null, attrXPath));
      }
   }

   /**
    * @return a thread-safe xpath expression object
    */
   public static XPathExpression compileXPath(final String xPathExpression) {
      return Types.createThreadLocalized(XPathExpression.class, new ThreadLocal<XPathExpression>() {
         @Override
         protected XPathExpression initialValue() {
            try {
               return XPATH.get().compile(xPathExpression);
            } catch (final XPathExpressionException ex) {
               throw new XMLException(ex);
            }
         }
      });
   }

   public static Comment createCommentBefore(final Node sibling, final String commentString) {
      Args.notNull("sibling", sibling);
      Args.notNull("commentString", commentString);

      final Node parent = sibling.getParentNode();
      Args.notNull("sibling.parentNode", parent);

      return (Comment) parent.insertBefore(_getOwnerDocument(parent).createComment(commentString), sibling);
   }

   /**
    * Creates a new XML element as child of the given parentNode.
    */
   public static Element createElement(final Node parent, final String tagName) {
      return createElement(parent, tagName, null);
   }

   /**
    * Creates a new XML element as child of the given parentNode with the given attributes.
    */
   public static Element createElement(final Node parent, final String tagName, final Map<String, String> tagAttributes) {
      Args.notNull("parent", parent);
      Args.notEmpty("tagName", tagName);

      final Element elem = (Element) parent.appendChild(_getOwnerDocument(parent).createElement(tagName));
      if (tagAttributes != null) {
         for (final Entry<String, String> attr : tagAttributes.entrySet()) {
            elem.setAttribute(attr.getKey(), attr.getValue());
         }
      }
      return elem;
   }

   public static Element createElementBefore(final Node sibling, final String tagName) {
      return createElementBefore(sibling, tagName, null);
   }

   public static Element createElementBefore(final Node sibling, final String tagName, final Map<String, String> tagAttributes) {
      Args.notNull("sibling", sibling);
      Args.notEmpty("tagName", tagName);

      final Node parent = sibling.getParentNode();
      Args.notNull("sibling.parentNode", parent);

      final Element elem = (Element) parent.insertBefore(_getOwnerDocument(parent).createElement(tagName), sibling);
      if (tagAttributes != null) {
         for (final Entry<String, String> attr : tagAttributes.entrySet()) {
            elem.setAttribute(attr.getKey(), attr.getValue());
         }
      }
      return elem;
   }

   public static Element createElementWithText(final Node parent, final String tagName, final Map<String, String> tagAttributes, final Object text) {
      final Element elem = createElement(parent, tagName, tagAttributes);
      createTextNode(elem, text);
      return elem;
   }

   public static Element createElementWithText(final Node parent, final String tagName, final Object text) {
      final Element elem = createElement(parent, tagName, null);
      createTextNode(elem, text);
      return elem;
   }

   public static Element createElementWithTextBefore(final Node sibling, final String tagName, final Map<String, String> tagAttributes, final Object text) {
      final Element elem = createElementBefore(sibling, tagName, tagAttributes);
      createTextNode(elem, text);
      return elem;
   }

   public static Element createElementWithTextBefore(final Node sibling, final String tagName, final Object text) {
      final Element elem = createElementBefore(sibling, tagName, null);
      createTextNode(elem, text);
      return elem;
   }

   public static Text createTextNode(final Node parent, final Object text) {
      Args.notNull("parent", parent);
      Args.notNull("text", text);

      final Text elem = (Text) parent.appendChild(_getOwnerDocument(parent).createTextNode(text.toString()));
      return elem;
   }

   public static Text createTextNodeBefore(final Node sibling, final Object text) {
      Args.notNull("sibling", sibling);
      Args.notNull("text", text);

      final Node parent = sibling.getParentNode();
      Args.notNull("sibling.parentNode", parent);

      final Text elem = (Text) parent.insertBefore(_getOwnerDocument(parent).createTextNode(text.toString()), sibling);
      return elem;
   }

   public static String evaluate(final Node searchScope, final String xPathExpression) throws XMLException {
      Args.notNull("searchScope", searchScope);
      Args.notNull("xPathExpression", xPathExpression);

      try {
         return XPATH.get().evaluate(xPathExpression, searchScope);
      } catch (final XPathExpressionException ex) {
         throw new XMLException(ex);
      }
   }

   @SuppressWarnings("unchecked")
   public static <T extends Node> T findNode(final Node searchScope, final String xPathExpression) throws XMLException {
      Args.notNull("searchScope", searchScope);
      Args.notNull("xPathExpression", xPathExpression);

      try {
         return (T) XPATH.get().evaluate(xPathExpression, searchScope, XPathConstants.NODE);
      } catch (final XPathExpressionException ex) {
         throw new XMLException(ex);
      }
   }

   public static <T extends Node> List<T> findNodes(final Node searchScope, final String xPathExpression) throws XMLException {
      Args.notNull("searchScope", searchScope);
      Args.notNull("xPathExpression", xPathExpression);

      try {
         return DOMUtils.nodeListToList((NodeList) XPATH.get().evaluate(xPathExpression, searchScope, XPathConstants.NODESET));
      } catch (final XPathExpressionException ex) {
         throw new XMLException(ex);
      }
   }

   /**
    * @param recursive return text content of child nodes
    */
   public static String findTextContent(final Node searchScope, final String xPathExpression, final boolean recursive) throws XMLException {
      Args.notNull("searchScope", searchScope);
      Args.notNull("xPathExpression", xPathExpression);

      try {
         if (recursive)
            return (String) XPATH.get().evaluate(xPathExpression, searchScope, XPathConstants.STRING);
         final Node node = findNode(searchScope, xPathExpression + "/text()");
         if (node == null)
            return null;
         return node.getNodeValue();
      } catch (final XPathExpressionException ex) {
         throw new XMLException(ex);
      }
   }

   public static List<Attr> getAttributes(final Node node) {
      Args.notNull("node", node);

      final NamedNodeMap nodeMap = node.getAttributes();
      final List<Attr> result = CollectionUtils.newArrayList(nodeMap.getLength());
      for (int i = 0, l = nodeMap.getLength(); i < l; i++) {
         result.add((Attr) nodeMap.item(i));
      }
      return result;
   }

   /**
    * @return all direct child nodes of this node.
    */
   public static <T extends Node> List<T> getChildNodes(final Node parent) {
      return nodeListToList(parent.getChildNodes());
   }

   /**
    * @param tagName The name of the tag to match on. The special value "*" matches all tags.
    * @return all child and sub-child nodes with the given tag name, in document order.
    */
   public static <T extends Node> List<T> getElementsByTagName(final Element parent, final String tagName) {
      Args.notNull("parent", parent);
      Args.notNull("tagName", tagName);

      return nodeListToList(parent.getElementsByTagName(tagName));
   }

   public static Node getFirstChild(final Node node) throws XMLException {
      Args.notNull("node", node);

      return findNode(node, "*");
   }

   public static List<Attr> getIdAttributes(final Node node) {
      Args.notNull("node", node);

      final NamedNodeMap nodeMap = node.getAttributes();
      final List<Attr> result = CollectionUtils.newArrayList(nodeMap.getLength());
      for (int i = 0, l = nodeMap.getLength(); i < l; i++) {
         final Attr attr = (Attr) nodeMap.item(i);
         if (attr.isId()) {
            result.add((Attr) nodeMap.item(i));
         }
      }
      return result;
   }

   /**
    * Returns a sorted map containing the XPath expression of all attributes as entry key and their value as entry value.
    */
   public static SortedMap<String, XPathNode> getXPathNodes(final Element element) {
      Args.notNull("element", element);

      final SortedMap<String, XPathNode> valuesByXPath = new TreeMap<>();
      _getXPathNodes(element, XPathNodeConfiguration.INTERNAL_SHARED_INSTANCE, "", valuesByXPath);
      return valuesByXPath;
   }

   /**
    * Returns a sorted map containing the XPath expression of all attributes as entry key and their value as entry value.
    */
   public static SortedMap<String, XPathNode> getXPathNodes(final Element element, final XPathNodeConfiguration config) {
      Args.notNull("element", element);
      Args.notNull("config", config);

      final SortedMap<String, XPathNode> valuesByXPath = new TreeMap<>();
      _getXPathNodes(element, config, "", valuesByXPath);
      return valuesByXPath;
   }

   /**
    * @return the imported node object
    */
   @SuppressWarnings("unchecked")
   public static <T extends Node> T importNode(final Node parent, final T nodeToImport) {
      Args.notNull("parent", parent);
      Args.notNull("nodeToImport", nodeToImport);

      return (T) parent.appendChild(_getOwnerDocument(parent).importNode(nodeToImport, true));
   }

   @SuppressWarnings("unchecked")
   public static <T extends Node> T importNodeBefore(final Node sibling, final T nodeToImport) {
      Args.notNull("sibling", sibling);
      Args.notNull("nodeToImport", nodeToImport);

      final Node parent = sibling.getParentNode();
      Args.notNull("sibling.parentNode", parent);

      final Node importedNode = _getOwnerDocument(parent).importNode(nodeToImport, true);
      return (T) parent.insertBefore(importedNode, sibling);
   }

   @SuppressWarnings("unchecked")
   public static <T extends Node> List<T> importNodes(final Node parent, final Collection<T> nodesToImport) {
      Args.notNull("parent", parent);
      Args.notNull("nodesToImport", nodesToImport);

      final Document targetDoc = _getOwnerDocument(parent);
      final List<T> importedNodes = new ArrayList<>(nodesToImport.size());
      for (final T nodeToImport : nodesToImport) {
         final T importedNode = (T) parent.appendChild(targetDoc.importNode(nodeToImport, true));
         importedNodes.add(importedNode);
      }
      return importedNodes;
   }

   @SuppressWarnings("unchecked")
   public static <T extends Node> List<T> importNodes(final Node parent, final NodeList nodesToImport) {
      Args.notNull("nodesToImport", nodesToImport);

      return importNodes(parent, (List<T>) nodeListToList(nodesToImport));
   }

   @SuppressWarnings("unchecked")
   public static <T extends Node> List<T> importNodesBefore(final Node sibling, final Collection<T> nodesToImport) {
      Args.notNull("sibling", sibling);
      Args.notNull("nodesToImport", nodesToImport);

      final Node parent = sibling.getParentNode();
      Args.notNull("sibling.parentNode", parent);

      final Document targetDoc = _getOwnerDocument(parent);
      final List<T> importedNodes = new ArrayList<>(nodesToImport.size());
      for (final T nodeToImport : nodesToImport) {
         final T importedNode = (T) parent.insertBefore(targetDoc.importNode(nodeToImport, true), sibling);
         importedNodes.add(importedNode);
      }
      return importedNodes;
   }

   @SuppressWarnings("unchecked")
   public static <T extends Node> List<T> importNodesBefore(final Node sibling, final NodeList nodesToImport) {
      Args.notNull("nodesToImport", nodesToImport);

      return importNodesBefore(sibling, (List<T>) nodeListToList(nodesToImport));
   }

   public static Node[] nodeListToArray(final NodeList nodes) {
      Args.notNull("nodes", nodes);

      final Node[] result = new Node[nodes.getLength()];
      for (int i = 0, l = nodes.getLength(); i < l; i++) {
         result[i] = nodes.item(i);
      }
      return result;
   }

   @SuppressWarnings("unchecked")
   public static <T extends Node> List<T> nodeListToList(final NodeList nodes) {
      Args.notNull("nodes", nodes);

      final List<T> result = CollectionUtils.newArrayList(nodes.getLength());

      for (int i = 0, l = nodes.getLength(); i < l; i++) {
         result.add((T) nodes.item(i));
      }
      return result;
   }

   /**
    * Parses the given file and returns a org.w3c.dom.Document.
    */
   public static Document parseFile(final File xmlFile) throws XMLException {
      Args.notNull("xmlFile", xmlFile);
      Assert.isFileReadable(xmlFile);

      return parseInputSource(new InputSource(xmlFile.toURI().toASCIIString()), xmlFile.getAbsolutePath(), null, (File[]) null);
   }

   /**
    * Parses the given file and returns a org.w3c.dom.Document.
    *
    * @param xmlSchemaFiles the XML schema files to validate against, the schema files are also required to apply default values
    */
   public static Document parseFile(final File xmlFile, final File... xmlSchemaFiles) throws XMLException {
      Args.notNull("xmlFile", xmlFile);
      Assert.isFileReadable(xmlFile);

      return parseInputSource(new InputSource(xmlFile.toURI().toASCIIString()), xmlFile.getAbsolutePath(), null, xmlSchemaFiles);
   }

   /**
    * Parses the given file and returns a org.w3c.dom.Document.
    *
    * @param defaultNamespace optional, may be null
    * @param xmlSchemaFiles the XML schema files to validate against, the schema files are also required to apply default values
    */
   public static Document parseFile(final File xmlFile, final String defaultNamespace, final File... xmlSchemaFiles) throws XMLException {
      Args.notNull("xmlFile", xmlFile);
      Assert.isFileReadable(xmlFile);

      return parseInputSource(new InputSource(xmlFile.toURI().toASCIIString()), xmlFile.getAbsolutePath(), defaultNamespace, xmlSchemaFiles);
   }

   /**
    * Parses the content of given input source and returns a org.w3c.dom.Document.
    *
    * @param input the input to parse
    * @param inputId an identifier / label for the input source, e.g. a file name
    */
   public static Document parseInputSource(final InputSource input, final String inputId) throws XMLException {
      return parseInputSource(input, inputId, null);
   }

   /**
    * Parses the content of given input source and returns a org.w3c.dom.Document.
    *
    * @param input the input to parse
    * @param inputId an identifier / label for the input source, e.g. a file name
    * @param defaultNamespace optional, may be null
    * @param xmlSchemaFiles the XML schema files to validate against, the schema files are also required to apply default values
    */
   public static Document parseInputSource(final InputSource input, final String inputId, final String defaultNamespace, final File... xmlSchemaFiles)
      throws XMLException {
      Args.notNull("input", input);

      try {
         LOG.debug("Parsing [%s]...", inputId);

         // IBM JDK: org.apache.xerces.jaxp.DocumentBuilderFactoryImpl
         // Sun JDK: com.sun.org.apache.xerces.jaxp.DocumentBuilderFactoryImpl
         final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

         domFactory.setCoalescing(true);
         domFactory.setIgnoringComments(false);
         domFactory.setXIncludeAware(true); // domFactory.setFeature("http://apache.org/xml/features/xinclude", true);

         // if no XML schema is provided simply parse without validation
         if (xmlSchemaFiles == null || xmlSchemaFiles.length == 0) {
            final DocumentBuilder domBuilder = domFactory.newDocumentBuilder();

            // disabling external DTD resolution to avoid errors like "java.net.UnknownHostException: java.sun.com"
            domBuilder.setEntityResolver(NOREMOTE_DTD_RESOLVER);

            return domBuilder.parse(input);
         }

         // enabling JAXP validation
         domFactory.setNamespaceAware(true); // domFactory.setFeature("http://xml.org/sax/features/namespaces", true);
         domFactory.setValidating(true);

         domFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", XMLConstants.W3C_XML_SCHEMA_NS_URI);
         domFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", xmlSchemaFiles);
         domFactory.setFeature("http://apache.org/xml/features/honour-all-schemaLocations", true);

         final DocumentBuilder domBuilder = domFactory.newDocumentBuilder();

         // disabling external DTD resolution to avoid errors like "java.net.UnknownHostException: java.sun.com"
         domBuilder.setEntityResolver(NOREMOTE_DTD_RESOLVER);

         final SAXParseExceptionHandler errorHandler = new SAXParseExceptionHandler();
         domBuilder.setErrorHandler(errorHandler);
         Document domDocument = domBuilder.parse(input);
         final Element domRoot = domDocument.getDocumentElement();

         // remove any schema location declarations
         domRoot.removeAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation");

         // if a defaultNamespace is provided and no namespace is declared or does not match, then add/change the namespace and re-parse the file
         final String ns = domRoot.getNamespaceURI();
         if (defaultNamespace != null && !defaultNamespace.equals(ns)) {
            LOG.debug("Fixing root namespace...");

            domRoot.setAttribute("jstuffNS", defaultNamespace);
            final String newXML = toXML(domDocument) //
               .replaceFirst("xmlns\\s*=\\s*(['][^']*[']|[\"][^\"]*[\"])", "") //
               .replaceFirst("jstuffNS", "xmlns");

            /* unfortunately the following does not seem to work reliable on all JVM implementations
            // change the root namespace
            final Stack<Node> nodes = new Stack<Node>();
            nodes.push(domRoot);
            while (!nodes.isEmpty())
            {
                Node node = nodes.pop();
                if (node.getNodeType() == Node.ATTRIBUTE_NODE || node.getNodeType() == Node.ELEMENT_NODE)
                {
                    final String nns = node.getNamespaceURI();
                    if (nns == null || nns.equals(ns))
                        node = domDocument.renameNode(node, defaultNamespace, node.getNodeName());
                }
                final NamedNodeMap attributes = node.getAttributes();
                if (attributes != null) for (int i = 0, l = attributes.getLength(); i < l; i++)
                    nodes.push(attributes.item(i));
                for (final Node childNode : nodeListToArray(node.getChildNodes()))
                    nodes.push(childNode);
            }
            final String newXML = renderToString(domDocument);
            */

            // re-parse the file with the new namespace
            errorHandler.violations.clear();
            domDocument = domBuilder.parse(new InputSource(new StringReader(newXML)));
         }

         Assert.isTrue(errorHandler.violations.size() == 0, errorHandler.violations.size() + " XML schema violation(s) detected in [" + inputId + "]:\n\n => "
            + Strings.join(errorHandler.violations, "\n => "));
         return domDocument;
      } catch (final ParserConfigurationException ex) {
         throw new XMLException(ex);
      } catch (final SAXException ex) {
         throw new XMLException(ex);
      } catch (final IOException ex) {
         throw new XMLException(ex);
      }
   }

   /**
    * Parses the given string and returns a org.w3c.dom.Document.
    *
    * @param input the input to parse
    * @param inputId an identifier / label for the input source, e.g. a file name
    */
   @SuppressWarnings("resource")
   public static Document parseString(final CharSequence input, final String inputId) throws XMLException {
      Args.notNull("input", input);

      return parseInputSource(new InputSource(new CharSequenceReader(input)), inputId, null, (File[]) null);
   }

   /**
    * Parses the given string and returns a org.w3c.dom.Document.
    *
    * @param input the input to parse
    * @param inputId an identifier / label for the input source, e.g. a file name
    * @param defaultNamespace optional, may be null
    * @param xmlSchemaFiles the XML schema files to validate against, the schema files are also required to apply default values
    */
   @SuppressWarnings("resource")
   public static Document parseString(final CharSequence input, final String inputId, final String defaultNamespace, final File... xmlSchemaFiles)
      throws XMLException {
      Args.notNull("input", input);

      return parseInputSource(new InputSource(new CharSequenceReader(input)), inputId, defaultNamespace, xmlSchemaFiles);
   }

   /**
    * Registers a namespace for the XPath Expression Engine.
    */
   public static void registerNamespace(final String namespaceURI, final String prefix) {
      Args.notNull("namespaceURI", namespaceURI);
      Args.notNull("prefix", prefix);

      NAMESPACE_CONTEXT.bindNamespace(namespaceURI, prefix);
   }

   /**
    * @return true if the node was removed and false if the node did not have a parent node
    * @exception DOMException
    *               <li>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.</li>
    *               <li>NOT_FOUND_ERR: Raised if <code>oldChild</code> is not a child
    *               of this node.</li>
    *               <li>NOT_SUPPORTED_ERR: if this node is of type <code>Document</code>, this exception might be raised if the DOM
    *               implementation doesn't support the removal of the <code>DocumentType</code> child or the <code>Element</code> child.</li>
    */
   public static boolean removeNode(final Node node) throws DOMException {
      final Node parent = node.getParentNode();
      if (parent == null)
         return false;
      parent.removeChild(node);
      return true;
   }

   /**
    * @return a list of the removed nodes
    */
   public static List<Node> removeNodes(final Node searchScope, final String xPathExpression) throws XMLException {
      Args.notNull("searchScope", searchScope);
      Args.notEmpty("xPathExpression", xPathExpression);

      final List<Node> nodesToRemove = findNodes(searchScope, xPathExpression);

      for (final Node nodeToRemove : nodesToRemove) {
         DOMUtils.removeNode(nodeToRemove);
      }
      return nodesToRemove;
   }

   public static void removeWhiteSpaceNodes(final Node searchScope) {
      removeNodes(searchScope, "text()[normalize-space()='']");
   }

   public static void saveToFile(final Node root, final File targetFile) throws IOException, XMLException {
      Args.notNull("root", root);
      Args.notNull("targetFile", targetFile);

      try {
         final Transformer transformer = TRANSFORMER_FACTORY.get().newTransformer();

         // http://xerces.apache.org/xerces2-j/javadocs/api/javax/xml/transform/OutputKeys.html
         transformer.setOutputProperty(OutputKeys.INDENT, "yes");
         transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
         transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
         transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

         final FileWriter fw = new FileWriter(targetFile);
         transformer.transform(new DOMSource(root), new StreamResult(fw));
         fw.flush();
         fw.close();
      } catch (final TransformerException ex) {
         throw new XMLException(ex);
      }
   }

   /**
    * First creates a backup of the targetFile and then saves the document to the targetFile.
    */
   public static void saveToFileAfterBackup(final Node root, final File targetFile) throws IOException, XMLException {
      Args.notNull("root", root);
      Args.notNull("targetFile", targetFile);

      FileUtils.backupFile(targetFile);

      saveToFile(root, targetFile);
   }

   /**
    * @param parentNode node whose children will be sorted
    */
   public static void sortChildNodes(final Node parentNode, final Comparator<Node> comparator) {
      Args.notNull("parentNode", parentNode);
      Args.notNull("comparator", comparator);

      final List<Node> sortedNodes = new ArrayList<>();
      for (final Node node : getChildNodes(parentNode)) {
         // Remove empty text nodes
         if (node instanceof Text && ((Text) node).getTextContent().trim().length() > 1) {
            continue;
         }
         sortedNodes.add(node);
      }

      Collections.sort(sortedNodes, comparator);
      for (final Node n : sortedNodes) {
         parentNode.appendChild(n);
      }
   }

   /**
    * @param parentNode node whose children will be sorted
    */
   public static void sortChildNodesByAttributes(final Node parentNode, final boolean ascending, final String... attributeNames) {
      Args.notNull("parentNode", parentNode);

      final List<Node> children = new ArrayList<>();
      for (final Node node : getChildNodes(parentNode)) {
         // Remove empty text nodes
         if (node instanceof Text && ((Text) node).getTextContent().trim().length() > 1) {
            continue;
         }
         children.add(node);
      }

      Collections.sort(children, new Comparator<Node>() {
         @Override
         public int compare(final Node n1, final Node n2) {
            final NamedNodeMap m1 = n1.getAttributes();
            final NamedNodeMap m2 = n2.getAttributes();
            for (final String attrName : attributeNames) {
               final Attr a1 = (Attr) m1.getNamedItem(attrName);
               final Attr a2 = (Attr) m2.getNamedItem(attrName);
               final String v1 = a1 == null ? null : a1.getValue();
               final String v2 = a2 == null ? null : a2.getValue();
               final int rc;
               // perform numeric sort if both values are integers
               if (StringUtils.isNumeric(v1) && StringUtils.isNumeric(v2)) {
                  final int i1 = Integer.parseInt(v1, 10);
                  final int i2 = Integer.parseInt(v2, 10);
                  rc = i1 < i2 ? -1 : i1 == i2 ? 0 : 1;
               } else {
                  rc = ObjectUtils.compare(v1, v2, false);
               }

               if (rc == 0) {
                  continue;
               }
               return ascending ? rc : -1 * rc;
            }
            return 0;
         }
      });

      for (final Node n : children) {
         parentNode.appendChild(n);
      }
   }

   public static String toXML(final Node root) throws XMLException {
      Args.notNull("root", root);

      return toXML(root, true, true);
   }

   @SuppressWarnings("resource")
   public static String toXML(final Node root, final boolean outputXMLDeclaration, final boolean formatPretty) throws XMLException {
      Args.notNull("root", root);

      final FastByteArrayOutputStream bos = new FastByteArrayOutputStream();
      try {
         toXML(root, bos, outputXMLDeclaration, formatPretty);
      } catch (final IOException e) {
         // ignore, never happens
      }
      return bos.toString();
   }

   public static void toXML(final Node root, final OutputStream out) throws XMLException, IOException {
      Args.notNull("root", root);
      Args.notNull("out", out);

      toXML(root, out, true, true);
   }

   public static void toXML(final Node root, final OutputStream out, final boolean outputXMLDeclaration, final boolean formatPretty) throws XMLException,
      IOException {
      Args.notNull("root", root);
      Args.notNull("out", out);

      try {
         final Transformer transformer = TRANSFORMER_FACTORY.get().newTransformer();

         // http://xerces.apache.org/xerces2-j/javadocs/api/javax/xml/transform/OutputKeys.html
         transformer.setOutputProperty(OutputKeys.METHOD, "xml");
         transformer.setOutputProperty(OutputKeys.MEDIA_TYPE, "text/xml");
         transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
         if (outputXMLDeclaration) {
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            // transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

            // because of a bug in Xalan omitting new line characters after the <?xml...> declaration header, we output the header own our own
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>".getBytes());
            out.write(Strings.NEW_LINE.getBytes());
         } else {
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
         }
         if (formatPretty) {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
         } else {
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
         }
         transformer.transform(new DOMSource(root), new StreamResult(out));
      } catch (final TransformerException ex) {
         throw new XMLException(ex);
      }
   }
}
