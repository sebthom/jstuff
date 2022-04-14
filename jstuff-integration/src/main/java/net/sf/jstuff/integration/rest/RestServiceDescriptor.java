/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
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
