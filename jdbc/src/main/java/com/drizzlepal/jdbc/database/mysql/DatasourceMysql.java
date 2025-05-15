package com.drizzlepal.jdbc.database.mysql;

import com.drizzlepal.jdbc.DatabaseConfigCommon;
import com.drizzlepal.jdbc.DatasourceCommon;
import com.drizzlepal.utils.StringUtils;

public class DatasourceMysql extends DatasourceCommon {

    public DatasourceMysql(DatabaseConfigCommon configCommon) {
        super(configCommon);
    }

    @Override
    public String buildJdbcUrl(DatabaseConfigCommon configCommon) {
        StringBuilder stringBuilder = new StringBuilder("jdbc:mysql://");
        stringBuilder.append(configCommon.getHost());
        stringBuilder.append(":");
        stringBuilder.append(configCommon.getPort());
        if (StringUtils.isNotBlank(configCommon.getDatabase())) {
            stringBuilder.append("/");
            stringBuilder.append(configCommon.getDatabase());
        }
        stringBuilder.append("?");
        configCommon.getConnectionParams().forEach((k, v) -> stringBuilder.append("&").append(k).append("=").append(v));
        return stringBuilder.toString();
    }

    @Override
    protected boolean checkDatabaseNameConfigExists() {
        return configCommon.getDatabase() != null && !configCommon.getDatabase().isEmpty();
    }

}
