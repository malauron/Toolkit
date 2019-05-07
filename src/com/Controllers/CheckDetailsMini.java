package com.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class CheckDetailsMini {
    @FXML
    private Label lblVoucherNo;
    @FXML
    private Label lblCheckNo;
    @FXML
    private Label lblDate;
    @FXML
    private Label lblPayee;
    @FXML
    private Label lblAmount;

    public void setVoucherNo(String voucherNo) {
        lblVoucherNo.setText(voucherNo);
    }

    public void setCheckNo(String checkNo) {
        lblCheckNo.setText(checkNo);
    }

    public void setDate(String date) {
        lblDate.setText(date);
    }

    public void setPayee(String payee) {
        lblPayee.setText(payee);
    }

    public void setAmount(BigDecimal amount) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
        lblAmount.setText(decimalFormat.format(amount));
    }
}
