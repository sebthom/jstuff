/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.atom.feed;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class AtomPerson {
   private String email;
   private String name;
   private String uri;

   public AtomPerson() {
   }

   public AtomPerson(final String name) {
      this.name = name;
   }

   public AtomPerson(final String name, final String email, final String uri) {
      this.name = name;
      this.email = email;
      this.uri = uri;
   }

   public String getEmail() {
      return email;
   }

   public String getName() {
      return name;
   }

   public String getUri() {
      return uri;
   }

   public void setEmail(final String email) {
      this.email = email;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setUri(final String uri) {
      this.uri = uri;
   }
}
