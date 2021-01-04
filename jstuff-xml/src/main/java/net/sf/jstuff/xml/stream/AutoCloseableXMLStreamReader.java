/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.xml.stream;

import javax.xml.stream.XMLStreamReader;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface AutoCloseableXMLStreamReader extends XMLStreamReader, AutoCloseable {

}
