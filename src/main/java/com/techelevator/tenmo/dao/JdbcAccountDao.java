package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Balance;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao {
    private final JdbcTemplate jdbcTemplate;
    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account createAccount(Account account) {
        Account createdAccount = null;
        String sql = "INSERT INTO account (balance, user_id) " +
                "VALUES (?, ?) RETURNING account_id;";

        try {
            int accountId = jdbcTemplate.queryForObject(sql, int.class, account.getBalance(), account.getUserId());
            createdAccount = findAccountById(accountId);
        }catch (CannotGetJdbcConnectionException e){
            throw new RuntimeException("Unable to contact the database!", e);
        }catch (BadSqlGrammarException e){
            throw new RuntimeException("Bad SQL query: " + e.getSql()
                    +"\n"+e.getSQLException(), e);
        }catch (DataIntegrityViolationException e){
            throw new RuntimeException("Database Integrity Violation", e);
        }
        return createdAccount;
    }

    @Override
    public List<Account> findByUsername(String username){
        List<Account> accountList = new ArrayList<>();
        String sql = "SELECT account_id, balance, account.user_id FROM account " +
                "   JOIN tenmo_user ON tenmo_user.user_id = account.user_id " +
                "   WHERE tenmo_user.username = ?;";

        //        String sql = "SELECT account_id, balance, user_id FROM account " +
//                    "   JOIN tenmo_user ON tenmo_user.user_id = account.user_id " +
//                    "   WHERE user_id = (SELECT user_id FROM tenmo_user WHERE username = ?);";


        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
            while(results.next()) {
                Account account = mapRowToAccount(results);
                accountList.add(account);
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new RuntimeException("Unable to contact the database!", e);
        }catch (BadSqlGrammarException e){
            throw new RuntimeException("Bad SQL query: " + e.getSql()
                    +"\n"+e.getSQLException(), e);
        }catch (DataIntegrityViolationException e){
            throw new RuntimeException("Database Integrity Violation", e);
        }
        return accountList;
    }

    @Override
    public Account findAccountById(int accountId) {
        Account account = null;
        String sql = "SELECT account_id, user_id, balance FROM account WHERE account_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
            if(results.next()) {
                account = mapRowToAccount(results);
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new RuntimeException("Unable to contact the database!", e);
        }catch (BadSqlGrammarException e){
            throw new RuntimeException("Bad SQL query: " + e.getSql()
                    +"\n"+e.getSQLException(), e);
        }catch (DataIntegrityViolationException e){
            throw new RuntimeException("Database Integrity Violation", e);
        }
        return account;
    }

//    // probably dont need this. Maybe rename to withdraw from account?
//    @Override
//    public Account updateAccount(int accountId, BigDecimal balance) {
//        Account account = null;
//        String sql = "UPDATE account SET balance = ? WHERE account_id = ?;";
//        try {
//            jdbcTemplate.update(sql, balance, accountId);
//            account = findAccountById(accountId);
//        }catch (CannotGetJdbcConnectionException e){
//            throw new RuntimeException("Unable to contact the database!", e);
//        }catch (BadSqlGrammarException e){
//            throw new RuntimeException("Bad SQL query: " + e.getSql()
//                    +"\n"+e.getSQLException(), e);
//        }catch (DataIntegrityViolationException e){
//            throw new RuntimeException("Database Integrity Violation", e);
//        }
//
//        return account;
//    }

    @Override
    public List<Balance> getBalanceByUsername(String username) {
        List<Balance> userBalances = new ArrayList<>();
        String sql = "SELECT balance FROM account WHERE user_id = (SELECT user_id FROM tenmo_user WHERE username = ?);";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
            while(results.next()) {
                Balance balance = new Balance();
                balance.setBalance(results.getBigDecimal("balance"));
                balance.setUsername(username);
                userBalances.add(balance);
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new RuntimeException("Unable to contact the database!", e);
        }catch (BadSqlGrammarException e){
            throw new RuntimeException("Bad SQL query: " + e.getSql()
                    +"\n"+e.getSQLException(), e);
        }catch (DataIntegrityViolationException e){
            throw new RuntimeException("Database Integrity Violation", e);
        }
        return userBalances;

    }

    public Account mapRowToAccount(SqlRowSet row) {
        Account account = new Account();
        account.setAccountId(row.getInt("account_id"));
        account.setUserId(row.getInt("user_id"));
        account.setBalance(row.getBigDecimal("balance"));


        return account;
    }


}
