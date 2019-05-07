/*
 *
 * 1. Class name should be specified for the connection settings. i.e. Class.forName("com.mysql.cj.jdbc.Driver");
 *
 */
package com.Utilities;

import java.sql.*;
import java.util.prefs.Preferences;

public class ConnectionMySQL {

    private Connection connection;

    private String server;
    private String username;
    private String password;
    private String port;
    private String database;
    private String url;

    public ConnectionMySQL() throws Exception {

        Preferences connectionSettings = Preferences.userRoot().node("mysqlserver");
        server = connectionSettings.get("server","");
        username = connectionSettings.get("username","");
        port = connectionSettings.get("port","");
        database = connectionSettings.get("database","");
        url = "jdbc:mysql://" + server + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
        password = CryptoUtil.getInstance().decrypt(connectionSettings.get("password",""));
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(url, username, password);

    }

    public ConnectionMySQL(String server, String username, String password, String port, String database) throws SQLException,Exception {
        String url = "jdbc:mysql://" + server + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(url, username, password);
    }

    public Connection cn() {
        return connection;
    }

    public String getServer() {
        return server;
    }

    public String getDatabase() {
        return database;
    }

    public ResultSet getResultSet(String query) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        return rs;
    }

    public ResultSet getResultSet(String query,String[] args) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(query);
        for (int i=0 ; i < args.length ; i++){
            stmt.setString(i+1, args[i]);
        }
        ResultSet rs = stmt.executeQuery();
        return rs;
    }
}
