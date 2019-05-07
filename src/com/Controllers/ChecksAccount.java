package com.Controllers;

import com.DataAccessObjects.BankAccounts;
import com.DataAccessObjects.Companies;
import com.DataModels.BankAccount;
import com.DataModels.Company;
import com.Interfaces.IChecksAccount;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class ChecksAccount {
    @FXML
    private JFXButton btnSet;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private JFXComboBox<Company> cboCompany;
    @FXML
    private JFXComboBox<BankAccount> cboBankAccount;

    private Task<ObservableList<Company>> companyListTask;
    private Task<ObservableList<BankAccount>> bankAccountListTask;
    private IChecksAccount checksAccount;
    private Company company;

    public void initialize() {

        taskLoadCompanies();
        new Thread(companyListTask).start();
        cboCompany.itemsProperty().bind(companyListTask.valueProperty());

        cboCompany.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            company = cboCompany.getSelectionModel().getSelectedItem();
            taskLoadBankAccounts();
            new Thread(bankAccountListTask).start();
            cboBankAccount.itemsProperty().bind(bankAccountListTask.valueProperty());
        });

        cboBankAccount.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

        });

        btnSet.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (checksAccount != null) {
                    if (cboCompany.getSelectionModel().getSelectedItem() != null &&
                            cboBankAccount.getSelectionModel().getSelectedItem() != null) {
                        checksAccount.getAccountSettings(
                                (Company) cboCompany.getSelectionModel().getSelectedItem(),
                                (BankAccount) cboBankAccount.getSelectionModel().getSelectedItem()
                        );
                        ((Stage)((Node)(event.getSource())).getScene().getWindow()).close();
                    }

                }
            }
        });

        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ((Stage)((Node)(event.getSource())).getScene().getWindow()).close();
            }
        });
    }

    private void taskLoadCompanies() {
        companyListTask = new Task<ObservableList<Company>>() {
            @Override
            protected ObservableList<Company> call() throws Exception {
                return new Companies().getCompanies();
            }
        };
    }

    private void taskLoadBankAccounts() {
        bankAccountListTask = new Task<ObservableList<BankAccount>>() {
            @Override
            protected ObservableList<BankAccount> call() throws Exception {
                return new BankAccounts().getBankAccounts(company);
            }
        };
    }

    public void setAccountSettings(IChecksAccount checksAccount) {
        this.checksAccount = checksAccount;
    }
}
