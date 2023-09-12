package com.techelevator.tenmo.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class TransferDTO {

    private int transferId;
    @NotNull
    @Positive
    private BigDecimal amount;

    private String from;
    @NotEmpty
    private String to;

    public TransferDTO() {
    }
    // For testing purposes
    public TransferDTO(int transferId, BigDecimal amount, String from, String to) {
        this.transferId = transferId;
        this.amount = amount;
        this.from = from;
        this.to = to;
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

}
