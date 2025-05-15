package com.drizzlepal.jdbc.metadata;

import java.util.ArrayList;

import lombok.Data;

@Data
public class TableMetaData {

    private String name;

    private String remarks;

    private ArrayList<ColumnMetaData> columns;

}
