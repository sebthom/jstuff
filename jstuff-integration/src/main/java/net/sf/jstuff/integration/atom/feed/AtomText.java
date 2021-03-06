/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.atom.feed;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 *
 */
public class AtomText {
   public static final String TYPE_HTML = "html";
   public static final String TYPE_TEXT = "text";

   private String content;
   private String type = TYPE_TEXT;

   public AtomText() {
   }

   public AtomText(final String content) {
      this.content = content;
   }

   public AtomText(final String content, final String type) {
      this.content = content;
      this.type = type;
   }

   public String getContent() {
      return content;
   }

   public String getType() {
      return type;
   }

   public void setContent(final String value) {
      content = value;
   }

   public void setType(final String type) {
      this.type = type;
   }
}
