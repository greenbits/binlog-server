package com.greenbits.binlog;

import com.google.code.or.binlog.BinlogEventListener;
import com.google.code.or.binlog.BinlogEventV4;
import com.google.code.or.binlog.BinlogEventV4Header;
import com.google.code.or.binlog.impl.event.*;

import java.util.HashMap;

public class RowEventListenerAdapter implements BinlogEventListener {
    private RowEventListener listener;
    private HashMap<Long, TableMapEvent> tableIdToTableMapEvent;
    private String databaseName;

    private PositionCheckpointer checkpointer;

    public RowEventListenerAdapter(RowEventListener listener,
                                   PositionCheckpointer checkpointer,
                                   String databaseName) {

        this.listener = listener;
        this.tableIdToTableMapEvent = new HashMap<Long, TableMapEvent>();
        this.checkpointer = checkpointer;
        this.databaseName = databaseName;
    }

    @Override
    public void onEvents(BinlogEventV4 event) {
        try {
            if (event instanceof FormatDescriptionEvent) {
                handleFormatDescriptionEvent((FormatDescriptionEvent) event);
            } else if (event instanceof WriteRowsEventV2) {
                handleWriteRowsEvent((WriteRowsEventV2) event);
            } else if (event instanceof UpdateRowsEventV2) {
                handleUpdateRowsEvent((UpdateRowsEventV2) event);
            } else if (event instanceof DeleteRowsEventV2) {
                handleDeleteRowsEvent((DeleteRowsEventV2) event);
            } else if (event instanceof RotateEvent) {
                handleRotateEvent((RotateEvent) event);
            } else if (event instanceof TableMapEvent) {
                handleTableMapEvent((TableMapEvent) event);
            } else if (event instanceof QueryEvent) {
                handleQueryEvent((QueryEvent) event);
            } else if (event instanceof XidEvent) {
                handleXidEvent((XidEvent)event);
            } else {
                handleUnknownEvent(event);
            }
        } catch (Throwable t) {
            handleError(t, event);
        }
    }

    private void handleFormatDescriptionEvent(FormatDescriptionEvent event) {
        listener.startup(new ServerVersion(
                event.getBinlogVersion(),
                event.getCreateTimestamp(),
                event.getServerVersion().toString()));
    }

    private void handleWriteRowsEvent(WriteRowsEventV2 event) {
        TableMapEvent tableMapEvent = getTableMapEvent(event.getTableId());
        if (shouldSkipOnDatabaseName(tableMapEvent.getDatabaseName().toString())) return;

        WriteEvent writeEvent = new WriteEvent(tableMapEvent,
                event.getRows(),
                event.getColumnCount().intValue(),
                event.getUsedColumns().getValue(),
                event.getExtraInfo());

        listener.onWrite(writeEvent);
        checkpointer.checkpoint(getNextPosition(event));
    }

    private void handleUpdateRowsEvent(UpdateRowsEventV2 event) {
        TableMapEvent tableMapEvent = getTableMapEvent(event.getTableId());
        if (shouldSkipOnDatabaseName(tableMapEvent.getDatabaseName().toString())) return;

        UpdateEvent updateEvent = new UpdateEvent(tableMapEvent,
                event.getRows(),
                event.getColumnCount().intValue(),
                event.getUsedColumnsBefore().getValue(),
                event.getUsedColumnsAfter().getValue(),
                event.getExtraInfo());

        listener.onUpdate(updateEvent);
        checkpointer.checkpoint(getNextPosition(event));
    }

    private void handleDeleteRowsEvent(DeleteRowsEventV2 event) {
        TableMapEvent tableMapEvent = getTableMapEvent(event.getTableId());
        if (shouldSkipOnDatabaseName(tableMapEvent.getDatabaseName().toString())) return;

        DeleteEvent deleteEvent = new DeleteEvent(tableMapEvent,
                event.getRows(),
                event.getColumnCount().intValue(),
                event.getUsedColumns().getValue(),
                event.getExtraInfo());

        listener.onDelete(deleteEvent);
        checkpointer.checkpoint(getNextPosition(event));
    }

    private void handleQueryEvent(QueryEvent event) {
        String sql = event.getSql().toString();
        if (sql.equals("BEGIN")) {
            listener.beginTransaction();
        } else if (sql.equals("COMMIT")) {
            commitTransaction(event);
        } else {
            listener.onQuery(sql);
        }
    }

    private void handleXidEvent(XidEvent event) {
        commitTransaction(event);
    }

    private void handleTableMapEvent(TableMapEvent event) {
        tableIdToTableMapEvent.put(event.getTableId(), event);
    }

    private void handleRotateEvent(RotateEvent event) {
        checkpointer.rotate(event.getBinlogFileName().toString(), event.getBinlogPosition());
    }

    private void handleUnknownEvent(BinlogEventV4 event) {
        BinlogEventV4Header header = event.getHeader();
        throw new IllegalStateException("Unknown MySQL binlog event type " + header.getEventType() + ".");
    }

    private void handleError(Throwable t, BinlogEventV4 event) {
        listener.onError(t, event);
    }

    private void commitTransaction(AbstractBinlogEventV4 event) {
        checkpointer.checkpoint(getNextPosition(event));
        listener.commitTransaction();
    }

    private TableMapEvent getTableMapEvent(long tableId) {
        TableMapEvent tableMapEvent = tableIdToTableMapEvent.get(tableId);
        if (tableMapEvent == null) {
            throw new IllegalStateException("Cannot find table with id '" + tableId + "' in the map.");
        }
        return tableMapEvent;
    }

    private long getNextPosition(BinlogEventV4 event) {
        return event.getHeader().getNextPosition();
    }

    private boolean shouldSkipOnDatabaseName(String databaseName) {
        return this.databaseName != null && !this.databaseName.equalsIgnoreCase(databaseName);
    }
}
