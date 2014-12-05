package com.greenbits.binlog;

/**
 * Contains all the information regarding the binlog version for the
 * master database server.
 */
public class ServerVersion {
    private int version;
    private long createdTimestamp;
    private String serverVersion;

    public ServerVersion(int version, long createdTimestamp, String serverVersion) {
        this.version = version;
        this.createdTimestamp = createdTimestamp;
        this.serverVersion = serverVersion;
    }

    public int getVersion() {
        return version;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public String getServerVersion() {
        return serverVersion;
    }
}
