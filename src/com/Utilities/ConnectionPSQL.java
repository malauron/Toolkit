package com.Utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionPSQL {

    private Connection connection;
    private String serverName;
    private String userName;
    private String portNumber;
    private String databaseName;

    public void setConnection(String serverName, String username, String password, String portNumber, String databaseName) throws SQLException, Exception {
        this.serverName = serverName;
        this.userName = username;
        this.portNumber = portNumber;
        this.databaseName = databaseName;
        String connectionURL = "jdbc:pervasive://"+ serverName +":"+ portNumber +"/"+ databaseName + "?transport=tcp";
        Class.forName("com.pervasive.jdbc.v2.Driver");
        connection = DriverManager.getConnection(connectionURL,username,password);
    }

    public Connection getConnection() {
        return connection;
    }

    public String getServerName() {
        return serverName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPortNumber() {
        return portNumber;
    }

    public String getDatabaseName() {
        return databaseName;
    }
}
