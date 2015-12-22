/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
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

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXParseException;

import net.sf.jstuff.core.io.FileUtils;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DOMFile {
    private static final Logger LOG = Logger.create();

    private final Document domDocument;
    private final Element domRoot;
    private final File xmlFile;

    public DOMFile(final File xmlFile) throws IOException, XMLException {
        this(xmlFile, null, (File[]) null);
    }

    public DOMFile(final File xmlFile, final File... xmlSchemaFiles) throws IOException, XMLException {
        this(xmlFile, null, xmlSchemaFiles);
    }

    public DOMFile(final File xmlFile, final String rootNamespace) throws IOException, XMLException {
        this(xmlFile, rootNamespace, (File[]) null);
    }

    /**
     * @param rootNamespace optional, may be null
     */
    public DOMFile(final File xmlFile, final String rootNamespace, final File... xmlSchemaFiles) throws IOException, XMLException {
        Args.notNull("xmlFile", xmlFile);
        Assert.isFileReadable(xmlFile);

        this.xmlFile = xmlFile;

        try {
            domDocument = DOMUtils.parseFile(xmlFile, rootNamespace, xmlSchemaFiles);
        } catch (final XMLException ex) {
            // debug code to analyze "Content is not allowed in prolog."
            if (ex.getCause() instanceof SAXParseException) {
                LOG.debug("Failed to parse file %s with content:\n%s", ex, xmlFile.getAbsolutePath(), FileUtils.readFileToString(xmlFile));
            }
            throw ex;
        }
        domRoot = domDocument.getDocumentElement();
    }

    public Comment createCommentBefore(final String commentString, final Node childToCreateBefore) {
        Args.notNull("commentString", commentString);
        Args.notNull("childToCreateBefore", childToCreateBefore);

        return (Comment) domDocument.insertBefore(domDocument.createComment(commentString), childToCreateBefore);
    }

    /**
     * Creates a new XML element as child of the given parentNode
     */
    public Element createElement(final String xmlTagName, final Node parentNode) {
        Args.notEmpty("xmlTagName", xmlTagName);
        Args.notNull("parentNode", parentNode);

        return createElement(xmlTagName, parentNode, null);
    }

    /**
     * Creates a new XML element as child of the given parentNode with the given attributes
     */
    public Element createElement(final String xmlTagName, final Node parentNode, final Map<String, String> elementAttributes) {
        Args.notEmpty("xmlTagName", xmlTagName);
        Args.notNull("parentNode", parentNode);

        final Element elem = (Element) parentNode.appendChild(domDocument.createElement(xmlTagName));
        if (elementAttributes != null) {
            for (final Entry<String, String> attr : elementAttributes.entrySet()) {
                elem.setAttribute(attr.getKey(), attr.getValue());
            }
        }
        return elem;
    }

    public Element createElementBefore(final String xmlTagName, final Node childToCreateBefore) {
        Args.notEmpty("tagName", xmlTagName);
        Args.notNull("childToCreateBefore", childToCreateBefore);

        return createElementBefore(xmlTagName, childToCreateBefore, null);
    }

    public Element createElementBefore(final String xmlTagName, final Node childToCreateBefore, final Map<String, String> elementAttributes) {
        Args.notEmpty("tagName", xmlTagName);
        Args.notNull("childToCreateBefore", childToCreateBefore);

        final Element elem = (Element) childToCreateBefore.getParentNode().insertBefore(domDocument.createElement(xmlTagName), childToCreateBefore);
        if (elementAttributes != null) {
            for (final Entry<String, String> attr : elementAttributes.entrySet()) {
                elem.setAttribute(attr.getKey(), attr.getValue());
            }
        }
        return elem;
    }

    public Text createTextNode(final String text, final Node parentNode) {
        Args.notNull("text", text);
        Args.notNull("parentNode", parentNode);

        final Text elem = (Text) parentNode.appendChild(domDocument.createTextNode(text));
        return elem;
    }

    public Text createTextNodeBefore(final String text, final Node childToCreateBefore) {
        Args.notNull("text", text);
        Args.notNull("childToCreateBefore", childToCreateBefore);

        final Text elem = (Text) childToCreateBefore.getParentNode().insertBefore(domDocument.createTextNode(text), childToCreateBefore);
        return elem;
    }

    public Node findNode(final String xPathExpression) throws XMLException {
        Args.notEmpty("xPathExpression", xPathExpression);

        return findNode(xPathExpression, domRoot);
    }

    public Node findNode(final String xPathExpression, final Node searchScope) throws XMLException {
        Args.notEmpty("xPathExpression", xPathExpression);
        Args.notNull("searchScope", searchScope);

        return DOMUtils.findNode(xPathExpression, searchScope);
    }

    public List<Node> findNodes(final String xPathExpression) throws XMLException {
        Args.notEmpty("xPathExpression", xPathExpression);

        return DOMUtils.findNodes(xPathExpression, domRoot);
    }

    public List<Node> findNodes(final String xPathExpression, final Node searchScope) throws XMLException {
        Args.notEmpty("xPathExpression", xPathExpression);
        Args.notNull("searchScope", searchScope);

        return DOMUtils.findNodes(xPathExpression, searchScope);
    }

    /**
     * @param recursive return text content of child nodes
     */
    public String findTextContent(final String xPathExpression, final boolean recursive) throws XMLException {
        Args.notNull("xPathExpression", xPathExpression);

        return DOMUtils.findTextContent(xPathExpression, domRoot, recursive);
    }

    /**
     * @param recursive return text content of child nodes
     */
    public String findTextContent(final String xPathExpression, final Node searchScope, final boolean recursive) throws XMLException {
        Args.notNull("xPathExpression", xPathExpression);
        Args.notNull("searchScope", searchScope);

        return DOMUtils.findTextContent(xPathExpression, searchScope, recursive);
    }

    public List<Node> getChildNodes(final Node node) {
        Args.notNull("node", node);

        return DOMUtils.getChildNodes(node);
    }

    public Document getDOMDocument() {
        return domDocument;
    }

    public Element getDOMRoot() {
        return domRoot;
    }

    /**
     * @return the xmlFile
     */
    public File getFile() {
        return xmlFile;
    }

    public String getFilePath() {
        return xmlFile.getAbsolutePath();
    }

    public Node getFirstChild(final Node parentNode) {
        Args.notNull("parentNode", parentNode);

        return DOMUtils.getFirstChild(parentNode);
    }

    @SuppressWarnings("unchecked")
    public <T extends Node> T importNodeBefore(final T nodeToImport, final Node childToImportBefore) {
        Args.notNull("nodeToImport", nodeToImport);
        Args.notNull("childToImportBefore", childToImportBefore);

        final Node importedNode = DOMUtils.importNode(nodeToImport, domRoot);
        return (T) childToImportBefore.getParentNode().insertBefore(importedNode, childToImportBefore);
    }

    /**
     * @return a list of the removed nodes
     */
    public List<Node> removeNodes(final String xPathExpression) throws XMLException {
        Args.notEmpty("xPathExpression", xPathExpression);

        return removeNodes(xPathExpression, domRoot);
    }

    /**
     * @return a list of the removed nodes
     */
    public List<Node> removeNodes(final String xPathExpression, final Node searchScope) throws XMLException {
        Args.notEmpty("xPathExpression", xPathExpression);
        Args.notNull("searchScope", searchScope);

        final List<Node> nodesToRemove = findNodes(xPathExpression, searchScope);

        for (final Node nodeToRemove : nodesToRemove) {
            DOMUtils.removeNode(nodeToRemove);
        }
        return nodesToRemove;
    }

    public void removeWhiteSpaceNodes() {
        removeWhiteSpaceNodes(domRoot);
    }

    public void removeWhiteSpaceNodes(final Node searchScope) {
        removeNodes("text()[normalize-space()='']", searchScope);
    }

    public void save() throws IOException, XMLException {
        DOMUtils.saveToFile(domDocument, xmlFile);
    }

    public void saveAs(final File file) throws IOException, XMLException {
        Args.notNull("file", file);

        DOMUtils.saveToFile(domDocument, file);
    }

    public void saveAs(final String filePath) throws IOException, XMLException {
        Args.notNull("filePath", filePath);

        DOMUtils.saveToFile(domDocument, new File(filePath));
    }

    public String toXML() {
        return DOMUtils.toXML(domDocument, true, true);
    }

    public String toXML(final boolean outputXMLDeclaration, final boolean formatPretty) {
        return DOMUtils.toXML(domDocument, outputXMLDeclaration, formatPretty);
    }
}
