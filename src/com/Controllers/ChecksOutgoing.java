package com.Controllers;

import com.DataAccessObjects.CheckVouchers;
import com.DataModels.CheckVoucher;
import com.DataModels.CheckVoucher.CheckStatus;
import com.Interfaces.IThread;
import com.Utilities.ExecuteStatus;
import com.jfoenix.controls.JFXProgressBar;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

public class ChecksOutgoing {
    @FXML
    private JFXProgressBar pbrIndicator;
    @FXML
    private Label lblIndicator;
    @FXML
    private HBox hbxChecksMenu;
    @FXML
    private MaterialDesignIconView ivRelease;
    @FXML
    private MaterialDesignIconView ivRefresh;
    @FXML
    private TableView<CheckVoucher> tblList;
    @FXML
    private MaterialDesignIconView ivDelete;

    private IThread taskRunning;
    private CheckStatus checkStatus;
    private TableColumn<CheckVoucher,Boolean> colSelect = new TableColumn("Selected");
    private TableColumn<CheckVoucher, Date> colActualDate;
    private TableColumn<CheckVoucher, Date> colCheckDate = new TableColumn("Check Date");
    private TableColumn<CheckVoucher,Integer> colTransref = new TableColumn<>("Voucher No.");
    private TableColumn<CheckVoucher,String> colCheckNum = new TableColumn<>("Check No.");
    private TableColumn<CheckVoucher,String> colVendorName = new TableColumn<>("Supplier");
    private TableColumn<CheckVoucher,BigDecimal> colCheckSum = new TableColumn<>("Amount");
    private TableColumn<CheckVoucher,String> colDBName = new TableColumn<>("Company");

    private Task<ExecuteStatus> handleChecksStatusTask;
    private Task<ObservableList<CheckVoucher>> checkVouchersTask;

    public ChecksOutgoing(CheckStatus checkStatus) {
        this.checkStatus = checkStatus;

    }

