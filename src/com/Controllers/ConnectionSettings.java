package com.Controllers;

import com.Interfaces.IThread;
import com.Utilities.ConnectionMySQL;
import com.Utilities.CryptoUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.prefs.Preferences;

public class ConnectionSettings {
    @FXML
    private JFXTextField txtServer;
    @FXML
    private JFXTextField txtUsername;
    @FXML
    private JFXPasswordField txtPassword;
    @FXML
    private JFXTextField txtPort;
    @FXML
    private JFXTextField txtDatabase;
    @FXML
    private JFXButton btnSave;
    @FXML
    private VBox vbxStatus;
    @FXML
    private JFXSpinner spnStatus;
    @FXML
    private Label lblStatus;


    private Preferences connectionSettings;

    private IThread taskRunning;
    private Task<Boolean> isSaved;

    public void initialize() {

        idleStatus();

        connectionSettings = Preferences.userRoot().node("mysqlserver");
        String decryptedPassword = CryptoUtil.getInstance().decrypt(connectionSettings.get("password",""));
        txtServer.setText(connectionSettings.get("server",""));
        txtUsername.setText(connectionSettings.get("username",""));
        txtPassword.setText(decryptedPassword);
        txtPort.setText(connectionSettings.get("port",""));
        txtDatabase.setText(connectionSettings.get("database",""));

        btnSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                saveConnectionSettings();

                isSaved.stateProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue == Worker.State.SUCCEEDED) {
                        try {
                            errorSuccessStatus();
                            if (isSaved.get()) {
                                lblStatus.setText("Connection has been successfully set.");
                            } else {
                                lblStatus.setText("Failed to connect to the database.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (newValue == Worker.State.RUNNING) {
                        System.out.println("Connecting to server..");
                        connectingStatus();
                    }
                });

                new Thread(isSaved).start();
            }
        });
    }

    private void saveConnectionSettings() {

        isSaved = new Task<Boolean>() {
            @Override
            protected Boolean call()  {

                try {
                    String status = "Connecting to database";
                    updateMessage(status);
                    for (int i = 0; i <= 2; i++) {
                        Thread.sleep(1000);
                        status = status + ".";
                        updateMessage(status);
                    }

                    ConnectionMySQL connectionMySQL = new ConnectionMySQL(
                            txtServer.getText(),txtUsername.getText(),txtPassword.getText(),txtPort.getText(),txtDatabase.getText()
                    );

                    String encryptedPassword = CryptoUtil.getInstance().encrypt(txtPassword.getText());
                    connectionSettings.put("server",txtServer.getText());
                    connectionSettings.put("username",txtUsername.getText());
                    connectionSettings.put("password",encryptedPassword);
                    connectionSettings.put("port",txtPort.getText());
                    connectionSettings.put("database",txtDatabase.getText());

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

        lblStatus.textProperty().bind(isSaved.messageProperty());
    }

    public void setTaskRunning(IThread taskRunning) {
        this.taskRunning = taskRunning;
    }

    private void connectingStatus() {
        if (taskRunning != null) {
            taskRunning.isTaskRunning(true);
        }

        FadeTransition ft1 = new FadeTransition(Duration.seconds(1),lblStatus);
        ft1.setToValue(1);
        ft1.setFromValue(0);
        ft1.play();

        vbxStatus.setVisible(true);
        spnStatus.setVisible(true);
        txtServer.setEditable(false);
        txtDatabase.setEditable(false);
        txtPort.setEditable(false);
        txtUsername.setEditable(false);
        txtPassword.setEditable(false);
        btnSave.setDisable(true);
    }

    private void idleStatus() {
        vbxStatus.setVisible(false);
        txtServer.setEditable(true);
        txtDatabase.setEditable(true);
        txtPort.setEditable(true);
        txtUsername.setEditable(true);
        txtPassword.setEditable(true);
        btnSave.setDisable(false);
    }

    private void errorSuccessStatus() {
        vbxStatus.setVisible(true);
        spnStatus.setVisible(false);
        txtServer.setEditable(true);
        txtDatabase.setEditable(true);
        txtPort.setEditable(true);
        txtUsername.setEditable(true);
        txtPassword.setEditable(true);
        btnSave.setDisable(false);
        lblStatus.textProperty().unbind();

        FadeTransition ft1 = new FadeTransition(Duration.seconds(2),lblStatus);
        ft1.setToValue(0);
        ft1.setFromValue(1);
        ft1.play();

        if (taskRunning != null) {
            taskRunning.isTaskRunning(false);
        }
    }


}
