package com.Interfaces;

import com.DataModels.CheckVoucher;
import com.Utilities.ExecuteStatus;
import javafx.collections.ObservableList;

public interface ICheckVouchers {
    CheckVoucher getMSSQLCheckVoucher(String dbName, String accountNumber, String voucherNumber);

    ObservableList<CheckVoucher> getChecksInTransit(String dbName, String accountNumber);

    ExecuteStatus handleCheckVoucher(CheckVoucher checkVoucher);
}
