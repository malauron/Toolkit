/*
 *
 * 1. Class name should be specified for the connection settings. i.e. Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
 *
 * 2. MSSQL JDBC Driver has digital signatures (MSFTSIG.RSA & MSFTSIG.FS). When the JAR file is repackaged
 *
 *    these signatures become broken. Removing these files from the jar (either from original or artifact)
 *
 *    the program will start fine.
 *
 */

package com.Utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.prefs.Preferences;

public class ConnectionMSSQL {

    private Connection connection;
    private String serverName;
    private String userName;
    private String portNumber;
    private String databaseName;

    public ConnectionMSSQL()  {
        try {
            Preferences connectionSettings = Preferences.userRoot().node("mssqlserver");
            serverName = connectionSettings.get("server","");
            userName = connectionSettings.get("username","");
            portNumber = connectionSettings.get("port","");
            String connectionUrl = "jdbc:sqlserver://" + serverName + ":" + portNumber + ";";
            String decryptedPassword = CryptoUtil.getInstance().decrypt(connectionSettings.get("password",""));
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(connectionUrl, userName, decryptedPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ConnectionMSSQL(String serverName, String username, String encryptedPassword, String portNumber, String databaseName) throws SQLException, Exception {
        this.serverName = serverName;
        this.userName = username;
        this.portNumber = portNumber;
        this.databaseName = databaseName;
        String connectionURL = "jdbc:sqlserver://"+ serverName +":"+ portNumber +";databaseName="+ databaseName;
        String decryptedPassword = CryptoUtil.getInstance().decrypt(encryptedPassword);
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connection = DriverManager.getConnection(connectionURL,username,decryptedPassword);
    }

    public void setConnection(String serverName, String username, String password, String portNumber) throws SQLException, Exception {
        this.serverName = serverName;
        this.userName = username;
        this.portNumber = portNumber;
        String connectionURL = "jdbc:sqlserver://"+ serverName +":"+ portNumber +";";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connection = DriverManager.getConnection(connectionURL,username,password);
    }

    public Connection cn() {
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