    public void initialize() {

        if (checkStatus.equals(CheckStatus.OUTSTANDING)) {
            lblIndicator.setText("Checks Releasing");
            ivRelease.setGlyphName("CHECKBOX_MARKED_CIRCLE_OUTLINE");
            colActualDate = new TableColumn("Release Date");
        } else {
            lblIndicator.setText("Checks Encashment");
            ivRelease.setGlyphName("WALLET");
            colActualDate = new TableColumn("Encash Date");
        }

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
                return new CheckBoxTableCell<>();
            }
        });

        colActualDate.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CheckVoucher, Date>, ObservableValue<Date>>() {
            @Override
            public ObservableValue<Date> call(TableColumn.CellDataFeatures<CheckVoucher, Date> param) {
                return param.getValue().actualDateProperty();
            }
        });

        colActualDate.setMinWidth(80);
        colActualDate.setEditable(true);
        colActualDate.setCellFactory(new Callback<TableColumn<CheckVoucher, Date>, TableCell<CheckVoucher, Date>>() {
            @Override
            public TableCell<CheckVoucher, Date> call(TableColumn<CheckVoucher, Date> param) {
                return new DatePickerTableCell();
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

        colVendorName.setMinWidth(350);
        colVendorName.setMaxWidth(350);
        colVendorName.setResizable(false);
        colVendorName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CheckVoucher, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CheckVoucher, String> param) {
                return param.getValue().vendorNameProperty();
            }
        });

        colCheckSum.setMinWidth(100);
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

        colDBName.setMinWidth(100);
        colDBName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CheckVoucher, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CheckVoucher, String> param) {
                return param.getValue().databaseNameProperty();
            }
        });

        tblList.setEditable(true);
        tblList.getColumns().addAll(colSelect,colActualDate,colTransref,colCheckNum,colVendorName,colCheckDate,colCheckSum,colDBName);

        ivRelease.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    taskHandleCheckStatus(checkVouchersTask.get(),checkStatus);
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
                taskLoadMySQLCheckVouchers();
                new Thread(checkVouchersTask).start();
                handleCheckVouchers();
                tblList.itemsProperty().bind(checkVouchersTask.valueProperty());
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

        taskLoadMySQLCheckVouchers();
        new Thread(checkVouchersTask).start();
        handleCheckVouchers();
        tblList.itemsProperty().bind(checkVouchersTask.valueProperty());

    }

    private void taskLoadMySQLCheckVouchers() {
        checkVouchersTask = new Task<ObservableList<CheckVoucher>>() {
            @Override
            protected ObservableList<CheckVoucher> call() throws Exception {
                if (checkStatus.equals(CheckStatus.OUTSTANDING)) {
                    return new CheckVouchers().getChecksOutgoing(CheckStatus.FUNDED);
                } else {
                    return new CheckVouchers().getChecksOutgoing(CheckStatus.OUTSTANDING);
                }
            }
        };
    }

    private void taskHandleCheckStatus(ObservableList<CheckVoucher> checkVouchers, CheckStatus chkStat) {
        handleChecksStatusTask = new Task<ExecuteStatus>() {
            @Override
            protected ExecuteStatus call() throws Exception {
                for (int i = 0; i <= 1; i++) {
                    Thread.sleep(1000);
                }

                return new CheckVouchers().setCheckStatus(checkVouchers,chkStat);
            }
        };
    }

    private void handleCheckVouchers() {
        checkVouchersTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.RUNNING) {
               lockControls();
            } else {
               unlockControls();
            }
        });
    }

    private void displayUpdatedCheckStatus() {
        handleChecksStatusTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                taskLoadMySQLCheckVouchers();
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
//        txtAccountBalance.setText("0.00");
//        txtDatabase.setText("");
//        txtAccountNo.setText("");
//        txtVoucher.setText("");
    }

    private void lockControls() {
        if (taskRunning != null) {
            taskRunning.isTaskRunning(true);
        }
        pbrIndicator.setVisible(true);
        hbxChecksMenu.setDisable(true);
        tblList.setEditable(false);
    }

    private void unlockControls() {
        pbrIndicator.setVisible(false);
        hbxChecksMenu.setDisable(false);
        tblList.setEditable(true);
        if (taskRunning != null) {
            taskRunning.isTaskRunning(false);
        }
    }

    public void setTaskRunning(IThread taskRunning) {
        this.taskRunning = taskRunning;

    }

    class DatePickerTableCell extends TableCell<CheckVoucher, Date> {
        private DatePicker datePicker;

        private DatePickerTableCell() {
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createDatePicker();
                setText(null);
                setGraphic(datePicker);
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText(getDate().toString());
            setGraphic(null);
        }

        @Override
        public void updateItem(java.sql.Date item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (datePicker != null) {
                        datePicker.setValue(LocalDate.parse((getDate().toString())));
                    }
                    setText(null);
                    setGraphic(datePicker);
                } else {
                    setText(getDate().toString());
                    setGraphic(null);
                }
            }
        }

        private void createDatePicker() {
            datePicker = new DatePicker(getDate().toLocalDate());
            String datePattern = "yyyy-MM-dd";
            datePicker.setPromptText(datePattern);
            datePicker.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            datePicker.setConverter(new StringConverter<LocalDate>() {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);
                @Override
                public String toString(LocalDate object) {
                    if (object != null) {
                        return dateFormatter.format(object);
                    } else {
                        return "";
                    }
                }

                @Override
                public LocalDate fromString(String string) {
                    if (string != null && !string.isEmpty()) {
                        return LocalDate.parse(string, dateFormatter);
                    } else {
                        return null;
                    }
                }
            });
            datePicker.setOnAction((e) -> {
                commitEdit(Date.valueOf(datePicker.getValue()));
            });
        }

        private Date getDate() {
            return getItem() == null ? Date.valueOf(LocalDate.now()) : getItem();
        }
    }


}
