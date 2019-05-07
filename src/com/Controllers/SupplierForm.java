package com.Controllers;

import com.DataAccessObjects.Suppliers;
import com.DataModels.Supplier;
import com.Interfaces.ISupplierSearch;
import com.Interfaces.ISuppliers;
import com.Interfaces.IThread;
import com.Utilities.ExecuteStatus;
import com.Utilities.FXMLPath;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
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
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;

public class SupplierForm {

    @FXML
    private HBox hbxSaveStatus;
    @FXML
    private HBox hbxSupplierInfo;
    @FXML
    private ImageView imvSupplierPhoto;
    @FXML
    private ImageView imvDummy;
    @FXML
    private JFXTextField txtSupplierName;
    @FXML
    private JFXTextArea txtSupplierRemarks;
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
    private Task<ExecuteStatus> handleSupplierTask;

    private double xOffset = 0;
    private double yOffset = 0;
    private Integer supplierID;
    private ISuppliers suppliers = new Suppliers();
    private Supplier supplier = new Supplier();

    public void initialize() {

        hbxError.setVisible(false);
        unlockControls();

        Circle circle = new Circle(85,85,85);
        imvSupplierPhoto.setClip(circle);
        Circle circle2 = new Circle(85,85,85);
        imvDummy.setClip(circle2);
        imvSupplierPhoto.setPreserveRatio(false);

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
                imvSupplierPhoto.setImage(null);

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
                handleSupplier();
            }
        });

        btnBrowse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                browseSupplier();
            }
        });

    }

    private void resetFields() {
        supplierID = 0;
        imvSupplierPhoto.setImage(null);
        txtSupplierName.setText("");
        txtSupplierRemarks.setText("");
    }

    private void attachPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Attach Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image files", "*.jpg","*.png")
        );
        File file = fileChooser.showOpenDialog(imvSupplierPhoto.getScene().getWindow());
        if(file != null) {
            Image img = new Image(file.toURI().toString());
            imvSupplierPhoto.setImage(img);

        }
    }

    private void handleSupplier() {

        if(txtSupplierName.getText().isEmpty()) {
            showError("Supplier name is blank.");
            txtSupplierName.requestFocus();
            return;
        }

        if(txtSupplierRemarks.getText().isEmpty()) {
            showError("Remarks is blank.");
            txtSupplierRemarks.requestFocus();
            return;
        }


        supplier.setSupplierID(supplierID);
        supplier.setSupplierName(txtSupplierName.getText().trim());
        supplier.setSupplierRemarks(txtSupplierRemarks.getText());
        supplier.setSupplierPhoto(imvSupplierPhoto.getImage());

        taskHandleSupplier();
        setHandledSupplier();
        new Thread(handleSupplierTask).start();

    }

    private void browseSupplier() {

        SupplierSearch supplierSearch = new SupplierSearch();

        supplierSearch.setSupplierSearch(new ISupplierSearch() {
            @Override
            public void getSelectedSupplier(Supplier supplier) {
                supplierID = supplier.getSupplierID();
                imvSupplierPhoto.setImage(supplier.getSupplierPhoto());
                txtSupplierName.setText(supplier.getSupplierName());
                txtSupplierRemarks.setText(supplier.getSupplierRemarks());
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

    private void taskHandleSupplier() {
        handleSupplierTask = new Task<ExecuteStatus>() {
            @Override
            protected ExecuteStatus call() throws Exception {
                for (int i = 0; i <= 1; i++) {
                    Thread.sleep(1000);
                }
                System.out.println("Thread is going to do some stuff....");
                return suppliers.handleSupplier(supplier);
            }
        };
    }

    private void setHandledSupplier() {
        handleSupplierTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                System.out.println("Thread Succeeded.");
                unlockControls();
                try {
                    if (handleSupplierTask.get() == ExecuteStatus.SAVED) {
                        System.out.println("New supplier has been saved.");
                        //supplierID = supplier.getSupplierID();
                        resetFields();
                    } else if (handleSupplierTask.get() == ExecuteStatus.UPDATED) {
                        System.out.println("Supplier information has been updated.");
                    } else if (handleSupplierTask.get() == ExecuteStatus.ERROR_OCCURED) {
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
        hbxSupplierInfo.setDisable(true);
        hbxSaveStatus.setVisible(true);
    }

    private void unlockControls() {
        btnBrowse.setDisable(false);
        hbxSupplierInfo.setDisable(false);
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
