package com.Controllers;

import com.DataAccessObjects.Companies;
import com.DataModels.Company;
import com.DataModels.Company.DBType;
import com.Interfaces.IPSQLConnectionSettingsSearch;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;

public class PSQLConnectionSettingsSearch {

    @FXML
    private JFXTextField txtSearch;
    @FXML
    private JFXButton btnSelect;
    @FXML
    private JFXButton btnBack;
    @FXML
    private TableView<Company> tblSearch;
    @FXML
    private JFXProgressBar pbrIndicator;

    private Task<ObservableList<Company>> companyListTask;
    private IPSQLConnectionSettingsSearch connectionSettingsSearch;

    private TableColumn<Company, String> colCompanyName = new TableColumn<>("Company");

    public void initialize() {

        pbrIndicator.setVisible(false);

        colCompanyName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Company, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Company, String> param) {
                return param.getValue().cmpNameProperty();
            }
        });

        tblSearch.getColumns().addAll(colCompanyName);

        btnSelect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(connectionSettingsSearch != null) {
                    if(tblSearch.getSelectionModel().getSelectedItem() != null) {
                        connectionSettingsSearch.getPSQLConnectionSetting(tblSearch.getSelectionModel().getSelectedItem());
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

        taskLoadCompanies();
        new Thread(companyListTask).start();
        displayCompanies();

    }

    public void setPSQLConnectionSettingSearch(IPSQLConnectionSettingsSearch i) {
        this.connectionSettingsSearch = i;
    }

    private void taskLoadCompanies() {
        lockControls();
        companyListTask = new Task<ObservableList<Company>>() {
            @Override
            protected ObservableList<Company> call() throws Exception {
                for (int i = 0; i <= 1; i++) {
                    Thread.sleep(1000);
                }
                System.out.println("Thread is going to do some stuff....");
                return new Companies().getCompanies(DBType.PSQL);
            }
        };
    }

    private void displayCompanies() {
        companyListTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                unlockControls();
                tblSearch.itemsProperty().bind(companyListTask.valueProperty());
            } else if (newValue == Worker.State.FAILED) {
                unlockControls();
            } else if (newValue == Worker.State.RUNNING) {
                lockControls();
            }else if (newValue == Worker.State.CANCELLED) {
                unlockControls();
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
