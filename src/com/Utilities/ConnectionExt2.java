package com.Utilities;

public class ConnectionExt2 extends ConnectionPSQL {
    private static ConnectionExt2 INSTANCE;
    public static ConnectionExt2 getInstance() throws Exception {
        if (INSTANCE == null) {
            synchronized(ConnectionExt2.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ConnectionExt2();
                }
            }
        }
        return INSTANCE;
    }
}
