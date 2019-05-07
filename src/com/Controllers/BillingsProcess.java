package com.Controllers;

import com.DataAccessObjects.Billings;
import com.DataModels.Billing;
import com.Interfaces.IBillings;
import com.Utilities.BillingStatus;
import com.Utilities.ExecuteStatus;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.text.NumberFormat;
import java.util.Date;

public class BillingsProcess {

    @FXML
    private JFXTextField txtSearch;
    @FXML
    private TableView<Billing> tblSearch;
    @FXML
    private JFXProgressBar pbrIndicator;

    private TableColumn<Billing, String> colSupplierName = new TableColumn<>("Supplier");
    private TableColumn<Billing, String> colBillingRefNo = new TableColumn<>("Reference No.");
    private TableColumn<Billing, Double> colBillingAmount = new TableColumn<>("Amount");
    private TableColumn<Billing, Date> colTrnxDate = new TableColumn<>("Date Received");
    private TableColumn<Billing, Boolean> colAction = new TableColumn<>("Tag");

    private IBillings billings = new Billings();
    private Billing billing = new  Billing();

    private Task<ObservableList<Billing>> billingListTask;
    private Task<ExecuteStatus> handleBillingStatusTask;

    public void initialize() {
        pbrIndicator.setVisible(false);

        colSupplierName.setMinWidth(200);
        colSupplierName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Billing, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Billing, String> param) {
                return param.getValue().supplierProperty().get().supplierNameProperty();
            }
        });

        colBillingRefNo.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Billing, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Billing, String> param) {
                return param.getValue().billingRefNoProperty();
            }
        });

        colBillingAmount.setStyle( "-fx-alignment: CENTER-RIGHT;");
        colBillingAmount.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Billing, Double>, ObservableValue<Double>>() {
            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<Billing, Double> param) {
                return param.getValue().billingAmountProperty();
            }
        });

        NumberFormat currencyFormat = NumberFormat.getNumberInstance();
        currencyFormat.setMinimumFractionDigits(2);
        colBillingAmount.setCellFactory(new Callback<TableColumn<Billing, Double>, TableCell<Billing, Double>>() {
            @Override
            public TableCell<Billing, Double> call(TableColumn<Billing, Double> param) {
                TableCell cell = new TableCell<Billing, Double>() {
                    @Override
                    protected void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(currencyFormat.format(item));
                        }
                    }
                };
                return cell;
            }
        });

        colTrnxDate.setStyle("-fx-alignment: CENTER;");
        colTrnxDate.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Billing, Date>, ObservableValue<Date>>() {
            @Override
            public ObservableValue<Date> call(TableColumn.CellDataFeatures<Billing, Date> param) {
                return param.getValue().transactionDateProperty();
            }
        });

        colAction.setSortable(false);
        colAction.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Billing, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Billing, Boolean> param) {
                return new SimpleBooleanProperty(param.getValue() != null);
            }
        });

        colAction.setCellFactory(new Callback<TableColumn<Billing, Boolean>, TableCell<Billing, Boolean>>() {
            @Override
            public TableCell<Billing, Boolean> call(TableColumn<Billing, Boolean> param) {
                return new ButtonTag();
            }
        });

        tblSearch.getColumns().addAll(colSupplierName, colBillingRefNo,colBillingAmount,colTrnxDate,colAction);

        txtSearch.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    taskLoadBillings();
                    new Thread(billingListTask).start();
                    displayBillings();
                }
            }
        });

        taskLoadBillings();
        new Thread(billingListTask).start();
        displayBillings();
    }

    private void taskLoadBillings() {
        lockControls();
        billingListTask = new Task<ObservableList<Billing>>() {
            @Override
            protected ObservableList<Billing> call() throws Exception {
                for (int i = 0; i <= 1; i++) {
                    Thread.sleep(1000);
                }
                System.out.println("Thread is going to do some stuff....");
                return billings.getBillings(txtSearch.getText(),
                        BillingStatus.RECEIVED);
            }
        };
    }

    private void displayBillings() {
        billingListTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                try {
                    tblSearch.setItems(billingListTask.get());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                unlockControls();
                txtSearch.requestFocus();
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
        });
    }

    private void taskHandleBilling() {
        handleBillingStatusTask = new Task<ExecuteStatus>() {
            @Override
            protected ExecuteStatus call() throws Exception {
                for (int i = 0; i <= 0; i++) {
                    Thread.sleep(1000);
                }
                System.out.println("Thread is going to do some stuff....");
                return billings.updateBillingStatus(billing,BillingStatus.PROCESSED);
            }
        };
    }

    private void setHandledBilling() {
        handleBillingStatusTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                System.out.println("Thread Succeeded.");
                unlockControls();
                try {
                    if (handleBillingStatusTask.get() == ExecuteStatus.UPDATED) {
                        System.out.println("Billing status has been updated.");

                        billingListTask.get().remove(billing);

                    } else if (handleBillingStatusTask.get() == ExecuteStatus.ERROR_OCCURED) {
                        System.out.println("Error encountered.");
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
        //tblSearch.setItems(null);
        pbrIndicator.setVisible(true);
        txtSearch.setDisable(true);
        tblSearch.setDisable(true);
    }

    private void unlockControls() {
        pbrIndicator.setVisible(false);
        txtSearch.setDisable(false);
        tblSearch.setDisable(false);
    }

    private class ButtonTag extends TableCell<Billing, Boolean> {
        final JFXButton btnProcess = new JFXButton("Process");
        //final JFXButton btnCancel = new JFXButton("Cancel");
        HBox hbxButtonHolder = new HBox(btnProcess);

        ButtonTag() {
            hbxButtonHolder.setAlignment(Pos.CENTER);
            hbxButtonHolder.setSpacing(5);
            btnProcess.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    int row = getTableRow().getIndex();
                    tblSearch.getSelectionModel().select(row);
                    billing = tblSearch.getSelectionModel().getSelectedItem();

                    taskHandleBilling();
                    setHandledBilling();
                    new Thread(handleBillingStatusTask).start();

                }
            });
        }

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if(!empty) {
                setGraphic(hbxButtonHolder);
            } else {
                setGraphic(null);
            }
        }
    }
}
