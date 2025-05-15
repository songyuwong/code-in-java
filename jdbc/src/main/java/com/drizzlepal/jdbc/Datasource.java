package com.drizzlepal.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

import com.drizzlepal.jdbc.exception.UnknowDatabaseException;
import com.drizzlepal.jdbc.metadata.ColumnMetaData;
import com.drizzlepal.jdbc.metadata.DatabaseMetaData;
import com.drizzlepal.jdbc.metadata.TableMetaData;

public interface Datasource {

    void close();

    Connection getConnection() throws SQLException;

    void doWithConnection(Consumer<Connection> thingsToDo) throws Exception;

    DatabaseMetaData getMetaData() throws UnknowDatabaseException, SQLException;

    DatabaseMetaData getMetaData(String schema, String databaseName) throws SQLException;

    TableMetaData getTableMetaData(String tableName) throws UnknowDatabaseException, SQLException;

    TableMetaData getTableMetaData(String schema, String databaseName, String tableName) throws SQLException;

    ColumnMetaData getColumnMetaData(String tableName, String columnName) throws UnknowDatabaseException, SQLException;

    ColumnMetaData getColumnMetaData(String schema, String databaseName, String tableName, String columnName)
            throws SQLException;

    List<String> getDatabaseNames() throws UnknowDatabaseException, SQLException;

}
