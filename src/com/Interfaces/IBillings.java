package com.Interfaces;

import com.DataModels.Billing;
import com.Utilities.BillingStatus;
import com.Utilities.ExecuteStatus;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;

public interface IBillings {
    Billing getBilling(int billingID);
    ObservableList<Billing> getBillings(String param, BillingStatus billingStatus);
    ObservableList<PieChart.Data> getPieChartData();
    ExecuteStatus handleBilling(Billing billing);
    ExecuteStatus updateBillingStatus(Billing billing, BillingStatus billingStatus);
    BarChart<String, Number> getBarChartData();

}
