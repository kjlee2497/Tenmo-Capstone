package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Balance;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping(path = "/api/v1")
public class AccountController {
    private AccountDao accountDao;
    private UserDao userDao;
    public AccountController(AccountDao accountDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
    }

    @RequestMapping(path = "/user/account", method = RequestMethod.GET)
    public List<Account> accountList(Principal principal) {
        String username = principal.getName();

        List<Account> accountList = accountDao.findByUsername(username);
        if(accountList == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        } else {
            return accountList;
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/user/account", method = RequestMethod.POST)
    public Account createAccount(@RequestBody @Valid Account account) {
        return accountDao.createAccount(account);
    }

//    @RequestMapping(path = "/user/account", method = RequestMethod.PUT)
//    public Account updateAccount(@RequestBody @Valid Account account) {
//        try {
//            return accountDao.updateAccount(account.getAccountId(), account.getBalance());
//        } catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found.");
//        }
//    }

    @RequestMapping(path = "/user/balances", method = RequestMethod.GET)
    public List<Balance> getBalancesByUsername(Principal principal){
        String username = principal.getName();
        return accountDao.getBalanceByUsername(username);
    }

    @PostMapping(path = "/user/create")
    public Account createNewAccount(Principal principal){
        String username = principal.getName();
        Account pendingAccount = new Account();
        pendingAccount.setUserId(userDao.findIdByUsername(username));

        return accountDao.createAccount(pendingAccount);
    }




}
