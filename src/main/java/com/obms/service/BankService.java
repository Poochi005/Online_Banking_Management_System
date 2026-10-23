package com.obms.service;

import com.obms.model.Account;
import com.obms.model.BankTransaction;
import com.obms.repository.BankRepository;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class BankService {
    private final BankRepository repository = new BankRepository();

    public long createAccount(String name,String email,String phone,String type) throws SQLException {
        if(name==null || name.isBlank()) throw new IllegalArgumentException("Customer name is required.");
        if(email==null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) throw new IllegalArgumentException("Enter a valid email.");
        if(!"SAVINGS".equals(type) && !"CURRENT".equals(type)) throw new IllegalArgumentException("Choose SAVINGS or CURRENT.");
        return repository.createAccount(name.trim(),email.trim(),phone==null?"":phone.trim(),type);
    }
    public List<Account> listAccounts() throws SQLException { return repository.listAccounts(); }
    public void deposit(long id,BigDecimal amount) throws SQLException { validateAmount(amount); repository.deposit(id,amount,"DEPOSIT"); }
    public void withdraw(long id,BigDecimal amount) throws SQLException { validateAmount(amount); repository.withdraw(id,amount); }
    public void transfer(long from,long to,BigDecimal amount) throws SQLException { validateAmount(amount); repository.transfer(from,to,amount); }
    public List<BankTransaction> history(long id) throws SQLException { return repository.history(id); }
    private void validateAmount(BigDecimal amount) {
        if(amount==null || amount.compareTo(BigDecimal.ZERO)<=0 || amount.scale()>2)
            throw new IllegalArgumentException("Amount must be positive and have no more than 2 decimal places.");
    }
}
