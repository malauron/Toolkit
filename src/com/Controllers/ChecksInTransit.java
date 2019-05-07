package com.Controllers;

import com.DataAccessObjects.BankAccounts;
import com.DataAccessObjects.CheckVouchers;
import com.DataModels.BankAccount;
import com.DataModels.CheckVoucher;
import com.DataModels.CheckVoucher.CheckStatus;
import com.DataModels.Company;
import com.DataModels.Company.DBType;
import com.Interfaces.IBankAccountAdjustment;
import com.Interfaces.ICheckDetails;
import com.Interfaces.IChecksAccount;
import com.Interfaces.IThread;
import com.Utilities.ConnectionExt2;
import com.Utilities.CryptoUtil;
import com.Utilities.ExecuteStatus;
import com.Utilities.FXMLPath;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;

public class ChecksInTransit {
    @FXML
    private HBox hbxMenu;
    @FXML
    private HBox hbxChecksMenu;
    @FXML
    private JFXTextField txtAccountBalance;
    @FXML
    private JFXTextField txtDatabase;
    @FXML
    private JFXTextField txtAccountNo;
    @FXML
    private JFXTextField txtVoucher;
    @FXML
    private JFXTextField txtAllChecksTotal;
    @FXML
    private JFXTextField txtPendingChecksTotal;
    @FXML
    private JFXTextField txtFundedChecksTotal;
    @FXML
    private JFXTextField txtSelectedChecksTotal;
    @FXML
    private JFXProgressBar pbrIndicator;
    @FXML
    private MaterialDesignIconView ivBankAccount;
    @FXML
    private MaterialDesignIconView ivAccountAdjustment;
    @FXML
    private MaterialDesignIconView ivFund;
    @FXML
    private MaterialDesignIconView ivRefresh;
    @FXML
    private MaterialDesignIconView ivExport;
    @FXML
    private MaterialDesignIconView ivDelete;
    @FXML
    private TableView<CheckVoucher> tblList;

    private BigDecimal selectCheckAmountTotal = new BigDecimal(0);
    private IThread taskRunning;

    private TableColumn<CheckVoucher,Boolean> colSelect = new TableColumn("Selected");
    private TableColumn<CheckVoucher,Integer> colTransref = new TableColumn<>("Voucher No.");
    private TableColumn<CheckVoucher,String> colCheckNum = new TableColumn<>("Check No.");
    private TableColumn<CheckVoucher,String> colVendorName = new TableColumn<>("Supplier");
    private TableColumn<CheckVoucher,Date> colCheckDate = new TableColumn<>("Check Date");
    private TableColumn<CheckVoucher,BigDecimal> colCheckSum = new TableColumn<>("Amount");

    private TableColumn<CheckVoucher,Node> colDetails = new TableColumn<>("Vouchers");
    private TableColumn<CheckVoucher, CheckVoucher.CheckStatus> colAction = new TableColumn<>("Status");

    private double xOffset = 0;
    private double yOffset = 0;
    private Company company;
    private BankAccount bankAccount;
    private Task<BankAccount> bankAccountStatusTask;
    private Task<ObservableList<CheckVoucher>> checkVouchersTask;
    private Task<CheckVoucher> checkVoucherTask;
    private Task<ExecuteStatus> handleChecksStatusTask;

