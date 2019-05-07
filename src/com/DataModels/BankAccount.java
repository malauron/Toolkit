package com.DataModels;

public class BankAccount {
    private String accountNumber;
    private String bankName;
    private Integer GLAccountNo;
    private Double balance1;
    private Double balance2;
    private Double balance3;


    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String toString() {
        return accountNumber + " - " + bankName;
    }

    public Integer getGLAccountNo() {
        return GLAccountNo;
    }

    public void setGLAccountNo(Integer GLAccountNo) {
        this.GLAccountNo = GLAccountNo;
    }

    public Double getBalance1() {
        return balance1;
    }

    public void setBalance1(Double balance1) {
        this.balance1 = balance1;
    }

    public Double getBalance2() {
        return balance2;
    }

    public void setBalance2(Double balance2) {
        this.balance2 = balance2;
    }

    public Double getBalance3() {
        return balance3;
    }

    public void setBalance3(Double balance3) {
        this.balance3 = balance3;
    }
}
