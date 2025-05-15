package com.drizzlepal.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.drizzlepal.jdbc.exception.UnknowDatabaseException;
import com.drizzlepal.jdbc.metadata.ColumnMetaData;
import com.drizzlepal.jdbc.metadata.DatabaseMetaData;
import com.drizzlepal.jdbc.metadata.TableMetaData;
import com.drizzlepal.utils.StringUtils;
import com.zaxxer.hikari.HikariDataSource;

public abstract class DatasourceCommon implements Datasource {

    protected final HikariDataSource dataSource;

    protected final DatabaseConfigCommon configCommon;

    public DatasourceCommon(DatabaseConfigCommon configCommon) {
        this.configCommon = configCommon;
        this.dataSource = new HikariDataSource();
        this.dataSource.setJdbcUrl(buildJdbcUrl(configCommon));
        this.dataSource.setUsername(configCommon.getUsername());
        this.dataSource.setPassword(configCommon.getPassword());
        this.dataSource.setMaximumPoolSize(configCommon.getMaxActive());
        this.dataSource.setConnectionTimeout(configCommon.getMaxWaitMs());
        this.dataSource.setMinimumIdle(configCommon.getMinIdle());
        this.dataSource.setDriverClassName(configCommon.getDatabaseType().getDriverClassName());
        this.dataSource.setConnectionInitSql(configCommon.getDatabaseType().getConnectionInitSql());
    }

    @Override
    public void close() {
        dataSource.close();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    @Override
    public void doWithConnection(Consumer<Connection> thingsToDo) throws Exception {
        try (Connection connection = this.getConnection()) {
            thingsToDo.accept(connection);
        }
    }

    public abstract String buildJdbcUrl(DatabaseConfigCommon configCommon);

    protected abstract boolean checkDatabaseNameConfigExists();

    @Override
    public List<String> getDatabaseNames() throws UnknowDatabaseException, SQLException {
        LinkedList<String> res = new LinkedList<>();
        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("show databases;");
                ResultSet executeQuery = statement.executeQuery();) {
            while (executeQuery.next()) {
                res.add(executeQuery.getString(1));
            }
        }
        return res;
    }

    @Override
    public DatabaseMetaData getMetaData() throws UnknowDatabaseException, SQLException {
        if (!checkDatabaseNameConfigExists()) {
            throw new UnknowDatabaseException("连接信息中未指定获取哪个数据库");
        }
        return getMetaData(configCommon.getSchema(), configCommon.getDatabase());
    }

    @Override
    public TableMetaData getTableMetaData(String tableName) throws UnknowDatabaseException, SQLException {
        if (!checkDatabaseNameConfigExists()) {
            throw new UnknowDatabaseException("连接信息中未指定获取哪个数据库");
        }
        return getTableMetaData(configCommon.getSchema(), configCommon.getDatabase(), tableName);
    }

    @Override
    public ColumnMetaData getColumnMetaData(String tableName, String columnName)
            throws UnknowDatabaseException, SQLException {
        if (!checkDatabaseNameConfigExists()) {
            throw new UnknowDatabaseException("连接信息中未指定获取哪个数据库");
        }
        return getColumnMetaData(configCommon.getSchema(), configCommon.getDatabase(), tableName, columnName);
    }

