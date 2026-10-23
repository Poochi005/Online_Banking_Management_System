package com.obms.repository;

import com.obms.config.DBConnection;
import com.obms.model.Account;
import com.obms.model.BankTransaction;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BankRepository {
    public long createAccount(String name, String email, String phone, String type) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                long customerId;
                try (PreparedStatement p = c.prepareStatement(
                        "INSERT INTO customers(full_name,email,phone) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                    p.setString(1, name); p.setString(2, email); p.setString(3, phone); p.executeUpdate();
                    try (ResultSet keys = p.getGeneratedKeys()) { keys.next(); customerId = keys.getLong(1); }
                }
                String number = "OB" + UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase();
                long accountId;
                try (PreparedStatement p = c.prepareStatement(
                        "INSERT INTO accounts(customer_id,account_number,account_type) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                    p.setLong(1, customerId); p.setString(2, number); p.setString(3, type); p.executeUpdate();
                    try (ResultSet keys = p.getGeneratedKeys()) { keys.next(); accountId = keys.getLong(1); }
                }
                c.commit();
                return accountId;
            } catch (SQLException e) { c.rollback(); throw e; }
        }
    }

    public List<Account> listAccounts() throws SQLException {
        List<Account> out = new ArrayList<>();
        String sql = "SELECT a.id,a.customer_id,a.account_number,a.account_type,a.balance,c.full_name " +
                     "FROM accounts a JOIN customers c ON c.id=a.customer_id ORDER BY a.id";
        try (Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql); ResultSet r=p.executeQuery()) {
            while (r.next()) out.add(new Account(r.getLong(1),r.getLong(2),r.getString(3),r.getString(4),r.getBigDecimal(5),r.getString(6)));
        }
        return out;
    }

    public void deposit(long accountId, BigDecimal amount, String type) throws SQLException {
        try (Connection c=DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement p=c.prepareStatement("UPDATE accounts SET balance=balance+? WHERE id=?")) {
                    p.setBigDecimal(1,amount); p.setLong(2,accountId);
                    if (p.executeUpdate()!=1) throw new SQLException("Account ID not found.");
                }
                addTransaction(c,accountId,type,amount,"Cash "+type.toLowerCase());
                c.commit();
            } catch (SQLException e) { c.rollback(); throw e; }
        }
    }

    public void withdraw(long accountId, BigDecimal amount) throws SQLException {
        try (Connection c=DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement p=c.prepareStatement("UPDATE accounts SET balance=balance-? WHERE id=? AND balance>=?")) {
                    p.setBigDecimal(1,amount); p.setLong(2,accountId); p.setBigDecimal(3,amount);
                    if (p.executeUpdate()!=1) throw new SQLException("Account not found or insufficient balance.");
                }
                addTransaction(c,accountId,"WITHDRAWAL",amount,"Cash withdrawal");
                c.commit();
            } catch (SQLException e) { c.rollback(); throw e; }
        }
    }

    public void transfer(long fromId, long toId, BigDecimal amount) throws SQLException {
        if (fromId==toId) throw new SQLException("Source and destination accounts must be different.");
        try (Connection c=DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement p=c.prepareStatement("UPDATE accounts SET balance=balance-? WHERE id=? AND balance>=?")) {
                    p.setBigDecimal(1,amount); p.setLong(2,fromId); p.setBigDecimal(3,amount);
                    if (p.executeUpdate()!=1) throw new SQLException("Source account not found or insufficient balance.");
                }
                try (PreparedStatement p=c.prepareStatement("UPDATE accounts SET balance=balance+? WHERE id=?")) {
                    p.setBigDecimal(1,amount); p.setLong(2,toId);
                    if (p.executeUpdate()!=1) throw new SQLException("Destination account ID not found.");
                }
                addTransaction(c,fromId,"TRANSFER_OUT",amount,"Transfer to account ID "+toId);
                addTransaction(c,toId,"TRANSFER_IN",amount,"Transfer from account ID "+fromId);
                c.commit();
            } catch (SQLException e) { c.rollback(); throw e; }
        }
    }

    private void addTransaction(Connection c,long id,String type,BigDecimal amount,String desc) throws SQLException {
        try (PreparedStatement p=c.prepareStatement("INSERT INTO transactions(account_id,transaction_type,amount,description) VALUES(?,?,?,?)")) {
            p.setLong(1,id); p.setString(2,type); p.setBigDecimal(3,amount); p.setString(4,desc); p.executeUpdate();
        }
    }

    public List<BankTransaction> history(long accountId) throws SQLException {
        List<BankTransaction> out=new ArrayList<>();
        try (Connection c=DBConnection.getConnection();
             PreparedStatement p=c.prepareStatement("SELECT id,account_id,transaction_type,amount,description,created_at FROM transactions WHERE account_id=? ORDER BY created_at DESC")) {
            p.setLong(1,accountId);
            try (ResultSet r=p.executeQuery()) {
                while(r.next()) out.add(new BankTransaction(r.getLong(1),r.getLong(2),r.getString(3),r.getBigDecimal(4),r.getString(5),r.getTimestamp(6).toLocalDateTime()));
            }
        }
        return out;
    }
}
