package com.drizzlepal.jdbc.database.sqlserver;

import java.util.HashMap;

import com.drizzlepal.jdbc.DatabaseConfigCommon;
import com.drizzlepal.jdbc.DatabaseType;

public class DatabaseConfigSqlServer extends DatabaseConfigCommon {

    public DatabaseConfigSqlServer() {
        super(DatabaseType.SQL_SERVER, new HashMap<>());
    }

}
