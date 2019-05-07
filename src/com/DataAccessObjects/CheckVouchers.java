package com.DataAccessObjects;

import com.DataModels.BankAccount;
import com.DataModels.CheckVoucher;
import com.DataModels.CheckVoucher.CheckStatus;
import com.DataModels.Company;
import com.DataModels.Company.DBType;
import com.Interfaces.ICheckVouchers;
import com.Utilities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.DataAccessObjects.DataAccessConstants.*;

public class CheckVouchers implements ICheckVouchers {

    public CheckVoucher importCheckVoucher(Company cmp, BankAccount ba, String voucherNumber) {
        StringBuilder  sql = new StringBuilder();
        PreparedStatement ps;
        ResultSet rs;
        try {
            if (cmp.getDbType() == DBType.MSSQL) {
                sql.append("select " + COL_CHECKKEY + "," + COL_TRANSREF + "," + COL_VENDORCODE + ","
                        + COL_VENDORNAME + "," + COL_CHECKDATE + "," + COL_CHECKSUM + ","
                        + COL_CHECKNUM + "," + COL_BANKNUM +
                        " FROM " + cmp.getDbName() + TBL_OCHO +
                        " where " + COL_ACCTNUM + " = ? and " + COL_TRANSREF + " = ? and " + COL_CANCELED + " = 'N'");
                ps = ConnectionExt.getInstance().cn().prepareStatement(sql.toString());
                ps.setString(1,ba.getAccountNumber());
                ps.setString(2,voucherNumber);
                rs = ps.executeQuery();
                if (rs.next()) {
                    CheckVoucher checkVoucher = new CheckVoucher();
                    checkVoucher.setDatabaseName(cmp.getDbName());
                    checkVoucher.setCheckKey(rs.getInt(COL_CHECKKEY));
                    checkVoucher.setTransRef(rs.getInt(COL_TRANSREF));
                    checkVoucher.setVendorCode(rs.getString(COL_VENDORCODE));
                    checkVoucher.setVendorName(rs.getString(COL_VENDORNAME));
                    checkVoucher.setCheckDate(rs.getDate(COL_CHECKDATE));
                    checkVoucher.setCheckAmount(rs.getBigDecimal(COL_CHECKSUM));
                    checkVoucher.setCheckNum(rs.getString(COL_CHECKNUM));
                    checkVoucher.setBankNum(rs.getString(COL_BANKNUM));
                    checkVoucher.setAccountNumber(ba.getAccountNumber());
                    rs.close();
                    ps.close();
                    return checkVoucher;
                } else {
                    return null;
                }
            } else {
                sql.append("select " + COL_POSTORDER + "," + COL_JRNLKEY + "," + COL_VENDID + "," + COL_JRNLDESC +
                           "," + COL_JRNLREF + "," + COL_TRNXDATE + ",-1*" + COL_MAINAMT + " " + COL_MAINAMT +
                           " from " + TBL_JRNLHDR +
                           " where " + COL_MODULE + " = 'P' and " + COL_JRNLREF + " = ? and " + COL_GLACCTNO + " = ? ");
                ps = ConnectionExt2.getInstance().getConnection().prepareStatement(sql.toString());
                ps.setString(1,voucherNumber);
                ps.setInt(2,ba.getGLAccountNo());
                rs = ps.executeQuery();
                if (rs.next()) {
                    CheckVoucher checkVoucher = new CheckVoucher();
                    checkVoucher.setDatabaseName(cmp.getDbName());
                    checkVoucher.setCheckKey(rs.getInt(COL_POSTORDER));
                    checkVoucher.setTransRef(rs.getInt(COL_JRNLKEY));
                    checkVoucher.setVendorCode(rs.getString(COL_VENDID));
                    checkVoucher.setVendorName(rs.getString(COL_JRNLDESC));
                    checkVoucher.setCheckDate(rs.getDate(COL_TRNXDATE));
                    checkVoucher.setCheckAmount(rs.getBigDecimal(COL_MAINAMT));
                    checkVoucher.setCheckNum(rs.getString(COL_JRNLREF));
                    checkVoucher.setBankNum(ba.getBankName());
                    checkVoucher.setAccountNumber(ba.getAccountNumber());
                    rs.close();
                    ps.close();
                    return checkVoucher;
                } else {
                    return null;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public CheckVoucher getMSSQLCheckVoucher(String dbName, String accountNumber, String voucherNumber) {
        String sql = "select " + COL_CHECKKEY + "," + COL_TRANSREF + "," + COL_VENDORCODE + ","
                     + COL_VENDORNAME + "," + COL_CHECKDATE + "," + COL_CHECKSUM + ","
                     + COL_CHECKNUM + "," + COL_BANKNUM +
                    " FROM " + dbName + TBL_OCHO +
                    " where " + COL_ACCTNUM + " = ? and " + COL_TRANSREF + " = ? and " + COL_CANCELED + " = 'N'";
        try {
            PreparedStatement ps = ConnectionExt.getInstance().cn().prepareStatement(sql);
            ps.setString(1,accountNumber);
            ps.setString(2,voucherNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CheckVoucher checkVoucher = new CheckVoucher();
                checkVoucher.setDatabaseName(dbName);
                checkVoucher.setCheckKey(rs.getInt(COL_CHECKKEY));
                checkVoucher.setTransRef(rs.getInt(COL_TRANSREF));
                checkVoucher.setVendorCode(rs.getString(COL_VENDORCODE));
                checkVoucher.setVendorName(rs.getString(COL_VENDORNAME));
                checkVoucher.setCheckDate(rs.getDate(COL_CHECKDATE));
                checkVoucher.setCheckAmount(rs.getBigDecimal(COL_CHECKSUM));
                checkVoucher.setCheckNum(rs.getString(COL_CHECKNUM));
                checkVoucher.setBankNum(rs.getString(COL_BANKNUM));
                checkVoucher.setAccountNumber(accountNumber);
                rs.close();
                ps.close();
                return checkVoucher;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ObservableList<CheckVoucher> getChecksOutgoing(CheckStatus checkStatus) {
        ObservableList<CheckVoucher> checkVouchers = FXCollections.observableArrayList();
        String sql = "select " + COL_DBNAME + ", " + COL_CHECKKEY + "," + COL_TRANSREF + "," + COL_VENDORCODE + ","
                + COL_VENDORNAME + "," + COL_CHECKDATE + "," + COL_CHECKSUM + "," + COL_ACCTNUM  + ", "
                + COL_CHECKNUM + "," + COL_BANKNUM + "," + COL_CHECKSTATUS + ", now() dateToday " +
                " from " + TBL_CHECKVOUCHERS +
                " where " + COL_CHECKSTATUS + " in (?) " +
                " order by " + COL_CHECKKEY + " desc";
        try {
            PreparedStatement ps = ConnectionMain.getInstance().cn().prepareStatement(sql);
            ps.setString(1,checkStatus.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CheckVoucher checkVoucher = new CheckVoucher();
                checkVoucher.setDatabaseName(rs.getString(COL_DBNAME));
                checkVoucher.setCheckKey(rs.getInt(COL_CHECKKEY));
                checkVoucher.setTransRef(rs.getInt(COL_TRANSREF));
                checkVoucher.setVendorCode(rs.getString(COL_VENDORCODE));
                checkVoucher.setVendorName(rs.getString(COL_VENDORNAME));
                checkVoucher.setCheckDate(rs.getDate(COL_CHECKDATE));
                checkVoucher.setCheckAmount(rs.getBigDecimal(COL_CHECKSUM));
                checkVoucher.setCheckNum(rs.getString(COL_CHECKNUM));
                checkVoucher.setBankNum(rs.getString(COL_BANKNUM));
                checkVoucher.setCheckStatus((CheckStatus.valueOf(rs.getString(COL_CHECKSTATUS))));
                checkVoucher.setAccountNumber(rs.getString(COL_ACCTNUM));
                checkVoucher.setActualDate(rs.getDate("dateToday"));
                checkVouchers.add(checkVoucher);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return checkVouchers;
    }

    @Override
    public ObservableList<CheckVoucher> getChecksInTransit(String dbName, String accountNumber) {
        ObservableList<CheckVoucher> checkVouchers = FXCollections.observableArrayList();
        String sql = "select " + COL_CHECKKEY + "," + COL_TRANSREF + "," + COL_VENDORCODE + ","
                + COL_VENDORNAME + "," + COL_CHECKDATE + "," + COL_CHECKSUM + ","
                + COL_CHECKNUM + "," + COL_BANKNUM + "," + COL_CHECKSTATUS +
                " from " + TBL_CHECKVOUCHERS +
                " where " + COL_DBNAME + " = ? and " + COL_ACCTNUM + " = ? " +
                " and " + COL_CHECKSTATUS + " in (?,?) " +
                " order by " + COL_CHECKKEY + " desc";
        try {
            PreparedStatement ps = ConnectionMain.getInstance().cn().prepareStatement(sql);
            ps.setString(1,dbName);
            ps.setString(2,accountNumber);
            ps.setString(3,CheckStatus.PENDING.name());
            ps.setString(4,CheckStatus.FUNDED.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CheckVoucher checkVoucher = new CheckVoucher();
                checkVoucher.setDatabaseName(dbName);
                checkVoucher.setCheckKey(rs.getInt(COL_CHECKKEY));
                checkVoucher.setTransRef(rs.getInt(COL_TRANSREF));
                checkVoucher.setVendorCode(rs.getString(COL_VENDORCODE));
                checkVoucher.setVendorName(rs.getString(COL_VENDORNAME));
                checkVoucher.setCheckDate(rs.getDate(COL_CHECKDATE));
                checkVoucher.setCheckAmount(rs.getBigDecimal(COL_CHECKSUM));
                checkVoucher.setCheckNum(rs.getString(COL_CHECKNUM));
                checkVoucher.setBankNum(rs.getString(COL_BANKNUM));
                checkVoucher.setCheckStatus((CheckStatus.valueOf(rs.getString(COL_CHECKSTATUS))));
                checkVoucher.setAccountNumber(accountNumber);
                checkVouchers.add(checkVoucher);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return checkVouchers;
    }

    public ObservableList<CheckVoucher> getCheckVouchers(String dbName, String accountNumber,CheckStatus checkStatus) {
        ObservableList<CheckVoucher> checkVouchers = FXCollections.observableArrayList();
        StringBuilder sql = new StringBuilder();
        String param;
        if (checkStatus == null) {
            param = " is not null ";
        } else {
            param = " = '" + checkStatus.name() + "' ";
        }
        sql.append("select " + COL_CHECKKEY + "," + COL_TRANSREF + "," + COL_VENDORCODE + ","
                + COL_VENDORNAME + "," + COL_CHECKDATE + "," + COL_CHECKSUM + ","
                + COL_CHECKNUM + "," + COL_BANKNUM + "," + COL_CHECKSTATUS +
                " from " + TBL_CHECKVOUCHERS +
                " where " + COL_DBNAME + " = ? and " + COL_ACCTNUM + " = ? " +
                " and " + COL_CHECKSTATUS + param +
                " order by " + COL_CHECKKEY + " desc");
        try {
            PreparedStatement ps = ConnectionMain.getInstance().cn().prepareStatement(sql.toString());
            ps.setString(1,dbName);
            ps.setString(2,accountNumber);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CheckVoucher checkVoucher = new CheckVoucher();
                checkVoucher.setDatabaseName(dbName);
                checkVoucher.setCheckKey(rs.getInt(COL_CHECKKEY));
                checkVoucher.setTransRef(rs.getInt(COL_TRANSREF));
                checkVoucher.setVendorCode(rs.getString(COL_VENDORCODE));
                checkVoucher.setVendorName(rs.getString(COL_VENDORNAME));
                checkVoucher.setCheckDate(rs.getDate(COL_CHECKDATE));
                checkVoucher.setCheckAmount(rs.getBigDecimal(COL_CHECKSUM));
                checkVoucher.setCheckNum(rs.getString(COL_CHECKNUM));
                checkVoucher.setBankNum(rs.getString(COL_BANKNUM));
                checkVoucher.setCheckStatus((CheckStatus.valueOf(rs.getString(COL_CHECKSTATUS))));
                checkVoucher.setAccountNumber(accountNumber);
                checkVouchers.add(checkVoucher);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return checkVouchers;
    }

    @Override
    public ExecuteStatus handleCheckVoucher(CheckVoucher checkVoucher) {

        ExecuteStatus  executeStatus;

        try {

            ConnectionMain.getInstance().cn().setAutoCommit(false);
            PreparedStatement ps;
            StringBuilder sql = new StringBuilder();

            if (checkVoucher != null) {
                //updateBankAccountBalance(checkVoucher);
                sql.append("insert into " + TBL_CHECKVOUCHERS +
                        " ("+ COL_DBNAME +","+ COL_CHECKKEY +","+ COL_TRANSREF +","+ COL_VENDORCODE +","+ COL_VENDORNAME +
                        ","+ COL_CHECKNUM +","+ COL_CHECKDATE +","+ COL_CHECKSUM +","+ COL_ACCTNUM +"," + COL_BANKNUM  +
                        ") VALUES (?,?,?,?,?,?,?,?,?,?)");
                ps = ConnectionMain.getInstance().cn().prepareStatement(sql.toString());
                ps.setString(1,checkVoucher.getDatabaseName());
                ps.setInt(2,checkVoucher.getCheckKey());
                ps.setInt(3,checkVoucher.getTransRef());
                ps.setString(4,checkVoucher.getVendorCode());
                ps.setString(5,checkVoucher.getVendorName());
                ps.setString(6,checkVoucher.getCheckNum());
                ps.setDate(7,checkVoucher.getCheckDate());
                ps.setBigDecimal(8,checkVoucher.getCheckAmount());
                ps.setString(9,checkVoucher.getAccountNumber());
                ps.setString(10,checkVoucher.getBankNum());
                ps.executeUpdate();
                ps.close();

                sql.setLength(0);
                sql.append("insert into " + TBL_CHECKVOUCHERSHISTORY + "("+ COL_DBNAME +","+ COL_CHECKKEY +
                           ","+ COL_ADDEDBY +","+ COL_DATEADDED +") values (?,?,?,now())");
                ps = ConnectionMain.getInstance().cn().prepareStatement(sql.toString());
                ps.setString(1,checkVoucher.getDatabaseName());
                ps.setInt(2,checkVoucher.getCheckKey());
                ps.setInt(3,CurrentUser.getInstance().getUserID());
                ps.executeUpdate();
                ps.close();
            }
            ConnectionMain.getInstance().cn().commit();
            executeStatus = ExecuteStatus.SAVED;

        } catch (Exception e) {
            executeStatus = ExecuteStatus.ERROR_OCCURED;
            e.printStackTrace();
            try {
                ConnectionMain.getInstance().cn().rollback();
            } catch (Exception e2) {
                e2.printStackTrace();
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

    public ExecuteStatus setCheckStatus(ObservableList<CheckVoucher> checkVouchers, CheckStatus checkStatus) {

        ExecuteStatus executeStatus = ExecuteStatus.ERROR_OCCURED;

        PreparedStatement ps,subPs,xPs;
        ResultSet rs,subRs;
        String comparator = "=";
        String accBalCol = null;
        String accBalHisCol;
        String accBalHisDateCol;
        String actualHisDateCol;

        StringBuilder sql = new StringBuilder();

        // CHECK STATUS TO BE UPDATED
        CheckStatus baseStatus;

        if (checkStatus.equals(CheckStatus.FUNDED)) {
            baseStatus = CheckStatus.PENDING;
            accBalCol = COL_BALANCE1;
            accBalHisCol = COL_FUNDEDBY;
            accBalHisDateCol = COL_DATEFUNDED;
            actualHisDateCol = COL_DATEFUNDED;
        } else if (checkStatus.equals(CheckStatus.OUTSTANDING)) {
            baseStatus = CheckStatus.FUNDED;
            accBalCol = COL_BALANCE2;
            accBalHisCol = COL_RELEASEDBY;
            accBalHisDateCol = COL_DATERELEASED;
            actualHisDateCol = COL_ACTUALDATERELEASED;
        } else if (checkStatus.equals(CheckStatus.ENCASHED)) {
            baseStatus = CheckStatus.OUTSTANDING;
            accBalCol = COL_BALANCE3;
            accBalHisCol = COL_TAGGEDENCASHEDBY;
            accBalHisDateCol = COL_DATETAGGED;
            actualHisDateCol = COL_ACTUALDATETAGGED;
        } else {
            comparator = "!=";
            baseStatus = CheckStatus.CANCELED;
            accBalHisCol = COL_CANCELEDBY;
            accBalHisDateCol = COL_DATECANCELED;
            actualHisDateCol = COL_DATECANCELED;
        }

        for (CheckVoucher cv: checkVouchers) {
            if (cv.isSelected()) {
                if (cv.getCheckStatus().equals(baseStatus) || baseStatus.equals(CheckStatus.CANCELED)) {
                    try {
                        ConnectionMain.getInstance().cn().setAutoCommit(false);

                        sql.setLength(0);
                        sql.append("select * from " + TBL_BANKACCOUNTS +
                                " where " + COL_ACCOUNT + " = ? for update;");

                        ps = ConnectionMain.getInstance().cn().prepareStatement(sql.toString());
                        ps.setString(1, cv.getAccountNumber());
                        rs = ps.executeQuery();

                        while (rs.next()) {

                            sql.setLength(0);
                            sql.append("select " + COL_CHECKKEY + "," + COL_TRANSREF + "," + COL_VENDORCODE + ","
                                    + COL_VENDORNAME + "," + COL_CHECKDATE + "," + COL_CHECKSUM + ","
                                    + COL_CHECKNUM + "," + COL_BANKNUM + "," + COL_CHECKSTATUS +
                                    " from " + TBL_CHECKVOUCHERS +
                                    " where " + COL_DBNAME + " = ? and " + COL_CHECKKEY + " = ? " +
                                    " and " + COL_CHECKSTATUS + " " + comparator + " ? for update;");
                            subPs = ConnectionMain.getInstance().cn().prepareStatement(sql.toString());
                            subPs.setString(1, cv.getDatabaseName());
                            subPs.setString(2, cv.getCheckKey().toString());
                            subPs.setString(3, baseStatus.name());
                            subRs = subPs.executeQuery();

                            while (subRs.next()) {

                                //Updates check status
                                sql.setLength(0);
                                sql.append("update " + TBL_CHECKVOUCHERS + " set " + COL_CHECKSTATUS + " = ? " +
                                        "where " + COL_DBNAME + " = ? and " + COL_CHECKKEY + " = ? ;");
                                xPs = ConnectionMain.getInstance().cn().prepareStatement(sql.toString());
                                xPs.setString(1, checkStatus.name());
                                xPs.setString(2, cv.getDatabaseName());
                                xPs.setString(3, cv.getCheckKey().toString());
                                xPs.executeUpdate();
                                xPs.close();

                                //Updates check history
                                sql.setLength(0);
                                sql.append("update " + TBL_CHECKVOUCHERSHISTORY + " set " + accBalHisCol + " = ? , " +
                                        actualHisDateCol + " = ?," + accBalHisDateCol + " = now() " +
                                        "where " + COL_DBNAME + " = ? and " + COL_CHECKKEY + " = ? ;");
                                xPs = ConnectionMain.getInstance().cn().prepareStatement(sql.toString());
                                xPs.setInt(1, CurrentUser.getInstance().getUserID());
                                xPs.setDate(2, cv.getActualDate());
                                xPs.setString(3, cv.getDatabaseName());
                                xPs.setString(4, cv.getCheckKey().toString());
                                xPs.executeUpdate();
                                xPs.close();

                                //Updates account balances
                                sql.setLength(0);

                                if (checkStatus.equals(CheckStatus.CANCELED)) {

                                    if (subRs.getString(COL_CHECKSTATUS).equals(CheckStatus.FUNDED.name())) {
                                        sql.append("update " + TBL_BANKACCOUNTS +
                                                " set " + COL_BALANCE1 + " = " + COL_BALANCE1 + " + ? " +
                                                "where " + COL_ACCOUNT + " = ? ;");
                                        xPs = ConnectionMain.getInstance().cn().prepareStatement(sql.toString());
                                        xPs.setBigDecimal(1, cv.getCheckAmount());
                                        xPs.setString(2, cv.getAccountNumber());
                                        xPs.executeUpdate();
                                        xPs.close();
                                    } else if (subRs.getString(COL_CHECKSTATUS).equals(CheckStatus.OUTSTANDING.name())) {
                                        sql.append("update " + TBL_BANKACCOUNTS +
                                                " set " + COL_BALANCE1 + " = " + COL_BALANCE1 + " + ? ," +
                                                COL_BALANCE2 + " = " + COL_BALANCE2 + " + ? " +
                                                "where " + COL_ACCOUNT + " = ? ;");
                                        xPs = ConnectionMain.getInstance().cn().prepareStatement(sql.toString());
                                        xPs.setBigDecimal(1, cv.getCheckAmount());
                                        xPs.setBigDecimal(2, cv.getCheckAmount());
                                        xPs.setString(3, cv.getAccountNumber());
                                        xPs.executeUpdate();
                                        xPs.close();
                                    } else if (subRs.getString(COL_CHECKSTATUS).equals(CheckStatus.ENCASHED.name())) {
                                        sql.append("update " + TBL_BANKACCOUNTS +
                                                " set " + COL_BALANCE1 + " = " + COL_BALANCE1 + " + ? ," +
                                                COL_BALANCE2 + " = " + COL_BALANCE2 + " + ? ," +
                                                COL_BALANCE3 + " = " + COL_BALANCE3 + " + ? ," +
                                                "where " + COL_ACCOUNT + " = ? ;");
                                        xPs = ConnectionMain.getInstance().cn().prepareStatement(sql.toString());
                                        xPs.setBigDecimal(1, cv.getCheckAmount());
                                        xPs.setBigDecimal(2, cv.getCheckAmount());
                                        xPs.setBigDecimal(3, cv.getCheckAmount());
                                        xPs.setString(4, cv.getAccountNumber());
                                        xPs.executeUpdate();
                                        xPs.close();
                                    }

                                } else {

                                    sql.append("update " + TBL_BANKACCOUNTS +
                                            " set " + accBalCol + " = " + accBalCol + " - ? " +
                                            "where " + COL_ACCOUNT + " = ? ;");
                                    xPs = ConnectionMain.getInstance().cn().prepareStatement(sql.toString());
                                    xPs.setBigDecimal(1, cv.getCheckAmount());
                                    xPs.setString(2, cv.getAccountNumber());
                                    xPs.executeUpdate();
                                    xPs.close();

                                }

                            }

                            subRs.close();
                            subPs.close();

                        }

                        ConnectionMain.getInstance().cn().commit();
                        rs.close();
                        ps.close();

                        executeStatus = ExecuteStatus.UPDATED;
                    } catch(SQLException se) {
                        try {
                            ConnectionMain.getInstance().cn().rollback();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            ConnectionMain.getInstance().cn().setAutoCommit(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

            }

        }

        return executeStatus;

    }

}