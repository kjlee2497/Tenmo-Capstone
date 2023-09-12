-- **************************************************************************************************
SELECT * FROM tenmo_user;

-- **************************************************************************************************
SELECT * FROM account;

-- **************************************************************************************************
SELECT * FROM transfer;

-- **************************************************************************************************
-- Return all transfers associated with the logged in user
SELECT transfer_id,amount ,t1.username AS from,t2.username AS to
FROM transfer
JOIN account AS a1 ON transfer.sender_account_id = a1.account_id
JOIN account AS a2 ON transfer.receiver_account_id = a2.account_id
JOIN tenmo_user AS t1 on a1.user_id = t1.user_id
JOIN tenmo_user AS t2 on a2.user_id = t2.user_id
WHERE sender_account_id IN (SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = 'kevin')
OR receiver_account_id IN (SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = 'kevin')
ORDER BY transfer_id;

-- **************************************************************************************************
-- Return all pending transfers associated with the logged in user
SELECT transfer_id,amount ,t1.username AS from,t2.username AS to
FROM transfer
JOIN account AS a1 ON transfer.sender_account_id = a1.account_id
JOIN account AS a2 ON transfer.receiver_account_id = a2.account_id
JOIN tenmo_user AS t1 on a1.user_id = t1.user_id
JOIN tenmo_user AS t2 on a2.user_id = t2.user_id
WHERE approve_status ILIKE '%pending%'
AND (sender_account_id=(SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = 'chris')
OR receiver_account_id = (SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = 'chris'))
ORDER BY transfer_id;

-- **************************************************************************************************
-- Return transfer by specific transfer ID
SELECT transfer_id,amount ,t1.username AS from,t2.username AS to
FROM transfer
JOIN account AS a1 ON transfer.sender_account_id = a1.account_id
JOIN account AS a2 ON transfer.receiver_account_id = a2.account_id
JOIN tenmo_user AS t1 on a1.user_id = t1.user_id
JOIN tenmo_user AS t2 on a2.user_id = t2.user_id
WHERE transfer_id = 3104;
-- **************************************************************************************************
-- Return transfer with approved status by specific transfer ID 
SELECT transfer_id,amount ,t1.username AS from,t2.username AS to,approve_status
FROM transfer
JOIN account AS a1 ON transfer.sender_account_id = a1.account_id
JOIN account AS a2 ON transfer.receiver_account_id = a2.account_id
JOIN tenmo_user AS t1 on a1.user_id = t1.user_id
JOIN tenmo_user AS t2 on a2.user_id = t2.user_id
WHERE transfer_id = 3104;

-- **************************************************************************************************
--create transfer (easier way)
INSERT INTO transfer(sender_account_id, receiver_account_id, approve_status, amount)
VALUES((SELECT account_id FROM account WHERE user_id = ?),(SELECT account_id FROM account WHERE user_id = ?),'*Pending*',?)RETURNING transfer_id;

(SELECT account_id FROM account WHERE user_id = 1102)

-- **************************************************************************************************
--create transfer (easiest way)
INSERT INTO transfer(sender_account_id, receiver_account_id, approve_status, amount)
VALUES(?,?,'*Pending*',?)RETURNING transfer_id;

-- **************************************************************************************************
-- create transfer (complicated way without user DAO and assumes one account per user...)
START TRANSACTION;

INSERT INTO transfer(sender_account_id, receiver_account_id, approve_status, amount)
VALUES((SELECT DISTINCT sender_account_id 
		FROM tenmo_user 
		JOIN account ON tenmo_user.user_id = account.user_id
		JOIN transfer ON account.account_id = transfer.sender_account_id
		WHERE tenmo_user.username = 'kevin'
		GROUP BY sender_account_id),(SELECT DISTINCT receiver_account_id 
									 FROM tenmo_user 
									 JOIN account ON tenmo_user.user_id = account.user_id
									 JOIN transfer ON account.account_id = transfer.sender_account_id
									 WHERE tenmo_user.username = 'eric'
									 GROUP BY receiver_account_id),'*Pending*',100)
									 RETURNING transfer_id;

ROLLBACK;									 

-- **************************************************************************************************
-- update transfer Status using id
UPDATE transfer SET approve_status = '*Rejected*'
WHERE transfer_id = 3101;

-- **************************************************************************************************
-- Find Sender account by name (or user USERDAO)
SELECT DISTINCT sender_account_id 
FROM tenmo_user 
JOIN account ON tenmo_user.user_id = account.user_id
JOIN transfer ON account.account_id = transfer.sender_account_id
WHERE tenmo_user.username = 'kevin'
GROUP BY sender_account_id

-- **************************************************************************************************
--Find Receiver account by name (or user USERDAO)
SELECT DISTINCT receiver_account_id 
FROM tenmo_user 
JOIN account ON tenmo_user.user_id = account.user_id
JOIN transfer ON account.account_id = transfer.sender_account_id
WHERE tenmo_user.username = 'eric'
GROUP BY receiver_account_id;