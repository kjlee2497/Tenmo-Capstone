package com.techelevator.tenmo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class TransferMultiAccountDTO {
    private int transferId;
    @NotNull
    @Positive
    private BigDecimal amount;

    private String from;
    @NotEmpty
    private String to;

    @Min(value = 2001)
    private int userAccountId;


    @Min(value = 2001)
    private int otherAccountId;

    public TransferMultiAccountDTO() {
    }

    //To be used for testing purposes
    public TransferMultiAccountDTO(int transferId, BigDecimal amount, String from, String to, int userAccountId, int otherAccountId) {
        this.transferId = transferId;
        this.amount = amount;
        this.from = from;
        this.to = to;
        this.userAccountId = userAccountId;
        this.otherAccountId = otherAccountId;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(int userAccountId) {
        this.userAccountId = userAccountId;
    }

    public int getOtherAccountId() {
        return otherAccountId;
    }

    public void setOtherAccountId(int otherAccountId) {
        this.otherAccountId = otherAccountId;
    }
}
