package com.Interfaces;

import com.DataModels.BankAccount;
import com.DataModels.Company;

public interface IChecksAccount {
    void getAccountSettings(Company company, BankAccount bankAccount);
}
