package com.Controllers;

import com.DataAccessObjects.Users;
import com.DataModels.User;
import com.Interfaces.IUserSearch;
import com.Interfaces.IUsers;
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

public class UserSearch {

    @FXML
    private JFXTextField txtSearch;
    @FXML
    private JFXButton btnSelect;
    @FXML
    private JFXButton btnBack;
    @FXML
    private TableView<User> tblSearch;
    @FXML
    private JFXProgressBar pbrIndicator;

    private TableColumn<User, ImageView> colUserPhoto = new TableColumn<>("Photo");
    private TableColumn<User, String> colUserDetails = new TableColumn<>("Details");

    private IUsers users = new Users();

    private ObservableList<User> usersList = FXCollections.observableArrayList();

    private Task<ObservableList<User>> userListTask;

    private IUserSearch userSearch;

    public void initialize() {
        pbrIndicator.setVisible(false);
        btnSelect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(userSearch != null) {
                    if(tblSearch.getSelectionModel().getSelectedItem() != null) {
                        userSearch.getSelectedUser(tblSearch.getSelectionModel().getSelectedItem());
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

        colUserPhoto.setMaxWidth(800);
        colUserPhoto.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<User, ImageView>, ObservableValue<ImageView>>() {
            @Override
            public ObservableValue<ImageView> call(TableColumn.CellDataFeatures<User, ImageView> param) {
                return param.getValue().imageViewProperty();
            }
        });

        colUserDetails.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<User, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<User, String> param) {
                return param.getValue().userDetailsProperty();
            }
        });

        tblSearch.getColumns().addAll(colUserPhoto, colUserDetails);

        txtSearch.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    taskLoadUsers();
                    new Thread(userListTask).start();
                    displayUsers();
                }
            }
        });

        taskLoadUsers();
        new Thread(userListTask).start();
        displayUsers();
    }

    public void setUserSearch(IUserSearch userSearch) {
        this.userSearch = userSearch;

    }

    private void taskLoadUsers() {
        lockControls();
        userListTask = new Task<ObservableList<User>>() {
            @Override
            protected ObservableList<User> call() throws Exception {
                for (int i = 0; i <= 1; i++) {
                    Thread.sleep(1000);
                }
                System.out.println("Thread is going to do some stuff....");
                return users.getUsers(txtSearch.getText());
            }
        };
    }


    private void displayUsers() {
        userListTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                try {
                    tblSearch.setItems(userListTask.get());
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
