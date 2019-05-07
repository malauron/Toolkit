package com.DataModels;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Company {

    public enum DBType {PSQL,MSSQL};

    private ObjectProperty<DBType> dbType = new SimpleObjectProperty<>();
    private StringProperty cmpName = new SimpleStringProperty();
    private StringProperty dbName = new SimpleStringProperty();
    private StringProperty server = new SimpleStringProperty();
    private StringProperty port = new SimpleStringProperty();
    private StringProperty username = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();

    public DBType getDbType() {
        return dbType.get();
    }

    public ObjectProperty<DBType> dbTypeProperty() {
        return dbType;
    }

    public void setDbType(DBType dbType) {
        this.dbType.set(dbType);
    }

    public String getCmpName() {
        return cmpName.get();
    }

    public StringProperty cmpNameProperty() {
        return cmpName;
    }

    public void setCmpName(String cmpName) {
        this.cmpName.set(cmpName);
    }

    public String getDbName() {
        return dbName.get();
    }

    public StringProperty dbNameProperty() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName.set(dbName);
    }

    public String getServer() {
        return server.get();
    }

    public StringProperty serverProperty() {
        return server;
    }

    public void setServer(String server) {
        this.server.set(server);
    }

    public String getPort() {
        return port.get();
    }

    public StringProperty portProperty() {
        return port;
    }

    public void setPort(String port) {
        this.port.set(port);
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    @Override
    public String toString() {
        return cmpName.get().toUpperCase();
    }
}
