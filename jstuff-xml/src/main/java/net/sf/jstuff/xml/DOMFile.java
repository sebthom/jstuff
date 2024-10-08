/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.xml;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXParseException;

import net.sf.jstuff.core.io.MoreFiles;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.reflection.StackTrace;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
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
    * Loads an existing XML file.
    */
   public DOMFile(final File xmlFile) throws IOException, XMLException {
      this(xmlFile, null, (File[]) null);
   }

   /**
    * Loads an existing XML file.
    */
   public DOMFile(final File xmlFile, final File... xmlSchemaFiles) throws IOException, XMLException {
      this(xmlFile, null, xmlSchemaFiles);
   }

   /**
    * Loads an existing XML file.
    */
   public DOMFile(final File xmlFile, final @Nullable String rootNamespace) throws IOException, XMLException {
      this(xmlFile, rootNamespace, (File[]) null);
   }

   /**
    * @param rootNamespace optional, may be null
    */
   public DOMFile(final File xmlFile, final @Nullable String rootNamespace, final File @Nullable... xmlSchemaFiles) throws IOException,
         XMLException {
      Args.notNull("xmlFile", xmlFile);
      Assert.isFileReadable(xmlFile);

      this.xmlFile = xmlFile;

      try {
         domDocument = DOMUtils.parseFile(xmlFile, rootNamespace, xmlSchemaFiles);
      } catch (final XMLException ex) {
         // debug code to analyze "Content is not allowed in prolog."
         if (ex.getCause() instanceof SAXParseException) {
            LOG.debug("Failed to parse file %s with content:\n%s", ex, xmlFile.getAbsolutePath(), MoreFiles.readAsString(xmlFile.toPath()));
         }
         throw ex;
      }
      domRoot = domDocument.getDocumentElement();
   }

   private void assertNodeBelongsToDocument(final String argumentName, final Node node) {
      if (DOMUtils._getOwnerDocument(node) != domDocument) {
         final var ex = new IllegalArgumentException("[" + argumentName + "] belongs to another DOM document!");
         StackTrace.removeFirstStackTraceElement(ex);
         throw ex;
      }
   }

   public Comment createCommentBefore(final Node sibling, final String commentString) {
      Args.notNull("sibling", sibling);
      assertNodeBelongsToDocument("sibling", sibling);

      return DOMUtils.createCommentBefore(sibling, commentString);
   }

   /**
    * Creates a new XML element as child of the given parentNode.
    */
   public Element createElement(final Node parent, final String xmlTagName) {
      Args.notNull("parent", parent);
      assertNodeBelongsToDocument("parent", parent);

      return DOMUtils.createElement(parent, xmlTagName);
   }

   /**
    * Creates a new XML element as child of the given parentNode with the given attributes.
    */
   public Element createElement(final Node parent, final String tagName, final @Nullable Map<String, String> tagAttributes) {
      Args.notNull("parent", parent);
      assertNodeBelongsToDocument("parent", parent);

      return DOMUtils.createElement(parent, tagName, tagAttributes);
   }

   public Element createElementBefore(final Node sibling, final String tagName) {
      Args.notNull("sibling", sibling);
      assertNodeBelongsToDocument("sibling", sibling);

      return DOMUtils.createElementBefore(sibling, tagName);
   }

   public Element createElementBefore(final Node sibling, final String tagName, final @Nullable Map<String, String> tagAttributes) {
      Args.notNull("sibling", sibling);
      assertNodeBelongsToDocument("sibling", sibling);

      return DOMUtils.createElementBefore(sibling, tagName, tagAttributes);
   }

   public Element createElementWithText(final Node parent, final String tagName, final @Nullable Map<String, String> tagAttributes,
         final Object text) {
      final Element elem = createElement(parent, tagName, tagAttributes);
      createTextNode(elem, text);
      return elem;
   }

   public Element createElementWithText(final Node parent, final String tagName, final Object text) {
      final Element elem = createElement(parent, tagName, null);
      createTextNode(elem, text);
      return elem;
   }

   public Element createElementWithTextBefore(final Node sibling, final String tagName, final @Nullable Map<String, String> tagAttributes,
         final Object text) {
      final Element elem = createElementBefore(sibling, tagName, tagAttributes);
      createTextNode(elem, text);
      return elem;
   }

   public Element createElementWithTextBefore(final Node sibling, final String tagName, final Object text) {
      final Element elem = createElementBefore(sibling, tagName, null);
      createTextNode(elem, text);
      return elem;
   }

   public Text createTextNode(final Node parent, final Object text) {
      Args.notNull("parent", parent);
      assertNodeBelongsToDocument("parent", parent);

      return DOMUtils.createTextNode(parent, text);
   }

   public Text createTextNodeBefore(final Node sibling, final Object text) {
      Args.notNull("sibling", sibling);
      assertNodeBelongsToDocument("sibling", sibling);

      return DOMUtils.createTextNodeBefore(sibling, text);
   }

   public Boolean evaluateAsBoolean(final Node searchScope, final String xPathExpression) throws XMLException {
      Args.notNull("searchScope", searchScope);
      assertNodeBelongsToDocument("parent", searchScope);

      return DOMUtils.evaluateAsBoolean(searchScope, xPathExpression);
   }

   public Boolean evaluateAsBoolean(final String xPathExpression) throws XMLException {
      return DOMUtils.evaluateAsBoolean(domRoot, xPathExpression);
   }

   public Node evaluateAsNode(final Node searchScope, final String xPathExpression) throws XMLException {
      Args.notNull("searchScope", searchScope);
      assertNodeBelongsToDocument("parent", searchScope);

      return DOMUtils.evaluateAsNode(searchScope, xPathExpression);
   }

   public Node evaluateAsNode(final String xPathExpression) throws XMLException {
      return DOMUtils.evaluateAsNode(domRoot, xPathExpression);
   }

   public <T extends Node> List<T> evaluateAsNodes(final Node searchScope, final String xPathExpression) throws XMLException {
      Args.notNull("searchScope", searchScope);
      assertNodeBelongsToDocument("parent", searchScope);

      return DOMUtils.evaluateAsNodes(searchScope, xPathExpression);
   }

   public <T extends Node> List<T> evaluateAsNodes(final String xPathExpression) throws XMLException {
      return DOMUtils.evaluateAsNodes(domRoot, xPathExpression);
   }

   public Number evaluateAsNumber(final Node searchScope, final String xPathExpression) throws XMLException {
      Args.notNull("searchScope", searchScope);
      assertNodeBelongsToDocument("parent", searchScope);

      return DOMUtils.evaluateAsNumber(searchScope, xPathExpression);
   }

   public Number evaluateAsNumber(final String xPathExpression) throws XMLException {
      return DOMUtils.evaluateAsNumber(domRoot, xPathExpression);
   }

   public String evaluateAsString(final Node searchScope, final String xPathExpression) throws XMLException {
      Args.notNull("searchScope", searchScope);
      assertNodeBelongsToDocument("parent", searchScope);

      return DOMUtils.evaluateAsString(searchScope, xPathExpression);
   }

   public String evaluateAsString(final String xPathExpression) throws XMLException {
      return DOMUtils.evaluateAsString(domRoot, xPathExpression);
   }

   public <T extends Node> @Nullable T findNode(final Node searchScope, final String xPathExpression) throws XMLException {
      Args.notNull("searchScope", searchScope);
      assertNodeBelongsToDocument("parent", searchScope);

      return DOMUtils.findNode(searchScope, xPathExpression);
   }

   public <T extends Node> @Nullable T findNode(final String xPathExpression) throws XMLException {
      return findNode(domRoot, xPathExpression);
   }

   public <T extends Node> List<T> findNodes(final String xPathExpression) throws XMLException {
      return DOMUtils.findNodes(domRoot, xPathExpression);
   }

   public <T extends Node> List<T> findNodes(final String xPathExpression, final Node searchScope) throws XMLException {
      Args.notNull("searchScope", searchScope);
      assertNodeBelongsToDocument("parent", searchScope);

      return DOMUtils.findNodes(searchScope, xPathExpression);
   }

   /**
    * @param recursive return text content of child nodes
    */
   public @Nullable String findTextContent(final Node searchScope, final String xPathExpression, final boolean recursive)
         throws XMLException {
      Args.notNull("searchScope", searchScope);
      assertNodeBelongsToDocument("parent", searchScope);

      return DOMUtils.findTextContent(searchScope, xPathExpression, recursive);
   }

   /**
    * @param recursive return text content of child nodes
    */
   public @Nullable String findTextContent(final String xPathExpression, final boolean recursive) throws XMLException {
      return DOMUtils.findTextContent(domRoot, xPathExpression, recursive);
   }

   public List<Attr> getAttributes(final Node node) {
      Args.notNull("node", node);
      assertNodeBelongsToDocument("node", node);

      return DOMUtils.getAttributes(node);
   }

   /**
    * @return all direct child nodes of this node.
    */
   public List<Node> getChildNodes(final Node parent) {
      Args.notNull("parent", parent);
      assertNodeBelongsToDocument("parent", parent);

      return DOMUtils.getChildNodes(parent);
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
   public <T extends Node> List<T> getElementsByTagName(final Element parent, final String tagName) {
      Args.notNull("parent", parent);
      assertNodeBelongsToDocument("parent", parent);

      return DOMUtils.getElementsByTagName(parent, tagName);
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

   public @Nullable Node getFirstChild(final Node parent) {
      Args.notNull("parent", parent);
      assertNodeBelongsToDocument("parent", parent);

      return DOMUtils.getFirstChild(parent);
   }

   public List<Attr> getIdAttributes(final Node node) {
      Args.notNull("node", node);
      assertNodeBelongsToDocument("parent", node);

      return DOMUtils.getIdAttributes(node);
   }

   /**
    * @return the imported node object
    */
   public <T extends Node> T importNode(final Node parent, final T nodeToImport) {
      Args.notNull("parent", parent);
      assertNodeBelongsToDocument("parent", parent);

      return DOMUtils.importNode(parent, nodeToImport);
   }

   public <T extends Node> T importNodeBefore(final Node sibling, final T nodeToImport) {
      Args.notNull("sibling", sibling);
      assertNodeBelongsToDocument("sibling", sibling);

      return DOMUtils.importNodeBefore(sibling, nodeToImport);
   }

   public <T extends Node> List<T> importNodes(final Node parent, final Collection<T> nodesToImport) {
      Args.notNull("parent", parent);
      assertNodeBelongsToDocument("parent", parent);

      return DOMUtils.importNodes(parent, nodesToImport);
   }

   public <T extends Node> List<T> importNodes(final Node parent, final NodeList nodesToImport) {
      Args.notNull("parent", parent);
      assertNodeBelongsToDocument("parent", parent);

      return DOMUtils.importNodes(parent, nodesToImport);
   }

   public <T extends Node> List<T> importNodesBefore(final Node sibling, final Collection<T> nodesToImport) {
      Args.notNull("sibling", sibling);
      assertNodeBelongsToDocument("sibling", sibling);

      return DOMUtils.importNodesBefore(sibling, nodesToImport);
   }

   public <T extends Node> List<T> importNodesBefore(final Node sibling, final NodeList nodesToImport) {
      Args.notNull("sibling", sibling);
      assertNodeBelongsToDocument("sibling", sibling);

      return DOMUtils.importNodesBefore(sibling, nodesToImport);
   }

   /**
    * @return true if the node was removed and false if the node did not have a parent node
    * @exception DOMException
    *               <li>NO_MODIFICATION_ALLOWED_ERR: Raised if this node is readonly.</li>
    *               <li>NOT_FOUND_ERR: Raised if <code>oldChild</code> is not a child
    *               of this node.</li>
    *               <li>NOT_SUPPORTED_ERR: if this node is of type <code>Document</code>, this exception might be raised if the DOM
    *               implementation doesn't support the removal of the <code>DocumentType</code> child or the <code>Element</code>
    *               child.</li>
    */
   public boolean removeNode(final Node node) throws DOMException {
      Args.notNull("node", node);
      assertNodeBelongsToDocument("node", node);

      return DOMUtils.removeNode(node);
   }

   /**
    * @return a list of the removed nodes
    */
   public List<Node> removeNodes(final Node searchScope, final String xPathExpression) throws XMLException {
      Args.notNull("searchScope", searchScope);
      assertNodeBelongsToDocument("searchScope", searchScope);

      return DOMUtils.removeNodes(searchScope, xPathExpression);
   }

   /**
    * @return a list of the removed nodes
    */
   public List<Node> removeNodes(final String xPathExpression) throws XMLException {
      return removeNodes(domRoot, xPathExpression);
   }

   public void removeWhiteSpaceNodes() {
      removeWhiteSpaceNodes(domRoot);
   }

   public void removeWhiteSpaceNodes(final Node searchScope) {
      Args.notNull("searchScope", searchScope);
      Assert.isTrue(DOMUtils._getOwnerDocument(searchScope) == domDocument, "[searchScope] belongs to another DOM document!");

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
    * @param parent node whose children will be sorted
    */
   public void sortChildNodes(final Node parent, final Comparator<Node> comparator) {
      Args.notNull("parent", parent);
      assertNodeBelongsToDocument("parent", parent);

      DOMUtils.sortChildNodes(parent, comparator);
   }

   /**
    * @param parent node whose children will be sorted
    */
   public void sortChildNodesByAttributes(final Node parent, final boolean ascending, final String... attributeNames) {
      Args.notNull("parent", parent);
      assertNodeBelongsToDocument("parent", parent);

      DOMUtils.sortChildNodesByAttributes(parent, ascending, attributeNames);
   }

   public String toXML() {
      return DOMUtils.toXML(domDocument, true, true);
   }

   public String toXML(final boolean outputXMLDeclaration, final boolean formatPretty) {
      return DOMUtils.toXML(domDocument, outputXMLDeclaration, formatPretty);
   }
}
