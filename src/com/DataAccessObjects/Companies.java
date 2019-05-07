package com.DataAccessObjects;

import com.DataModels.Company;
import com.DataModels.Company.DBType;
import com.Interfaces.ICompanies;
import com.Utilities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.DataAccessObjects.DataAccessConstants.*;

public class Companies implements ICompanies {
    @Override
    public ObservableList<Company> getCompanies() {
        ObservableList<Company> companies = FXCollections.observableArrayList();


        try {
            StringBuilder sql = new StringBuilder();
            sql.append("select " + COL_DBNAME + "," + COL_CMPNAME + " from " + TBL_SRGC + " order by " + COL_CMPNAME);

            PreparedStatement ps = ConnectionExt.getInstance().cn().prepareStatement(sql.toString());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Company company = new Company();
                company.setDbType(DBType.MSSQL);
                company.setCmpName(rs.getString(COL_CMPNAME));
                company.setDbName(rs.getString(COL_DBNAME));
                companies.add(company);
            }

            sql.setLength(0);
            rs.close();
            ps.close();

            sql.append("select * from " + TBL_PSQLCOMPANIES);

            ps = ConnectionMain.getInstance().cn().prepareStatement(sql.toString());
            rs = ps.executeQuery();

            while (rs.next()) {
                Company company = new Company();
                company.setDbType(DBType.PSQL);
                company.setCmpName(rs.getString(COL_CMPNAME));
                company.setDbName(rs.getString(COL_DBNAME));
                company.setServer(rs.getString(COL_SERVER));
                company.setPort(rs.getString(COL_PORT));
                company.setUsername(rs.getString(COL_USER));
                company.setPassword(rs.getString(COL_PASS));
                companies.add(company);
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return companies;
    }

    public ObservableList<Company> getCompanies(DBType dbType) {

        ObservableList<Company> companies = FXCollections.observableArrayList();

        try {
            StringBuilder sql = new StringBuilder();
            PreparedStatement ps;
            ResultSet rs;

            if (dbType == DBType.MSSQL) {
                sql.append("select " + COL_DBNAME + "," + COL_CMPNAME + " from " + TBL_SRGC + " order by " + COL_CMPNAME);
                ps = ConnectionExt.getInstance().cn().prepareStatement(sql.toString());
                rs = ps.executeQuery();

                while (rs.next()) {
                    Company company = new Company();
                    company.setDbType(DBType.MSSQL);
                    company.setCmpName(rs.getString(COL_CMPNAME));
                    company.setDbName(rs.getString(COL_DBNAME));
                    companies.add(company);
                }

                rs.close();
                ps.close();

            } else {
                sql.append("select * from " + TBL_PSQLCOMPANIES);
                ps = ConnectionMain.getInstance().cn().prepareStatement(sql.toString());
                rs = ps.executeQuery();

                while (rs.next()) {
                    Company company = new Company();
                    company.setDbType(DBType.PSQL);
                    company.setCmpName(rs.getString(COL_CMPNAME));
                    company.setDbName(rs.getString(COL_DBNAME));
                    company.setServer(rs.getString(COL_SERVER));
                    company.setPort(rs.getString(COL_PORT));
                    company.setUsername(rs.getString(COL_USER));
                    company.setPassword(rs.getString(COL_PASS));
                    companies.add(company);
                }

                rs.close();
                ps.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return companies;
    }

    public ExecuteStatus handleCompany(Company c) {
        ExecuteStatus executeStatus = ExecuteStatus.ERROR_OCCURED;
        ConnectionPSQL connectionPSQL = new ConnectionPSQL();
        String password="";
        if (!c.getPassword().isEmpty()) {
            password = CryptoUtil.getInstance().encrypt(c.getPassword());
        }
        try {
            connectionPSQL.setConnection(
                    c.getServer(),c.getUsername(),c.getPassword(),c.getPort(),c.getDbName()
            );
            StringBuilder s = new StringBuilder();
            s.append("insert into " + TBL_PSQLCOMPANIES + " ("+ COL_CMPNAME + "," + COL_DBNAME +
                     "," + COL_SERVER + "," + COL_PORT + "," + COL_USER + "," + COL_PASS +") values " +
                    "(?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " + COL_CMPNAME + " = ?," +
                    COL_SERVER + " = ?," + COL_PORT + " = ?," + COL_USER + " = ?," + COL_PASS + " = ?");
            PreparedStatement ps = ConnectionMain.getInstance().cn().prepareStatement(s.toString());
            ps.setString(1,c.getCmpName());
            ps.setString(2,c.getDbName());
            ps.setString(3,c.getServer());
            ps.setString(4,c.getPort());
            ps.setString(5,c.getUsername());
            ps.setString(6, password);
            ps.setString(7,c.getCmpName());
            ps.setString(8,c.getServer());
            ps.setString(9,c.getPort());
            ps.setString(10,c.getUsername());
            ps.setString(11, password);


            ps.executeUpdate();
            ps.close();
            executeStatus = ExecuteStatus.SAVED;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return executeStatus;
    }
}
