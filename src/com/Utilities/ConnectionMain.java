package com.Utilities;

public class ConnectionMain extends ConnectionMySQL {
    private static ConnectionMain INSTANCE;
    private ConnectionMain() throws Exception {}
    public static ConnectionMain getInstance() throws Exception {
        if (INSTANCE == null) {
            synchronized(ConnectionMain.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ConnectionMain();
                }
            }
        }
        return INSTANCE;
    }
}
