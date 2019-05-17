package com.DataAccessObjects;

public class DataAccessConstants {

    //User Groups
    public static final String TBL_USERGROUPS = "usergroups";
    public static final String COL_USERGROUP_ID = "usergroup_id";
    public static final String COL_USERGROUP_NAME = "usergroup_name";

    //Users
    public static final String TBL_USERS = "users";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_FULLNAME = "fullname";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";

    //Users Photo
    public static final String TBL_USERS_PHOTO = "users_photos";
    public static final String COL_USRFILE_SIZE = "file_size";
    public static final String COL_USRFILE_BLOB = "file_blob";

    //Suppliers
    public static final String TBL_SUPPLIERS = "suppliers";
    public static final String COL_SUPPLIER_ID = "supplier_id";
    public static final String COL_SUPPLIER_NAME = "supplier_name";
    public static final String COL_SUPPLIER_REMARKS = "remarks";

    //Suppliers Photo
    public static final String TBL_SUPPLIERS_PHOTO = "suppliers_photos";
    public static final String COL_SUPFILE_SIZE = "file_size";
    public static final String COL_SUPFILE_BLOB = "file_blob";

    //Billings
    public static final String TBL_BILLINGS = "billings";
    public static final String COL_BILLING_ID = "billing_id";
    public static final String COL_BILLING_REFNO = "billing_refno";
    public static final String COL_BILLING_TRNX_DATE = "transaction_date";
    public static final String COL_BILLING_AMOUNT = "billing_amount";
    public static final String COL_BILLING_REMARKS = "remarks";
    public static final String COL_BILLING_STATUS = "billing_status";
    public static final String COL_RECEIVED_USER_ID = "receiver_user_id";
    public static final String COL_PROCESSOR_USER_ID = "processor_user_id";
    public static final String COL_PROCESSING_DATE = "processing_date";
    public static final String COL_PROCESSED_DATE  = "processed_date";
    public static final String COL_CANCELLED_DATE = "cancelled_date";

    //Bar chart Billings
    public static final String VW_BARCHARTBILLINGS = "barchartBillings";
    public static final String COL_AGE_DESCRIPTION = "age_description";
    public static final String COL_CTR = "ctr";

    //Companies
    public static final String TBL_SRGC = "[SBO-COMMON].dbo.srgc";
    public static final String TBL_PSQLCOMPANIES = "psql_companies";
    public static final String COL_CMPNAME = "cmpName";
    public static final String COL_DBNAME = "dbName";
    public static final String COL_SERVER = "server_name";
    public static final String COL_PORT = "port_number";
    public static final String COL_USER = "user_name";
    public static final String COL_PASS = "user_password";

    //Bank Accounts
    public static final String TBL_DSC1 = ".dbo.DSC1"; //MSSQL TABLE
    public static final String TBL_BANKACCOUNTS = "bank_accounts"; //MySQL TABLE
    public static final String COL_ACCOUNT = "account";
    public static final String COL_BANKCODE = "bankcode";
    public static final String COL_BALANCE1 = "balance1";
    public static final String COL_BALANCE2 = "balance2";
    public static final String COL_BALANCE3 = "balance3";

    //Check Vouchers
    public static final String TBL_OCHO = ".dbo.OCHO"; //MSSQL TABLE
    public static final String TBL_CHECKVOUCHERS = "check_vouchers"; //MySQL TABLE
    public static final String COL_CHECKKEY = "checkkey";
    public static final String COL_TRANSREF = "transref";
    public static final String COL_VENDORCODE = "vendorcode";
    public static final String COL_VENDORNAME = "vendorname";
    public static final String COL_CHECKDATE = "checkdate";
    public static final String COL_CHECKSUM = "checksum";
    public static final String COL_CHECKNUM = "checknum";
    public static final String COL_ACCTNUM = "acctnum";
    public static final String COL_BANKNUM = "banknum";
    public static final String COL_CANCELED = "canceled";
    public static final String COL_CHECKSTATUS = "check_status";

    //Check Vouhcers History
    public static final String TBL_CHECKVOUCHERSHISTORY = "check_vouchers_history";
    public static final String COL_ADDEDBY = "added_by";
    public static final String COL_DATEADDED = "date_added";
    public static final String COL_FUNDEDBY = "funded_by";
    public static final String COL_DATEFUNDED = "date_funded";
    public static final String COL_RELEASEDBY = "released_by";
    public static final String COL_DATERELEASED = "date_released";
    public static final String COL_ACTUALDATERELEASED = "actual_date_released";
    public static final String COL_TAGGEDENCASHEDBY = "tagged_encashed_by";
    public static final String COL_DATETAGGED = "date_tagged";
    public static final String COL_ACTUALDATETAGGED = "actual_date_tagged";
    public static final String COL_CANCELEDBY = "canceled_by";
    public static final String COL_DATECANCELED = "date_canceled";

    //Peachtree JRNLHDR
    public static final String TBL_JRNLHDR = "jrnlhdr";
    public static final String COL_POSTORDER = "postorder";
    public static final String COL_JRNLKEY = "jrnlkey_trxnumber";
    public static final String COL_JRNLREF = "reference";
    public static final String COL_VENDID = "custvendid";
    public static final String COL_JRNLDESC = "description";
    public static final String COL_MAINAMT = "mainamount";
    public static final String COL_TRNXDATE = "transactiondate";
    public static final String COL_MODULE = "module";

    //Peachtree CHART
    public static final String TBL_CHART = "chart";
    public static final String COL_GLACCTNO = "glacntnumber";
    public static final String COL_ACCOUNTID = "accountid";
    public static final String COL_ACCOUNTTYPE = "accounttype";
    public static final String COL_ACCOUNTDESC = "accountdescription";
    public static final String COL_ACCTINACTIVE = "accountisinactive";



    //Bank Account Adjustments
    public static final String TBL_BANKACCTADJ = "bank_accounts_adjustments";
    public static final String COL_ADJUSTAMT = "adjustment_amount";
    public static final String COL_ADJDATE = "adjustment_date";
    public static final String COL_ADJREMARKS = "remarks";





}
