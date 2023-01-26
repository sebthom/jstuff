/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.xml.stream;

import javax.xml.stream.XMLEventReader;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface AutoCloseableXMLEventReader extends XMLEventReader, AutoCloseable {

}
