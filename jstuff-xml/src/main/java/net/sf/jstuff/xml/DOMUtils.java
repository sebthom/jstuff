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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
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
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sf.jstuff.core.Assert;
import net.sf.jstuff.core.Logger;
import net.sf.jstuff.core.StringUtils;
import net.sf.jstuff.core.collection.CollectionUtils;
import net.sf.jstuff.core.collection.MapWithLists;
import net.sf.jstuff.core.io.FileUtils;

import org.apache.commons.lang.ObjectUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DOMUtils
{
	private static final class NamespaceContextImpl implements NamespaceContext
	{
		private final Map<String, LinkedHashSet<String>> namespaceURIsByPrefix = CollectionUtils.newHashMap(2);
		private final Map<String, LinkedHashSet<String>> prefixesByNamespaceURI = CollectionUtils.newHashMap(2);

		public String getNamespaceURI(final String prefix)
		{
			if (namespaceURIsByPrefix.containsKey(prefix)) return namespaceURIsByPrefix.get(prefix).iterator().next();
			return XMLConstants.NULL_NS_URI;
		}

		public String getPrefix(final String namespaceURI)
		{
			return prefixesByNamespaceURI.containsKey(namespaceURI) ? prefixesByNamespaceURI.get(namespaceURI)
					.iterator().next() : null;
		}

		public Iterator<String> getPrefixes(final String namespaceURI)
		{
			return prefixesByNamespaceURI.containsKey(namespaceURI) ? prefixesByNamespaceURI.get(namespaceURI)
					.iterator() : null;
		}

		public void registerNamespace(final String namespaceURI, final String prefix)
		{
			if (!namespaceURIsByPrefix.containsKey(prefix))
				namespaceURIsByPrefix.put(prefix, new LinkedHashSet<String>(2));
			namespaceURIsByPrefix.get(prefix).add(namespaceURI);

			if (!prefixesByNamespaceURI.containsKey(namespaceURI))
				prefixesByNamespaceURI.put(namespaceURI, new LinkedHashSet<String>(2));
			prefixesByNamespaceURI.get(namespaceURI).add(prefix);
		}
	}

	private static final class SAXParseExceptionHandler extends org.xml.sax.helpers.DefaultHandler
	{
		public final List<SAXParseException> violations = new ArrayList<SAXParseException>();

		@Override
		public void error(final SAXParseException ex) throws SAXException
		{
			violations.add(ex);
		}

		@Override
		public void warning(final SAXParseException ex) throws SAXException
		{
			violations.add(ex);
		}
	}

	public static class XPathAttributeConfiguration implements Serializable
	{
		private static final long serialVersionUID = 1L;
		private static final XPathAttributeConfiguration INSTANCE = new XPathAttributeConfiguration();

		public boolean recursive = true;
		public boolean useSchemaIdAttributes = true;
		public MapWithLists<String, String> idAttributesByXMLTagName = new MapWithLists<String, String>();

		public List<String> getIdAttributesForXMLTagName(final String xmlTagName)
		{
			return idAttributesByXMLTagName.getSafe(xmlTagName);
		}
	}

	public static class XPathAttribute implements Serializable
	{
		private static final long serialVersionUID = 1L;

		public final String name;
		public final String value;
		public final String xpath;

		public XPathAttribute(final String name, final String value, final String xpath)
		{
			this.name = name;
			this.value = value;
			this.xpath = xpath;
		}

		@Override
		public boolean equals(final Object obj)
		{
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			final XPathAttribute other = (XPathAttribute) obj;

			if (!ObjectUtils.equals(name, other.name)) return false;
			if (!ObjectUtils.equals(value, other.value)) return false;
			if (!ObjectUtils.equals(xpath, other.xpath)) return false;
			return true;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + (name == null ? 0 : name.hashCode());
			result = prime * result + (value == null ? 0 : value.hashCode());
			result = prime * result + (xpath == null ? 0 : xpath.hashCode());
			return result;
		}

		@Override
		public String toString()
		{
			return value;
		}
	}

	private static final Logger LOG = Logger.get();
	private static final NamespaceContextImpl NAMESPACE_CONTEXT = new NamespaceContextImpl();
	private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();

	private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();

	/**
	 * @throws XMLException
	 */
	public static Node findNode(final String xPathExpression, final Node searchScope) throws XMLException
	{
		Assert.argumentNotNull("xPathExpression", xPathExpression);

		try
		{
			final XPath xpath = XPATH_FACTORY.newXPath();
			xpath.setNamespaceContext(NAMESPACE_CONTEXT);

			return (Node) xpath.evaluate(xPathExpression, searchScope, XPathConstants.NODE);
		}
		catch (final XPathExpressionException ex)
		{
			throw new XMLException(ex);
		}
	}

	/**
	 * @throws XMLException
	 */
	public static List<Node> findNodes(final String xPathExpression, final Node searchScope) throws XMLException
	{
		Assert.argumentNotNull("xPathExpression", xPathExpression);
		Assert.argumentNotNull("searchScope", searchScope);

		try
		{
			final XPath xpath = XPATH_FACTORY.newXPath();
			xpath.setNamespaceContext(NAMESPACE_CONTEXT);

			return DOMUtils.nodeListToList((NodeList) xpath.evaluate(xPathExpression, searchScope,
					XPathConstants.NODESET));
		}
		catch (final XPathExpressionException ex)
		{
			throw new XMLException(ex);
		}
	}

	public static List<Attr> getAttributes(final Element element)
	{
		Assert.argumentNotNull("element", element);

		final NamedNodeMap nodeMap = element.getAttributes();
		final List<Attr> result = CollectionUtils.newArrayList(nodeMap.getLength());
		for (int i = 0, l = nodeMap.getLength(); i < l; i++)
			result.add((Attr) nodeMap.item(i));
		return result;
	}

	public static List<Node> getChildNodes(final Node node)
	{
		return nodeListToList(node.getChildNodes());
	}

	/**
	 * @throws XMLException
	 */
	public static Node getFirstChild(final Node node) throws XMLException
	{
		Assert.argumentNotNull("node", node);

		return findNode("*", node);
	}

	public static List<Attr> getIdAttributes(final Element element)
	{
		Assert.argumentNotNull("element", element);

		final NamedNodeMap nodeMap = element.getAttributes();
		final List<Attr> result = CollectionUtils.newArrayList(nodeMap.getLength());
		for (int i = 0, l = nodeMap.getLength(); i < l; i++)
		{
			final Attr attr = (Attr) nodeMap.item(i);
			if (attr.isId()) result.add((Attr) nodeMap.item(i));
		}
		return result;
	}

	private static List<Attr> getIdAttributes(final Element element, final XPathAttributeConfiguration config)
	{
		Assert.argumentNotNull("element", element);

		final NamedNodeMap nodeMap = element.getAttributes();
		final List<Attr> result = CollectionUtils.newArrayList(nodeMap.getLength());
		if (config.useSchemaIdAttributes) for (int i = 0, l = nodeMap.getLength(); i < l; i++)
		{
			final Attr attr = (Attr) nodeMap.item(i);
			if (attr.isId()) result.add((Attr) nodeMap.item(i));
		}
		if (result.size() == 0 && config.idAttributesByXMLTagName.size() > 0)
		{
			for (final String idAttrName : config.getIdAttributesForXMLTagName(element.getTagName()))
				if (element.hasAttribute(idAttrName))
				{
					result.add(element.getAttributeNode(idAttrName));
					break;
				}
			if (result.size() == 0) for (final String idAttrName : config.getIdAttributesForXMLTagName("*"))
				if (element.hasAttribute(idAttrName))
				{
					result.add(element.getAttributeNode(idAttrName));
					break;
				}
		}
		return result;
	}

	/**
	 * Returns a sorted map containing the XPath expression of all attributes as entry key and their value as entry value.
	 */
	public static SortedMap<String, XPathAttribute> getXPathAttributes(final Element element)
	{
		Assert.argumentNotNull("element", element);

		final SortedMap<String, XPathAttribute> valuesByXPath = new TreeMap<String, XPathAttribute>();
		getXPathAttributes(element, XPathAttributeConfiguration.INSTANCE, "", valuesByXPath);
		return valuesByXPath;
	}

	/**
	 * Returns a sorted map containing the XPath expression of all attributes as entry key and their value as entry value.
	 */
	public static SortedMap<String, XPathAttribute> getXPathAttributes(final Element element,
			final XPathAttributeConfiguration config)
	{
		Assert.argumentNotNull("element", element);
		Assert.argumentNotNull("config", config);

		final SortedMap<String, XPathAttribute> valuesByXPath = new TreeMap<String, XPathAttribute>();
		getXPathAttributes(element, config, "", valuesByXPath);
		return valuesByXPath;
	}

	private static void getXPathAttributes(final Element element, final XPathAttributeConfiguration config,
			final CharSequence parentXPath, final Map<String, XPathAttribute> valuesByXPath)
	{
		/*
		 * build the xPath of the current element
		 */
		final StringBuilder xPath = new StringBuilder(parentXPath);
		xPath.append('/');
		xPath.append(element.getTagName());
		final List<Attr> attrs = getIdAttributes(element, config);
		if (attrs.size() > 0)
		{
			xPath.append('[');
			boolean isFirst = true;
			for (final Attr idAttribute : getIdAttributes(element, config))
			{
				if (isFirst)
					isFirst = false;
				else
					xPath.append(" and ");
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
		for (final Attr attr : getAttributes(element))
		{
			final String attrXPath = xPath + "/@" + attr.getName();
			valuesByXPath.put(attrXPath, new XPathAttribute(attr.getName(), attr.getValue(), attrXPath));
		}
		/*
		 * iterate child elements
		 */
		boolean foundTextNode = false;
		for (final Node child : DOMUtils.nodeListToList(element.getChildNodes()))
			if (config.recursive && child instanceof Element)
				getXPathAttributes((Element) child, config, xPath, valuesByXPath);
			else if ("#text".equals(child.getNodeName()))
			{
				foundTextNode = true;
				final String nodeValue = child.getNodeValue().trim();
				if (nodeValue.length() > 0)
				{
					final String attrXPath = xPath + "/text()";
					valuesByXPath.put(attrXPath, new XPathAttribute("#text", nodeValue, attrXPath));
				}
			}
		if (!foundTextNode)
		{
			final String attrXPath = xPath + "/text()";
			valuesByXPath.put(attrXPath, new XPathAttribute("#text", null, attrXPath));
		}
	}

	/**
	 * @return the imported node object
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Node> T importNode(final T nodeToImport, final Node newParentNode)
	{
		Assert.argumentNotNull("nodeToImport", nodeToImport);
		Assert.argumentNotNull("newParentNode", newParentNode);

		return (T) newParentNode.appendChild(newParentNode.getOwnerDocument().importNode(nodeToImport, true));
	}

	public static <T extends Node> List<T> importNodes(final Collection<T> nodesToImport, final Node newParentNode)
	{
		Assert.argumentNotNull("nodesToImport", nodesToImport);
		Assert.argumentNotNull("newParentNode", newParentNode);

		return importNodes(nodesToImport, newParentNode, null);
	}

	/**
	 * @param insertBeforeNode optional, may be null
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Node> List<T> importNodes(final Collection<T> nodesToImport, final Node newParentNode,
			final Node insertBeforeNode)
	{
		Assert.argumentNotNull("nodesToImport", nodesToImport);
		Assert.argumentNotNull("newParentNode", newParentNode);

		final List<T> newNodes = new ArrayList<T>(nodesToImport.size());
		for (final T nodeToImport : nodesToImport)
		{
			final T importedNode = (T) newParentNode.appendChild(newParentNode.getOwnerDocument().importNode(
					nodeToImport, true));
			newNodes.add(importedNode);
			if (insertBeforeNode != null) newParentNode.insertBefore(importedNode, insertBeforeNode);
		}
		return newNodes;
	}

	public static List<Node> importNodes(final NodeList nodesToImport, final Node newParentNode)
	{
		Assert.argumentNotNull("nodesToImport", nodesToImport);
		Assert.argumentNotNull("newParentNode", newParentNode);

		return importNodes(nodeListToList(nodesToImport), newParentNode, null);
	}

	/**
	 * @param insertBeforeNode optional, may be null
	 */
	public static List<Node> importNodes(final NodeList nodesToImport, final Node newParentNode,
			final Node insertBeforeNode)
	{
		Assert.argumentNotNull("nodesToImport", nodesToImport);
		Assert.argumentNotNull("newParentNode", newParentNode);

		return importNodes(nodeListToList(nodesToImport), newParentNode, insertBeforeNode);
	}

	public static Node[] nodeListToArray(final NodeList nodes)
	{
		Assert.argumentNotNull("nodes", nodes);

		final Node[] result = new Node[nodes.getLength()];
		for (int i = 0, l = nodes.getLength(); i < l; i++)
			result[i] = nodes.item(i);
		return result;
	}

	public static List<Node> nodeListToList(final NodeList nodes)
	{
		Assert.argumentNotNull("nodes", nodes);

		final List<Node> result = CollectionUtils.newArrayList(nodes.getLength());

		for (int i = 0, l = nodes.getLength(); i < l; i++)
			result.add(nodes.item(i));
		return result;
	}

	/**
	 * Parses the given file and returns a org.w3c.dom.Document.
	 * @throws IOException
	 * @throws XMLException
	 */
	public static Document parseFile(final File xmlFile) throws IOException, XMLException
	{
		Assert.argumentNotNull("xmlFile", xmlFile);
		Assert.isFileReadable(xmlFile);

		return parseInputSource(new InputSource(xmlFile.toURI().toASCIIString()), xmlFile.getAbsolutePath(), null,
				(File[]) null);
	}

	/**
	 * Parses the given file and returns a org.w3c.dom.Document.
	 * @param xmlSchemaFiles the XML schema files to validate against, the schema files are also required to apply default values
	 * @throws IOException
	 * @throws XMLException
	 */
	public static Document parseFile(final File xmlFile, final File... xmlSchemaFiles) throws IOException, XMLException
	{
		Assert.argumentNotNull("xmlFile", xmlFile);
		Assert.isFileReadable(xmlFile);

		return parseInputSource(new InputSource(xmlFile.toURI().toASCIIString()), xmlFile.getAbsolutePath(), null,
				xmlSchemaFiles);
	}

	/**
	 * Parses the given file and returns a org.w3c.dom.Document.
	 * @param rootElementNamespace optional, may be null
	 * @param xmlSchemaFiles the XML schema files to validate against, the schema files are also required to apply default values
	 * @throws IOException
	 * @throws XMLException
	 */
	public static Document parseFile(final File xmlFile, final String rootElementNamespace,
			final File... xmlSchemaFiles) throws IOException, XMLException
	{
		Assert.argumentNotNull("xmlFile", xmlFile);
		Assert.isFileReadable(xmlFile);

		return parseInputSource(new InputSource(xmlFile.toURI().toASCIIString()), xmlFile.getAbsolutePath(),
				rootElementNamespace, xmlSchemaFiles);
	}

	/**
	 * Parses the content of given input source and returns a org.w3c.dom.Document.
	 * @param input the input to parse
	 * @param inputId an identifier / label for the input source, e.g. a file name
	 * @param rootElementNamespace optional, may be null
	 * @param xmlSchemaFiles the XML schema files to validate against, the schema files are also required to apply default values
	 * @throws IOException
	 * @throws XMLException
	 */
	public static Document parseInputSource(final InputSource input, final String inputId,
			final String rootElementNamespace, final File... xmlSchemaFiles) throws IOException, XMLException
	{
		Assert.argumentNotNull("input", input);

		try
		{
			LOG.debug("Parsing %s", inputId);

			// IBM JDK: org.apache.xerces.jaxp.DocumentBuilderFactoryImpl
			// Sun JDK: com.sun.org.apache.xerces.jaxp.DocumentBuilderFactoryImpl
			final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

			domFactory.setCoalescing(true);
			domFactory.setIgnoringComments(false);
			domFactory.setXIncludeAware(true); // domFactory.setFeature("http://apache.org/xml/features/xinclude", true);

			DocumentBuilder domBuilder;
			Document domDocument;

			// associate schema to the XML    
			if (xmlSchemaFiles == null || xmlSchemaFiles.length == 0)
			{
				domBuilder = domFactory.newDocumentBuilder();
				domDocument = domBuilder.parse(input);
			}
			else
			{
				// enabling JAXP validation
				domFactory.setNamespaceAware(true); // domFactory.setFeature("http://xml.org/sax/features/namespaces", true);
				domFactory.setValidating(true);

				domFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
						XMLConstants.W3C_XML_SCHEMA_NS_URI);
				domFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", xmlSchemaFiles);
				domFactory.setFeature("http://apache.org/xml/features/honour-all-schemaLocations", true);

				domBuilder = domFactory.newDocumentBuilder();
				final SAXParseExceptionHandler errorHandler = new SAXParseExceptionHandler();
				domBuilder.setErrorHandler(errorHandler);
				domDocument = domBuilder.parse(input);
				final Element domRoot = domDocument.getDocumentElement();

				// remove any schema location declarations
				domRoot.removeAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation");

				// if a rootElementNamespace is provided and no namespace is declared or does not match, then add/change the namespace and reparse the file
				final String ns = domRoot.getNamespaceURI();
				if (rootElementNamespace != null && !rootElementNamespace.equals(ns))
				{
					// set the namespace
					//domDocument.renameNode(domRoot, rootElementNamespace, domRoot())
					// workaround since renameNode is ignored by the TransfomerFactory for some reason:
					if (StringUtils.isNotBlank(domRoot.getPrefix()))
						domRoot.setAttribute("xmlns:" + domRoot.getPrefix(), rootElementNamespace);
					else
						domRoot.setAttribute("xmlns", rootElementNamespace);

					// reparse the file with the new namespace
					errorHandler.violations.clear();
					domDocument = domBuilder.parse(new InputSource(new StringReader(renderToString(domDocument))));
				}

				Assert.isTrue(errorHandler.violations.size() == 0,
						errorHandler.violations.size() + " XML schema violation(s) detected in [" + inputId
								+ "]:\n\n => " + StringUtils.join(errorHandler.violations, "\n => "));
			}
			return domDocument;
		}
		catch (final ParserConfigurationException ex)
		{
			throw new XMLException(ex);
		}
		catch (final SAXException ex)
		{
			throw new XMLException(ex);
		}
	}

	/**
	 * Parses the given string and returns a org.w3c.dom.Document.
	 * @param input the input to parse
	 * @param inputId an identifier / label for the input source, e.g. a file name
	 * @throws IOException
	 * @throws XMLException
	 */
	public static Document parseString(final String input, final String inputId) throws IOException, XMLException
	{
		Assert.argumentNotNull("input", input);

		return parseInputSource(new InputSource(new StringReader(input)), inputId, null, (File[]) null);
	}

	/**
	 * Parses the given string and returns a org.w3c.dom.Document.
	 * @param input the input to parse
	 * @param inputId an identifier / label for the input source, e.g. a file name
	 * @param rootElementNamespace optional, may be null
	 * @param xmlSchemaFiles the XML schema files to validate against, the schema files are also required to apply default values
	 * @throws IOException
	 * @throws XMLException
	 */
	public static Document parseString(final String input, final String inputId, final String rootElementNamespace,
			final File... xmlSchemaFiles) throws IOException, XMLException
	{
		Assert.argumentNotNull("input", input);

		return parseInputSource(new InputSource(new StringReader(input)), inputId, rootElementNamespace, xmlSchemaFiles);
	}

	/**
	 * Registers a namespace for the XPath Expression Engine
	 */
	public static void registerNamespace(final String namespaceURI, final String prefix)
	{
		Assert.argumentNotNull("namespaceURI", namespaceURI);
		Assert.argumentNotNull("prefix", prefix);

		NAMESPACE_CONTEXT.registerNamespace(namespaceURI, prefix);
	}

	/**
	 * @throws XMLException
	 */
	public static String renderToString(final Document domDocument) throws XMLException
	{
		Assert.argumentNotNull("domDocument", domDocument);

		try
		{
			final StringWriter sw = new StringWriter();
			final Transformer transformer = TRANSFORMER_FACTORY.newTransformer();

			// http://xerces.apache.org/xerces2-j/javadocs/api/javax/xml/transform/OutputKeys.html
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.transform(new DOMSource(domDocument), new StreamResult(sw));
			return sw.toString();
		}
		catch (final TransformerException ex)
		{
			throw new XMLException(ex);
		}
	}

	/**
	 * @throws IOException
	 * @throws XMLException
	 */
	public static void saveToFile(final Document domDocument, final File file) throws IOException, XMLException
	{
		Assert.argumentNotNull("domDocument", domDocument);
		Assert.argumentNotNull("file", file);

		try
		{
			final FileWriter fw = new FileWriter(file);
			final Transformer transformer = TRANSFORMER_FACTORY.newTransformer();

			// http://xerces.apache.org/xerces2-j/javadocs/api/javax/xml/transform/OutputKeys.html
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.transform(new DOMSource(domDocument), new StreamResult(fw));

			fw.flush();
			fw.close();
		}
		catch (final TransformerException ex)
		{
			throw new XMLException(ex);
		}
	}

	/**
	 * @throws IOException
	 * @throws XMLException
	 */
	public static void saveToFileAfterBackup(final Document domDocument, final File file) throws IOException,
			XMLException
	{
		Assert.argumentNotNull("domDocument", domDocument);
		Assert.argumentNotNull("file", file);

		FileUtils.backupFile(file);

		saveToFile(domDocument, file);
	}

	protected DOMUtils()
	{
		super();
	}
}