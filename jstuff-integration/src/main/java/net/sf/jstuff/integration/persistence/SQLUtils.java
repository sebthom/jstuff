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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import net.sf.jstuff.core.StringUtils;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class SQLUtils {
    /**
     * Create insert-preparedStatement-String for specific table and column names
     * 
     * @param table table name as String
     * @param columnNames column names as String Array
     */
    public static String buildInsertSQL(final String table, final String... columnNames) {
        Args.notEmpty("table", table);
        Args.notEmpty("columnNames", columnNames);

        if (columnNames.length == 1)
            return "INSERT INTO " + table + "(" + columnNames[0] + ") VALUES (?)";

        return "INSERT INTO " + table + "(" + StringUtils.join(columnNames, ",") + ") VALUES (" + StringUtils.repeat("?,", columnNames.length - 1) + "?)";
    }

    /**
     * Create insert-preparedStatement by connection with table and columns
     * 
     * @param con
     * @param table
     * @param columns map with column name as key and the column value as value
     * @throws SQLException
     */
    public static PreparedStatement buildInsertStatement(final Connection con, final String table, final Map<String, Object> columns) throws SQLException {
        Args.notEmpty("table", table);
        Args.notEmpty("columns", columns);

        // convert column names to Array
        final String[] columnNames = columns.keySet().toArray(new String[columns.size()]);

        final PreparedStatement stmt = con.prepareStatement(buildInsertSQL(table, columnNames));
        int parameterIndex = 1;
        // insert column values
        for (final String columnName : columnNames) {
            stmt.setObject(parameterIndex, columns.get(columnName));
            parameterIndex++;
        }
        return stmt;
    }

    /**
     * Create update-preparedStatement-String with table name, column names and where-clause
     * 
     * @param table
     * @param columnNames
     */
    public static String buildUpdateSQL(final String table, final String... columnNames) {
        Args.notEmpty("table", table);
        Args.notEmpty("columnNames", columnNames);

        return buildUpdateSQL(table, columnNames, null);
    }

    /**
     * Create update-preparedStatement-String with table name, column names and where-clause
     * 
     * @param table
     * @param columnNames
     * @param where
     */
    public static String buildUpdateSQL(final String table, final String columnNames[], final String where) {
        Args.notEmpty("table", table);
        Args.notEmpty("columnNames", columnNames);

        if (columnNames.length == 1)
            return "UPDATE " + table + " SET " + columnNames[0] + " = ? " + (where == null ? "" : " WHERE " + where);

        return "UPDATE " + table + " SET " + StringUtils.join(columnNames, " = ?, ") + " = ? " + (where == null ? "" : " WHERE " + where);
    }

    /**
     * Create update-preparedStatement by connection with table, columns, and where clause
     * 
     * @param con Connection required for creating prepared Statement
     * @param table table name for statement
     * @param columns columns map for name and value
     * @param where
     * @param whereValues values for where clause
     * @return preparedStatement generated by input values
     * @throws SQLException
     */
    public static PreparedStatement buildUpdateStatement(final Connection con, final String table, final Map<String, Object> columns, final String where,
            final Object... whereValues) throws SQLException {
        Args.notEmpty("table", table);
        Args.notEmpty("columns", columns);

        final String[] columnNames = columns.keySet().toArray(new String[columns.size()]);

        final PreparedStatement stmt = con.prepareStatement(buildUpdateSQL(table, columnNames, where));

        int parameterIndex = 1;
        for (final String columnName : columnNames) {
            stmt.setObject(parameterIndex, columns.get(columnName));
            parameterIndex++;
        }

        if (whereValues != null && whereValues.length > 0)
            for (final Object val : whereValues) {
            stmt.setObject(parameterIndex, val);
            parameterIndex++;
        }
        return stmt;
    }

    public static Map<String, Integer> getColumnInfo(final Connection con, final String tableName) throws SQLException {
        final Map<String, Integer> m = new HashMap<String, Integer>();
        final ResultSet rs = con.getMetaData().getColumns("%", "%", tableName, "%");
        while (rs.next())
            m.put(rs.getString("COLUMN_NAME").toLowerCase(), rs.getInt("DATA_TYPE"));
        rs.close();
        return m;
    }

    public static int getSQLType(final Class<?> type) {
        if (type == Integer.class || type == int.class)
            return Types.INTEGER;
        if (type == Short.class || type == short.class)
            return Types.INTEGER;
        if (type == Long.class || type == long.class)
            return Types.INTEGER;
        if (type == Byte.class || type == byte.class)
            return Types.INTEGER;
        if (type == Float.class || type == float.class)
            return Types.FLOAT;
        if (type == Double.class || type == double.class)
            return Types.FLOAT;
        if (type == BigDecimal.class)
            return Types.FLOAT;
        if (type == String.class)
            return Types.VARCHAR;
        if (type == Boolean.class || type == boolean.class)
            return Types.BOOLEAN;
        if (type == java.util.Date.class || type == java.sql.Date.class)
            return Types.DATE;
        if (type == java.sql.Time.class)
            return Types.TIME;
        if (type == java.sql.Timestamp.class)
            return Types.TIMESTAMP;
        if (type == byte[].class)
            return Types.VARBINARY;
        throw new IllegalArgumentException("Unknown type " + type);
    }

    /**
     * Check if table exists in connection
     * 
     * @param con
     * @param tableName
     * @return <code>TRUE</code> if table exists, <code>FALSE</code> if table does not exists
     * @throws SQLException
     */
    public static boolean tableExists(final Connection con, final String tableName) throws SQLException {
        /*
         * This is a workaround for the following code, as it is unreliable.
         * In some cases tables.next() returns true even if the table does not exist.
         * <pre>
         * final DatabaseMetaData dbm = c.getMetaData();
         * final ResultSet tables = dbm.getTables(null, null, tableName, null);
         * if (!tables.next()) { ... }
         * </pre>
         */
        PreparedStatement stmt = null;
        ResultSet results = null;
        try {
            stmt = con.prepareStatement("SELECT COUNT(*) FROM " + tableName + " WHERE 0 = 1");
            results = stmt.executeQuery();
            return true; // if table does exist, no rows will ever be returned
        } catch (final SQLException e) {
            return false; // if table does not exist, an exception will be thrown
        } finally {
            if (results != null)
                results.close();
            if (stmt != null)
                stmt.close();
        }
    }
}