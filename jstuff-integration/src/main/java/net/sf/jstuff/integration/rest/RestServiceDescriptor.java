/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.rest;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class RestServiceDescriptor implements Serializable {
   private static final long serialVersionUID = 1L;

   private Collection<RestResourceAction> actions;

   public Collection<RestResourceAction> getActions() {
      return actions;
   }

   public void setActions(final Collection<RestResourceAction> actions) {
      this.actions = actions;
   }
}
