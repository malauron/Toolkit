package com.Controllers;

import com.DataAccessObjects.Suppliers;
import com.DataModels.Supplier;
import com.Interfaces.ISupplierSearch;
import com.Interfaces.ISuppliers;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SupplierSearch {

    @FXML
    private JFXTextField txtSearch;
    @FXML
    private JFXButton btnSelect;
    @FXML
    private JFXButton btnBack;
    @FXML
    private TableView<Supplier> tblSearch;
    @FXML
    private JFXProgressBar pbrIndicator;

    private TableColumn<Supplier, ImageView> colSupplierPhoto = new TableColumn<>("Photo");
    private TableColumn<Supplier, String> colSupplierDetails = new TableColumn<>("Details");

    private ISuppliers suppliers = new Suppliers();

    private ObservableList<Supplier> supplierList = FXCollections.observableArrayList();

    private Task<ObservableList<Supplier>> supplierListTask;

    private ISupplierSearch supplierSearch;

    public void initialize() {
        pbrIndicator.setVisible(false);
        btnSelect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(supplierSearch != null) {
                    if(tblSearch.getSelectionModel().getSelectedItem() != null) {
                        supplierSearch.getSelectedSupplier(tblSearch.getSelectionModel().getSelectedItem());
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

        colSupplierPhoto.setMaxWidth(100);
        colSupplierPhoto.setPrefWidth(100);
        colSupplierPhoto.setMinWidth(100);
        colSupplierPhoto.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Supplier, ImageView>, ObservableValue<ImageView>>() {
            @Override
            public ObservableValue<ImageView> call(TableColumn.CellDataFeatures<Supplier, ImageView> param) {
                return param.getValue().imageViewProperty();
            }
        });

        colSupplierDetails.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Supplier, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Supplier, String> param) {
                return param.getValue().supplierDetailsProperty();
            }
        });

        tblSearch.getColumns().addAll(colSupplierPhoto, colSupplierDetails);

        txtSearch.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    taskLoadSuppliers();
                    new Thread(supplierListTask).start();
                    displaySuppliers();
                }
            }
        });

        taskLoadSuppliers();
        new Thread(supplierListTask).start();
        displaySuppliers();
    }

    public void setSupplierSearch(ISupplierSearch supplierSearch) {
        this.supplierSearch = supplierSearch;

    }

    private void taskLoadSuppliers() {
        lockControls();
        supplierListTask = new Task<ObservableList<Supplier>>() {
            @Override
            protected ObservableList<Supplier> call() throws Exception {
                for (int i = 0; i <= 0; i++) {
                    Thread.sleep(1000);
                }
                return suppliers.getSuppliers(txtSearch.getText());
            }
        };
    }


    private void displaySuppliers() {
        supplierListTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                try {
                    tblSearch.setItems(supplierListTask.get());

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
