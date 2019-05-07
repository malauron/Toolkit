package com.DataAccessObjects;

import com.Utilities.ConnectionMain;
import com.Interfaces.IUserGroups;
import com.DataModels.UserGroup;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.ResultSet;

import static com.DataAccessObjects.DataAccessConstants.COL_USERGROUP_ID;
import static com.DataAccessObjects.DataAccessConstants.COL_USERGROUP_NAME;
import static com.DataAccessObjects.DataAccessConstants.TBL_USERGROUPS;

public class UserGroups implements IUserGroups {

    @Override
    public UserGroup getUserGroup(int userGroupID) {
        UserGroup userGroup;
        String sql = "select "+ COL_USERGROUP_ID +",ucase("+ COL_USERGROUP_NAME +") usergroup_name " +
                     "from "+ TBL_USERGROUPS +" where "+ COL_USERGROUP_ID +" = " + userGroupID + " " +
                     "order by " + COL_USERGROUP_NAME;
        try {
            ResultSet rs = ConnectionMain.getInstance().getResultSet(sql);
            while (rs.next()) {
                userGroup = new UserGroup();
                userGroup.setUserGroupID(rs.getInt(COL_USERGROUP_ID));
                userGroup.setUserGroupName(rs.getString(COL_USERGROUP_NAME));
                return userGroup;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ObservableList<UserGroup> getUserGroups() {
        ObservableList<UserGroup> userGroups = FXCollections.observableArrayList();
        String sql = "select "+ COL_USERGROUP_ID +",ucase("+ COL_USERGROUP_NAME +") usergroup_name " +
                     "from "+ TBL_USERGROUPS +" order by " + COL_USERGROUP_NAME;

        try {
            ResultSet rs = ConnectionMain.getInstance().getResultSet(sql);
            while (rs.next()) {
                UserGroup userGroup = new UserGroup();
                userGroup.setUserGroupID(rs.getInt(COL_USERGROUP_ID));
                userGroup.setUserGroupName(rs.getString(COL_USERGROUP_NAME));
                userGroups.add(userGroup);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userGroups;
    }

    @Override
    public void insertUserGroup(UserGroup userGroup) {

    }

    @Override
    public void updateUserGroup(UserGroup userGroup) {

    }
}
