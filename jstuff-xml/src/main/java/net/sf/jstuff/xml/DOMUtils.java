/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2013 Sebastian
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

import static net.sf.jstuff.core.collection.CollectionUtils.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringReader;
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

import net.sf.jstuff.core.Logger;
import net.sf.jstuff.core.StringUtils;
import net.sf.jstuff.core.collection.CollectionUtils;
import net.sf.jstuff.core.collection.MapWithLists;
import net.sf.jstuff.core.io.FileUtils;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

import org.apache.commons.lang3.ObjectUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class DOMUtils
{
	private static final class NamespaceContextImpl implements NamespaceContext
	{
		private final Map<String, LinkedHashSet<String>> namespaceURIsByPrefix = newHashMap(2);
		private final Map<String, LinkedHashSet<String>> prefixesByNamespaceURI = newHashMap(2);

		public String getNamespaceURI(final String prefix)
		{
			if (namespaceURIsByPrefix.containsKey(prefix)) return namespaceURIsByPrefix.get(prefix).iterator().next();
			return XMLConstants.NULL_NS_URI;
		}

		public String getPrefix(final String namespaceURI)
		{
			return prefixesByNamespaceURI.containsKey(namespaceURI) ? prefixesByNamespaceURI.get(namespaceURI).iterator().next() : null;
		}

		public Iterator<String> getPrefixes(final String namespaceURI)
		{
			return prefixesByNamespaceURI.containsKey(namespaceURI) ? prefixesByNamespaceURI.get(namespaceURI).iterator() : null;
		}

		public void registerNamespace(final String namespaceURI, final String prefix)
		{
			if (!namespaceURIsByPrefix.containsKey(prefix)) namespaceURIsByPrefix.put(prefix, new LinkedHashSet<String>(2));
			namespaceURIsByPrefix.get(prefix).add(namespaceURI);

			if (!prefixesByNamespaceURI.containsKey(namespaceURI)) prefixesByNamespaceURI.put(namespaceURI, new LinkedHashSet<String>(2));
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

	public static class XPathNode implements Serializable
	{
		private static final long serialVersionUID = 1L;

		public final String name;
		public final String value;
		public final String xPath;

		public XPathNode(final String name, final String value, final String xPath)
		{
			this.name = name;
			this.value = value;
			this.xPath = xPath;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object obj)
		{
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			final XPathNode other = (XPathNode) obj;

			if (!ObjectUtils.equals(name, other.name)) return false;
			if (!ObjectUtils.equals(value, other.value)) return false;
			if (!ObjectUtils.equals(xPath, other.xPath)) return false;
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + (name == null ? 0 : name.hashCode());
			result = prime * result + (value == null ? 0 : value.hashCode());
			result = prime * result + (xPath == null ? 0 : xPath.hashCode());
			return result;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString()
		{
			return value;
		}
	}

	public static class XPathNodeConfiguration implements Serializable, Cloneable
	{
		private static final long serialVersionUID = 1L;
		private static final XPathNodeConfiguration INTERNAL_SHARED_INSTANCE = new XPathNodeConfiguration();

		public boolean recursive = true;
		public boolean useSchemaIdAttributes = true;
		public final MapWithLists<String, String> idAttributesByXMLTagName = new MapWithLists<String, String>();

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected XPathNodeConfiguration clone() throws CloneNotSupportedException
		{
			final XPathNodeConfiguration clone = new XPathNodeConfiguration();
			clone.recursive = recursive;
			clone.useSchemaIdAttributes = useSchemaIdAttributes;
			clone.idAttributesByXMLTagName.putAll(idAttributesByXMLTagName);
			return clone;
		}

		public List<String> getIdAttributesForXMLTagName(final String xmlTagName)
		{
			return idAttributesByXMLTagName.getNullSafe(xmlTagName);
		}
	}

	private static final EntityResolver NOREMOTE_DTD_RESOLVER = new EntityResolver()
		{
			@SuppressWarnings("hiding")
			private final Logger LOG = Logger.create();

			public InputSource resolveEntity(final String schemaId, final String schemaLocation) throws SAXException, IOException
			{
				if (!schemaLocation.startsWith("file://"))
				{
					LOG.debug("Ignoring DTD [%s] [%s]", schemaId, schemaLocation);
					return new InputSource(new StringReader(""));
				}
				return null;
			}
		};

	private static final Logger LOG = Logger.create();

	protected static final NamespaceContextImpl NAMESPACE_CONTEXT = new NamespaceContextImpl();
	protected static final ThreadLocal<TransformerFactory> TRANSFORMER_FACTORY = new ThreadLocal<TransformerFactory>()
		{
			protected TransformerFactory initialValue()
			{
				return TransformerFactory.newInstance();
			}
		};
	protected static final ThreadLocal<XPathFactory> XPATH_FACTORY = new ThreadLocal<XPathFactory>()
		{
			protected XPathFactory initialValue()
			{
				return XPathFactory.newInstance();
			}
		};

	private static List<Attr> _getIdAttributes(final Element element, final XPathNodeConfiguration config)
	{
		Args.notNull("element", element);

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

	private static void _getXPathNodes(final Element element, final XPathNodeConfiguration config, final CharSequence parentXPath,
			final Map<String, XPathNode> valuesByXPath)
	{
		/*
		 * build the xPath of the current element
		 */
		final StringBuilder xPath = new StringBuilder(parentXPath);
		xPath.append('/');
		xPath.append(element.getTagName());
		final List<Attr> attrs = _getIdAttributes(element, config);
		if (attrs.size() > 0)
		{
			xPath.append('[');
			boolean isFirst = true;
			for (final Attr idAttribute : _getIdAttributes(element, config))
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
			valuesByXPath.put(attrXPath, new XPathNode(attr.getName(), attr.getValue(), attrXPath));
		}
		/*
		 * iterate child elements
		 */
		boolean foundTextNode = false;
		for (final Node child : DOMUtils.nodeListToList(element.getChildNodes()))
			if (config.recursive && child instanceof Element)
				_getXPathNodes((Element) child, config, xPath, valuesByXPath);
			else if ("#text".equals(child.getNodeName()))
			{
				foundTextNode = true;
				final String nodeValue = child.getNodeValue().trim();
				if (nodeValue.length() > 0)
				{
					final String attrXPath = xPath + "/text()";
					valuesByXPath.put(attrXPath, new XPathNode("#text", nodeValue, attrXPath));
				}
			}
		if (!foundTextNode)
		{
			final String attrXPath = xPath + "/text()";
			valuesByXPath.put(attrXPath, new XPathNode("#text", null, attrXPath));
		}
	}

	/**
	 * @throws XMLException
	 */
	public static Node findNode(final String xPathExpression, final Node searchScope) throws XMLException
	{
		Args.notNull("xPathExpression", xPathExpression);

		try
		{
			final XPath xPath = XPATH_FACTORY.get().newXPath();
			xPath.setNamespaceContext(NAMESPACE_CONTEXT);

			return (Node) xPath.evaluate(xPathExpression, searchScope, XPathConstants.NODE);
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
		Args.notNull("xPathExpression", xPathExpression);
		Args.notNull("searchScope", searchScope);

		try
		{
			final XPath xPath = XPATH_FACTORY.get().newXPath();
			xPath.setNamespaceContext(NAMESPACE_CONTEXT);

			return DOMUtils.nodeListToList((NodeList) xPath.evaluate(xPathExpression, searchScope, XPathConstants.NODESET));
		}
		catch (final XPathExpressionException ex)
		{
			throw new XMLException(ex);
		}
	}

	public static List<Attr> getAttributes(final Element element)
	{
		Args.notNull("element", element);

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

	public static List<Node> getElementsByTagName(final Element element, final String tagName)
	{
		Args.notNull("element", element);
		Args.notNull("tagName", tagName);

		return nodeListToList(element.getElementsByTagName(tagName));
	}

	/**
	 * @throws XMLException
	 */
	public static Node getFirstChild(final Node node) throws XMLException
	{
		Args.notNull("node", node);

		return findNode("*", node);
	}

	public static List<Attr> getIdAttributes(final Element element)
	{
		Args.notNull("element", element);

		final NamedNodeMap nodeMap = element.getAttributes();
		final List<Attr> result = CollectionUtils.newArrayList(nodeMap.getLength());
		for (int i = 0, l = nodeMap.getLength(); i < l; i++)
		{
			final Attr attr = (Attr) nodeMap.item(i);
			if (attr.isId()) result.add((Attr) nodeMap.item(i));
		}
		return result;
	}

	/**
	 * Returns a sorted map containing the XPath expression of all attributes as entry key and their value as entry value.
	 */
	public static SortedMap<String, XPathNode> getXPathNodes(final Element element)
	{
		Args.notNull("element", element);

		final SortedMap<String, XPathNode> valuesByXPath = new TreeMap<String, XPathNode>();
		_getXPathNodes(element, XPathNodeConfiguration.INTERNAL_SHARED_INSTANCE, "", valuesByXPath);
		return valuesByXPath;
	}

	/**
	 * Returns a sorted map containing the XPath expression of all attributes as entry key and their value as entry value.
	 */
	public static SortedMap<String, XPathNode> getXPathNodes(final Element element, final XPathNodeConfiguration config)
	{
		Args.notNull("element", element);
		Args.notNull("config", config);

		final SortedMap<String, XPathNode> valuesByXPath = new TreeMap<String, XPathNode>();
		_getXPathNodes(element, config, "", valuesByXPath);
		return valuesByXPath;
	}

	/**
	 * @return the imported node object
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Node> T importNode(final T nodeToImport, final Node newParentNode)
	{
		Args.notNull("nodeToImport", nodeToImport);
		Args.notNull("newParentNode", newParentNode);

		return (T) newParentNode.appendChild(newParentNode.getOwnerDocument().importNode(nodeToImport, true));
	}

	public static <T extends Node> List<T> importNodes(final Collection<T> nodesToImport, final Node newParentNode)
	{
		Args.notNull("nodesToImport", nodesToImport);
		Args.notNull("newParentNode", newParentNode);

		return importNodes(nodesToImport, newParentNode, null);
	}

	/**
	 * @param insertBeforeNode optional, may be null
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Node> List<T> importNodes(final Collection<T> nodesToImport, final Node newParentNode,
			final Node insertBeforeNode)
	{
		Args.notNull("nodesToImport", nodesToImport);
		Args.notNull("newParentNode", newParentNode);

		final List<T> newNodes = new ArrayList<T>(nodesToImport.size());
		for (final T nodeToImport : nodesToImport)
		{
			final T importedNode = (T) newParentNode.appendChild(newParentNode.getOwnerDocument().importNode(nodeToImport, true));
			newNodes.add(importedNode);
			if (insertBeforeNode != null) newParentNode.insertBefore(importedNode, insertBeforeNode);
		}
		return newNodes;
	}

	public static List<Node> importNodes(final NodeList nodesToImport, final Node newParentNode)
	{
		Args.notNull("nodesToImport", nodesToImport);
		Args.notNull("newParentNode", newParentNode);

		return importNodes(nodeListToList(nodesToImport), newParentNode, null);
	}

	/**
	 * @param insertBeforeNode optional, may be null
	 */
	public static List<Node> importNodes(final NodeList nodesToImport, final Node newParentNode, final Node insertBeforeNode)
	{
		Args.notNull("nodesToImport", nodesToImport);
		Args.notNull("newParentNode", newParentNode);

		return importNodes(nodeListToList(nodesToImport), newParentNode, insertBeforeNode);
	}

	public static Node[] nodeListToArray(final NodeList nodes)
	{
		Args.notNull("nodes", nodes);

		final Node[] result = new Node[nodes.getLength()];
		for (int i = 0, l = nodes.getLength(); i < l; i++)
			result[i] = nodes.item(i);
		return result;
	}

	public static List<Node> nodeListToList(final NodeList nodes)
	{
		Args.notNull("nodes", nodes);

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
		Args.notNull("xmlFile", xmlFile);
		Assert.isFileReadable(xmlFile);

		return parseInputSource(new InputSource(xmlFile.toURI().toASCIIString()), xmlFile.getAbsolutePath(), null, (File[]) null);
	}

	/**
	 * Parses the given file and returns a org.w3c.dom.Document.
	 * @param xmlSchemaFiles the XML schema files to validate against, the schema files are also required to apply default values
	 * @throws IOException
	 * @throws XMLException
	 */
	public static Document parseFile(final File xmlFile, final File... xmlSchemaFiles) throws IOException, XMLException
	{
		Args.notNull("xmlFile", xmlFile);
		Assert.isFileReadable(xmlFile);

		return parseInputSource(new InputSource(xmlFile.toURI().toASCIIString()), xmlFile.getAbsolutePath(), null, xmlSchemaFiles);
	}

	/**
	 * Parses the given file and returns a org.w3c.dom.Document.
	 * @param rootNamespace optional, may be null
	 * @param xmlSchemaFiles the XML schema files to validate against, the schema files are also required to apply default values
	 * @throws IOException
	 * @throws XMLException
	 */
	public static Document parseFile(final File xmlFile, final String rootNamespace, final File... xmlSchemaFiles) throws IOException,
			XMLException
	{
		Args.notNull("xmlFile", xmlFile);
		Assert.isFileReadable(xmlFile);

		return parseInputSource(new InputSource(xmlFile.toURI().toASCIIString()), xmlFile.getAbsolutePath(), rootNamespace, xmlSchemaFiles);
	}

	/**
	 * Parses the content of given input source and returns a org.w3c.dom.Document.
	 * @param input the input to parse
	 * @param inputId an identifier / label for the input source, e.g. a file name
	 * @param rootNamespace optional, may be null
	 * @param xmlSchemaFiles the XML schema files to validate against, the schema files are also required to apply default values
	 * @throws IOException
	 * @throws XMLException
	 */
	public static Document parseInputSource(final InputSource input, final String inputId, final String rootNamespace,
			final File... xmlSchemaFiles) throws IOException, XMLException
	{
		Args.notNull("input", input);

		try
		{
			LOG.debug("Parsing [%s]...", inputId);

			// IBM JDK: org.apache.xerces.jaxp.DocumentBuilderFactoryImpl
			// Sun JDK: com.sun.org.apache.xerces.jaxp.DocumentBuilderFactoryImpl
			final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

			domFactory.setCoalescing(true);
			domFactory.setIgnoringComments(false);
			domFactory.setXIncludeAware(true); // domFactory.setFeature("http://apache.org/xml/features/xinclude", true);

			// if no XML schema is provided simply parse without validation
			if (xmlSchemaFiles == null || xmlSchemaFiles.length == 0)
			{
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

			// if a rootNamespace is provided and no namespace is declared or does not match, then add/change the namespace and re-parse the file
			final String ns = domRoot.getNamespaceURI();
			if (rootNamespace != null && !rootNamespace.equals(ns))
			{
				LOG.debug("Fixing root namespace...");

				domRoot.setAttribute("jstuffNS", rootNamespace);
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
							node = domDocument.renameNode(node, rootNamespace, node.getNodeName());
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

			Assert.isTrue(errorHandler.violations.size() == 0, errorHandler.violations.size() + " XML schema violation(s) detected in ["
					+ inputId + "]:\n\n => " + StringUtils.join(errorHandler.violations, "\n => "));
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
		Args.notNull("input", input);

		return parseInputSource(new InputSource(new StringReader(input)), inputId, null, (File[]) null);
	}

	/**
	 * Parses the given string and returns a org.w3c.dom.Document.
	 * @param input the input to parse
	 * @param inputId an identifier / label for the input source, e.g. a file name
	 * @param rootNamespace optional, may be null
	 * @param xmlSchemaFiles the XML schema files to validate against, the schema files are also required to apply default values
	 * @throws IOException
	 * @throws XMLException
	 */
	public static Document parseString(final String input, final String inputId, final String rootNamespace, final File... xmlSchemaFiles)
			throws IOException, XMLException
	{
		Args.notNull("input", input);

		return parseInputSource(new InputSource(new StringReader(input)), inputId, rootNamespace, xmlSchemaFiles);
	}

	/**
	 * Registers a namespace for the XPath Expression Engine
	 */
	public static void registerNamespace(final String namespaceURI, final String prefix)
	{
		Args.notNull("namespaceURI", namespaceURI);
		Args.notNull("prefix", prefix);

		NAMESPACE_CONTEXT.registerNamespace(namespaceURI, prefix);
	}

	/**
	 * @return true if the node was removed and false if the node did not have a parent node
	 * @exception DOMException
	 *   <li>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.</li>
	 *   <li>NOT_FOUND_ERR: Raised if <code>oldChild</code> is not a child of this node.</li>
	 *   <li>NOT_SUPPORTED_ERR: if this node is of type <code>Document</code>, this exception might be raised if the DOM implementation doesn't support the removal of the <code>DocumentType</code> child or the <code>Element</code> child.</li>
	 */
	public static boolean removeNode(final Node node) throws DOMException
	{
		final Node parent = node.getParentNode();
		if (parent == null) return false;
		parent.removeChild(node);
		return true;
	}

	/**
	 * @throws IOException
	 * @throws XMLException
	 */
	public static void saveToFile(final Document domDocument, final File targetFile) throws IOException, XMLException
	{
		Args.notNull("domDocument", domDocument);
		Args.notNull("targetFile", targetFile);

		try
		{
			final Transformer transformer = TRANSFORMER_FACTORY.get().newTransformer();

			// http://xerces.apache.org/xerces2-j/javadocs/api/javax/xml/transform/OutputKeys.html
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

			final FileWriter fw = new FileWriter(targetFile);
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
	 * First creates a backup of the targetFile and then saves the document to the targetFile.
	 *
	 * @throws IOException
	 * @throws XMLException
	 */
	public static void saveToFileAfterBackup(final Document domDocument, final File targetFile) throws IOException, XMLException
	{
		Args.notNull("domDocument", domDocument);
		Args.notNull("targetFile", targetFile);

		FileUtils.backupFile(targetFile);

		saveToFile(domDocument, targetFile);
	}

	public static String toXML(final Node domNode) throws XMLException
	{
		Args.notNull("domNode", domNode);

		return toXML(domNode, true, true);
	}

	public static String toXML(final Node domNode, final boolean outputXMLDeclaration, final boolean formatPretty) throws XMLException
	{
		Args.notNull("domNode", domNode);

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try
		{
			toXML(domNode, bos, outputXMLDeclaration, formatPretty);
		}
		catch (final IOException e)
		{
			// ignore, never happens
		}
		return bos.toString();
	}

	public static void toXML(final Node domNode, final OutputStream out) throws XMLException, IOException
	{
		Args.notNull("domNode", domNode);
		Args.notNull("out", out);

		toXML(domNode, out, true, true);
	}

	public static void toXML(final Node domNode, final OutputStream out, final boolean outputXMLDeclaration, final boolean formatPretty)
			throws XMLException, IOException
	{
		Args.notNull("domNode", domNode);
		Args.notNull("out", out);

		try
		{
			final Transformer transformer = TRANSFORMER_FACTORY.get().newTransformer();

			// http://xerces.apache.org/xerces2-j/javadocs/api/javax/xml/transform/OutputKeys.html
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.MEDIA_TYPE, "text/xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			if (outputXMLDeclaration)
			{
				// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
				// transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

				// because of a bug in Xalan omitting new line characters after the <?xml...> declaration header, we output the header own our own
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>".getBytes());
				out.write(StringUtils.NEW_LINE.getBytes());
			}
			else
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			if (formatPretty)
			{
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			}
			else
				transformer.setOutputProperty(OutputKeys.INDENT, "no");
			transformer.transform(new DOMSource(domNode), new StreamResult(out));
		}
		catch (final TransformerException ex)
		{
			throw new XMLException(ex);
		}
	}

	protected DOMUtils()
	{
		super();
	}
}