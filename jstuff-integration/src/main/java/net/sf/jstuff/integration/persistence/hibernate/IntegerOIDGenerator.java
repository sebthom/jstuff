/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
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

import net.sf.jstuff.core.logging.Logger;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.TableHiLoGenerator;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;
import org.hibernate.util.PropertiesHelper;

/**
 * <code>
 * &#64;javax.persistence.Id
 * &#64;javax.persistence.GeneratedValueValue(generator = "oid")
 * &#64;org.hibernate.annotations.GenericGenerator(
 *    name = "oid", strategy = net.sf.jstuff.integration.persistence.hibernate.IntegerOidGenerator.class.getName(),
 *    parameters = {
 *	     &#64;Parameter(name = net.sf.jstuff.integration.persistence.hibernate.IntegerOidGenerator.PARAM_SUFFIX, value = "001"),
 *       &#64;Parameter(name = "max_lo", value = "1")}
 * )
 * private Integer oid;
 * </code>
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class IntegerOIDGenerator extends TableHiLoGenerator {
    private static final Logger LOG = Logger.create();

    public static final String PARAM_PREFIX = "prefix";
    public static final String PARAM_SUFFIX = "suffix";

    private String prefix;
    private String suffix;

    public IntegerOIDGenerator() {
        LOG.infoNew(this);
    }

    @Override
    public void configure(final Type type, final Properties params, final Dialect d) {
        super.configure(new IntegerType(), params, d);

        prefix = PropertiesHelper.getString(PARAM_PREFIX, params, "");
        suffix = PropertiesHelper.getString(PARAM_SUFFIX, params, "");
    }

    @Override
    public synchronized Serializable generate(final SessionImplementor session, final Object obj) throws HibernateException {
        final int id = ((Integer) super.generate(session, obj)).intValue();

        return Integer.parseInt(prefix + id + suffix);
    }
}
