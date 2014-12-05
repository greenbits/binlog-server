package com.greenbits.binlog;

import com.google.code.or.common.glossary.Row;

import java.util.List;

public class WriteEvent {
    private String tableName;
    private List<Row> rows;
    private int columnCount;
    private byte[] usedColumns;
    private byte[] extraInfo;

    public WriteEvent(String tableName,
                      List<Row> rows,
                      int columnCount,
                      byte[] usedColumns,
                      byte[] extraInfo) {

        this.tableName = tableName;
        this.rows = rows;
        this.columnCount = columnCount;
        this.usedColumns = usedColumns;
        this.extraInfo = extraInfo;
    }

    public String getTableName() {
        return tableName;
    }

    public List<Row> getRows() {
        return rows;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public byte[] getUsedColumns() {
        return usedColumns;
    }

    public byte[] getExtraInfo() {
        return extraInfo;
    }
}
