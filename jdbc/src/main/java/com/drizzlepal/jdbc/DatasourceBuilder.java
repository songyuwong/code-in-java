package com.drizzlepal.jdbc;

import com.drizzlepal.jdbc.exception.JdbcException;

public class DatasourceBuilder {

    private final DatabaseConfigCommon databaseConfig;

    private DatasourceBuilder(DatabaseType databaseType) {
        databaseConfig = databaseType.initDataConfig();
    }

    public static DatasourceBuilder builder(DatabaseType databaseType) {
        return new DatasourceBuilder(databaseType);
    }

    public DatasourceBuilder userName(String userName) {
        databaseConfig.setUsername(userName);
        return this;
    }

    public DatasourceBuilder password(String password) {
        databaseConfig.setPassword(password);
        return this;
    }

    public DatasourceBuilder host(String host) {
        databaseConfig.setHost(host);
        return this;
    }

    public DatasourceBuilder port(int port) {
        databaseConfig.setPort(port);
        return this;
    }

    public DatasourceBuilder database(String database) {
        databaseConfig.setDatabase(database);
        return this;
    }

    public DatasourceBuilder schema(String schema) {
        databaseConfig.setSchema(schema);
        return this;
    }

    public DatasourceBuilder connectionParam(String key, String value) {
        databaseConfig.getConnectionParams().put(key, value);
        return this;
    }

    public DatasourceBuilder maxActive(int maxActive) {
        databaseConfig.setMaxActive(maxActive);
        return this;
    }

    public DatasourceBuilder maxWaitMs(long maxWaitMs) {
        databaseConfig.setMaxWaitMs(maxWaitMs);
        return this;
    }

    public DatasourceBuilder minIdle(int minIdle) {
        databaseConfig.setMinIdle(minIdle);
        return this;
    }

    public Datasource build() throws JdbcException {
        databaseConfig.isValid();
        return databaseConfig.getDatabaseType().initDataSource(databaseConfig);
    }

}