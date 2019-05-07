package com.Controllers;

import com.Interfaces.IThread;
import com.Utilities.ConnectionExt;
import com.Utilities.CryptoUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.prefs.Preferences;

public class MSSQLConnectionSettings {

    @FXML
    private JFXTextField txtServer;
    @FXML
    private JFXTextField txtPort;
    @FXML
    private JFXTextField txtUsername;
    @FXML
    private JFXPasswordField txtPassword;
    @FXML
    private JFXButton btnSave;
    @FXML
    private HBox hbxSaveStatus;
    @FXML
    private HBox hbxConnectionInfo;
    @FXML
    private HBox hbxError;
    @FXML
    private Label lblError;

    private Preferences connectionSettings;
    private IThread taskRunning;
    private Task<Boolean> isSaved;

    public void initialize() {

        hbxError.setVisible(false);
        unlockControls();

        connectionSettings = Preferences.userRoot().node("mssqlserver");
        String decryptedPassword = CryptoUtil.getInstance().decrypt(connectionSettings.get("password",""));
        txtServer.setText(connectionSettings.get("server",""));
        txtUsername.setText(connectionSettings.get("username",""));
        txtPassword.setText(decryptedPassword);
        txtPort.setText(connectionSettings.get("port",""));

        btnSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                taskSaveSettings();
                handleSaveSettingsTask();
                new Thread(isSaved).start();
            }
        });
    }

    private void taskSaveSettings() {
        isSaved = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                try {
                    ConnectionExt.getInstance().setConnection(
                            txtServer.getText(),txtUsername.getText(),txtPassword.getText(),txtPort.getText()
                    );
                    String encryptedPassword = CryptoUtil.getInstance().encrypt(txtPassword.getText());
                    connectionSettings.put("server",txtServer.getText());
                    connectionSettings.put("username",txtUsername.getText());
                    connectionSettings.put("password",encryptedPassword);
                    connectionSettings.put("port",txtPort.getText());
                    return true;
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                    return false;
                } catch (SQLException se) {
                    se.printStackTrace();
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };
    }

    private void handleSaveSettingsTask() {
        isSaved.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                try {
                    if (isSaved.get()) {

                    } else {
                        showError("Unable to connect to server.");
                    }
                    unlockControls();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (newValue == Worker.State.FAILED) {
                showError("Unable to connect to server.");
                unlockControls();
            } else if (newValue == Worker.State.RUNNING) {
                lockControls();
                System.out.println("Connecting to server.");
            }else if (newValue == Worker.State.CANCELLED) {
                showError("Unable to connect to server.");
                unlockControls();
            }
        });

    }

    private void lockControls() {
        if (taskRunning != null) {
            taskRunning.isTaskRunning(true);
        }
        hbxConnectionInfo.setDisable(true);
        hbxSaveStatus.setVisible(true);
    }

    private void unlockControls() {
        hbxConnectionInfo.setDisable(false);
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

}