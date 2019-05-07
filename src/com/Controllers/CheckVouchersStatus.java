package com.Controllers;

import com.DataAccessObjects.BankAccounts;
import com.DataAccessObjects.CheckVouchers;
import com.DataModels.BankAccount;
import com.DataModels.CheckVoucher;
import com.DataModels.CheckVoucher.CheckStatus;
import com.DataModels.Company;
import com.Interfaces.IChecksAccount;
import com.Utilities.FXMLPath;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
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

;

public class CheckVouchersStatus {

    @FXML
    private JFXTextField txtDatabase;
    @FXML
    private JFXTextField txtAccountNo;
    @FXML
    private JFXComboBox cboCheckStatus;
    @FXML
    private JFXTextField txtFundingBalance;
    @FXML
    private JFXTextField txtReleasingBalance;
    @FXML
    private JFXTextField txtEncashmentBalance;
    @FXML
    private HBox hbxMenu;
    @FXML
    private MaterialDesignIconView ivBankAccount;
    @FXML
    private MaterialDesignIconView ivExport;
    @FXML
    private MaterialDesignIconView ivRefresh;
    @FXML
    private JFXProgressBar pbrIndicator;
    @FXML
    private TableView<CheckVoucher> tblList;

    private double xOffset = 0;
    private double yOffset = 0;
    private Company company;
    private BankAccount bankAccount;
    private Task<BankAccount> bankAccountStatusTask;
    private Task<ObservableList<CheckVoucher>> checkVouchersTask;
    private TableColumn<CheckVoucher, Date> colCheckDate = new TableColumn("Check Date");
    private TableColumn<CheckVoucher,Integer> colTransref = new TableColumn<>("Voucher No.");
    private TableColumn<CheckVoucher,String> colCheckNum = new TableColumn<>("Check No.");
    private TableColumn<CheckVoucher,String> colVendorName = new TableColumn<>("Supplier");
    private TableColumn<CheckVoucher,BigDecimal> colCheckSum = new TableColumn<>("Amount");
    private TableColumn<CheckVoucher,CheckStatus> colCheckStatus = new TableColumn<>("Status");

    public void initialize() {
        cboCheckStatus.setVisible(false);
        ivRefresh.setVisible(false);
        unlockControls();
        clearFields();

        colTransref.setMinWidth(100);
        colTransref.setResizable(false);
        colTransref.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CheckVoucher, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<CheckVoucher, Integer> param) {
                return param.getValue().transRefProperty();
            }
        });

        colCheckNum.setMinWidth(100);
        colCheckNum.setResizable(false);
        colCheckNum.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CheckVoucher, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CheckVoucher, String> param) {
                return param.getValue().checkNumProperty();
            }
        });

        colVendorName.setMinWidth(420);
        colVendorName.setMaxWidth(420);
        colVendorName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CheckVoucher, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CheckVoucher, String> param) {
                return param.getValue().vendorNameProperty();
            }
        });

        colCheckDate.setMinWidth(80);
        colCheckDate.setResizable(false);
        colCheckDate.setStyle( "-fx-alignment: CENTER;");
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

        colCheckStatus.setMinWidth(100);
        colCheckStatus.setResizable(false);
        colCheckStatus.setStyle( "-fx-alignment: CENTER;");
        colCheckStatus.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CheckVoucher, CheckStatus>, ObservableValue<CheckStatus>>() {
            @Override
            public ObservableValue<CheckStatus> call(TableColumn.CellDataFeatures<CheckVoucher, CheckStatus> param) {
                return param.getValue().checkStatusProperty();
            }
        });

        tblList.getColumns().addAll(colTransref,colCheckNum,colVendorName,colCheckDate,colCheckSum,colCheckStatus);

        ivBankAccount.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                loadAccountSettings();
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
    }

    private void loadAccountSettings() {
        ChecksAccount checksAccount = new ChecksAccount();
        checksAccount.setAccountSettings(new IChecksAccount() {
            @Override
            public void getAccountSettings(Company cmp, BankAccount acct) {
                clearFields();
                company = cmp;
                bankAccount = acct;
                txtDatabase.setText(company.getCmpName());
                txtAccountNo.setText(bankAccount.getAccountNumber() + " - " + bankAccount.getBankName());

                taskBankAccountStatus();
                new Thread(bankAccountStatusTask).start();
                displayBankAccountStatus();

                taskLoadMySQLCheckVouchers();
                new Thread(checkVouchersTask).start();
                handleCheckVouchers();
                tblList.itemsProperty().bind(checkVouchersTask.valueProperty());

            }
        });

        try {

            FXMLLoader checksaccountfxmlLoader = FXMLPath.CHECKSACCOUNT.getFXMLLoader();
            checksaccountfxmlLoader.setController(checksAccount);

            Parent root = checksaccountfxmlLoader.load();
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

    private void displayBankAccountStatus() {
        bankAccountStatusTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                try {
                    if (bankAccountStatusTask.get() != null) {
                        DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
                        txtFundingBalance.setText(decimalFormat.format(bankAccountStatusTask.get().getBalance1()));
                        txtReleasingBalance.setText(decimalFormat.format(bankAccountStatusTask.get().getBalance2()));
                        txtEncashmentBalance.setText(decimalFormat.format(bankAccountStatusTask.get().getBalance3()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void taskLoadMySQLCheckVouchers() {
        checkVouchersTask = new Task<ObservableList<CheckVoucher>>() {
            @Override
            protected ObservableList<CheckVoucher> call() throws Exception {
                return new CheckVouchers().getCheckVouchers(company.getDbName(),bankAccount.getAccountNumber(),null);
            }
        };
    }

    private void handleCheckVouchers() {
        checkVouchersTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.RUNNING) {
                lockControls();
            }else if (newValue == Worker.State.SUCCEEDED){
                unlockControls();
            } else {
                unlockControls();
            }
        });
    }
    private void clearFields() {
        txtDatabase.setText("");
        txtAccountNo.setText("");
        txtFundingBalance.setText("0.00");
        txtReleasingBalance.setText("0.00");
        txtEncashmentBalance.setText("0.00");
    }

    private void lockControls() {
//        if (taskRunning != null) {
//            taskRunning.isTaskRunning(true);
//        }
        pbrIndicator.setVisible(true);
        tblList.setEditable(false);
        hbxMenu.setDisable(true);
    }

    private void unlockControls() {
        pbrIndicator.setVisible(false);
        tblList.setEditable(true);
        hbxMenu.setDisable(false);
//        if (taskRunning != null) {
//            taskRunning.isTaskRunning(false);
//        }
    }
}
