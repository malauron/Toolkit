package com.Utilities;

import com.DataModels.User;

public class CurrentUser extends User {
    private static CurrentUser INSTANCE = new CurrentUser();
    private CurrentUser() {}
    public static CurrentUser getInstance() {
        return INSTANCE;
    }
}
