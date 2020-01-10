/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
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
