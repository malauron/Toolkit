package com.Interfaces;

import com.DataModels.User;
import com.Utilities.ExecuteStatus;
import javafx.collections.ObservableList;

public interface IUsers {
    ObservableList<User> getUsers(String param);
    User getUser(String username, String password);
    ExecuteStatus handleUser(User user);
}
