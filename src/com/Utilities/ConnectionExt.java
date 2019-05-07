package com.Utilities;

public class ConnectionExt extends ConnectionMSSQL {
    private static ConnectionExt INSTANCE;
    private ConnectionExt() throws Exception {}
    public static ConnectionExt getInstance() throws Exception {
        if (INSTANCE == null) {
            synchronized(ConnectionExt.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ConnectionExt();
                }
            }
        }
        return INSTANCE;
    }
}
