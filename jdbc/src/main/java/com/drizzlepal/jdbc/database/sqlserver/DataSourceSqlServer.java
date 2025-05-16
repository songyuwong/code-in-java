package com.drizzlepal.jdbc.database.sqlserver;

import com.drizzlepal.jdbc.DatabaseConfigCommon;
import com.drizzlepal.jdbc.DataSourceCommon;

public class DataSourceSqlServer extends DataSourceCommon {

    public DataSourceSqlServer(DatabaseConfigCommon configCommon) {
        super(configCommon);
    }

    @Override
    public String buildJdbcUrl(DatabaseConfigCommon configCommon) {
        return String.format("jdbc:sqlserver://%s:%s;databaseName=%s", configCommon.getHost(), configCommon.getPort(),
                configCommon.getDatabase());
    }

    @Override
    protected boolean checkDatabaseNameConfigExists() {
        return configCommon.getSchema() != null && !configCommon.getSchema().isEmpty()
                && configCommon.getDatabase() != null && !configCommon.getDatabase().isEmpty();
    }

}
