package com.greenbits.binlog;

import com.google.code.or.OpenReplicator;

import java.util.concurrent.TimeUnit;

/**
 * The server that parses Herer's master database and ETL's models into a reporting database via
 * a MySQL binlog stream.
 */
public class RowServer {
    private OpenReplicator server;
    private String user;
    private String password;
    private String host;
    private int port;
    private int serverId;
    private PositionCheckpointer checkpointer;
    private RowEventListener listener;

    public RowServer() {
        server = new OpenReplicator();
        port = -1;
        serverId = -1;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public PositionCheckpointer getCheckpointer() {
        return checkpointer;
    }

    public void setCheckpointer(PositionCheckpointer checkpointer) {
        this.checkpointer = checkpointer;
    }

    public RowEventListener getListener() {
        return listener;
    }

    public void setListener(RowEventListener listener) {
        this.listener = listener;
    }

    public void start() throws Exception {
        assertOption(user != null, "User");
        assertOption(password != null, "Password");
        assertOption(host != null, "Host");
        assertOption(port != -1, "Port");
        assertOption(serverId != -1, "Server ID");
        assertOption(checkpointer != null, "Checkpointer");
        assertOption(listener != null, "Listener");

        server.setUser(user);
        server.setPassword(password);
        server.setHost(host);
        server.setPort(port);
        server.setServerId(serverId);
        server.setBinlogFileName(checkpointer.getFileName());
        server.setBinlogPosition(checkpointer.getPosition());
        server.setBinlogEventListener(new RowEventListenerAdapter(listener, checkpointer));

        server.start();
    }

    public void stop(int seconds) throws Exception {
        if (server != null) {
            server.stop(seconds, TimeUnit.SECONDS);
        }
    }

    private void assertOption(boolean valid, String option) {
        if (!valid) {
            throw new IllegalArgumentException(option + "is required.");
        }
    }
}

