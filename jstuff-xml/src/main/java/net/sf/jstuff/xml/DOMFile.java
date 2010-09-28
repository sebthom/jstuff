/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.jstuff.core.Assert;
import net.sf.jstuff.core.Logger;
import net.sf.jstuff.core.io.FileUtils;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXParseException;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DOMFile
{
	private static final Logger LOG = Logger.get();

	private final Document domDocument;
	private final Element domRoot;
	private final File xmlFile;

	public DOMFile(final File xmlFile) throws IOException, XMLException
	{
		this(xmlFile, null, (File[]) null);
	}

	public DOMFile(final File xmlFile, final File... xmlSchemaFiles) throws IOException, XMLException
	{
		this(xmlFile, null, xmlSchemaFiles);
	}

	public DOMFile(final File xmlFile, final String rootElementNamespace) throws IOException, XMLException
	{
		this(xmlFile, rootElementNamespace, (File[]) null);
	}

	/**
	 * @param rootElementNamespace optional, may be null
	 */
	public DOMFile(final File xmlFile, final String rootElementNamespace, final File... xmlSchemaFiles)
			throws IOException, XMLException
	{
		Assert.argumentNotNull("xmlFile", xmlFile);
		Assert.isFileReadable(xmlFile);

		this.xmlFile = xmlFile;

		try
		{
			domDocument = DOMUtils.parseFile(xmlFile, rootElementNamespace, xmlSchemaFiles);
		}
		catch (final XMLException ex)
		{
			// debug code to analyze "Content is not allowed in prolog."
			if (ex.getCause() instanceof SAXParseException)
				LOG.debug("Failed to parse file %s with content:\n%s", ex, xmlFile.getAbsolutePath(),
						FileUtils.readFileToString(xmlFile));
			throw ex;
		}
		domRoot = domDocument.getDocumentElement();
	}

	public Comment createCommentBefore(final String commentString, final Node childToCreateBefore)
	{
		Assert.argumentNotNull("commentString", commentString);
		Assert.argumentNotNull("childToCreateBefore", childToCreateBefore);

		return (Comment) domDocument.insertBefore(domDocument.createComment(commentString), childToCreateBefore);
	}

	/**
	 * Creates a new XML element as child of the given parentNode
	 */
	public Element createElement(final String xmlTagName, final Node parentNode)
	{
		Assert.argumentNotEmpty("xmlTagName", xmlTagName);
		Assert.argumentNotNull("parentNode", parentNode);

		return createElement(xmlTagName, parentNode, null);
	}

	/**
	 * Creates a new XML element as child of the given parentNode with the given attributes
	 */
	public Element createElement(final String xmlTagName, final Node parentNode,
			final Map<String, String> elementAttributes)
	{
		Assert.argumentNotEmpty("xmlTagName", xmlTagName);
		Assert.argumentNotNull("parentNode", parentNode);

		final Element elem = (Element) parentNode.appendChild(domDocument.createElement(xmlTagName));
		if (elementAttributes != null) for (final Entry<String, String> attr : elementAttributes.entrySet())
			elem.setAttribute(attr.getKey(), attr.getValue());
		return elem;
	}

	public Element createElementBefore(final String xmlTagName, final Node childToCreateBefore)
	{
		Assert.argumentNotEmpty("tagName", xmlTagName);
		Assert.argumentNotNull("childToCreateBefore", childToCreateBefore);

		return createElementBefore(xmlTagName, childToCreateBefore, null);
	}

	public Element createElementBefore(final String xmlTagName, final Node childToCreateBefore,
			final Map<String, String> elementAttributes)
	{
		Assert.argumentNotEmpty("tagName", xmlTagName);
		Assert.argumentNotNull("childToCreateBefore", childToCreateBefore);

		final Element elem = (Element) childToCreateBefore.getParentNode().insertBefore(
				domDocument.createElement(xmlTagName), childToCreateBefore);
		if (elementAttributes != null) for (final Entry<String, String> attr : elementAttributes.entrySet())
			elem.setAttribute(attr.getKey(), attr.getValue());
		return elem;
	}

	public Node findNode(final String xPathExpression) throws XMLException
	{
		Assert.argumentNotEmpty("xPathExpression", xPathExpression);

		return findNode(xPathExpression, domRoot);
	}

	public Node findNode(final String xPathExpression, final Node searchScope) throws XMLException
	{
		Assert.argumentNotEmpty("xPathExpression", xPathExpression);
		Assert.argumentNotNull("searchScope", searchScope);

		return DOMUtils.findNode(xPathExpression, searchScope);
	}

	public List<Node> findNodes(final String xPathExpression) throws XMLException
	{
		Assert.argumentNotEmpty("xPathExpression", xPathExpression);

		return DOMUtils.findNodes(xPathExpression, domRoot);
	}

	public List<Node> findNodes(final String xPathExpression, final Node searchScope) throws XMLException
	{
		Assert.argumentNotEmpty("xPathExpression", xPathExpression);
		Assert.argumentNotNull("searchScope", searchScope);

		return DOMUtils.findNodes(xPathExpression, searchScope);
	}

	public List<Node> getChildNodes(final Node node)
	{
		Assert.argumentNotNull("node", node);

		return DOMUtils.getChildNodes(node);
	}

	public Document getDOMDocument()
	{
		return domDocument;
	}

	public Element getDOMRoot()
	{
		return domRoot;
	}

	public Node getFirstChild(final Node parentNode)
	{
		Assert.argumentNotNull("parentNode", parentNode);

		return DOMUtils.getFirstChild(parentNode);
	}

	@SuppressWarnings("unchecked")
	public <T extends Node> T importNodeBefore(final T nodeToImport, final Node childToImportBefore)
	{
		Assert.argumentNotNull("nodeToImport", nodeToImport);
		Assert.argumentNotNull("childToImportBefore", childToImportBefore);

		final Node importedNode = DOMUtils.importNode(nodeToImport, domRoot);
		return (T) childToImportBefore.getParentNode().insertBefore(importedNode, childToImportBefore);
	}

	public void removeNodes(final String xPathExpression) throws XMLException
	{
		Assert.argumentNotEmpty("xPathExpression", xPathExpression);

		removeNodes(xPathExpression, domRoot);
	}

	public void removeNodes(final String xPathExpression, final Node searchScope) throws XMLException
	{
		Assert.argumentNotEmpty("xPathExpression", xPathExpression);
		Assert.argumentNotNull("searchScope", searchScope);

		for (final Node nodeToRemove : findNodes(xPathExpression, searchScope))
			nodeToRemove.getParentNode().removeChild(nodeToRemove);
	}

	public void save() throws IOException, XMLException
	{
		DOMUtils.saveToFile(domDocument, xmlFile);
	}

	public void saveAs(final File file) throws IOException, XMLException
	{
		Assert.argumentNotNull("file", file);

		DOMUtils.saveToFile(domDocument, file);
	}
}
