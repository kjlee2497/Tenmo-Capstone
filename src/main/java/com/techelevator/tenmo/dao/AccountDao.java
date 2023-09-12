package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Balance;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

public interface AccountDao {

    Account createAccount(Account account); //tested
    List<Account> findByUsername(String username); //tested
    Account findAccountById(int accountId); //tested
//    Account updateAccount(int accountId, BigDecimal balance); //tested
    List<Balance> getBalanceByUsername(String username);

}
