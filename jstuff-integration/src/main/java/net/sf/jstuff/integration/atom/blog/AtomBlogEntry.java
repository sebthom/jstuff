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
package net.sf.jstuff.integration.atom.blog;

import net.sf.jstuff.core.types.Identifiable;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class AtomBlogEntry extends Identifiable.Default<String> {
    private static final long serialVersionUID = 1L;

    private String title;
    private String body;
    private boolean bodyIsHTML = true;
    private String[] tags;
    private String displayURL;
    private String editURL;

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @return the displayURL
     */
    public String getDisplayURL() {
        return displayURL;
    }

    /**
     * @return the editURL
     */
    public String getEditURL() {
        return editURL;
    }

    /**
     * @return the tags
     */
    public String[] getTags() {
        return tags;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the bodyIsHTML
     */
    public boolean isBodyIsHTML() {
        return bodyIsHTML;
    }

    /**
     * @param body the body to set
     */
    public void setBody(final String body) {
        this.body = body;
    }

    /**
     * @param bodyIsHTML the bodyIsHTML to set
     */
    public void setBodyIsHTML(final boolean bodyIsHTML) {
        this.bodyIsHTML = bodyIsHTML;
    }

    /**
     * @param displayURL the displayURL to set
     */
    public void setDisplayURL(final String displayURL) {
        this.displayURL = displayURL;
    }

    /**
     * @param editURL the editURL to set
     */
    public void setEditURL(final String editURL) {
        this.editURL = editURL;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(final String[] tags) {
        this.tags = tags;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(final String title) {
        this.title = title;
    }
}
