package com.greenbits.binlog;

import com.google.code.or.binlog.impl.event.TableMapEvent;
import com.google.code.or.common.glossary.Row;

import java.util.List;

public class DeleteEvent {
    private final TableMapEvent tableMapEvent;
    private final List<Row> rows;
    private final int columnCount;
    private final byte[] usedColumns;
    private final byte[] extraInfo;

    public DeleteEvent(TableMapEvent tableMapEvent,
                       List<Row> rows,
                       int columnCount,
                       byte[] usedColumns,
                       byte[] extraInfo) {

        this.tableMapEvent = tableMapEvent;
        this.rows = rows;
        this.columnCount = columnCount;
        this.usedColumns = usedColumns;
        this.extraInfo = extraInfo;
    }

    public String getTableName() {
        return tableMapEvent.getTableName().toString();
    }

    public String getDatabaseName() {
        return tableMapEvent.getDatabaseName().toString();
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
