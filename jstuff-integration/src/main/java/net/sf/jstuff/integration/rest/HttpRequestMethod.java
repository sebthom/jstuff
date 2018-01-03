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
package net.sf.jstuff.integration.rest;

/**
 * http://www.restapitutorial.com/lessons/httpmethods.html
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public enum HttpRequestMethod {

    /**
     * CRUD: Delete
     */
    DELETE,

    /**
     * CRUD: Read
     */
    GET,

    HEAD,

    /**
     * CRUD: Create
     */
    POST,

    /**
     * CRUD: Update/Replace
     */
    PUT,

    /**
     * CRUD: Update/Modify
     */
    PATCH
    
    /*,
    TRACE,
    CONNECT,
    OPTIONS*/
}
