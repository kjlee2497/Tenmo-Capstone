package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Balance {
    private String username;
    private BigDecimal balance;

    public Balance() {
    }

    public Balance(String username, BigDecimal balance) {
        this.username = username;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
