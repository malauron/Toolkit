package com.Controllers;

import com.DataAccessObjects.Users;
import com.DataModels.User;
import com.Interfaces.IThread;
import com.Interfaces.IUsers;
import com.Utilities.CryptoUtil;
import com.Utilities.CurrentUser;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Login {

    @FXML
    private JFXTextField txtUsername;
    @FXML
    private JFXPasswordField txtPassword;
    @FXML
    private JFXButton btnLogin;
    @FXML
    private JFXSpinner spnStatus;
    @FXML
    private Label lblStatus;
    @FXML
    private VBox vbxStatus;

    private IThread taskRunning;
    private Task<User> user;

    private double xOffset = 0;
    private double yOffset = 0;

    public void initialize() {

        idleStatus();

        btnLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                verifyCredentials();
                loadMain(event);
                new Thread(user).start();
            }
        });

    }

    private void verifyCredentials() {

        user = new Task<User>() {
            @Override
            protected User call() {
                try {
                    String status = "Connecting to database";
                    updateMessage(status);
                    for (int i = 0; i <= 2; i++) {
                        Thread.sleep(1000);
                        status = status + ".";
                        updateMessage(status);
                    }

                    IUsers users = new Users();
                    return users.getUser(txtUsername.getText(),CryptoUtil.getInstance().encrypt(txtPassword.getText()));
                } catch (InterruptedException ie) {
                    return null;
                }
            }
        };

        lblStatus.textProperty().bind(user.messageProperty());
    }

    private void loadMain(ActionEvent event) {

        user.stateProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                try {
                    if (user.get() != null) {

                        CurrentUser.getInstance().setUserID(user.get().getUserID());
                        CurrentUser.getInstance().setFullName(user.get().getFullName());
                        CurrentUser.getInstance().setUserGroup(user.get().getUserGroup());
                        CurrentUser.getInstance().setUserPhoto(user.get().getUserPhoto());
                        CurrentUser.getInstance().setUserName(user.get().getUserName());

                        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                        stage.hide();
                        Parent root = FXMLLoader.load(getClass().getResource("/views/FXMLs/ApplicationHolder.fxml"));
                        Scene scene = new Scene(root);

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

                        stage.setScene(scene);
                        stage.centerOnScreen();
                        stage.show();

                    } else {
                        System.out.println("Unable to connect to the server.");
                        errorStatus();
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            } else if (newValue == Worker.State.RUNNING) {
                System.out.println("Connecting to server...");
                connectingStatus();
            }
        }));


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
        txtUsername.setEditable(false);
        txtPassword.setEditable(false);
        btnLogin.setDisable(true);
    }

    private void idleStatus() {
        vbxStatus.setVisible(false);
        spnStatus.setVisible(false);
        txtUsername.setEditable(true);
        txtPassword.setEditable(true);
        btnLogin.setDisable(false);
    }

    private void errorStatus() {

        vbxStatus.setVisible(true);
        spnStatus.setVisible(false);
        txtUsername.setEditable(true);
        txtPassword.setEditable(true);
        btnLogin.setDisable(false);
        lblStatus.textProperty().unbind();
        lblStatus.setText("Invalid username/password!");

        FadeTransition ft1 = new FadeTransition(Duration.seconds(2),lblStatus);
        ft1.setToValue(0);
        ft1.setFromValue(1);
        ft1.play();

        if (taskRunning != null) {
            taskRunning.isTaskRunning(false);
        }

    }

}
