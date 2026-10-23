package com.obms;

import com.obms.model.Account;
import com.obms.model.BankTransaction;
import com.obms.service.BankService;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class App {
    private static final Scanner sc = new Scanner(System.in);
    private static final BankService bank = new BankService();

    public static void main(String[] args) {
        System.out.println("======================================");
        System.out.println("   ONLINE BANKING MANAGEMENT SYSTEM");
        System.out.println("======================================");
        boolean running=true;
        while(running) {
            System.out.println("\n1. Create Account\n2. View All Accounts\n3. Deposit Money\n4. Withdraw Money\n5. Transfer Money\n6. Transaction History\n7. Exit");
            int choice=readInt("Choose option: ");
            try {
                switch(choice) {
                    case 1 -> createAccount();
                    case 2 -> showAccounts();
                    case 3 -> deposit();
                    case 4 -> withdraw();
                    case 5 -> transfer();
                    case 6 -> history();
                    case 7 -> { running=false; System.out.println("Thank you for using OBMS!"); }
                    default -> System.out.println("Please choose a number from 1 to 7.");
                }
            } catch (SQLException e) {
                System.out.println("Database operation failed: " + e.getMessage());
                System.out.println("Check MySQL is running, database/obms.sql was executed, and DB credentials are correct.");
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
        }
    }

    private static void createAccount() throws SQLException {
        System.out.print("Customer full name: "); String name=sc.nextLine().trim();
        System.out.print("Email: "); String email=sc.nextLine().trim();
        System.out.print("Phone (optional): "); String phone=sc.nextLine().trim();
        System.out.print("Account type (SAVINGS/CURRENT): "); String type=sc.nextLine().trim().toUpperCase();
        long id=bank.createAccount(name,email,phone,type);
        System.out.println("Account created successfully. Account ID: "+id);
    }
    private static void showAccounts() throws SQLException {
        List<Account> list=bank.listAccounts();
        if(list.isEmpty()) { System.out.println("No accounts yet."); return; }
        System.out.printf("%-5s %-20s %-28s %-10s %-14s %s%n","ID","CUSTOMER","ACCOUNT NUMBER","TYPE","BALANCE","CUSTOMER ID");
        for(Account a:list) System.out.printf("%-5d %-20s %-28s %-10s %-14s %d%n",a.getId(),a.getCustomerName(),a.getAccountNumber(),a.getAccountType(),a.getBalance(),a.getCustomerId());
    }
    private static void deposit() throws SQLException {
        long id=readLong("Account ID: "); BigDecimal amount=readAmount("Deposit amount: ");
        bank.deposit(id,amount); System.out.println("Deposit successful.");
    }
    private static void withdraw() throws SQLException {
        long id=readLong("Account ID: "); BigDecimal amount=readAmount("Withdrawal amount: ");
        bank.withdraw(id,amount); System.out.println("Withdrawal successful.");
    }
    private static void transfer() throws SQLException {
        long from=readLong("From account ID: "); long to=readLong("To account ID: ");
        BigDecimal amount=readAmount("Transfer amount: ");
        bank.transfer(from,to,amount); System.out.println("Transfer successful.");
    }
    private static void history() throws SQLException {
        long id=readLong("Account ID: "); List<BankTransaction> list=bank.history(id);
        if(list.isEmpty()) { System.out.println("No transactions found for this account."); return; }
        for(BankTransaction t:list) System.out.printf("#%d | %-12s | %s | %s | %s%n",t.getId(),t.getType(),t.getAmount(),t.getCreatedAt(),t.getDescription());
    }
    private static int readInt(String prompt) {
        while(true) { try { System.out.print(prompt); return Integer.parseInt(sc.nextLine().trim()); }
            catch(NumberFormatException e) { System.out.println("Enter a whole number."); } }
    }
    private static long readLong(String prompt) {
        while(true) { try { System.out.print(prompt); long v=Long.parseLong(sc.nextLine().trim()); if(v<=0) throw new NumberFormatException(); return v; }
            catch(NumberFormatException e) { System.out.println("Enter a positive ID."); } }
    }
    private static BigDecimal readAmount(String prompt) {
        while(true) { try { System.out.print(prompt); return new BigDecimal(sc.nextLine().trim()); }
            catch(NumberFormatException e) { System.out.println("Enter a valid amount, e.g. 250.00"); } }
    }
}
