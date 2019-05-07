package com.DataModels;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class User {

    private ObjectProperty<Integer> userID = new SimpleObjectProperty<>();
    private ObjectProperty<UserGroup> userGroup = new SimpleObjectProperty<>();
    private StringProperty fullName = new SimpleStringProperty();
    private StringProperty userName = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private ObjectProperty<Image> userPhoto = new SimpleObjectProperty<>();

    @Override
    public String toString() {
        return fullName.toString();
    }

    public Integer getUserID() {
        return userID.get();
    }

    public ObjectProperty<Integer> userIDProperty() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID.set(userID);
    }

    public UserGroup getUserGroup() {
        return userGroup.get();
    }

    public ObjectProperty<UserGroup> userGroupProperty() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup.set(userGroup);
    }

    public String getFullName() {
        return fullName.get();
    }

    public StringProperty fullNameProperty() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName.set(fullName);
    }

    public String getUserName() {
        return userName.get();
    }

    public StringProperty userNameProperty() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName.set(userName);
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

    public Image getUserPhoto() {
        return userPhoto.get();
    }

    public ObjectProperty<Image> userPhotoProperty() {
        return userPhoto;
    }

    public void setUserPhoto(Image userPhoto) {
        this.userPhoto.set(userPhoto);
    }

    public ObjectProperty<ImageView> imageViewProperty() {
        ObjectProperty<ImageView> imgViewProp = new SimpleObjectProperty<>();
        Circle circle = new Circle(47.5,47.5,47.5);

        ImageView imgView = new ImageView(userPhoto.get());
        imgView.setFitHeight(95);
        imgView.setFitWidth(95);
        imgView.setClip(circle);
        imgViewProp.set(imgView);
        //imgView.setPreserveRatio(false);
        return imgViewProp;
    }

    public StringProperty userDetailsProperty() {
        StringProperty usrDetails = new SimpleStringProperty();
        String rawInfo = fullName.get() + " (" + userName.get() + ")\n" + userGroup.get().getUserGroupName();
        usrDetails.set(rawInfo);
        return usrDetails;
    }
}
