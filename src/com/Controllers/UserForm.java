package com.Controllers;

import com.DataAccessObjects.UserGroups;
import com.DataAccessObjects.Users;
import com.DataModels.User;
import com.DataModels.UserGroup;
import com.Interfaces.IThread;
import com.Interfaces.IUserSearch;
import com.Interfaces.IUsers;
import com.Utilities.ExecuteStatus;
import com.Utilities.FXMLPath;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;

public class UserForm {

    @FXML
    private HBox hbxSaveStatus;
    @FXML
    private HBox hbxUserInfo;
    @FXML
    private AnchorPane ancUserForm;
    @FXML
    private ImageView imvUserPhoto;
    @FXML
    private ImageView imvDummy;
    @FXML
    private JFXComboBox cboUserGroup;
    @FXML
    private JFXTextField txtFullname;
    @FXML
    private JFXTextField txtUsername;
    @FXML
    private JFXPasswordField txtPassword;
    @FXML
    private JFXPasswordField txtConfirmPassword;
    @FXML
    private MaterialDesignIconView ivAttachPhoto;
    @FXML
    private MaterialDesignIconView ivRemovePhoto;
    @FXML
    private JFXButton btnBrowse;
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnNew;
    @FXML
    private HBox hbxError;
    @FXML
    private Label lblError;

    private IThread taskRunning;
    private Task<ExecuteStatus> handleUserTask;

    private double xOffset = 0;
    private double yOffset = 0;
    private Integer userID;
    private IUsers users = new Users();
    private User user = new User();

    public void initialize() {

        hbxError.setVisible(false);
        unlockControls();

        Circle circle = new Circle(85,85,85);
        imvUserPhoto.setClip(circle);
        Circle circle2 = new Circle(85,85,85);
        imvDummy.setClip(circle2);
        imvUserPhoto.setPreserveRatio(false);

        resetFields();

        ivAttachPhoto.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                attachPhoto();
            }
        });

        ivRemovePhoto.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                imvUserPhoto.setImage(null);

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
                handleUser();
            }
        });

        btnBrowse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                browseUser();
            }
        });

    }

    private void resetFields() {
        userID = 0;
        UserGroups userGroups = new UserGroups();
        imvUserPhoto.setImage(null);
        cboUserGroup.setItems(userGroups.getUserGroups());
        cboUserGroup.getSelectionModel().selectFirst();
        txtFullname.setText("");
        txtUsername.setText("");
        txtUsername.setEditable(true);
        txtPassword.setText("");
        txtConfirmPassword.setText("");
    }

    private void attachPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Attach Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image files", "*.jpg","*.png")
        );
        File file = fileChooser.showOpenDialog(imvUserPhoto.getScene().getWindow());
        if(file != null) {
            Image img = new Image(file.toURI().toString());
            imvUserPhoto.setImage(img);

        }
    }

    private void handleUser() {
        UserGroup userGroup = (UserGroup) cboUserGroup.getSelectionModel().getSelectedItem();
        if(userGroup == null) {
            showError("Please choose a user group.");
            cboUserGroup.requestFocus();
            cboUserGroup.show();
            return;
        }

        if(txtFullname.getText().isEmpty()) {
            showError("Full name is blank.");
            txtFullname.requestFocus();
            return;
        }

        if(txtUsername.getText().isEmpty()) {
            showError("Username is blank.");
            txtUsername.requestFocus();
            return;
        }

        if(txtPassword.getText().isEmpty()) {
            showError("Password is blank.");
            txtPassword.requestFocus();
            return;
        }

        if(!txtPassword.getText().equals(txtConfirmPassword.getText())) {
            showError("Password and confirmation password do not match.");
            txtPassword.requestFocus();
            return;
        }

        user.setUserID(userID);
        user.setUserGroup(userGroup);
        user.setFullName(txtFullname.getText().trim());
        user.setUserName(txtUsername.getText());
        user.setPassword(txtPassword.getText());
        user.setUserPhoto(imvUserPhoto.getImage());

        taskHandleUser();
        setHandledUser();
        new Thread(handleUserTask).start();

    }

    private void browseUser() {

        UserSearch userSearch = new UserSearch();

        userSearch.setUserSearch(new IUserSearch() {
            @Override
            public void getSelectedUser(User user) {
                userID = user.getUserID();
                imvUserPhoto.setImage(user.getUserPhoto());
                cboUserGroup.getSelectionModel().select(user.getUserGroup());
                txtFullname.setText(user.getFullName());
                txtUsername.setText(user.getUserName());
                txtUsername.setEditable(false);
                txtPassword.setText(user.getPassword());
                txtConfirmPassword.setText(user.getPassword());
            }
        });

        try {

            FXMLLoader userFXML = FXMLPath.SEARCH.getFXMLLoader();
            userFXML.setController(userSearch);

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

    private void taskHandleUser() {
        handleUserTask = new Task<ExecuteStatus>() {
            @Override
            protected ExecuteStatus call() throws Exception {
                for (int i = 0; i <= 1; i++) {
                    Thread.sleep(1000);
                }
                System.out.println("Thread is going to do some stuff....");
                return users.handleUser(user);
            }
        };
    }

    private void setHandledUser() {
        handleUserTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                System.out.println("Thread Succeeded.");
                unlockControls();
                try {
                    if (handleUserTask.get() == ExecuteStatus.SAVED) {
                        System.out.println("New user has been saved.");
                        //userID = user.getUserID();
                        resetFields();
                    } else if (handleUserTask.get() == ExecuteStatus.UPDATED) {
                        System.out.println("User information has been updated.");
                    } else if (handleUserTask.get() == ExecuteStatus.ERROR_OCCURED) {
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

    private void lockControls() {
        if (taskRunning != null) {
            taskRunning.isTaskRunning(true);
        }
        btnBrowse.setDisable(true);
        hbxUserInfo.setDisable(true);
        hbxSaveStatus.setVisible(true);
    }

    private void unlockControls() {
        btnBrowse.setDisable(false);
        hbxUserInfo.setDisable(false);
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
