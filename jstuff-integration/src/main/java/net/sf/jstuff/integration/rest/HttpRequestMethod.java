/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.rest;

/**
 * http://www.restapitutorial.com/lessons/httpmethods.html
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
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
