package com.DataModels;

import com.Controllers.CheckDetailsMini;
import com.Utilities.FXMLPath;
import javafx.beans.property.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.math.BigDecimal;
import java.sql.Date;

public class CheckVoucher {

    private StringProperty databaseName = new SimpleStringProperty();
    private ObjectProperty<Integer> checkKey = new SimpleObjectProperty();
    private ObjectProperty<Integer> transRef = new SimpleObjectProperty<>();
    private StringProperty vendorCode = new SimpleStringProperty();
    private StringProperty vendorName = new SimpleStringProperty();
    private StringProperty checkNum = new SimpleStringProperty();
    private ObjectProperty<Date> checkDate = new SimpleObjectProperty<>();
    private ObjectProperty<Date> actualDate = new SimpleObjectProperty<>();
    private ObjectProperty<BigDecimal> checkAmount = new SimpleObjectProperty();
    private StringProperty accountNumber = new SimpleStringProperty();
    private StringProperty bankNum = new SimpleStringProperty();
    private ObjectProperty<Node> details = new SimpleObjectProperty();
    private BooleanProperty selected = new SimpleBooleanProperty();
    private ObjectProperty<CheckStatus> checkStatus = new SimpleObjectProperty<>();

    public enum CheckStatus {
        PENDING,
        FUNDED,
        OUTSTANDING,
        ENCASHED,
        CANCELED;

        @Override
        public String toString() {
            return this.name();
        }
    }

    public CheckVoucher() {
        setCheckStatus(CheckStatus.PENDING);
    }

    public String getDatabaseName() {
        return databaseName.get();
    }

    public StringProperty databaseNameProperty() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName.set(databaseName);
    }

    public Integer getCheckKey() {
        return checkKey.get();
    }

    public ObjectProperty<Integer> checkKeyProperty() {
        return checkKey;
    }

    public void setCheckKey(Integer checkKey) {
        this.checkKey.set(checkKey);
    }

    public Integer getTransRef() {
        return transRef.get();
    }

    public ObjectProperty<Integer> transRefProperty() {
        return transRef;
    }

    public void setTransRef(Integer transRef) {
        this.transRef.set(transRef);
    }

    public String getVendorCode() {
        return vendorCode.get();
    }

    public StringProperty vendorCodeProperty() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode.set(vendorCode);
    }

    public String getVendorName() {
        return vendorName.get();
    }

    public StringProperty vendorNameProperty() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName.set(vendorName);
    }

    public String getCheckNum() {
        return checkNum.get();
    }

    public StringProperty checkNumProperty() {
        return checkNum;
    }

    public void setCheckNum(String checkNum) {
        this.checkNum.set(checkNum);
    }

    public Date getCheckDate() {
        return checkDate.get();
    }

    public ObjectProperty<Date> checkDateProperty() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate.set(checkDate);
    }

    public Date getActualDate() {
        return actualDate.get();
    }

    public ObjectProperty<Date> actualDateProperty() {
        return actualDate;
    }

    public void setActualDate(Date actualDate) {
        this.actualDate.set(actualDate);
    }

    public BigDecimal getCheckAmount() {
        return checkAmount.get();
    }

    public ObjectProperty<BigDecimal> checkAmountProperty() {
        return checkAmount;
    }

    public void setCheckAmount(BigDecimal checkAmount) {
        this.checkAmount.set(checkAmount);
    }

    public String getAccountNumber() {
        return accountNumber.get();
    }

    public StringProperty accountNumberProperty() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber.set(accountNumber);
    }

    public String getBankNum() {
        return bankNum.get();
    }

    public StringProperty bankNumProperty() {
        return bankNum;
    }

    public void setBankNum(String bankNum) {
        this.bankNum.set(bankNum);
    }

    public Node getDetails() {
        return details.get();
    }

    public ObjectProperty<Node> detailsProperty() {
        FXMLLoader fxmlLoader;
        Node nodeDetails;
        try {
            fxmlLoader = FXMLPath.CHECKDETAILSMINI.getFXMLLoader();
            nodeDetails = fxmlLoader.load();
            CheckDetailsMini checkDetailsMini = fxmlLoader.getController();
            checkDetailsMini.setVoucherNo(transRef.get().toString());
            checkDetailsMini.setCheckNo(checkNum.get());
            checkDetailsMini.setDate(checkDate.get().toString());
            checkDetailsMini.setPayee(vendorName.get());
            checkDetailsMini.setAmount(checkAmount.get());
            details.set(nodeDetails);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    public void setDetails(Node details) {
        this.details.set(details);
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public CheckStatus getCheckStatus() {
        return checkStatus.get();
    }

    public ObjectProperty<CheckStatus> checkStatusProperty() {
        return checkStatus;
    }

    public void setCheckStatus(CheckStatus checkStatus) {
        this.checkStatus.set(checkStatus);
    }
}
