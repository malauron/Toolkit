package com.DataModels;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Date;

public class Billing {


    private ObjectProperty<Integer> billingID = new SimpleObjectProperty<>();
    private ObjectProperty<Supplier> supplier = new SimpleObjectProperty<>();
    private StringProperty billingRefNo = new SimpleStringProperty();
    private ObjectProperty<Date> transactionDate = new SimpleObjectProperty<>();
    private ObjectProperty<Double> billingAmount = new SimpleObjectProperty<>();
    private StringProperty remarks = new SimpleStringProperty();

    @Override
    public String toString() {
        return "Supplier: " + supplier.get().getSupplierName() + " Ref#: " + billingRefNo.toString() + "Amount: " + billingAmount.get().toString();
    }

    public Integer getBillingID() {
        return billingID.get();
    }

    public ObjectProperty<Integer> billingIDProperty() {
        return billingID;
    }

    public void setBillingID(Integer billingID) {
        this.billingID.set(billingID);
    }

    public Supplier getSupplier() {
        return supplier.get();
    }

    public ObjectProperty<Supplier> supplierProperty() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier.set(supplier);
    }

    public String getBillingRefNo() {
        return billingRefNo.get();
    }

    public StringProperty billingRefNoProperty() {
        return billingRefNo;
    }

    public void setBillingRefNo(String billingRefNo) {
        this.billingRefNo.set(billingRefNo);
    }

    public Date getTransactionDate() {
        return transactionDate.get();
    }

    public ObjectProperty<Date> transactionDateProperty() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate.set(transactionDate);
    }

    public Double getBillingAmount() {
        return billingAmount.get();
    }

    public ObjectProperty<Double> billingAmountProperty() {
        return billingAmount;
    }

    public void setBillingAmount(Double billingAmount) {
        this.billingAmount.set(billingAmount);
    }

    public String getRemarks() {
        return remarks.get();
    }

    public StringProperty remarksProperty() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks.set(remarks);
    }

    public StringProperty billingDetailsProperty() {
        StringProperty billingDetails = new SimpleStringProperty();
        String rawInfo = supplier.get().getSupplierName() + "\n" + billingRefNo.get() ;
        billingDetails.set(rawInfo);
        return billingDetails;
    }
}
