/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.example.guestbook.service.command;

import java.io.Serializable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class UpdateGuestBookEntryCommand implements Serializable {
   private static final long serialVersionUID = 1L;

   public String message;
}
