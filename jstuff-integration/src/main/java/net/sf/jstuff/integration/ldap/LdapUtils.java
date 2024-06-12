/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.ldap;

import java.io.IOException;
import java.util.NoSuchElementException;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.StartTlsResponse;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class LdapUtils {

   public static void closeQuietly(final @Nullable Context context) {
      try {
         if (context != null) {
            context.close();
         }
      } catch (final NamingException ex) {
         // ignore
      }
   }

   public static void closeQuietly(final @Nullable StartTlsResponse tls) {
      try {
         if (tls != null) {
            tls.close();
         }
      } catch (final IOException ex) {
         // ignore
      }
   }

   @NonNullByDefault({})
   public static <T> T getAttributeValue(@Nullable final SearchResult sr, final @NonNull String attrName, final T ifNullOrNotExisting)
         throws NamingException {
      if (sr == null)
         return ifNullOrNotExisting;

      return getAttributeValue(sr.getAttributes(), attrName, ifNullOrNotExisting);
   }

   @NonNullByDefault({})
   public static <T> T getAttributeValue(@Nullable final Attributes attrs, final @NonNull String attrName, final T ifNullOrNotExisting)
         throws NamingException {
      if (attrs == null)
         return ifNullOrNotExisting;

      return getAttributeValue(attrs.get(attrName), ifNullOrNotExisting);
   }

   @SuppressWarnings("unchecked")
   @NonNullByDefault({})
   public static <T> T getAttributeValue(@Nullable final Attribute attr, final T ifNullOrNotExisting) throws NamingException {
      if (attr == null)
         return ifNullOrNotExisting;
      try {
         final var obj = attr.get();
         if (obj == null)
            return ifNullOrNotExisting;
         return (T) obj;
      } catch (final NoSuchElementException ex) {
         return ifNullOrNotExisting;
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

      final var sb = new StringBuilder(textLen + 16);
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
      final var sb = new StringBuilder();
      for (final String chunk : Strings.split(dn, ',')) {
         final String[] pair = Strings.split(chunk, "=", 2);
         if (sb.length() > 0) {
            sb.append(',');
         }
         sb.append(pair[0].trim().toUpperCase());
         sb.append('=');
         sb.append(pair[1].trim());
      }
      return sb.toString();
   }
}
