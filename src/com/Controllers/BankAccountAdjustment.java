package com.Controllers;

import com.DataAccessObjects.BankAccounts;
import com.DataModels.BankAccount;
import com.Interfaces.IBankAccountAdjustment;
import com.Utilities.ExecuteStatus;
import com.Utilities.NumberFormatter;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;
import java.util.function.UnaryOperator;

public class BankAccountAdjustment {

    @FXML
    private JFXTextField txtAmount;
    @FXML
    private JFXTextArea txtRemarks;
    @FXML
    private JFXButton btnAdd;
    @FXML
    private JFXButton btnCancel;

    private IBankAccountAdjustment bankAccountAdjustment;
    private BankAccount bankAccount;
    private Task<ExecuteStatus> handleAdjustmentTask;
    private DecimalFormat df = new DecimalFormat("#,##0.00");

    public BankAccountAdjustment(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public void initialize() {

        txtAmount.setTextFormatter(new NumberFormatter());

        txtRemarks.setTextFormatter(new TextFormatter<String>(new UnaryOperator<TextFormatter.Change>() {
          @Override
          public TextFormatter.Change apply(TextFormatter.Change change) {
            return change.getControlNewText().length() <= 50 ? change : null;
          }
        }));

        btnAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                taskHandleAdjustment();
                new Thread(handleAdjustmentTask).start();
                handleAdjustment(event);
            }
        });

        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ((Stage)((Node)(event.getSource())).getScene().getWindow()).close();
            }
        });
    }

    public void setBankAccountAdjustment(IBankAccountAdjustment bankAccountAdjustment) {
        this.bankAccountAdjustment = bankAccountAdjustment;
    }

    private void taskHandleAdjustment() {
        handleAdjustmentTask = new Task<ExecuteStatus>() {
            @Override
            protected ExecuteStatus call() throws Exception {
                return new BankAccounts().handleBankAccountAdjustment(
                        bankAccount, df.parse(txtAmount.getText()).doubleValue(), txtRemarks.getText()
                );
            }
        };
    }

    private void handleAdjustment(ActionEvent event) {
        handleAdjustmentTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                try {
                    if (bankAccountAdjustment != null) {
                        bankAccountAdjustment.getExecuteStatus(handleAdjustmentTask.get());
                    }
                    if (handleAdjustmentTask.get() == ExecuteStatus.SAVED) {
                        ((Stage)((Node)(event.getSource())).getScene().getWindow()).close();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                unlockControls();
            } else if (newValue == Worker.State.RUNNING) {
                lockControls();
            } else {
                unlockControls();
            }
        });
    }

    private void lockControls() {
        txtAmount.setEditable(false);
        txtRemarks.setEditable(false);
        btnAdd.setDisable(true);
        btnCancel.setDisable(true);
    }

    private void unlockControls() {
        txtAmount.setEditable(true);
        txtRemarks.setEditable(true);
        btnAdd.setDisable(false);
        btnCancel.setDisable(false);
    }

}
