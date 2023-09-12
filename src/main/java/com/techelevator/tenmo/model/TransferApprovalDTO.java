package com.techelevator.tenmo.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class TransferApprovalDTO {
    @Min(value = 3001)
    private int transferId;
    @NotNull
    @Positive
    private BigDecimal amount;
    @NotEmpty
    private String from;
    @NotEmpty
    private String to;
    @NotNull
    private boolean approve;

    private String status;

    public TransferApprovalDTO() {
    }

    //For testing purposes
    public TransferApprovalDTO(int transferId, BigDecimal amount, String from, String to, boolean approve, String status) {
        this.transferId = transferId;
        this.amount = amount;
        this.from = from;
        this.to = to;
        this.approve = approve;
        this.status = status;
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

    public boolean isApprove() {
        return approve;
    }

    public void setApprove(boolean approve) {
        this.approve = approve;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
