/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.persistence;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class SQLUtils {
   /**
    * Create insert-preparedStatement-String for specific table and column names
    *
    * @param columnNames column names as String Array
    */
   public static String buildInsertSQL(final String tableName, final String... columnNames) {
      Args.notEmpty("tableName", tableName);
      Args.notEmpty("columnNames", columnNames);

      if (columnNames.length == 1)
         return "INSERT INTO " + tableName + "(" + columnNames[0] + ") VALUES (?)";

      return "INSERT INTO " + tableName + "(" + Strings.join(columnNames, ",") + ") VALUES (" + Strings.repeat("?,", columnNames.length - 1) + "?)";
   }

   /**
    * Create insert-preparedStatement by connection with table and columns
    *
    * @param con Connection required for creating prepared Statement
    * @param columns map with column name as key and the column value as value
    */
   public static PreparedStatement buildInsertStatement(final Connection con, final String tableName, final Map<String, Object> columns) throws SQLException {
      Args.notNull("con", con);
      Args.notEmpty("tableName", tableName);
      Args.notEmpty("columns", columns);

      // convert column names to Array
      final String[] columnNames = columns.keySet().toArray(new String[columns.size()]);

      final PreparedStatement stmt = con.prepareStatement(buildInsertSQL(tableName, columnNames));
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
    * @param tableName table name for statement
    */
   public static String buildUpdateSQL(final String tableName, final String... columnNames) {
      Args.notEmpty("tableName", tableName);
      Args.notEmpty("columnNames", columnNames);

      return buildUpdateSQL(tableName, columnNames, null);
   }

   /**
    * Create update-preparedStatement-String with table name, column names and where-clause.
    */
   public static String buildUpdateSQL(final String tableName, final String[] columnNames, final String where) {
      Args.notEmpty("tableName", tableName);
      Args.notEmpty("columnNames", columnNames);

      if (columnNames.length == 1)
         return "UPDATE " + tableName + " SET " + columnNames[0] + " = ? " + (where == null ? "" : " WHERE " + where);

      return "UPDATE " + tableName + " SET " + Strings.join(columnNames, " = ?, ") + " = ? " + (where == null ? "" : " WHERE " + where);
   }

   /**
    * Create update-preparedStatement by connection with table, columns, and where clause
    *
    * @param con Connection required for creating prepared Statement
    * @param columns columns map for name and value
    * @param whereValues values for where clause
    * @return preparedStatement generated by input values
    */
   public static PreparedStatement buildUpdateStatement(final Connection con, final String tableName, final Map<String, Object> columns, final String where,
      final Object... whereValues) throws SQLException {
      Args.notNull("con", con);
      Args.notEmpty("tableName", tableName);
      Args.notEmpty("columns", columns);

      final String[] columnNames = columns.keySet().toArray(new String[columns.size()]);

      final PreparedStatement stmt = con.prepareStatement(buildUpdateSQL(tableName, columnNames, where));

      int parameterIndex = 1;
      for (final String columnName : columnNames) {
         stmt.setObject(parameterIndex, columns.get(columnName));
         parameterIndex++;
      }

      if (whereValues != null && whereValues.length > 0) {
         for (final Object val : whereValues) {
            stmt.setObject(parameterIndex, val);
            parameterIndex++;
         }
      }
      return stmt;
   }

   public static Map<String, Integer> getColumnInfo(final Connection con, final String tableName) throws SQLException {
      Args.notNull("con", con);
      Args.notEmpty("tableName", tableName);

      final Map<String, Integer> m = new HashMap<String, Integer>();
      final ResultSet rs = con.getMetaData().getColumns("%", "%", tableName, "%");
      while (rs.next()) {
         m.put(rs.getString("COLUMN_NAME").toLowerCase(), rs.getInt("DATA_TYPE"));
      }
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
    * @return <code>TRUE</code> if table exists, <code>FALSE</code> if table does not exists
    */
   public static boolean tableExists(final Connection con, final String tableName) throws SQLException {
      Args.notNull("con", con);
      Args.notEmpty("tableName", tableName);

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
         if (results != null) {
            results.close();
         }
         if (stmt != null) {
            stmt.close();
         }
      }
   }
}
