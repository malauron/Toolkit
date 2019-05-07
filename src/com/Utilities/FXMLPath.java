package com.Utilities;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;

public enum FXMLPath {

    MAINFORM("/views/FXMLs/MainForm.fxml"),
    CONNECT("/views/FXMLs/ConnectionSettings.fxml"),
    LOGIN("/views/FXMLs/Login.fxml"),
    USERFORM("/views/FXMLs/UserForm.fxml"),
    SUPPLIERFORM("/views/FXMLs/SupplierForm.fxml"),
    BILLINGSRECEIVED("/views/FXMLs/BillingsReceived.fxml"),
    BILLINGSPROCESS("/views/FXMLs/BillingsProcess.fxml"),
    CHECKSINTRANSIT("/views/FXMLs/ChecksInTransit.fxml"),
    CHECKSRELEASING("/views/FXMLs/ChecksOutgoing.fxml"),
    CHECKSENCASHMENT("/views/FXMLs/ChecksOutgoing.fxml"),
    CHECKSSTATUS("/views/FXMLs/CheckVouchersStatus.fxml"),
    MSSQLCONNECTION("/views/FXMLs/MSSQLConnectionSettings.fxml"),
    CHECKSACCOUNT("/views/FXMLs/ChecksAccount.fxml"),
    CHECKDETAILS("/views/FXMLs/CheckDetails.fxml"),
    CHECKDETAILSMINI("/views/FXMLs/CheckDetailsMini.fxml"),
    BANKACCOUNTADJUSTMENT("/views/FXMLs/BankAccountAdjustment.fxml"),
    PSQLCONNECTIONS("/views/FXMLs/PSQLConnectionSettings.fxml"),
    SEARCH("/views/FXMLs/Search.fxml");

    private final String path;

    FXMLPath(String path) {
        this.path = path;

    }

    public Node getNode() {
        Node node = null;
        try {
            node = FXMLLoader.load(getClass().getResource(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return node;
    }

    public FXMLLoader getFXMLLoader() throws IOException {

        return new FXMLLoader(getClass().getResource(path));
    }

}
