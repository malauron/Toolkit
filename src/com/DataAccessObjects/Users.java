package com.DataAccessObjects;

import com.Utilities.ConnectionMain;
import com.Utilities.CryptoUtil;
import com.Interfaces.IUsers;
import com.DataModels.User;
import com.Utilities.ExecuteStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static com.DataAccessObjects.DataAccessConstants.*;


public class Users implements IUsers{

    @Override
    public User getUser(String username, String password) {

        String sql = "SELECT x1."+ COL_USER_ID +", ucase(x1."+ COL_FULLNAME +") fullname, x1."+ COL_USERNAME +", x1."+ COL_USERGROUP_ID +", x1."+ COL_PASSWORD +", x2."+ COL_USRFILE_BLOB +" " +
                "FROM "+ TBL_USERS +" x1 " +
                "left outer join "+ TBL_USERS_PHOTO +" x2 ON x1."+ COL_USER_ID +" = x2."+ COL_USER_ID +" " +
                "where x1."+ COL_USERNAME +" = ? and x1."+ COL_PASSWORD +" = ?";

        try {
            PreparedStatement ps = ConnectionMain.getInstance().cn().prepareStatement(sql);
            ps.setString(1,username);
            ps.setString(2,password);
            ResultSet rs = ps.executeQuery();
            User user = null;
            if (rs.next()) {
                user = setUser(rs);
            } else {

            }
            rs.close();
            ps.close();
            return user;
        } catch (Exception e) {
            System.err.println("Error: ");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ObservableList<User> getUsers(String param) {

        ObservableList<User> users = FXCollections.observableArrayList();
        String sql = "SELECT x1."+ COL_USER_ID +",ucase(x1."+ COL_FULLNAME +") "+ COL_FULLNAME +",x1."+ COL_USERNAME +",x1."+ COL_USERGROUP_ID +",x1."+ COL_PASSWORD +", x2."+ COL_USRFILE_BLOB +" " +
                "FROM "+ TBL_USERS + " x1 " +
                "left outer join "+ TBL_USERS_PHOTO +" x2 on x1."+ COL_USER_ID +" = x2."+ COL_USER_ID + " " +
                "WHERE x1." + COL_FULLNAME + " like ? " ;

        try {

            PreparedStatement ps = ConnectionMain.getInstance().cn().prepareStatement(sql);
            ps.setString(1, "%" + param + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(setUser(rs));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    @Override
    public ExecuteStatus handleUser(User user) {

        ExecuteStatus executeStatus;

        try {

            ConnectionMain.getInstance().cn().setAutoCommit(false);
            PreparedStatement ps;
            String sql;

            if(user.getUserID() == 0) {

                sql = "insert into "+ TBL_USERS +" ("+ COL_FULLNAME +", "+ COL_USERNAME +", "+ COL_PASSWORD +", "+ COL_USERGROUP_ID +") " +
                        "values (?, ?, ?, ?)";
                ps = ConnectionMain.getInstance().cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getFullName());
                ps.setString(2, user.getUserName());
                ps.setString(3,CryptoUtil.getInstance().encrypt(user.getPassword()));
                ps.setInt(4, user.getUserGroup().getUserGroupID());
                ps.executeUpdate();
                ResultSet lastID = ps.getGeneratedKeys();
                lastID.next();
                user.setUserID(lastID.getInt(1));
                lastID.close();
                executeStatus = ExecuteStatus.SAVED;
            } else {

                sql = "update "+ TBL_USERS +" set "+ COL_FULLNAME +" = ?, "+ COL_PASSWORD +" = ?, "+ COL_USERGROUP_ID +" = ?  " +
                        "where "+ COL_USER_ID +" = ?";
                ps = ConnectionMain.getInstance().cn().prepareStatement(sql);
                ps.setString(1, user.getFullName());
                ps.setString(2,CryptoUtil.getInstance().encrypt(user.getPassword()));
                ps.setInt(3, user.getUserGroup().getUserGroupID());
                ps.setInt(4, user.getUserID());
                ps.executeUpdate();
                executeStatus = ExecuteStatus.UPDATED;
            }
            ps.close();
            sql = "delete from "+ TBL_USERS_PHOTO +" where "+ COL_USER_ID +" = " + user.getUserID();
            ps = ConnectionMain.getInstance().cn().prepareStatement(sql);
            ps.executeUpdate();

            if(user.getUserPhoto() != null) {
                sql = "insert into "+ TBL_USERS_PHOTO +" ("+ COL_USER_ID +","+ COL_USRFILE_SIZE +", "+ COL_USRFILE_BLOB +") values (?,?,?)";
                ps = ConnectionMain.getInstance().cn().prepareStatement(sql);
                ps.setInt(1,user.getUserID());

                BufferedImage bImage = SwingFXUtils.fromFXImage(user.getUserPhoto(),null);
                ByteArrayOutputStream osPhoto = new ByteArrayOutputStream();
                ImageIO.write(bImage,"jpg",osPhoto);
                byte[] res = osPhoto.toByteArray();
                ps.setInt(2, osPhoto.size());
                InputStream inputStream = new ByteArrayInputStream(res);
                osPhoto.close();

                ps.setBinaryStream(3,inputStream);

                ps.executeUpdate();
            }
            ps.close();
            ConnectionMain.getInstance().cn().commit();

        } catch (Exception e) {
            executeStatus = ExecuteStatus.ERROR_OCCURED;
            e.printStackTrace();
            try {
                ConnectionMain.getInstance().cn().rollback();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                ConnectionMain.getInstance().cn().setAutoCommit(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return executeStatus;
    }

    private User setUser(ResultSet rs) {

        try {

            User user = new User();
            UserGroups userGroups = new UserGroups();
            Blob blobPhoto;
            InputStream isPhoto;

            if(rs.getBlob(COL_USRFILE_BLOB) != null) {
                blobPhoto = rs.getBlob(COL_USRFILE_BLOB);
                isPhoto = blobPhoto.getBinaryStream();
                user.setUserPhoto(new Image(isPhoto));
                isPhoto.close();
            }

            user.setUserID(rs.getInt(COL_USER_ID));
            user.setUserGroup(userGroups.getUserGroup(rs.getInt(COL_USERGROUP_ID)));
            user.setFullName(rs.getString(COL_FULLNAME));
            user.setUserName(rs.getString(COL_USERNAME));
            user.setPassword(CryptoUtil.getInstance().decrypt(rs.getString(COL_PASSWORD)));

            return user;

        } catch (Exception e) {

            System.err.println("Error: ");
            e.printStackTrace();

        }

        return null;
    }
}














