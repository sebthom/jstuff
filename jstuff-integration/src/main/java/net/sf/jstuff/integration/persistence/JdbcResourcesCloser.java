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
package net.sf.jstuff.integration.persistence;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class JdbcResourcesCloser
{
	private static boolean close(final Object resource)
	{
		if (resource == null) return false;

		try
		{
			if (resource instanceof Connection)
			{
				((Connection) resource).close();
			}
			else if (resource instanceof Statement)
			{
				((Statement) resource).close();
			}
			else if (resource instanceof ResultSet)
			{
				((ResultSet) resource).close();
			}
			else
				return false;
		}
		catch (final SQLException ex)
		{
			LOG.warn(ex, "Closing JDBC Resource %s failed.", resource);
			return false;
		}
		return true;
	}

	private static final Logger LOG = Logger.create();

	private final LinkedList<Object> resources = new LinkedList<Object>();

	/**
	 * closes all resources in LIFO order.
	 */
	public void closeAll()
	{
		for (final Object resource : resources)
		{
			close(resource);
		}
		resources.clear();
	}

	/**
	 * Closes the resources registered at last.
	 */
	public Object closeLast()
	{
		final Object resource = resources.removeFirst();
		close(resource);
		return resource;
	}

	/**
	 * Registers a resource for later closing.
	 *
	 * @param stmt the resource
	 *
	 * @return the resource
	 */
	public CallableStatement register(final CallableStatement stmt)
	{
		Args.notNull("stmt", stmt);

		resources.addFirst(stmt);
		return stmt;
	}

	/**
	 * Registers a resource for later closing.
	 *
	 * @param con the resource
	 *
	 * @return the resource
	 */
	public Connection register(final Connection con)
	{
		Args.notNull("con", con);

		resources.addFirst(con);
		return con;
	}

	/**
	 * Registers a resource for later closing.
	 *
	 * @param stmt the resource
	 *
	 * @return the resource
	 */
	public PreparedStatement register(final PreparedStatement stmt)
	{
		Args.notNull("stmt", stmt);

		resources.addFirst(stmt);
		return stmt;
	}

	/**
	 * Registers a resource for later closing.
	 *
	 * @param rs the resource
	 *
	 * @return the resource
	 */
	public ResultSet register(final ResultSet rs)
	{
		Args.notNull("rs", rs);

		resources.addFirst(rs);
		return rs;
	}

	/**
	 * Registers a resource for later closing.
	 *
	 * @param stmt the resource
	 *
	 * @return the resource
	 */
	public Statement register(final Statement stmt)
	{
		Args.notNull("stmt", stmt);

		resources.addFirst(stmt);
		return stmt;
	}
}
