/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
 * Thomschke.
 * 
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.integration.ldap;

import javax.naming.Context;
import javax.naming.NamingException;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class LDAPUtils
{
	public static void closeQuietly(final Context context)
	{
		try
		{
			if (context != null) context.close();
		}
		catch (final NamingException ex)
		{
			// ignore
		}
	}

	/**
	 * Escapes the characters \ ( ) * for LDAP queries.
	 */
	public static CharSequence ldapEscape(final String text)
	{
		Args.notNull("text", text);

		// check if the string needs to be escaped at all
		final int textLen = text.length();
		if (textLen == 0) return text;

		final StringBuilder sb = new StringBuilder(textLen + 16);
		for (int i = 0; i < textLen; i++)
		{
			final char ch = text.charAt(i);
			switch (ch)
			{
			// backslash \
				case 0x5c :
					sb.append("\\5c");
					break;
				// (
				case 0x28 :
					sb.append("\\28");
					break;
				// )
				case 0x29 :
					sb.append("\\29");
					break;
				// *
				case 0x2A :
					sb.append("\\2a");
					break;
				default :
					sb.append(ch);
			}
		}
		return sb;
	}

	protected LDAPUtils()
	{
		super();
	}
}
