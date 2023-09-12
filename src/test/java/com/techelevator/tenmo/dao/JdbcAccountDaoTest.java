package com.techelevator.tenmo.dao;

import com.techelevator.dao.BaseDaoTests;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class JdbcAccountDaoTest extends BaseDaoTests {
    private final User USER_1 = new User(1101,"kevin","password1", true);
    private final User USER_2 = new User(1102,"chris","password2", true);
    private final User USER_3 = new User(1103,"eric","password3", true);
    private final User USER_4 = new User(1104,"thwin","password4", true);

    private final Account ACCOUNT_1 = new Account(2101,1101,new BigDecimal("1000.00"));
    private final Account ACCOUNT_2 = new Account(2102,1102,new BigDecimal("2000.00"));
    private final Account ACCOUNT_3 = new Account(2103,1103,new BigDecimal("3000.00"));
    private final Account ACCOUNT_4 = new Account(2104,1104,new BigDecimal("40000.00"));
    private final Account ACCOUNT_5 = new Account(2105,1101,new BigDecimal("3456.12"));

    private final Balance BALANCE_1 = new Balance("kevin", new BigDecimal("1000.00"));
    private final Balance BALANCE_2 = new Balance("chris", new BigDecimal("2000.00"));
    private final Balance BALANCE_5 = new Balance("kevin", new BigDecimal("3456.12"));


    private JdbcAccountDao sut;

    private JdbcTemplate jdbcTemplate;
    private Account testAccount;


    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcAccountDao(jdbcTemplate);
        testAccount = new Account(0,1102,new BigDecimal("123100.00"));

    }

    @Test
    public void findAccountByID_returns_correct_Account_for_id(){
        //call the method in the dao to get rows associated with id 2104
        Account account2104 = sut.findAccountById(2104);
        //verify the accounts match
        assertAccountsMatch(ACCOUNT_4,account2104);

        //call the method in the dao to get rows associated with id 2101
        Account account2101 = sut.findAccountById(2101);
        //verify the accounts match
        assertAccountsMatch(ACCOUNT_1,account2101);
    }

    @Test
    public void findAccountByID_returns_null_when_id_not_found(){
        //create account with non-existent id
        Account account = sut.findAccountById(123456);
        //verify the expected output is null
        Assert.assertNull(account);

        //create account with non-existent id
        account = sut.findAccountById(654321);
        //verify the expected output is null
        Assert.assertNull(account);
    }

    @Test
    public void findByUsername_returns_list_of_all_accounts_for_username(){
        //create a list of accounts using DAO method
        List<Account> accountList = sut.findByUsername("kevin");
        //verify the size of the list matches the expected;
        Assert.assertEquals("Expected 2 account(s) for username \"kevin\"",2,accountList.size());
        assertAccountsMatch(ACCOUNT_1,accountList.get(0));
        assertAccountsMatch(ACCOUNT_5,accountList.get(1));

        //create a list of accounts using DAO method
        accountList = sut.findByUsername("chris");
        //verify the size of the list matches the expected;
        Assert.assertEquals("Expected 1 account(s) for username \"chris\"",1,accountList.size());
        assertAccountsMatch(ACCOUNT_2,accountList.get(0));
    }

    @Test
    public void created_Account_has_expected_values_when_retrieved(){
        // create an account using the mock data in setup
        Account createdAccount = sut.createAccount(testAccount);
        //get the ide of the created account
        int newID = createdAccount.getAccountId();
        //verify that the ID of the account is no longer zero
        Assert.assertTrue(newID>0);
        //return the account from the database
        Account retrievedAccount = sut.findAccountById(newID);
        //verify that the created account matches the retrieved account
        assertAccountsMatch(createdAccount,retrievedAccount);
    }

//    @Test
//    public void withdrawFromAccount_AKA_updateAccount_has_expected_values_when_retrieved(){
//        Account accountToUpdate = sut.findAccountById(2104);
//        accountToUpdate.setBalance(new BigDecimal("30000"));
//        sut.updateAccount(accountToUpdate.getAccountId(),accountToUpdate.getBalance());
//        Account retrievedAccount= sut.findAccountById(2104);
//        assertAccountsMatch(accountToUpdate,retrievedAccount);
//
//    }

    @Test
    public void getBalanceByUsername_returns_list_of_balances_for_user(){
        //create a list of balances using DAO method
        List<Balance> balanceList = sut.getBalanceByUsername("kevin");
        //verify the size of the list matches the expected;
        Assert.assertEquals(2,balanceList.size());
        assertBalancesMatch(BALANCE_1,balanceList.get(0));
        assertBalancesMatch(BALANCE_5,balanceList.get(1));

        //create a list of balances using DAO method
        balanceList = sut.getBalanceByUsername("chris");
        //verify the size of the list matches the expected;
        Assert.assertEquals(1,balanceList.size());
        assertBalancesMatch(BALANCE_2,balanceList.get(0));



    }






    /**
     * Helper method that checks all attributes of an Account object to verify
     * the tested methods return the expected object and its associated properties
     *
     * @param expected the expected Account Object
     * @param actual the returned Account Object
     */

    private void assertAccountsMatch (Account expected, Account actual){

        Assert.assertEquals("Account IDs do not match",expected.getAccountId(),actual.getAccountId());
        Assert.assertEquals("User Ids do not match",expected.getUserId(),expected.getUserId());
        Assert.assertEquals("Balances do not match",expected.getBalance(),expected.getBalance());
    }

    /**
     * Helper method that checks all attributes of a Balance object to verify
     * the tested method(s) return the expected object and its associated properties
     *
     * @param expected the expected Balance Object
     * @param actual the returned Balance Object
     */
    public void assertBalancesMatch(Balance expected, Balance actual){

        Assert.assertEquals("Usernames do not match",expected.getUsername(),actual.getUsername());
        Assert.assertEquals("Balance amounts do not match",expected.getBalance(),actual.getBalance());

    }











}