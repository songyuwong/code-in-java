package com.drizzlepal.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.drizzlepal.jdbc.exception.ConnectionOperationException;
import com.drizzlepal.jdbc.exception.UnknowDatabaseException;
import com.drizzlepal.jdbc.metadata.ColumnInfoLabels;
import com.drizzlepal.jdbc.metadata.ColumnMetaData;
import com.drizzlepal.jdbc.metadata.DatabaseMetaData;
import com.drizzlepal.jdbc.metadata.IndexMetaData;
import com.drizzlepal.jdbc.metadata.TableMetaData;
import com.drizzlepal.utils.StringUtils;
import com.drizzlepal.utils.functions.ConsumerThrowable;
import com.zaxxer.hikari.HikariDataSource;

public abstract class DataSourceCommon implements DataSource {

    protected final HikariDataSource dataSource;

    protected final DatabaseConfigCommon configCommon;

    public DataSourceCommon(DatabaseConfigCommon configCommon) {
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
    public void close() throws Exception {
        dataSource.close();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    @Override
    public void doWithConnection(ConsumerThrowable<Connection> thingsToDo) throws ConnectionOperationException {
        try (Connection connection = this.getConnection()) {
            thingsToDo.accept(connection);
        } catch (Throwable e) {
            throw new ConnectionOperationException(e);
        }
    }

    public abstract String buildJdbcUrl(DatabaseConfigCommon configCommon);

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
    public List<IndexMetaData> getIndexMetaData(String tableName, boolean unique)
            throws UnknowDatabaseException, SQLException {
        if (!checkDatabaseNameConfigExists()) {
            throw new UnknowDatabaseException("连接信息中未指定获取哪个数据库");
        }
        return getIndexMetaData(configCommon.getSchema(), configCommon.getDatabase(), tableName, unique);
    }

    @Override
    public DatabaseMetaData getMetaData(String schema, String databaseName) throws SQLException {
        DatabaseMetaData databaseMetaData = new DatabaseMetaData();
        databaseMetaData.setName(databaseName);
        databaseMetaData.setSchema(schema);
        LinkedList<TableMetaData> tableMetaDataList = new LinkedList<>();
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

    @Override
    public List<IndexMetaData> getIndexMetaData(String schema, String databaseName, String tableName, boolean unique)
            throws SQLException {
        LinkedList<IndexMetaData> indexes = new LinkedList<>();
        try (Connection connection = getConnection();) {
            if (StringUtils.isNotBlank(configCommon.getDatabaseType().getDatabaseMetaDataQuerySql())) {

            } else {
                java.sql.DatabaseMetaData metaData = connection.getMetaData();
                HashMap<IndexMetaData, LinkedList<ColumnMetaData>> indexColumnMetaDataMap = new HashMap<>();
                try (ResultSet indexInfo = metaData.getIndexInfo(databaseName, schema, tableName, unique, false);) {
                    while (indexInfo.next()) {
                        IndexMetaData indexMetaData = new IndexMetaData();
                        indexMetaData.setName(indexInfo.getString("INDEX_NAME"));
                        indexMetaData.setType(indexInfo.getString("TYPE"));
                        indexMetaData.setUnique(!indexInfo.getBoolean("NON_UNIQUE"));
                        ColumnMetaData columnMetaData = new ColumnMetaData();
                        columnMetaData.setName(indexInfo.getString("COLUMN_NAME"));
                        columnMetaData.setOrdinalPosition(indexInfo.getInt("ORDINAL_POSITION"));
                        if (!indexColumnMetaDataMap.containsKey(indexMetaData)) {
                            indexColumnMetaDataMap.put(indexMetaData, new LinkedList<>());
                        }
                        indexColumnMetaDataMap.get(indexMetaData).addLast(columnMetaData);
                    }
                    indexColumnMetaDataMap.forEach((indexMetaData, columnMetaDataList) -> {
                        indexMetaData.setColumns(new ArrayList<>(columnMetaDataList));
                        indexes.add(indexMetaData);
                    });
                }
            }
        }
        return indexes;
    }

    @Override
    public List<String> getDefaultColumnsResultSetMetaDataColumnLabels(String tableName)
            throws UnknowDatabaseException, SQLException {
        if (!checkDatabaseNameConfigExists()) {
            throw new UnknowDatabaseException("连接信息中未指定获取哪个数据库");
        }
        return getDefaultColumnsResultSetMetaDataColumnLabels(configCommon.getSchema(), configCommon.getDatabase(),
                tableName);
    }

    @Override
    public List<String> getDefaultColumnsResultSetMetaDataColumnLabels(String schema, String databaseName,
            String tableName) throws SQLException {
        try (Connection connection = getConnection();
                ResultSet resultSet = connection.getMetaData().getColumns(databaseName, schema, tableName, "%");) {
            return getResultSetMetaDataColumnLabels(resultSet);
        }
    }

    @Override
    public List<String> getDefaultIndexesResultSetMetaDataColumnLabels(String tableName)
            throws UnknowDatabaseException, SQLException {
        if (!checkDatabaseNameConfigExists()) {
            throw new UnknowDatabaseException("连接信息中未指定获取哪个数据库");
        }
        return getDefaultIndexesResultSetMetaDataColumnLabels(configCommon.getSchema(), configCommon.getDatabase(),
                tableName);
    }

    @Override
    public List<String> getDefaultIndexesResultSetMetaDataColumnLabels(String schema, String databaseName,
            String tableName) throws SQLException {
        try (Connection connection = getConnection();
                ResultSet indexInfo = connection.getMetaData().getIndexInfo(databaseName, schema, tableName, false,
                        false);) {
            return getResultSetMetaDataColumnLabels(indexInfo);
        }
    }

    @Override
    public List<String> getDefaultTableResultSetMetaDataColumnLabels(String tableName)
            throws UnknowDatabaseException, SQLException {
        if (!checkDatabaseNameConfigExists()) {
            throw new UnknowDatabaseException("连接信息中未指定获取哪个数据库");
        }
        return getDefaultTableResultSetMetaDataColumnLabels(configCommon.getSchema(), configCommon.getDatabase(),
                tableName);
    }

    @Override
    public List<String> getDefaultTableResultSetMetaDataColumnLabels(String schema, String databaseName,
            String tableName) throws SQLException {
        try (Connection connection = getConnection();
                ResultSet tables = connection.getMetaData().getTables(databaseName, schema, tableName,
                        configCommon.getDatabaseType().getDatabaseTableTypes());) {
            return getResultSetMetaDataColumnLabels(tables);
        }
    }

    @Override
    public List<String> getResultSetMetaDataColumnLabels(ResultSet resultSet) throws SQLException {
        LinkedList<String> res = new LinkedList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            res.add(metaData.getColumnLabel(i));
        }
        return res;
    }

    @Override
    public List<String> getTableNames(String databaseName) throws UnknowDatabaseException, SQLException {
        LinkedList<String> res = new LinkedList<>();
        Connection connection = getConnection();
        try {
            connection.setAutoCommit(false);
            connection.prepareStatement("use " + databaseName).execute();
            try (ResultSet executeQuery = connection.prepareStatement("show tables").executeQuery();) {
                while (executeQuery.next()) {
                    res.add(executeQuery.getString(1));
                }
            }
        } finally {
            if (connection != null) {
                connection.close();
                connection.commit();
            }
        }
        return res;
    }

    protected abstract boolean checkDatabaseNameConfigExists();

    protected ColumnMetaData readColumnMetaDataFromResultSet(ResultSet columns) throws SQLException {
        ColumnMetaData columnMetaData = new ColumnMetaData();
        columnMetaData.setName(columns.getString(ColumnInfoLabels.COLUMN_NAME));
        columnMetaData.setRemarks(columns.getString(ColumnInfoLabels.REMARKS));
        columnMetaData.setTypeName(columns.getString(ColumnInfoLabels.TYPE_NAME));
        columnMetaData.setLength(columns.getInt(ColumnInfoLabels.COLUMN_SIZE));
        columnMetaData.setDatatype(columns.getInt(ColumnInfoLabels.DATA_TYPE));
        columnMetaData.setOrdinalPosition(columns.getInt(ColumnInfoLabels.ORDINAL_POSITION));
        columnMetaData.setNullable(columns.getInt(ColumnInfoLabels.NULLABLE));
        columnMetaData.setDecimalDigits(columns.getInt(ColumnInfoLabels.DECIMAL_DIGITS));
        return columnMetaData;
    }

}
