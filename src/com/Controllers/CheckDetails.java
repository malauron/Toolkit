package com.Controllers;

import com.DataAccessObjects.CheckVouchers;
import com.DataModels.CheckVoucher;
import com.Interfaces.ICheckDetails;
import com.Utilities.ExecuteStatus;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;

public class CheckDetails {
    @FXML
    private JFXTextField txtVoucherNo;
    @FXML
    private JFXTextField txtCheckNo;
    @FXML
    private JFXTextField txtDate;
    @FXML
    private JFXTextField txtPayee;
    @FXML
    private JFXTextField txtAmount;
    @FXML
    private JFXButton btnAdd;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private HBox hbxError;
    @FXML
    private Label lblError;

    private ICheckDetails checkDetails;
    private CheckVoucher checkVoucher;
    private Task<ExecuteStatus> handleCheckVoucherTask;

    public CheckDetails(CheckVoucher checkVoucher) {
        this.checkVoucher = checkVoucher;
    }

    public void initialize() {

        txtVoucherNo.setText(checkVoucher.getTransRef().toString());
        txtCheckNo.setText(checkVoucher.getCheckNum());
        txtDate.setText(checkVoucher.getCheckDate().toString());
        txtPayee.setText(checkVoucher.getVendorName());
        DecimalFormat format = new DecimalFormat("#,###.00");
        txtAmount.setText(format.format(checkVoucher.getCheckAmount()));

        btnAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                taskHandleCheckVoucher();
                new Thread(handleCheckVoucherTask).start();
                handleCheckVoucher(event);
            }
        });

        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ((Stage)((Node)(event.getSource())).getScene().getWindow()).close();
            }
        });

    }

    public void setCheckDetails(ICheckDetails checkDetails) {
        this.checkDetails = checkDetails;
    }

    private void taskHandleCheckVoucher() {
        handleCheckVoucherTask = new Task<ExecuteStatus>() {
            @Override
            protected ExecuteStatus call() throws Exception {
                return new  CheckVouchers().handleCheckVoucher(checkVoucher);
            }
        };
    }

    private void handleCheckVoucher(ActionEvent event) {
        handleCheckVoucherTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                try {
                    if (checkDetails != null) {
                        checkDetails.getExecuteStatus(handleCheckVoucherTask.get());
                    }
                    if (handleCheckVoucherTask.get() == ExecuteStatus.SAVED) {
                        ((Stage)((Node)(event.getSource())).getScene().getWindow()).close();
                    } else {
                        showError("This voucher was already added.");
                        unlockControls();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } else if (newValue == Worker.State.RUNNING) {
                lockControls();
            } else {
                unlockControls();
            }
        });
    }

    private void lockControls() {
        txtAmount.setEditable(false);
        btnAdd.setDisable(true);
        btnCancel.setDisable(true);
    }

    private void unlockControls() {
        txtAmount.setEditable(true);
        btnAdd.setDisable(false);
        btnCancel.setDisable(false);
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

}
