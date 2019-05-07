package com.Interfaces;

import com.DataModels.Supplier;
import com.Utilities.ExecuteStatus;
import javafx.collections.ObservableList;

public interface ISuppliers {
    Supplier getSupplier(int supplierID);
    ObservableList<Supplier> getSuppliers(String param);
    ExecuteStatus handleSupplier(Supplier supplier);
}
