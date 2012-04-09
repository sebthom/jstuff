/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2012 Sebastian
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
package net.sf.jstuff.integration.persistence.hibernate;

import java.io.Serializable;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.TableHiLoGenerator;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;
import org.hibernate.util.PropertiesHelper;

/**
 * <code>
 * @javax.persistence.Id
 * @javax.persistence.GeneratedValueValue(generator = "oid")
 * @org.hibernate.annotations.GenericGenerator(
 *    name = "oid", strategy = net.sf.jstuff.integration.persistence.hibernate.IntegerOidGenerator.class.getName(), 
 *    parameters = {
 *	     @Parameter(name = net.sf.jstuff.integration.persistence.hibernate.IntegerOidGenerator.PARAM_SUFFIX, value = "001"),
 *       @Parameter(name = "max_lo", value = "1")}
 * )
 * private Integer oid;
 * </code>
 *  
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class IntegerOIDGenerator extends TableHiLoGenerator
{
	public static final String PARAM_PREFIX = "prefix";
	public static final String PARAM_SUFFIX = "suffix";

	private String prefix;
	private String suffix;

	@Override
	public void configure(final Type type, final Properties params, final Dialect d)
	{
		super.configure(new IntegerType(), params, d);

		prefix = PropertiesHelper.getString(PARAM_PREFIX, params, "");
		suffix = PropertiesHelper.getString(PARAM_SUFFIX, params, "");
	}

	@Override
	public synchronized Serializable generate(final SessionImplementor session, final Object obj)
			throws HibernateException
	{
		final int id = ((Integer) super.generate(session, obj)).intValue();

		return Integer.parseInt(prefix + id + suffix);
	}
}
