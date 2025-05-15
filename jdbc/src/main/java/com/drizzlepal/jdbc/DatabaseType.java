package com.drizzlepal.jdbc;

import java.util.function.Function;
import java.util.function.Supplier;

import com.drizzlepal.jdbc.database.mysql.DatabaseConfigMysql;
import com.drizzlepal.jdbc.database.mysql.DatasourceMysql;
import com.drizzlepal.jdbc.database.sqlserver.DatabaseConfigSqlServer;
import com.drizzlepal.jdbc.database.sqlserver.DatasourceSqlServer;

import lombok.Getter;

public enum DatabaseType {

    MYSQL(config -> new DatasourceMysql(config), () -> new DatabaseConfigMysql(), "com.mysql.cj.jdbc.Driver",
            "SELECT 1", new String[] { "TABLE" }, null, null),

    SQL_SERVER(config -> new DatasourceSqlServer(config), () -> new DatabaseConfigSqlServer(),
            "com.microsoft.sqlserver.jdbc.SQLServerDriver", "SELECT 1", new String[] { "TABLE" }, null, null);

    private final Function<DatabaseConfigCommon, DatasourceCommon> datasourceSupplier;

    private final Supplier<DatabaseConfigCommon> databaseConfigSupplier;

    @Getter
    private final String driverClassName;

    @Getter
    private final String connectionInitSql;

    @Getter
    private final String databaseMetaDataQuerySql;

    @Getter
    private final DatabaseMetaDataQuerySqlParameterSetter databaseMetaDataQuerySqlParameterSetter;

    @Getter
    private final String[] databaseTableTypes;

    private DatabaseType(Function<DatabaseConfigCommon, DatasourceCommon> datasourceSupplier,
            Supplier<DatabaseConfigCommon> databaseConfigSupplier, String driverClassName, String connectionInitSql,
            String[] databaseTableTypes,
            String databaseMetaDataQuerySql,
            DatabaseMetaDataQuerySqlParameterSetter databaseMetaDataQuerySqlParameterSetter) {
        this.datasourceSupplier = datasourceSupplier;
        this.databaseConfigSupplier = databaseConfigSupplier;
        this.driverClassName = driverClassName;
        this.connectionInitSql = connectionInitSql;
        this.databaseMetaDataQuerySql = databaseMetaDataQuerySql;
        this.databaseMetaDataQuerySqlParameterSetter = databaseMetaDataQuerySqlParameterSetter;
        this.databaseTableTypes = databaseTableTypes;
    }

    DatasourceCommon initDataSource(DatabaseConfigCommon config) {
        return datasourceSupplier.apply(config);
    }

    DatabaseConfigCommon initDataConfig() {
        return databaseConfigSupplier.get();
    }

}
