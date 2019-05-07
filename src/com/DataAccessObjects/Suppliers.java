package com.DataAccessObjects;

import com.DataModels.Supplier;
import com.Interfaces.ISuppliers;
import com.Utilities.ConnectionMain;
import com.Utilities.ExecuteStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static com.DataAccessObjects.DataAccessConstants.*;


public class Suppliers implements ISuppliers{

    @Override
    public Supplier getSupplier(int supplierID) {
        Supplier supplier;
        String sql = "select x1."+ COL_SUPPLIER_ID +",ucase(x1."+ COL_SUPPLIER_NAME +") " + COL_SUPPLIER_NAME + " " +
                "from "+ TBL_SUPPLIERS +" x1 where x1."+ COL_SUPPLIER_ID +" = " + supplierID + " " +
                "order by x1." + COL_SUPPLIER_NAME;
        try {
            ResultSet rs = ConnectionMain.getInstance().getResultSet(sql);
            while (rs.next()) {
                supplier = new Supplier();
                supplier.setSupplierID(rs.getInt(COL_SUPPLIER_ID));
                supplier.setSupplierName(rs.getString(COL_SUPPLIER_NAME));
                return supplier;
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ObservableList<Supplier> getSuppliers(String param) {

        ObservableList<Supplier> suppliers = FXCollections.observableArrayList();
        String sql = "SELECT x1."+ COL_SUPPLIER_ID +",ucase(x1."+ COL_SUPPLIER_NAME +") "+ COL_SUPPLIER_NAME +",x1."+ COL_SUPPLIER_REMARKS +", x2."+ COL_SUPFILE_BLOB +" " +
                "FROM "+ TBL_SUPPLIERS + " x1 " +
                "left outer join "+ TBL_SUPPLIERS_PHOTO +" x2 on x1."+ COL_SUPPLIER_ID +" = x2."+ COL_SUPPLIER_ID + " " +
                "WHERE x1." + COL_SUPPLIER_NAME + " like ? order by x1." + COL_SUPPLIER_NAME ;

        try {

            PreparedStatement ps = ConnectionMain.getInstance().cn().prepareStatement(sql);
            ps.setString(1, "%" + param + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                suppliers.add(setSupplier(rs));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return suppliers;
    }

    @Override
    public ExecuteStatus handleSupplier(Supplier supplier) {

        ExecuteStatus executeStatus = ExecuteStatus.ERROR_OCCURED;

        try {

            ConnectionMain.getInstance().cn().setAutoCommit(false);
            PreparedStatement ps;
            String sql;

            if(supplier.getSupplierID() == 0) {
                executeStatus = ExecuteStatus.SAVED;
                sql = "insert into "+ TBL_SUPPLIERS +" ("+ COL_SUPPLIER_NAME +", "+ COL_SUPPLIER_REMARKS +") " +
                        "values (?, ?)";
                ps = ConnectionMain.getInstance().cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, supplier.getSupplierName());
                ps.setString(2, supplier.getSupplierRemarks());
                ps.executeUpdate();
                ResultSet lastID = ps.getGeneratedKeys();
                lastID.next();
                supplier.setSupplierID(lastID.getInt(1));

            } else {
                executeStatus = ExecuteStatus.UPDATED;
                sql = "update "+ TBL_SUPPLIERS +" set "+ COL_SUPPLIER_NAME +" = ?, "+ COL_SUPPLIER_REMARKS +" = ? " +
                        "where "+ COL_SUPPLIER_ID +" = ?";
                ps = ConnectionMain.getInstance().cn().prepareStatement(sql);
                ps.setString(1, supplier.getSupplierName());
                ps.setString(2,supplier.getSupplierRemarks());
                ps.setInt(3, supplier.getSupplierID());
                ps.executeUpdate();

            }

            sql = "delete from "+ TBL_SUPPLIERS_PHOTO +" where "+ COL_SUPPLIER_ID +" = " + supplier.getSupplierID();
            ps = ConnectionMain.getInstance().cn().prepareStatement(sql);
            ps.executeUpdate();

            if(supplier.getSupplierPhoto() != null) {
                sql = "insert into "+ TBL_SUPPLIERS_PHOTO +" ("+ COL_SUPPLIER_ID +","+ COL_SUPFILE_SIZE +", "+ COL_SUPFILE_BLOB +") values (?,?,?)";
                ps = ConnectionMain.getInstance().cn().prepareStatement(sql);
                ps.setInt(1,supplier.getSupplierID());

                BufferedImage bImage = SwingFXUtils.fromFXImage(supplier.getSupplierPhoto(),null);
                ByteArrayOutputStream osPhoto = new ByteArrayOutputStream();
                ImageIO.write(bImage,"jpg",osPhoto);
                byte[] res = osPhoto.toByteArray();
                ps.setInt(2, osPhoto.size());
                InputStream inputStream = new ByteArrayInputStream(res);
                osPhoto.close();

                ps.setBinaryStream(3,inputStream);

                ps.executeUpdate();
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

    private Supplier setSupplier(ResultSet rs) {

        try {

            Supplier supplier = new Supplier();
            Blob blobPhoto;
            InputStream isPhoto;

            if(rs.getBlob(COL_SUPFILE_BLOB) != null) {
                blobPhoto = rs.getBlob(COL_SUPFILE_BLOB);
                isPhoto = blobPhoto.getBinaryStream();
                supplier.setSupplierPhoto(new Image(isPhoto));
                isPhoto.close();
            }

            supplier.setSupplierID(rs.getInt(COL_SUPPLIER_ID));
            supplier.setSupplierName(rs.getString(COL_SUPPLIER_NAME));
            supplier.setSupplierRemarks(rs.getString(COL_SUPPLIER_REMARKS));

            return supplier;

        } catch (Exception e) {

            System.err.println("Error: ");
            e.printStackTrace();

        }

        return null;
    }
}














