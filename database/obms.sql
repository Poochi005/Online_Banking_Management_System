CREATE DATABASE IF NOT EXISTS obms;
USE obms;

CREATE TABLE IF NOT EXISTS customers (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL UNIQUE,
  phone VARCHAR(25),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS accounts (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  customer_id BIGINT NOT NULL,
  account_number VARCHAR(24) NOT NULL UNIQUE,
  account_type ENUM('SAVINGS','CURRENT') NOT NULL DEFAULT 'SAVINGS',
  balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_accounts_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
  CONSTRAINT chk_balance_nonnegative CHECK (balance >= 0)
);

CREATE TABLE IF NOT EXISTS transactions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  account_id BIGINT NOT NULL,
  transaction_type ENUM('DEPOSIT','WITHDRAWAL','TRANSFER_IN','TRANSFER_OUT') NOT NULL,
  amount DECIMAL(15,2) NOT NULL,
  description VARCHAR(255),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_transactions_account FOREIGN KEY (account_id) REFERENCES accounts(id),
  CONSTRAINT chk_transaction_amount CHECK (amount > 0),
  INDEX idx_transactions_account_date (account_id, created_at)
);
