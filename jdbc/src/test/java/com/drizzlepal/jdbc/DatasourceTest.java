package com.drizzlepal.jdbc;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter.Feature;
import com.drizzlepal.jdbc.exception.JdbcException;
import com.drizzlepal.jdbc.metadata.TableMetaData;

public class DatasourceTest {

    @Test
    public void testGetDatasource() throws JdbcException, SQLException {
        DataSource datasource = DataSourceBuilder.builder(DatabaseType.MYSQL).host("127.0.0.1").port(3306)
                .userName("root").password("KJGBKAHJSKDAHBS@$@Qadf@$!@wf(*&").database("drptest").build();
        List<String> defaultColumnsResultSetMetaDataColumnLabels = datasource
                .getDefaultColumnsResultSetMetaDataColumnLabels("data_exchange_log");
        System.out.println(defaultColumnsResultSetMetaDataColumnLabels);
        List<String> tableNames = datasource.getTableNames();
        System.out.println(tableNames);
        TableMetaData tableMetaData = datasource.getTableMetaData("lab_management");
        System.out.println(JSON.toJSONString(tableMetaData, Feature.PrettyFormat, Feature.WriteMapNullValue));
    }

}
