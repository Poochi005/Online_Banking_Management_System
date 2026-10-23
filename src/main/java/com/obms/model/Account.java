package com.obms.model;

import java.math.BigDecimal;

public class Account {
    private final long id;
    private final long customerId;
    private final String accountNumber;
    private final String accountType;
    private final BigDecimal balance;
    private final String customerName;

    public Account(long id, long customerId, String accountNumber, String accountType, BigDecimal balance, String customerName) {
        this.id = id; this.customerId = customerId; this.accountNumber = accountNumber;
        this.accountType = accountType; this.balance = balance; this.customerName = customerName;
    }
    public long getId() { return id; }
    public long getCustomerId() { return customerId; }
    public String getAccountNumber() { return accountNumber; }
    public String getAccountType() { return accountType; }
    public BigDecimal getBalance() { return balance; }
    public String getCustomerName() { return customerName; }
}
