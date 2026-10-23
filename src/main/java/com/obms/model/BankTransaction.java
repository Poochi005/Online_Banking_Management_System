package com.obms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BankTransaction {
    private final long id;
    private final long accountId;
    private final String type;
    private final BigDecimal amount;
    private final String description;
    private final LocalDateTime createdAt;

    public BankTransaction(long id, long accountId, String type, BigDecimal amount, String description, LocalDateTime createdAt) {
        this.id=id; this.accountId=accountId; this.type=type; this.amount=amount; this.description=description; this.createdAt=createdAt;
    }
    public long getId(){ return id; }
    public long getAccountId(){ return accountId; }
    public String getType(){ return type; }
    public BigDecimal getAmount(){ return amount; }
    public String getDescription(){ return description; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
}
