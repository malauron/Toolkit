package com.DataAccessObjects;

import com.DataModels.Billing;
import com.Interfaces.IBillings;
import com.Utilities.BillingStatus;
import com.Utilities.ConnectionMain;
import com.Utilities.CurrentUser;
import com.Utilities.ExecuteStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static com.DataAccessObjects.DataAccessConstants.*;

public class Billings implements IBillings {

    @Override
    public Billing getBilling(int billingID) {

        String sql = "select x1.*,ucase(x2."+ COL_SUPPLIER_NAME +") + " + COL_SUPPLIER_NAME + " " +
                "from "+ TBL_BILLINGS +" x1 " +
                "left outer join " + TBL_SUPPLIERS + " x2 on x1." + COL_SUPPLIER_ID + " = x2." + COL_SUPPLIER_ID + " " +
                "where x1."+ COL_BILLING_ID +" = " + billingID + " " +
                "order by x1." + COL_BILLING_TRNX_DATE + " DESC";
        try {
            PreparedStatement ps = ConnectionMain.getInstance().cn().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            Billing billing = null;
            if (rs.next()) {
                billing = setBilling(rs);
            } else {

            }
            rs.close();
            ps.close();
            return billing;
        } catch (Exception e) {
            System.err.println("Error: ");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ObservableList<Billing> getBillings(String param, BillingStatus param2) {

        ObservableList<Billing> billings = FXCollections.observableArrayList();
        String sql = "SELECT x1.*,ucase(x2."+ COL_SUPPLIER_NAME +") "+ COL_SUPPLIER_NAME +" " +
                "FROM "+ TBL_BILLINGS + " x1 " +
                "left outer join " + TBL_SUPPLIERS + " x2 on x1." + COL_SUPPLIER_ID + " = x2." + COL_SUPPLIER_ID + " " +
                "WHERE (x2." + COL_SUPPLIER_NAME + " like ? or x1." + COL_BILLING_REFNO + " like ? ) and (" +
                "billing_status = ? and billing_status <> 'CANCELLED')";

        try {
            PreparedStatement ps = ConnectionMain.getInstance().cn().prepareStatement(sql);
            ps.setString(1, "%" + param + "%");
            ps.setString(2, "%" + param + "%");
            ps.setString(3,  param2.name() );

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                billings.add(setBilling(rs));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return billings;
    }

    @Override
    public ObservableList<PieChart.Data> getPieChartData() {

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        String sql = "SELECT COUNT("+ COL_BILLING_ID +") "+ COL_CTR +", "+ COL_BILLING_STATUS +
                " FROM "+ TBL_BILLINGS +" GROUP BY "+ COL_BILLING_STATUS;

        try {
            PreparedStatement ps = ConnectionMain.getInstance().cn().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                pieChartData.add(new PieChart.Data(rs.getString(COL_BILLING_STATUS),rs.getInt(COL_CTR)));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pieChartData;
    }

    @Override
    public ExecuteStatus handleBilling(Billing billing) {

        ExecuteStatus executeStatus = ExecuteStatus.ERROR_OCCURED;

        try {

            ConnectionMain.getInstance().cn().setAutoCommit(false);
            PreparedStatement ps;
            String sql;

            if(billing.getBillingID() == 0) {

                sql = "insert into "+ TBL_BILLINGS +" ("+ COL_SUPPLIER_ID +", "+ COL_BILLING_REFNO +", "+ COL_BILLING_TRNX_DATE +", " +
                        ""+ COL_BILLING_AMOUNT +", "+ COL_BILLING_REMARKS +","+ COL_RECEIVED_USER_ID +") " +
                        "values (?, ?, now(), ?, ?, ?)";
                ps = ConnectionMain.getInstance().cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, billing.getSupplier().getSupplierID().toString());
                ps.setString(2, billing.getBillingRefNo());
                ps.setDouble(3, billing.getBillingAmount());
                ps.setString(4, billing.getRemarks());
                ps.setInt(5, CurrentUser.getInstance().getUserID());
                ps.executeUpdate();
                ResultSet lastID = ps.getGeneratedKeys();
                lastID.next();
                billing.setBillingID(lastID.getInt(1));
                executeStatus = ExecuteStatus.SAVED;

            } else {
                sql = "update "+ TBL_BILLINGS +" set "+ COL_SUPPLIER_ID +" = ?, "+ COL_BILLING_REFNO +" = ?, " +
                        " "+ COL_BILLING_AMOUNT +" = ?, "+ COL_BILLING_REMARKS +" = ?  " +
                        "where "+ COL_BILLING_ID +" = ?";
                ps = ConnectionMain.getInstance().cn().prepareStatement(sql);
                ps.setString(1, billing.getSupplier().getSupplierID().toString());
                ps.setString(2, billing.getBillingRefNo());
                ps.setDouble(3, billing.getBillingAmount());
                ps.setString(4, billing.getRemarks());
                ps.setInt(5,billing.getBillingID());
                ps.executeUpdate();
                executeStatus = ExecuteStatus.UPDATED;

            }

            ps.close();
            ConnectionMain.getInstance().cn().commit();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                ConnectionMain.getInstance().cn().rollback();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                ConnectionMain.getInstance().cn().setAutoCommit(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return executeStatus;
    }

    @Override
    public ExecuteStatus updateBillingStatus(Billing billing, BillingStatus param) {
        ExecuteStatus executeStatus = ExecuteStatus.ERROR_OCCURED;
        String COL_STATUS_DATE = "";
        if (param.equals(BillingStatus.PROCESSED)) {
            COL_STATUS_DATE = COL_PROCESSED_DATE;
        } else if(param.equals(BillingStatus.CANCELLED)) {
            COL_STATUS_DATE = COL_CANCELLED_DATE;
        }

        try {

            ConnectionMain.getInstance().cn().setAutoCommit(false);
            PreparedStatement ps;
            String sql;

            sql = "update "+ TBL_BILLINGS +" set "+ COL_BILLING_STATUS +" = ?, "+
                    COL_PROCESSOR_USER_ID  +" = ?, " + COL_STATUS_DATE + "= NOW() " +
                    "where "+ COL_BILLING_ID +" = ?";

            ps = ConnectionMain.getInstance().cn().prepareStatement(sql);
            ps.setString(1, param.name());
            ps.setInt(2, CurrentUser.getInstance().getUserID());
            ps.setInt(3,billing.getBillingID());
            ps.executeUpdate();

            ps.close();
            ConnectionMain.getInstance().cn().commit();
            executeStatus = ExecuteStatus.UPDATED;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                ConnectionMain.getInstance().cn().rollback();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                ConnectionMain.getInstance().cn().setAutoCommit(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return executeStatus;
    }

    @Override
    public BarChart<String, Number> getBarChartData() {

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Billing Status");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Total Billings");

        BarChart<String, Number> barchartBilling = new BarChart<>(xAxis,yAxis);

        XYChart.Series<String,Number> xyReceived = new XYChart.Series<>();
        XYChart.Series<String,Number> xyProcessing = new XYChart.Series<>();

        xyReceived.setName(BillingStatus.RECEIVED.name());
        xyProcessing.setName(BillingStatus.PROCESSING.name());

        String sql = "select * from " + VW_BARCHARTBILLINGS;
        try {
            PreparedStatement ps = ConnectionMain.getInstance().cn().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString(COL_BILLING_STATUS).equals(BillingStatus.RECEIVED.name())) {
                    xyReceived.getData().add(new XYChart.Data<>(rs.getString(COL_AGE_DESCRIPTION),rs.getInt(COL_CTR)));
                } else if (rs.getString(COL_BILLING_STATUS).equals(BillingStatus.PROCESSING.name())) {
                    xyProcessing.getData().add(new XYChart.Data<>(rs.getString(COL_AGE_DESCRIPTION),rs.getInt(COL_CTR)));
                }
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        barchartBilling.getData().addAll(xyReceived,xyProcessing);
        return barchartBilling;
    }

    private Billing setBilling(ResultSet rs) {

        try {

            Billing billing = new Billing();
            Suppliers suppliers = new Suppliers();

            billing.setBillingID(rs.getInt(COL_BILLING_ID));
            billing.setSupplier(suppliers.getSupplier(rs.getInt(COL_SUPPLIER_ID)));
            billing.setBillingRefNo(rs.getString(COL_BILLING_REFNO));
            billing.setBillingAmount(rs.getDouble(COL_BILLING_AMOUNT));
            billing.setTransactionDate(rs.getDate(COL_BILLING_TRNX_DATE));
            billing.setRemarks(rs.getString(COL_BILLING_REMARKS));

            return billing;

        } catch (Exception e) {

            System.err.println("Error: ");
            e.printStackTrace();

        }

        return null;
    }
}














