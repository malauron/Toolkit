package com.Controllers;

import com.DataAccessObjects.Companies;
import com.DataModels.Company;
import com.Interfaces.IPSQLConnectionSettingsSearch;
import com.Interfaces.IThread;
import com.Utilities.CryptoUtil;
import com.Utilities.ExecuteStatus;
import com.Utilities.FXMLPath;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
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
import java.util.concurrent.ExecutionException;

public class PSQLConnectionSettings {

    @FXML
    private JFXTextField txtCompanyName;
    @FXML
    private JFXTextField txtServer;
    @FXML
    private JFXTextField txtPort;
    @FXML
    private JFXTextField txtDatabaseName;
    @FXML
    private JFXTextField txtUsername;
    @FXML
    private JFXPasswordField txtPassword;
    @FXML
    private JFXButton btnNew;
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnBrowse;
    @FXML
    private HBox hbxConnectionInfo;
    @FXML
    private HBox hbxError;
    @FXML
    private HBox hbxSaveStatus;
    @FXML
    private Label lblError;

    private double xOffset = 0;
    private double yOffset = 0;
    private IThread taskRunning;
    private Task<ExecuteStatus> handleConSettingTask;

    public void initialize() {
        hbxError.setVisible(false);
        unlockControls();

        btnBrowse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                browseCompany();
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
                handleConSetting();
            }
        });

    }

    private void browseCompany() {

        PSQLConnectionSettingsSearch psqlConnectionSettingsSearch = new PSQLConnectionSettingsSearch();

        psqlConnectionSettingsSearch.setPSQLConnectionSettingSearch(new IPSQLConnectionSettingsSearch() {
            @Override
            public void getPSQLConnectionSetting(Company c) {
                resetFields();
                txtDatabaseName.setEditable(false);
                txtCompanyName.setText(c.getCmpName());
                txtDatabaseName.setText(c.getDbName());
                txtServer.setText(c.getServer());
                txtPort.setText(c.getPort());
                txtUsername.setText(c.getUsername());
                if (!c.getPassword().isEmpty()) {
                    txtPassword.setText(CryptoUtil.getInstance().decrypt(c.getPassword()));
                }
            }
        });

        try {

            FXMLLoader userFXML = FXMLPath.SEARCH.getFXMLLoader();
            userFXML.setController(psqlConnectionSettingsSearch);

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

    private void handleConSetting() {

        if (txtCompanyName.getText().isEmpty()) {
            showError("Company name is blank.");
            txtCompanyName.requestFocus();
            return;
        }

        if (txtServer.getText().isEmpty()) {
            showError("Server name is blank.");
            txtServer.requestFocus();
            return;
        }

        if (txtPort.getText().isEmpty()) {
            showError("Port is blank.");
            txtPort.requestFocus();
            return;
        }

        if (txtDatabaseName.getText().isEmpty()) {
            showError("Database name is blank.");
            txtDatabaseName.requestFocus();
            return;
        }

        if (txtUsername.getText().isEmpty()) {
            showError("Username is blank.");
            txtUsername.requestFocus();
            return;
        }

        Company company = new Company();
        company.setCmpName(txtCompanyName.getText());
        company.setDbName(txtDatabaseName.getText());
        company.setServer(txtServer.getText());
        company.setPort(txtPort.getText());
        company.setUsername(txtUsername.getText());
        company.setPassword(txtPassword.getText());

        taskHandleConSetting(company);
        new Thread(handleConSettingTask).start();
        setHandleConSetting();
    }

    private void taskHandleConSetting(Company c) {
        handleConSettingTask = new Task<ExecuteStatus>() {
            @Override
            protected ExecuteStatus call() throws Exception {
                for (int i = 0; i <= 1; i++) {
                    Thread.sleep(1000);
                }
                return new Companies().handleCompany(c);
            }
        };
    }

    private void setHandleConSetting() {
        handleConSettingTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                try {
                    if (handleConSettingTask.get() == ExecuteStatus.SAVED) {
                        System.out.println("Settings saved!");
                    } else {
                        showError("Invalid connection settings.");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    showError("Invalid connection settings.");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    showError("Invalid connection settings.");
                }
                unlockControls();
            } else if (newValue == Worker.State.RUNNING) {
                lockControls();
            } else {
                unlockControls();
            }
        });
    }

    private void resetFields() {
        txtDatabaseName.setEditable(true);
        txtCompanyName.setText("");
        txtServer.setText("");
        txtPort.setText("");
        txtUsername.setText("");
        txtDatabaseName.setText("");
        txtPassword.setText("");
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

    public void setTaskRunning(IThread taskRunning) {
        this.taskRunning = taskRunning;
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