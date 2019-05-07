package com.Controllers;

import com.DataAccessObjects.Billings;
import com.DataModels.Billing;
import com.DataModels.Supplier;
import com.Interfaces.IBillings;
import com.Interfaces.IBillingsSearch;
import com.Interfaces.ISupplierSearch;
import com.Interfaces.IThread;
import com.Utilities.ExecuteStatus;
import com.Utilities.FXMLPath;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;

public class BillingsReceived {

    @FXML
    private HBox hbxSaveStatus;
    @FXML
    private HBox hbxInfo;
    @FXML
    private JFXTextField txtSupplier;
    @FXML
    private JFXTextField txtBillingRefNo;
    @FXML
    private JFXTextField txtBillingAmount;
    @FXML
    private JFXTextArea txtRemarks;
    @FXML
    private JFXButton btnBrowse;
    @FXML
    private JFXButton btnBrowseSupplier;
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnNew;
    @FXML
    private HBox hbxError;
    @FXML
    private Label lblError;

    private IThread taskRunning;
    private Task<ExecuteStatus> handleBillingTask;

    private double xOffset = 0;
    private double yOffset = 0;
    private Integer billingID;
    private IBillings billings = new Billings();
    private Billing billing = new  Billing();
    private Supplier supplier;

    public void initialize() {

        hbxError.setVisible(false);
        unlockControls();
        resetFields();

        txtBillingAmount.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,9}([\\.]\\d{0,2})?")) {
                    txtBillingAmount.setText(oldValue);
                }
            }
        });


        btnNew.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                resetFields();

            }
        });

        btnSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleBilling();
            }
        });

        btnBrowse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                browseBilling();
            }
        });

        btnBrowseSupplier.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                browseSupplier();
            }
        });
    }

    private void resetFields() {
        billingID = 0;
        supplier = null;
        txtSupplier.setText("");
        txtBillingRefNo.setText("");
        txtBillingAmount.setText("");
        txtRemarks.setText("");
    }


    private void handleBilling() {

        if(supplier == null) {
            showError("Please choose a supplier.");
            txtSupplier.requestFocus();
            return;
        }

        if(txtBillingRefNo.getText().isEmpty()) {
            showError("Reference number is blank.");
            txtBillingRefNo.requestFocus();
            return;
        }

        if(!isNumeric(txtBillingAmount.getText())) {
            showError("Please enter a valid amount.");
            txtBillingAmount.requestFocus();
            return;
        }

        if(txtRemarks.getText().isEmpty()) {
            showError("Remarks is blank.");
            txtRemarks.requestFocus();
            return;
        }

        billing.setBillingID(billingID);
        billing.setSupplier(supplier);
        billing.setBillingRefNo(txtBillingRefNo.getText().trim());
        billing.setBillingAmount(Double.parseDouble(txtBillingAmount.getText().replace(",","")));
        billing.setRemarks(txtRemarks.getText());

        taskHandleBilling();
        setHandledBilling();
        new Thread(handleBillingTask).start();

    }

    private void browseBilling() {

        BillingsReceivedSearch billingsReceivedSearch = new BillingsReceivedSearch();

        billingsReceivedSearch.setBillingsReceivedSearch(new IBillingsSearch() {
            @Override
            public void getSelectedBilling(Billing billing) {
                billingID = billing.getBillingID();
                supplier = billing.getSupplier();
                txtSupplier.setText(supplier.getSupplierName());
                txtBillingRefNo.setText(billing.getBillingRefNo());
                txtBillingAmount.setText(String.valueOf(billing.getBillingAmount()));
                txtRemarks.setText(billing.getRemarks());
            }
        });

        try {

            FXMLLoader userFXML = FXMLPath.SEARCH.getFXMLLoader();
            userFXML.setController(billingsReceivedSearch);

            Parent root = userFXML.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            root.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });

            root.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            });

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void taskHandleBilling() {
        handleBillingTask = new Task<ExecuteStatus>() {
            @Override
            protected ExecuteStatus call() throws Exception {
                for (int i = 0; i <= 1; i++) {
                    Thread.sleep(1000);
                }
                System.out.println("Thread is going to do some stuff....");
                return billings.handleBilling(billing);
            }
        };
    }

    private void setHandledBilling() {
        handleBillingTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                System.out.println("Thread Succeeded.");
                unlockControls();
                try {
                    if (handleBillingTask.get() == ExecuteStatus.SAVED) {
                        System.out.println("New user has been saved.");
                        //billingID = billing.getBillingID();
                        resetFields();
                    } else if (handleBillingTask.get() == ExecuteStatus.UPDATED) {
                        System.out.println("User information has been updated.");
                    } else if (handleBillingTask.get() == ExecuteStatus.ERROR_OCCURED) {
                        showError("Error encountered.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        });
    }

    private void browseSupplier() {

        SupplierSearch supplierSearch = new SupplierSearch();

        supplierSearch.setSupplierSearch(new ISupplierSearch() {
            @Override
            public void getSelectedSupplier(Supplier selectedSupplier) {
                supplier = selectedSupplier;
                txtSupplier.setText(selectedSupplier.getSupplierName());
//                supplierID = supplier.getSupplierID();
//                imvSupplierPhoto.setImage(supplier.getSupplierPhoto());
//                txtSupplierName.setText(supplier.getSupplierName());
//                txtSupplierRemarks.setText(supplier.getSupplierRemarks());
            }
        });

        try {

            FXMLLoader supplierFXML = FXMLPath.SEARCH.getFXMLLoader();
            supplierFXML.setController(supplierSearch);

            Parent root = supplierFXML.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            root.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });

            root.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            });

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void lockControls() {
        if (taskRunning != null) {
            taskRunning.isTaskRunning(true);
        }
        btnBrowse.setDisable(true);
        hbxInfo.setDisable(true);
        hbxSaveStatus.setVisible(true);
    }

    private void unlockControls() {
        btnBrowse.setDisable(false);
        hbxInfo.setDisable(false);
        hbxSaveStatus.setVisible(false);
        if (taskRunning != null) {
            taskRunning.isTaskRunning(false);
        }
    }

    private void showError(String error) {
        hbxError.setVisible(true);
        lblError.setText(error);

        FadeTransition ft1 = new FadeTransition(Duration.seconds(1),hbxError);
        ft1.setToValue(1);
        ft1.setFromValue(0);
        ft1.play();
        ft1.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FadeTransition ft2 = new FadeTransition(Duration.seconds(3),hbxError);
                ft2.setToValue(0);
                ft2.setFromValue(1);
                ft2.play();
            }
        });
    }

    public void setTaskRunning(IThread taskRunning) {
        this.taskRunning = taskRunning;

    }

    public boolean isNumeric(String string) {
        try {
            Double.parseDouble(string.replace(",",""));
            return true;
        } catch (Exception e) {
            return false;
        }

    }


}
