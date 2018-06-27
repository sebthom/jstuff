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
package net.sf.jstuff.integration.atom.feed;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
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