    @Override
    public DatabaseMetaData getMetaData(String schema, String databaseName) throws SQLException {
        DatabaseMetaData databaseMetaData = new DatabaseMetaData();
        databaseMetaData.setName(databaseName);
        databaseMetaData.setSchema(schema);
        LinkedList<TableMetaData> tableMetaDataList = new LinkedList<>();
        databaseMetaData.setTables(null);
        try (Connection connection = getConnection();) {
            if (StringUtils.isNotBlank(configCommon.getDatabaseType().getDatabaseMetaDataQuerySql())) {
                try (PreparedStatement statement = connection
                        .prepareStatement(configCommon.getDatabaseType().getDatabaseMetaDataQuerySql());) {
                    configCommon.getDatabaseType().getDatabaseMetaDataQuerySqlParameterSetter().setParameters(statement,
                            schema, databaseName);
                    HashMap<String, LinkedList<ColumnMetaData>> tableColumnMap = new HashMap<>();
                    try (ResultSet columns = statement.executeQuery();) {
                        while (columns.next()) {
                            String tableName = columns.getString("TABLE_NAME");
                            if (!tableColumnMap.containsKey(tableName)) {
                                tableColumnMap.put(tableName, new LinkedList<>());
                            }
                            tableColumnMap.get(tableName).addLast(readColumnMetaDataFromResultSet(columns));
                        }
                    }
                }
            } else {
                java.sql.DatabaseMetaData metaData = connection.getMetaData();
                HashMap<String, LinkedList<ColumnMetaData>> tableColumnMap = new HashMap<>();
                try (ResultSet columns = metaData.getColumns(databaseName, schema, "%", "%");) {
                    while (columns.next()) {
                        String tableName = columns.getString("TABLE_NAME");
                        if (!tableColumnMap.containsKey(tableName)) {
                            tableColumnMap.put(tableName, new LinkedList<>());
                        }
                        tableColumnMap.get(tableName).addLast(readColumnMetaDataFromResultSet(columns));
                    }
                }
                try (ResultSet tables = metaData.getTables(databaseName, schema, "%",
                        configCommon.getDatabaseType().getDatabaseTableTypes());) {
                    while (tables.next()) {
                        TableMetaData tableMetaData = new TableMetaData();
                        tableMetaData.setName(tables.getString("TABLE_NAME"));
                        tableMetaData.setRemarks(tables.getString("REMARKS"));
                        if (tableColumnMap.containsKey(tableMetaData.getName())) {
                            tableMetaData.setColumns(new ArrayList<>(tableColumnMap.remove(tableMetaData.getName())));
                        }
                        tableMetaDataList.addLast(tableMetaData);
                    }
                }
            }
        }
        databaseMetaData.setTables(new ArrayList<>(tableMetaDataList));
        return databaseMetaData;
    }

    @Override
    public TableMetaData getTableMetaData(String schema, String databaseName, String tableName) throws SQLException {
        try (Connection connection = getConnection();) {
            if (StringUtils.isNotBlank(configCommon.getDatabaseType().getDatabaseMetaDataQuerySql())) {

            } else {
                java.sql.DatabaseMetaData metaData = connection.getMetaData();
                try (ResultSet tables = metaData.getTables(databaseName, schema, tableName,
                        configCommon.getDatabaseType().getDatabaseTableTypes());) {
                    while (tables.next()) {
                        TableMetaData tableMetaData = new TableMetaData();
                        tableMetaData.setName(tables.getString("TABLE_NAME"));
                        tableMetaData.setRemarks(tables.getString("REMARKS"));
                        try (ResultSet columns = metaData.getColumns(databaseName, schema, tableMetaData.getName(),
                                "%");) {
                            LinkedList<ColumnMetaData> columnMetaDataList = new LinkedList<>();
                            while (columns.next()) {
                                columnMetaDataList.addLast(readColumnMetaDataFromResultSet(columns));
                            }
                            tableMetaData.setColumns(new ArrayList<>(columnMetaDataList));
                        }
                        return tableMetaData;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public ColumnMetaData getColumnMetaData(String schema, String databaseName, String tableName, String columnName)
            throws SQLException {
        try (Connection connection = getConnection();) {
            if (StringUtils.isNotBlank(configCommon.getDatabaseType().getDatabaseMetaDataQuerySql())) {

            } else {
                java.sql.DatabaseMetaData metaData = connection.getMetaData();
                try (ResultSet columns = metaData.getColumns(databaseName, schema, tableName, columnName);) {
                    while (columns.next()) {
                        return readColumnMetaDataFromResultSet(columns);
                    }
                }
            }
        }
        return null;
    }

    protected ColumnMetaData readColumnMetaDataFromResultSet(ResultSet columns) throws SQLException {
        ColumnMetaData columnMetaData = new ColumnMetaData();
        columnMetaData.setName(columns.getString("COLUMN_NAME"));
        columnMetaData.setRemarks(columns.getString("REMARKS"));
        columnMetaData.setTypeName(columns.getString("TYPE_NAME"));
        columnMetaData.setLength(columns.getInt("COLUMN_SIZE"));
        columnMetaData.setDatatype(columns.getInt("DATA_TYPE"));
        columnMetaData.setOrdinalPosition(columns.getInt("ORDINAL_POSITION"));
        return columnMetaData;
    }

}
