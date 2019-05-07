package com.Controllers;

import com.DataAccessObjects.Billings;
import com.DataModels.Billing;
import com.Interfaces.IBillings;
import com.Interfaces.IBillingsSearch;
import com.Utilities.BillingStatus;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

public class BillingsReceivedSearch {

    @FXML
    private JFXTextField txtSearch;
    @FXML
    private JFXButton btnSelect;
    @FXML
    private JFXButton btnBack;
    @FXML
    private TableView<Billing> tblSearch;
    @FXML
    private JFXProgressBar pbrIndicator;

    private TableColumn<Billing, String> colSupplierName = new TableColumn<>("Photo");
    private TableColumn<Billing, String> colBillingRefNo = new TableColumn<>("Details");

    private IBillings billingsReceived = new Billings();

    private ObservableList<Billing> billingList = FXCollections.observableArrayList();

    private Task<ObservableList<Billing>> billingListTask;

    private IBillingsSearch billingsReceivedSearch;

    public void initialize() {
        pbrIndicator.setVisible(false);
        btnSelect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(billingsReceivedSearch != null) {
                    if(tblSearch.getSelectionModel().getSelectedItem() != null) {
                        billingsReceivedSearch.getSelectedBilling(tblSearch.getSelectionModel().getSelectedItem());
                        ((Stage)((Node)(event.getSource())).getScene().getWindow()).close();
                    }
                }
            }
        });

        btnBack.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ((Stage)((Node)(event.getSource())).getScene().getWindow()).close();
            }
        });

        //colSupplierName.setMaxWidth(800);
        colSupplierName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Billing, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Billing, String> param) {
                return param.getValue().supplierProperty().get().supplierNameProperty();
            }
        });

        colBillingRefNo.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Billing, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Billing, String> param) {
                return param.getValue().billingRefNoProperty();
            }
        });

        tblSearch.getColumns().addAll(colSupplierName, colBillingRefNo);

        txtSearch.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    taskLoadBillings();
                    new Thread(billingListTask).start();
                    displayBillings();
                }
            }
        });

        taskLoadBillings();
        new Thread(billingListTask).start();
        displayBillings();
    }

    public void setBillingsReceivedSearch(IBillingsSearch billingsReceivedSearch) {
        this.billingsReceivedSearch = billingsReceivedSearch;

    }

    private void taskLoadBillings() {
        lockControls();
        billingListTask = new Task<ObservableList<Billing>>() {
            @Override
            protected ObservableList<Billing> call() throws Exception {
                for (int i = 0; i <= 1; i++) {
                    Thread.sleep(1000);
                }
                System.out.println("Thread is going to do some stuff....");
                return billingsReceived.getBillings(txtSearch.getText(), 
                        BillingStatus.RECEIVED);
            }
        };
    }


    private void displayBillings() {
        billingListTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                try {
                    tblSearch.setItems(billingListTask.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                unlockControls();
                System.out.println("Thread successful.");
            } else if (newValue == Worker.State.FAILED) {
                unlockControls();
                System.out.println("Thread failed.");
            } else if (newValue == Worker.State.RUNNING) {
                lockControls();
                System.out.println("Thread is running.");
            }else if (newValue == Worker.State.CANCELLED) {
                unlockControls();
                System.out.println("Thread is cancelled.");
            }
            txtSearch.requestFocus();
        });
    }

    private void lockControls() {
        tblSearch.setItems(null);
        pbrIndicator.setVisible(true);
        btnSelect.setDisable(true);
        btnBack.setDisable(true);
        txtSearch.setDisable(true);
    }

    private void unlockControls() {
        pbrIndicator.setVisible(false);
        btnSelect.setDisable(false);
        btnBack.setDisable(false);
        txtSearch.setDisable(false);
    }
}
