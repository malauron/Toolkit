package com.DataAccessObjects;

import com.DataModels.BankAccount;
import com.DataModels.Company;
import com.DataModels.Company.DBType;
import com.Utilities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.DataAccessObjects.DataAccessConstants.*;

public class BankAccounts {

    public ObservableList<BankAccount> getBankAccounts(Company c) {

        ObservableList<BankAccount> bankAccounts = FXCollections.observableArrayList();
        StringBuilder sql = new StringBuilder();
        PreparedStatement ps;
        ResultSet rs;
        try {
            if (c.getDbType() == DBType.MSSQL) {
                sql.append("select "+ COL_ACCOUNT +","+ COL_BANKCODE +
                        " from "+ c.getDbName() + TBL_DSC1 +
                        " order by " + COL_ACCOUNT);
                ps = ConnectionExt.getInstance().cn().prepareStatement(sql.toString());
                rs = ps.executeQuery();
                while (rs.next()) {
                    BankAccount bankAccount = new BankAccount();
                    bankAccount.setAccountNumber(rs.getString(COL_ACCOUNT));
                    bankAccount.setBankName(rs.getString(COL_BANKCODE));
                    bankAccounts.add(bankAccount);
                }
            } else {
                sql.append("select "+ COL_ACCOUNTID +","+ COL_ACCOUNTDESC + "," + COL_GLACCTNO +
                        " from "+ TBL_CHART +
                        " where " + COL_ACCOUNTTYPE + " = 0 and " + COL_ACCTINACTIVE + " = 0 " +
                        " order by " + COL_ACCOUNTDESC);
                String password="";
                if (!c.getPassword().isEmpty()) {
                    password = CryptoUtil.getInstance().decrypt(c.getPassword());
                }
                ConnectionPSQL connectionPSQL = new ConnectionPSQL();
                connectionPSQL.setConnection(
                        c.getServer(),c.getUsername(),password,c.getPort(),c.getDbName()
                );
                ps = connectionPSQL.getConnection().prepareStatement(sql.toString());
                rs = ps.executeQuery();
                while (rs.next()) {
                    BankAccount bankAccount = new BankAccount();
                    bankAccount.setAccountNumber(rs.getString(COL_ACCOUNTID));
                    bankAccount.setBankName(rs.getString(COL_ACCOUNTDESC));
                    bankAccount.setGLAccountNo(rs.getInt(COL_GLACCTNO));
                    bankAccounts.add(bankAccount);
                }
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bankAccounts;
    }

    public BankAccount getBankAccount(String accountNo) {
        String sql = "select * FROM " + TBL_BANKACCOUNTS +
                     " where " + COL_ACCOUNT + " = '" + accountNo + "'";
        BankAccount bankAccount = null;
        try {
            PreparedStatement ps = ConnectionMain.getInstance().cn().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bankAccount = new BankAccount();
                bankAccount.setAccountNumber(rs.getString(COL_ACCOUNT));
                bankAccount.setBankName(rs.getString(COL_BANKCODE));
                bankAccount.setBalance1(rs.getDouble(COL_BALANCE1));
                bankAccount.setBalance2(rs.getDouble(COL_BALANCE2));
                bankAccount.setBalance3(rs.getDouble(COL_BALANCE3));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bankAccount;
    }

    public ExecuteStatus handleBankAccountAdjustment(BankAccount bankAccount,Double adjAmount,String remarks) {

        ExecuteStatus executeStatus = ExecuteStatus.ERROR_OCCURED;

        try {
            ConnectionMain.getInstance().cn().setAutoCommit(false);

            updateBankAccountBalance(bankAccount,adjAmount);

            String sql = "insert into " + TBL_BANKACCTADJ +
                    "(" + COL_ACCTNUM + "," + COL_ADJUSTAMT + "," + COL_ADJDATE +"," + COL_USER_ID + "," + COL_ADJREMARKS +
                    ") values (?,?,now(),?,?)";
            PreparedStatement ps = ConnectionMain.getInstance().cn().prepareStatement(sql);
            ps.setString(1,bankAccount.getAccountNumber());
            ps.setDouble(2,adjAmount);
            ps.setInt(3, CurrentUser.getInstance().getUserID());
            ps.setString(4, remarks);
            ps.executeUpdate();
            ps.close();
            ConnectionMain.getInstance().cn().commit();
            executeStatus = ExecuteStatus.SAVED;
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

    private void updateBankAccountBalance(BankAccount bankAccount,Double adjAmount) throws Exception {

            String sql = "select * from " + TBL_BANKACCOUNTS +
                    " where " + COL_ACCOUNT + " = ? for update";
            PreparedStatement ps = ConnectionMain.getInstance().cn().prepareStatement(sql);
            ps.setString(1,bankAccount.getAccountNumber());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                sql = "update " + TBL_BANKACCOUNTS +
                        " set " + COL_BALANCE1 + " = " + COL_BALANCE1 + " + ? " +
                        ", " + COL_BALANCE2 + " = " + COL_BALANCE2 + " + ? " +
                        ", " + COL_BALANCE3 + " = " + COL_BALANCE3 + " + ? " +
                        " where " + COL_ACCOUNT + " = ?";
                PreparedStatement ps2 = ConnectionMain.getInstance().cn().prepareStatement(sql);
                ps2.setDouble(1,adjAmount);
                ps2.setDouble(2,adjAmount);
                ps2.setDouble(3,adjAmount);
                ps2.setString(4,bankAccount.getAccountNumber());
                ps2.executeUpdate();
                ps2.close();
            } else {
                sql = "insert into " + TBL_BANKACCOUNTS +
                        " ("+ COL_ACCOUNT +","+ COL_BANKCODE +","+ COL_BALANCE1 +
                        ","+ COL_BALANCE2 +","+ COL_BALANCE3 +") values (?,?,?,?,?)";
                PreparedStatement ps2 = ConnectionMain.getInstance().cn().prepareStatement(sql);
                ps2.setString(1,bankAccount.getAccountNumber());
                ps2.setString(2,bankAccount.getBankName());
                ps2.setDouble(3,adjAmount);
                ps2.setDouble(4,adjAmount);
                ps2.setDouble(5,adjAmount);
                ps2.executeUpdate();
                ps2.close();
            }
            rs.close();
            ps.close();

    }

}