    public void initialize() {
        unlockControls();
        clearFields();

        colSelect.setMinWidth(30);
        colSelect.setResizable(false);
        colSelect.setEditable(true);
        colSelect.setSortable(false);
        colSelect.setStyle( "-fx-alignment: CENTER;");
        colSelect.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CheckVoucher, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<CheckVoucher, Boolean> param) {
                return param.getValue().selectedProperty();
            }
        });

        colSelect.setCellFactory(new Callback<TableColumn<CheckVoucher, Boolean>, TableCell<CheckVoucher, Boolean>>() {
            @Override
            public TableCell<CheckVoucher, Boolean> call(TableColumn<CheckVoucher, Boolean> param) {
                return new CheckBoxTableCell();
            }
        });

        colTransref.setMinWidth(90);
        colTransref.setResizable(false);
        colTransref.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CheckVoucher, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<CheckVoucher, Integer> param) {
                return param.getValue().transRefProperty();
            }
        });

        colCheckNum.setMinWidth(80);
        colCheckNum.setResizable(false);
        colCheckNum.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CheckVoucher, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CheckVoucher, String> param) {
                return param.getValue().checkNumProperty();
            }
        });

        colVendorName.setMinWidth(430);
        colVendorName.setMaxWidth(430);
        colVendorName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CheckVoucher, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CheckVoucher, String> param) {
                return param.getValue().vendorNameProperty();
            }
        });

        colCheckDate.setMinWidth(50);
        colCheckDate.setResizable(false);
        colCheckDate.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CheckVoucher, Date>, ObservableValue<Date>>() {
            @Override
            public ObservableValue<Date> call(TableColumn.CellDataFeatures<CheckVoucher, Date> param) {
                return param.getValue().checkDateProperty();
            }
        });

        colCheckSum.setMinWidth(150);
        colCheckSum.setResizable(false);
        colCheckSum.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CheckVoucher, BigDecimal>, ObservableValue<BigDecimal>>() {
            @Override
            public ObservableValue<BigDecimal> call(TableColumn.CellDataFeatures<CheckVoucher, BigDecimal> param) {
                return param.getValue().checkAmountProperty();
            }
        });

        colCheckSum.setStyle( "-fx-alignment: CENTER-RIGHT;");
        colCheckSum.setCellFactory(new Callback<TableColumn<CheckVoucher, BigDecimal>, TableCell<CheckVoucher, BigDecimal>>() {
            @Override
            public TableCell<CheckVoucher, BigDecimal> call(TableColumn<CheckVoucher, BigDecimal> param) {
                TableCell cell = new TableCell<CheckVoucher,BigDecimal>() {
                    @Override
                    protected void updateItem(BigDecimal item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            DecimalFormat df = new DecimalFormat("#,##0.00");
                            setText(df.format(item));
                        }
                    }
                };
                return cell;
            }
        });

        colAction.setMaxWidth(75);
        colAction.setResizable(false);
        colAction.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CheckVoucher, CheckVoucher.CheckStatus>, ObservableValue<CheckVoucher.CheckStatus>>() {
            @Override
            public ObservableValue<CheckVoucher.CheckStatus> call(TableColumn.CellDataFeatures<CheckVoucher, CheckVoucher.CheckStatus> param) {
                return param.getValue().checkStatusProperty();
            }
        });

        colAction.setCellFactory(new Callback<TableColumn<CheckVoucher, CheckStatus>, TableCell<CheckVoucher, CheckStatus>>() {
            @Override
            public TableCell<CheckVoucher, CheckStatus> call(TableColumn<CheckVoucher, CheckStatus> param) {
                return new CheckStatusCell();
            }
        });

        tblList.setEditable(true);
        tblList.getColumns().addAll(colSelect,colTransref,colCheckNum,colVendorName,colCheckDate,colCheckSum,colAction);

        ivBankAccount.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                loadAccountSettings();
            }
        });

        ivAccountAdjustment.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (company != null && bankAccount != null) {
                    loadAccountAdjustment();
                }
            }
        });

        ivFund.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    taskHandleCheckStatus(checkVouchersTask.get(),CheckStatus.FUNDED);
                    new Thread(handleChecksStatusTask).start();
                    displayUpdatedCheckStatus();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        ivDelete.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    taskHandleCheckStatus(checkVouchersTask.get(),CheckStatus.CANCELED);
                    new Thread(handleChecksStatusTask).start();
                    displayUpdatedCheckStatus();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        ivRefresh.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (company != null && bankAccount != null) {
                    taskLoadMySQLCheckVouchers(company.getDbName(),bankAccount.getAccountNumber());
                    new Thread(checkVouchersTask).start();
                    handleCheckVouchers();
                    tblList.itemsProperty().bind(checkVouchersTask.valueProperty());
                }
            }
        });

        ivExport.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FileChooser saveFile = new FileChooser();
                saveFile.setTitle("Export to file");
                saveFile.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Excel","*.xls")
                );
                File file = saveFile.showSaveDialog(((Node)event.getSource()).getScene().getWindow());
                if(file != null) {

                    System.out.println(file.getPath());

                    Workbook workbook = new HSSFWorkbook();
                    Sheet spreadsheet = workbook.createSheet("Check Vouchers");
                    Row row = spreadsheet.createRow(0);
                    for (int j = 0; j < tblList.getColumns().size(); j++) {
                        row.createCell(j).setCellValue(tblList.getColumns().get(j).getText());
                    }

                    for (int i = 0; i < tblList.getItems().size(); i++) {
                        row = spreadsheet.createRow(i + 1);
                        for (int j = 0; j < tblList.getColumns().size(); j++) {
                            if(tblList.getColumns().get(j).getCellData(i) != null) {
                                row.createCell(j).setCellValue(tblList.getColumns().get(j).getCellData(i).toString());
                            }
                            else {
                                row.createCell(j).setCellValue("");
                            }
                        }
                    }

                    FileOutputStream fileOut = null;
                    try {
                        fileOut = new FileOutputStream(file.getPath());
                        workbook.write(fileOut);
                        fileOut.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    System.out.println("Export was canceled.");
                }
            }
        });

        txtVoucher.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    if (company != null && bankAccount != null && !txtVoucher.getText().trim().isEmpty()) {
                        taskLoadMSSQLCheckVoucher(
                                company.getDbName(),bankAccount.getAccountNumber(),txtVoucher.getText()
                        );
                        new Thread(checkVoucherTask).start();
                        displayMSSQLCheckVoucher();
                    }
                }
            }
        });
    }

    private void loadAccountSettings() {
        ChecksAccount checksAccount = new ChecksAccount();
        checksAccount.setAccountSettings(new IChecksAccount() {
            @Override
            public void getAccountSettings(Company cmp, BankAccount acct) {
                clearFields();
                company = cmp;

                if (company.getDbType() == DBType.PSQL) {
                    try {
                        ConnectionExt2.getInstance().setConnection(
                                company.getServer(),company.getUsername(),
                                CryptoUtil.getInstance().decrypt(company.getPassword()),
                                company.getPort(),company.getDbName()
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                bankAccount = acct;
                txtDatabase.setText(company.getCmpName());
                txtAccountNo.setText(bankAccount.getAccountNumber() + " - " + bankAccount.getBankName());

                taskBankAccountStatus();
                new Thread(bankAccountStatusTask).start();
                displayBankAccountStatus();

                taskLoadMySQLCheckVouchers(company.getDbName(),bankAccount.getAccountNumber());
                new Thread(checkVouchersTask).start();
                handleCheckVouchers();
                tblList.itemsProperty().bind(checkVouchersTask.valueProperty());

            }
        });

        try {

            FXMLLoader checksaccountfxmlLoader = FXMLPath.CHECKSACCOUNT.getFXMLLoader();
            checksaccountfxmlLoader.setController(checksAccount);

            Parent root = checksaccountfxmlLoader.load();

            DropShadow shadow = new DropShadow();
            shadow.setWidth(20);
            shadow.setHeight(20);
            shadow.setOffsetX(0);
            shadow.setOffsetY(0);
            shadow.setRadius(20);

            root.setEffect(shadow); //Trial
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT); //Trial
            Stage stage = new Stage(StageStyle.TRANSPARENT);

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
            //stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAccountAdjustment() {
        BankAccountAdjustment bankAccountAdjustment = new BankAccountAdjustment(bankAccount);
        bankAccountAdjustment.setBankAccountAdjustment(new IBankAccountAdjustment() {
            @Override
            public void getExecuteStatus(ExecuteStatus executeStatus) {
                if (executeStatus == ExecuteStatus.SAVED) {

                    taskBankAccountStatus();
                    new Thread(bankAccountStatusTask).start();
                    displayBankAccountStatus();

                    taskLoadMySQLCheckVouchers(company.getDbName(),bankAccount.getAccountNumber());
                    new Thread(checkVouchersTask).start();
                    handleCheckVouchers();
                    tblList.itemsProperty().bind(checkVouchersTask.valueProperty());
                }
            }
        });

        try {

            FXMLLoader adjustmentLoader = FXMLPath.BANKACCOUNTADJUSTMENT.getFXMLLoader();
            adjustmentLoader.setController(bankAccountAdjustment);

            Parent root = adjustmentLoader.load();
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
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void taskBankAccountStatus() {
        bankAccountStatusTask = new Task<BankAccount>() {
            @Override
            protected BankAccount call() throws Exception {
                return new BankAccounts().getBankAccount(bankAccount.getAccountNumber());
            }
        };
    }


    private void taskLoadMSSQLCheckVoucher(String dbName,String accountNumber,String voucherNumber) {
        checkVoucherTask = new Task<CheckVoucher>() {
            @Override
            protected CheckVoucher call() throws Exception {
                //return new CheckVouchers().getMSSQLCheckVoucher(dbName,accountNumber,voucherNumber);
                return new CheckVouchers().importCheckVoucher(company,bankAccount,txtVoucher.getText());
            }
        };
    }

    private void taskLoadMySQLCheckVouchers(String dbName,String accountNumber) {
        checkVouchersTask = new Task<ObservableList<CheckVoucher>>() {
            @Override
            protected ObservableList<CheckVoucher> call() throws Exception {
                return new CheckVouchers().getChecksInTransit(dbName,accountNumber);
            }
        };
    }

    private void taskHandleCheckStatus(ObservableList<CheckVoucher> checkVouchers, CheckStatus checkStatus) {
        handleChecksStatusTask = new Task<ExecuteStatus>() {
            @Override
            protected ExecuteStatus call() throws Exception {
                for (int i = 0; i <= 1; i++) {
                    Thread.sleep(1000);
                }
                return new CheckVouchers().setCheckStatus(checkVouchers,checkStatus);
            }
        };
    }

    private void handleCheckVouchers() {
        selectCheckAmountTotal = new BigDecimal("0.00");
        txtSelectedChecksTotal.setText("0.00");
        checkVouchersTask.stateProperty().addListener((observable, oldValue, newValue) -> {
           if (newValue == Worker.State.RUNNING) {
               lockControls();
           }else if (newValue == Worker.State.SUCCEEDED){
               unlockControls();

               try {
                   if (checkVouchersTask.get() != null) {
                       DecimalFormat dc = new DecimalFormat("#,##0.00");
                       BigDecimal allChecks = new BigDecimal(0);
                       BigDecimal pendingChecks = new BigDecimal(0);
                       BigDecimal fundedChecks = new BigDecimal(0);

                       for (CheckVoucher cv : tblList.getItems()) {

                           cv.selectedProperty().addListener((observable1, oldValue1, newValue1) -> {
                               if (newValue1) {
                                   selectCheckAmountTotal = selectCheckAmountTotal.add(cv.getCheckAmount());
                               } else {
                                   selectCheckAmountTotal = selectCheckAmountTotal.subtract(cv.getCheckAmount());
                               }
                               txtSelectedChecksTotal.setText(dc.format(selectCheckAmountTotal.doubleValue()));
                           });

                           allChecks = allChecks.add(cv.getCheckAmount());

                           if (cv.getCheckStatus().equals(CheckStatus.PENDING)) {
                               pendingChecks = pendingChecks.add(cv.getCheckAmount());
                           } else if (cv.getCheckStatus().equals(CheckStatus.FUNDED)) {
                               fundedChecks = fundedChecks.add(cv.getCheckAmount());
                           }

                       }

                       txtAllChecksTotal.setText(dc.format(allChecks.doubleValue()));
                       txtPendingChecksTotal.setText(dc.format(pendingChecks.doubleValue()));
                       txtFundedChecksTotal.setText(dc.format(fundedChecks.doubleValue()));
                   }

               } catch (InterruptedException e) {
                   e.printStackTrace();
               } catch (ExecutionException e) {
                   e.printStackTrace();
               }
           } else {
               unlockControls();
           }
        });
    }

    private void displayBankAccountStatus() {
        bankAccountStatusTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                try {
                    if (bankAccountStatusTask.get() != null) {
                        DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
                        txtAccountBalance.setText(decimalFormat.format(bankAccountStatusTask.get().getBalance1()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void displayMSSQLCheckVoucher() {

        checkVoucherTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                try {

                    if (checkVoucherTask.get() != null) {

                        CheckDetails checkDetails = new CheckDetails(checkVoucherTask.get());
                        checkDetails.setCheckDetails(new ICheckDetails() {
                            @Override
                            public void getExecuteStatus(ExecuteStatus executeStatus) {
                                if (executeStatus == ExecuteStatus.SAVED) {

                                    taskBankAccountStatus();
                                    new Thread(bankAccountStatusTask).start();
                                    displayBankAccountStatus();

                                    taskLoadMySQLCheckVouchers(company.getDbName(),bankAccount.getAccountNumber());
                                    new Thread(checkVouchersTask).start();
                                    handleCheckVouchers();
                                    tblList.itemsProperty().bind(checkVouchersTask.valueProperty());

                                }
                            }
                        });

                        try {

                            FXMLLoader userFXML = FXMLPath.CHECKDETAILS.getFXMLLoader();
                            userFXML.setController(checkDetails);

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
                            stage.centerOnScreen();
                            stage.show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("No record found!");
                    }
                    unlockControls();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("Thread successful.");
            }  else if (newValue == Worker.State.RUNNING) {
                lockControls();
            }else  {
                unlockControls();
            }
        });
    }

    private void displayUpdatedCheckStatus() {
        handleChecksStatusTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                taskBankAccountStatus();
                new Thread(bankAccountStatusTask).start();
                displayBankAccountStatus();
                taskLoadMySQLCheckVouchers(company.getDbName(),bankAccount.getAccountNumber());
                new Thread(checkVouchersTask).start();
                handleCheckVouchers();
                tblList.itemsProperty().bind(checkVouchersTask.valueProperty());
            } else if (newValue == Worker.State.RUNNING) {
                lockControls();
            } else {
                unlockControls();
            }
        });
    }

    private void clearFields() {
        selectCheckAmountTotal = new BigDecimal("0.00");
        txtAccountBalance.setText("0.00");
        txtDatabase.setText("");
        txtAccountNo.setText("");
        txtVoucher.setText("");
        txtAllChecksTotal.setText("0.00");
        txtPendingChecksTotal.setText("0.00");
        txtFundedChecksTotal.setText("0.00");
        txtSelectedChecksTotal.setText("0.00");
    }

    private void lockControls() {
        if (taskRunning != null) {
            taskRunning.isTaskRunning(true);
        }
        pbrIndicator.setVisible(true);
        txtVoucher.setEditable(false);
        tblList.setEditable(false);
        hbxMenu.setDisable(true);
        hbxChecksMenu.setDisable(true);
    }

    private void unlockControls() {
        pbrIndicator.setVisible(false);
        txtVoucher.setEditable(true);
        tblList.setEditable(true);
        hbxMenu.setDisable(false);
        hbxChecksMenu.setDisable(false);
        if (taskRunning != null) {
            taskRunning.isTaskRunning(false);
        }
    }

    private class CheckStatusCell extends TableCell<CheckVoucher, CheckStatus> {

        MaterialDesignIconView ivFunded = new MaterialDesignIconView(MaterialDesignIcon.CASH_MULTIPLE);
        MaterialDesignIconView ivPending = new MaterialDesignIconView(MaterialDesignIcon.MINUS);
        HBox hb1,hb2;
        CheckStatusCell() {

            ivFunded.setSize("25");
            ivFunded.setStyleClass("actionButton");

            ivPending.setSize("25");
            ivPending.setStyleClass("actionButton");

            hb1 = new HBox(ivFunded);
            hb1.setSpacing(10);
            hb1.setAlignment(Pos.CENTER);
            hb1.setPrefSize(50,25);

            hb2 = new HBox(ivPending);
            hb2.setSpacing(10);
            hb2.setAlignment(Pos.CENTER);
            hb2.setPrefSize(50,25);

        }

        @Override
        protected void updateItem(CheckStatus item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                if (item.equals(CheckStatus.FUNDED)) {
                    setGraphic(hb1);
                } else {
                    setGraphic(hb2);
                }
            } else {
                setGraphic(null);
            }
        }
    }

    public void setTaskRunning(IThread taskRunning) {
        this.taskRunning = taskRunning;

    }
}
