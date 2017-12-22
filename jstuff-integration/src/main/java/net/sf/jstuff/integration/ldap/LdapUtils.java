/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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
package net.sf.jstuff.integration.ldap;

import java.io.IOException;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.StartTlsResponse;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class LdapUtils {
    public static void closeQuietly(final Context context) {
        try {
            if (context != null) {
                context.close();
            }
        } catch (final NamingException ex) {
            // ignore
        }
    }

    public static void closeQuietly(final StartTlsResponse tls) {
        try {
            if (tls != null) {
                tls.close();
            }
        } catch (final IOException ex) {
            // ignore
        }
    }

    /**
     * Escapes the characters \ ( ) * for LDAP queries.
     */
    public static CharSequence ldapEscape(final String text) {
        Args.notNull("text", text);

        // check if the string needs to be escaped at all
        final int textLen = text.length();
        if (textLen == 0)
            return text;

        final StringBuilder sb = new StringBuilder(textLen + 16);
        for (int i = 0; i < textLen; i++) {
            final char ch = text.charAt(i);
            switch (ch) {
                // backslash \
                case 0x5c:
                    sb.append("\\5c");
                    break;
                // (
                case 0x28:
                    sb.append("\\28");
                    break;
                // )
                case 0x29:
                    sb.append("\\29");
                    break;
                // *
                case 0x2A:
                    sb.append("\\2a");
                    break;
                default:
                    sb.append(ch);
            }
        }
        return sb;
    }

    public static String prettifyDN(final String dn) {
        final StringBuilder sb = new StringBuilder();
        for (final String chunk : dn.split(",")) {
            final String[] pair = chunk.split("=", 2);
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(pair[0].trim().toUpperCase());
            sb.append('=');
            sb.append(pair[1].trim());
        }
        return sb.toString();
    }
}
