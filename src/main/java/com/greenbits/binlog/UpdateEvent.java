package com.greenbits.binlog;

import com.google.code.or.common.glossary.Pair;
import com.google.code.or.common.glossary.Row;

import java.util.List;

public class UpdateEvent {
    private final String tableName;
    private final List<Pair<Row>> rows;
    private final int columnCount;
    private final byte[] usedColumnsBefore;
    private final byte[] usedColumnsAfter;
    private final byte[] extraInfo;

    public UpdateEvent(String tableName,
                       List<Pair<Row>> rows,
                       int columnCount,
                       byte[] usedColumnsBefore,
                       byte[] usedColumnsAfter,
                       byte[] extraInfo) {

        this.tableName = tableName;
        this.rows = rows;
        this.columnCount = columnCount;
        this.usedColumnsBefore = usedColumnsBefore;
        this.usedColumnsAfter = usedColumnsAfter;
        this.extraInfo = extraInfo;
    }

    public String getTableName() {
        return tableName;
    }

    public List<Pair<Row>> getRows() {
        return rows;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public byte[] getUsedColumnsBefore() {
        return usedColumnsBefore;
    }

    public byte[] getUsedColumnsAfter() {
        return usedColumnsAfter;
    }

    public byte[] getExtraInfo() {
        return extraInfo;
    }
}
