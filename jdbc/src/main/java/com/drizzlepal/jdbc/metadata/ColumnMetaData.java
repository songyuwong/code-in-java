package com.drizzlepal.jdbc.metadata;

import lombok.Data;

@Data
public class ColumnMetaData {

    private String name;

    private String remarks;

    private int datatype;

    private String typeName;

    private Integer length;

    private Integer ordinalPosition;

}
