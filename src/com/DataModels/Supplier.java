package com.DataModels;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class Supplier {

    private ObjectProperty<Integer> supplierID = new SimpleObjectProperty<>();
    private StringProperty supplierName = new SimpleStringProperty();
    private StringProperty supplierRemarks = new SimpleStringProperty();
    private ObjectProperty<Image> supplierPhoto = new SimpleObjectProperty<>();

    @Override
    public String toString() {
        return supplierName.get();
    }

    public Integer getSupplierID() {
        return supplierID.get();
    }

    public ObjectProperty<Integer> supplierIDProperty() {
        return supplierID;
    }

    public void setSupplierID(Integer supplierID) {
        this.supplierID.set(supplierID);
    }

    public String getSupplierName() {
        return supplierName.get();
    }

    public StringProperty supplierNameProperty() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName.set(supplierName);
    }

    public String getSupplierRemarks() {
        return supplierRemarks.get();
    }

    public StringProperty supplierRemarksProperty() {
        return supplierRemarks;
    }

    public void setSupplierRemarks(String supplierRemarks) {
        this.supplierRemarks.set(supplierRemarks);
    }

    public Image getSupplierPhoto() {
        return supplierPhoto.get();
    }

    public ObjectProperty<Image> supplierPhotoProperty() {
        return supplierPhoto;
    }

    public void setSupplierPhoto(Image supplierPhoto) {
        this.supplierPhoto.set(supplierPhoto);
    }

    public ObjectProperty<ImageView> imageViewProperty() {
        ObjectProperty<ImageView> imgViewProp = new SimpleObjectProperty<>();
        Circle circle = new Circle(47.5,47.5,47.5);

        ImageView imgView = new ImageView(supplierPhoto.get());
        imgView.setFitHeight(95);
        imgView.setFitWidth(95);
        imgView.setClip(circle);
        imgViewProp.set(imgView);
        return imgViewProp;
    }

    public StringProperty supplierDetailsProperty() {
        StringProperty supDetails = new SimpleStringProperty();
        String rawInfo = supplierName.get();
        supDetails.set(rawInfo);
        return supDetails;
    }
}
