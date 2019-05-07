package com.Interfaces;

import com.DataModels.UserGroup;
import javafx.collections.ObservableList;

public interface IUserGroups {
    UserGroup getUserGroup(int userTypeID);
    ObservableList<UserGroup> getUserGroups();
    void insertUserGroup(UserGroup userGroup);
    void updateUserGroup(UserGroup userGroup);
}
