BEGIN TRANSACTION;

DROP TABLE IF EXISTS tenmo_user, account, transfer;

DROP SEQUENCE IF EXISTS seq_user_id, seq_account_id, seq_transfer_id;

-- Sequence to start user_id values at 1001 instead of 1
CREATE SEQUENCE seq_user_id
  INCREMENT BY 1
  START WITH 1001
  NO MAXVALUE;

CREATE TABLE tenmo_user (
	user_id int NOT NULL DEFAULT nextval('seq_user_id'),
	username varchar(50) NOT NULL,
	password_hash varchar(200) NOT NULL,
	CONSTRAINT PK_tenmo_user PRIMARY KEY (user_id),
	CONSTRAINT UQ_username UNIQUE (username)
);

-- Sequence to start account_id values at 2001 instead of 1
-- Note: Use similar sequences with unique starting values for additional tables
CREATE SEQUENCE seq_account_id
  INCREMENT BY 1
  START WITH 2001
  NO MAXVALUE;

CREATE TABLE account (
	account_id int NOT NULL DEFAULT nextval('seq_account_id'),
	user_id int NOT NULL,
	balance numeric(13, 2) NOT NULL,
	CONSTRAINT PK_account PRIMARY KEY (account_id),
	CONSTRAINT FK_account_tenmo_user FOREIGN KEY (user_id) REFERENCES tenmo_user (user_id)
);

CREATE SEQUENCE seq_transfer_id
  INCREMENT BY 1
  START WITH 3001
  NO MAXVALUE;

CREATE TABLE transfer(
	transfer_id int NOT NULL DEFAULT nextval ('seq_transfer_id'),
	sender_account_id int NOT NULL,
	receiver_account_id int NOT NULL,
	approve_status varchar(10) NOT NULL,
	amount numeric(13,2) NOT NULL,
	CONSTRAINT PK_transfer PRIMARY KEY(transfer_id),
	CONSTRAINT FK_transfer_send_tenmo_user FOREIGN KEY (sender_account_id) REFERENCES account(account_id),
	CONSTRAINT FK_transfer_receive_tenmo_user FOREIGN KEY (receiver_account_id) REFERENCES account(account_id)
);

INSERT INTO tenmo_user (user_id, username, password_hash)
VALUES(1101,'kevin','$2a$10$EPV7k.ntx6zIQaDiVF1ptuFeUCkwUkQnDq17fUHhPojTxIG/0xis6'),
(1102,'chris','$2a$10$bnnRv/C9XDXlRA9.paxgbODzi4n5fw/D06WI2AWyoMmGY75MwZeoG'),
(1103,'eric','$2a$10$/OMTuByaTxKtyqXN.m9hnezEv9DdhCaB9Jnnmnc7yh6ODp1KV8Cz.'),
(1104,'thwin','$2a$10$QB.eOvz/SYiltE.g8ghNPu.jW23vKIu5cjLSfGeFYOn/f.6UJi1su');

INSERT INTO account(account_id,user_id,balance)
VALUES(2101,1101,1000),
(2102,1102,2000),
(2103,1103,3000),
(2104,1104,40000)
;

INSERT INTO transfer (transfer_id,sender_account_id,receiver_account_id,approve_status,amount)
VALUES(3101,2101,2102,'*Approved*',500),
(3102,2104,2103,'*Approved*',20000),
(3103, 2103,2101, '*Pending*',1531.52),
(3104, 2101,2102, '*Rejected*',500);
COMMIT;



