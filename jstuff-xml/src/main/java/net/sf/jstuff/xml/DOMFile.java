/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.xml;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
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

    /**
     * Constructs an instance based on the given XML document.
     * When calling the {@link #save()} method, the XML structure will be written to the <code>targetFile</code>
     */
    public DOMFile(final Document doc, final File targetFile) {
        Args.notNull("doc", doc);
        Args.notNull("targetFile", targetFile);
        domDocument = doc;
        domRoot = doc.getDocumentElement();
        xmlFile = targetFile;
    }

    /**
     * Loads an existing XML file
     */
    public DOMFile(final File xmlFile) throws IOException, XMLException {
        this(xmlFile, null, (File[]) null);
    }

    /**
     * Loads an existing XML file
     */
    public DOMFile(final File xmlFile, final File... xmlSchemaFiles) throws IOException, XMLException {
        this(xmlFile, null, xmlSchemaFiles);
    }

    /**
     * Loads an existing XML file
     */
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
        Args.notNull("childToCreateBefore", childToCreateBefore);
        Assert.isTrue(childToCreateBefore.getOwnerDocument() == domDocument, "[childToCreateBefore] belongs to another DOM document!");

        return DOMUtils.createCommentBefore(commentString, childToCreateBefore);
    }

    /**
     * Creates a new XML element as child of the given parentNode
     */
    public Element createElement(final String xmlTagName, final Node parentNode) {
        Args.notNull("parentNode", parentNode);
        Assert.isTrue(parentNode.getOwnerDocument() == domDocument, "[parentNode] belongs to another DOM document!");

        return DOMUtils.createElement(xmlTagName, parentNode);
    }

    /**
     * Creates a new XML element as child of the given parentNode with the given attributes
     */
    public Element createElement(final String xmlTagName, final Node parentNode, final Map<String, String> elementAttributes) {
        Args.notNull("parentNode", parentNode);
        Assert.isTrue(parentNode.getOwnerDocument() == domDocument, "[parentNode] belongs to another DOM document!");

        return DOMUtils.createElement(xmlTagName, parentNode, elementAttributes);
    }

    public Element createElementBefore(final String xmlTagName, final Node childToCreateBefore) {
        Args.notNull("childToCreateBefore", childToCreateBefore);
        Assert.isTrue(childToCreateBefore.getOwnerDocument() == domDocument, "[childToCreateBefore] belongs to another DOM document!");

        return DOMUtils.createElementBefore(xmlTagName, childToCreateBefore);
    }

    public Element createElementBefore(final String xmlTagName, final Node childToCreateBefore, final Map<String, String> elementAttributes) {
        Args.notNull("childToCreateBefore", childToCreateBefore);
        Assert.isTrue(childToCreateBefore.getOwnerDocument() == domDocument, "[childToCreateBefore] belongs to another DOM document!");

        return DOMUtils.createElementBefore(xmlTagName, childToCreateBefore, elementAttributes);
    }

    public Text createTextNode(final String text, final Node parentNode) {
        Args.notNull("parentNode", parentNode);
        Assert.isTrue(parentNode.getOwnerDocument() == domDocument, "[parentNode] belongs to another DOM document!");

        return DOMUtils.createTextNode(text, parentNode);
    }

    public Text createTextNodeBefore(final String text, final Node childToCreateBefore) {
        Args.notNull("childToCreateBefore", childToCreateBefore);
        Assert.isTrue(childToCreateBefore.getOwnerDocument() == domDocument, "[childToCreateBefore] belongs to another DOM document!");

        return DOMUtils.createTextNodeBefore(text, childToCreateBefore);
    }

    public String evaluate(final String xPathExpression) throws XMLException {
        return DOMUtils.evaluate(xPathExpression, domRoot);
    }

    public String evaluate(final String xPathExpression, final Node searchScope) throws XMLException {
        Args.notNull("searchScope", searchScope);
        Assert.isTrue(searchScope.getOwnerDocument() == domDocument, "[searchScope] belongs to another DOM document!");

        return DOMUtils.evaluate(xPathExpression, searchScope);
    }

    public Node findNode(final String xPathExpression) throws XMLException {
        return findNode(xPathExpression, domRoot);
    }

    public Node findNode(final String xPathExpression, final Node searchScope) throws XMLException {
        Args.notNull("searchScope", searchScope);
        Assert.isTrue(searchScope.getOwnerDocument() == domDocument, "[searchScope] belongs to another DOM document!");

        return DOMUtils.findNode(xPathExpression, searchScope);
    }

    public List<Node> findNodes(final String xPathExpression) throws XMLException {
        return DOMUtils.findNodes(xPathExpression, domRoot);
    }

    public List<Node> findNodes(final String xPathExpression, final Node searchScope) throws XMLException {
        Args.notNull("searchScope", searchScope);
        Assert.isTrue(searchScope.getOwnerDocument() == domDocument, "[searchScope] belongs to another DOM document!");

        return DOMUtils.findNodes(xPathExpression, searchScope);
    }

    /**
     * @param recursive return text content of child nodes
     */
    public String findTextContent(final String xPathExpression, final boolean recursive) throws XMLException {
        return DOMUtils.findTextContent(xPathExpression, domRoot, recursive);
    }

    /**
     * @param recursive return text content of child nodes
     */
    public String findTextContent(final String xPathExpression, final Node searchScope, final boolean recursive) throws XMLException {
        Args.notNull("searchScope", searchScope);
        Assert.isTrue(searchScope.getOwnerDocument() == domDocument, "[searchScope] belongs to another DOM document!");

        return DOMUtils.findTextContent(xPathExpression, searchScope, recursive);
    }

    public List<Attr> getAttributes(final Node node) {
        Args.notNull("node", node);
        Assert.isTrue(node.getOwnerDocument() == domDocument, "[node] belongs to another DOM document!");

        return DOMUtils.getAttributes(node);
    }

    /**
     * @return all direct child nodes of this node.
     */
    public List<Node> getChildNodes(final Node parentNode) {
        Args.notNull("parentNode", parentNode);
        Assert.isTrue(parentNode.getOwnerDocument() == domDocument, "[parentNode] belongs to another DOM document!");

        return DOMUtils.getChildNodes(parentNode);
    }

    public Document getDOMDocument() {
        return domDocument;
    }

    public Element getDOMRoot() {
        return domRoot;
    }

    /**
     * @param tagName The name of the tag to match on. The special value "*" matches all tags.
     * @return all child and sub-child nodes with the given tag name, in document order.
     */
    public <T extends Node> List<T> getElementsByTagName(final Element parentElement, final String tagName) {
        Args.notNull("parentElement", parentElement);
        Assert.isTrue(parentElement.getOwnerDocument() == domDocument, "[parentElement] belongs to another DOM document!");

        return DOMUtils.getElementsByTagName(parentElement, tagName);
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
        Assert.isTrue(parentNode.getOwnerDocument() == domDocument, "[parentNode] belongs to another DOM document!");

        return DOMUtils.getFirstChild(parentNode);
    }

    public List<Attr> getIdAttributes(final Node node) {
        Args.notNull("node", node);
        Assert.isTrue(node.getOwnerDocument() == domDocument, "[node] belongs to another DOM document!");

        return DOMUtils.getIdAttributes(node);
    }

    /**
     * @return the imported node object
     */
    public <T extends Node> T importNode(final T nodeToImport, final Node newParentNode) {
        Args.notNull("newParentNode", newParentNode);
        Assert.isTrue(newParentNode.getOwnerDocument() == domDocument, "[newParentNode] belongs to another DOM document!");

        return DOMUtils.importNode(nodeToImport, newParentNode);
    }

    public <T extends Node> T importNodeBefore(final T nodeToImport, final Node insertBeforeNode) {
        Args.notNull("insertBeforeNode", insertBeforeNode);
        Assert.isTrue(insertBeforeNode.getOwnerDocument() == domDocument, "[insertBeforeNode] belongs to another DOM document!");

        return DOMUtils.importNodeBefore(nodeToImport, insertBeforeNode);
    }

    public <T extends Node> List<T> importNodes(final Collection<T> nodesToImport, final Node newParentNode) {
        Args.notNull("newParentNode", newParentNode);
        Assert.isTrue(newParentNode.getOwnerDocument() == domDocument, "[newParentNode] belongs to another DOM document!");

        return DOMUtils.importNodes(nodesToImport, newParentNode);
    }

    public <T extends Node> List<T> importNodes(final Collection<T> nodesToImport, final Node newParentNode, final Node insertBeforeNode) {
        Args.notNull("newParentNode", newParentNode);
        Assert.isTrue(newParentNode.getOwnerDocument() == domDocument, "[newParentNode] belongs to another DOM document!");

        return DOMUtils.importNodes(nodesToImport, newParentNode, insertBeforeNode);
    }

    /**
     * @return true if the node was removed and false if the node did not have a parent node
     * @exception DOMException
     *                <li>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.</li>
     *                <li>NOT_FOUND_ERR: Raised if <code>oldChild</code> is not a child
     *                of this node.</li>
     *                <li>NOT_SUPPORTED_ERR: if this node is of type <code>Document</code>, this exception might be raised if the DOM
     *                implementation doesn't support the removal of the <code>DocumentType</code> child or the <code>Element</code> child.</li>
     */
    public boolean removeNode(final Node node) throws DOMException {
        Args.notNull("node", node);
        Assert.isTrue(node.getOwnerDocument() == domDocument, "[node] belongs to another DOM document!");

        return DOMUtils.removeNode(node);
    }

    /**
     * @return a list of the removed nodes
     */
    public List<Node> removeNodes(final String xPathExpression) throws XMLException {
        return removeNodes(xPathExpression, domRoot);
    }

    /**
     * @return a list of the removed nodes
     */
    public List<Node> removeNodes(final String xPathExpression, final Node searchScope) throws XMLException {
        Args.notNull("searchScope", searchScope);
        Assert.isTrue(searchScope.getOwnerDocument() == domDocument, "[searchScope] belongs to another DOM document!");

        return DOMUtils.removeNodes(xPathExpression, searchScope);
    }

    public void removeWhiteSpaceNodes() {
        removeWhiteSpaceNodes(domRoot);
    }

    public void removeWhiteSpaceNodes(final Node searchScope) {
        Args.notNull("searchScope", searchScope);
        Assert.isTrue(searchScope.getOwnerDocument() == domDocument, "[searchScope] belongs to another DOM document!");

        DOMUtils.removeWhiteSpaceNodes(searchScope);
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

    /**
     * @param parentNode node whose children will be sorted
     */
    public void sortChildNodes(final Node parentNode, final Comparator<Node> comparator) {
        Args.notNull("parentNode", parentNode);
        Assert.isTrue(parentNode.getOwnerDocument() == domDocument, "[parentNode] belongs to another DOM document!");

        DOMUtils.sortChildNodes(parentNode, comparator);
    }

    /**
     * @param parentNode node whose children will be sorted
     */
    public void sortChildNodesByAttributes(final Node parentNode, final boolean ascending, final String... attributeNames) {
        Args.notNull("parentNode", parentNode);
        Assert.isTrue(parentNode.getOwnerDocument() == domDocument, "[parentNode] belongs to another DOM document!");

        DOMUtils.sortChildNodesByAttributes(parentNode, ascending, attributeNames);
    }

    public String toXML() {
        return DOMUtils.toXML(domDocument, true, true);
    }

    public String toXML(final boolean outputXMLDeclaration, final boolean formatPretty) {
        return DOMUtils.toXML(domDocument, outputXMLDeclaration, formatPretty);
    }
}
