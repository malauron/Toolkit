package com.DataModels;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UserGroup {

    private ObjectProperty<Integer> userGroupID = new SimpleObjectProperty<>();
    private StringProperty userGroupName = new SimpleStringProperty();

    @Override
    public String toString() {
        return userGroupName.get();
    }

    public Integer getUserGroupID() {
        return userGroupID.get();
    }

    public ObjectProperty<Integer> userGroupIDProperty() {
        return userGroupID;
    }

    public void setUserGroupID(Integer userGroupID) {
        this.userGroupID.set(userGroupID);
    }

    public String getUserGroupName() {
        return userGroupName.get();
    }

    public StringProperty userGroupNameProperty() {
        return userGroupName;
    }

    public void setUserGroupName(String userGroupName) {
        this.userGroupName.set(userGroupName);
    }
}
