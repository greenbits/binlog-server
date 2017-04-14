package com.greenbits.binlog;

/**
 * Contains all the information regarding the binlog version for the
 * master database server.
 */
public class ServerVersion {
    private int version;
    private String serverVersion;

    public ServerVersion(int version, String serverVersion) {
        this.version = version;
        this.serverVersion = serverVersion;
    }

    public int getVersion() {
        return version;
    }

    public String getServerVersion() {
        return serverVersion;
    }
}
