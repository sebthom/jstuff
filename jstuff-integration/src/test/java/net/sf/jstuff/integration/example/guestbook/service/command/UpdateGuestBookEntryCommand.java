/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.example.guestbook.service.command;

import java.io.Serializable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class UpdateGuestBookEntryCommand implements Serializable {
   private static final long serialVersionUID = 1L;

   public String message;
}
