package com.techelevator.tenmo.dao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferApprovalDTO;
import com.techelevator.tenmo.model.TransferDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Component
public class JdbcTransferDao implements TransferDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<TransferDTO> getTransferDTOsByUsername(String username) {
        /*  ---Return all transfers associated with the logged in user---
            SELECT transfer_id,amount ,t1.username AS from,t2.username AS to
            FROM transfer
            JOIN account AS a1 ON transfer.sender_account_id = a1.account_id
            JOIN account AS a2 ON transfer.receiver_account_id = a2.account_id
            JOIN tenmo_user AS t1 on a1.user_id = t1.user_id
            JOIN tenmo_user AS t2 on a2.user_id = t2.user_id
            WHERE sender_account_id=(SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = 'chris')
            OR receiver_account_id = (SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = 'chris')
            ORDER BY transfer_id;
         */

        String sql ="SELECT transfer_id,amount ,t1.username AS from,t2.username AS to\n" +
                    "FROM transfer\n" +
                    "JOIN account AS a1 ON transfer.sender_account_id = a1.account_id\n" +
                    "JOIN account AS a2 ON transfer.receiver_account_id = a2.account_id\n" +
                    "JOIN tenmo_user AS t1 on a1.user_id = t1.user_id\n" +
                    "JOIN tenmo_user AS t2 on a2.user_id = t2.user_id\n" +

                    "WHERE sender_account_id IN (SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = ?)\n" +
                    "OR receiver_account_id IN (SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = ?)\n" +
                    "ORDER BY transfer_id;";
        List<TransferDTO> returnedTransferDTOs = new ArrayList<>();
        try{
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql,username,username);
            while(results.next()){
                returnedTransferDTOs.add(mapSqlRowsetToDTO(results));
            }

        }catch (CannotGetJdbcConnectionException e){
            throw new RuntimeException("Unable to contact the database!", e);
        }catch (BadSqlGrammarException e){
            throw new RuntimeException("Bad SQL query: " + e.getSql()
                                        +"\n"+e.getSQLException(), e);
        }catch (DataIntegrityViolationException e){
            throw new RuntimeException("Database Integrity Violation", e);
        }
        return returnedTransferDTOs;
    }


    @Override
    public TransferDTO getTransferDTOByID(int id) {
        /*  -- Return transfer by specific transfer ID
            SELECT transfer_id,amount ,t1.username AS from,t2.username AS to
            FROM transfer
            JOIN account AS a1 ON transfer.sender_account_id = a1.account_id
            JOIN account AS a2 ON transfer.receiver_account_id = a2.account_id
            JOIN tenmo_user AS t1 on a1.user_id = t1.user_id
            JOIN tenmo_user AS t2 on a2.user_id = t2.user_id
            WHERE transfer_id = ?;
         */

        String sql = "SELECT transfer_id,amount ,t1.username AS from,t2.username AS to\n" +
                     "FROM transfer\n" +
                     "JOIN account AS a1 ON transfer.sender_account_id = a1.account_id\n" +
                     "JOIN account AS a2 ON transfer.receiver_account_id = a2.account_id\n" +
                     "JOIN tenmo_user AS t1 on a1.user_id = t1.user_id\n" +
                     "JOIN tenmo_user AS t2 on a2.user_id = t2.user_id\n" +
                     "WHERE transfer_id = ?;";

        //create object to return and set to null
        TransferDTO returnedTransferDTO = null;

        try{
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql,id);
            if(results.next()){
                returnedTransferDTO = mapSqlRowsetToDTO(results);
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new RuntimeException("Unable to contact the database!", e);
        }catch (BadSqlGrammarException e){
            throw new RuntimeException("Bad SQL query: " + e.getSql()
                    +"\n"+e.getSQLException(), e);
        }catch (DataIntegrityViolationException e){
            throw new RuntimeException("Database Integrity Violation", e);
        }
        return returnedTransferDTO;
    }

    @Override
    public TransferDTO createTransfer(Transfer newTransfer, int senderUserId, int receiverUserId) {
        /*
            --create transfer (easy way)
            INSERT INTO transfer(sender_account_id, receiver_account_id, approve_status, amount)
            VALUES((SELECT account_id FROM account WHERE user_id = ?),(SELECT account_id FROM account WHERE user_id = ?),'*Pending*',?)RETURNING transfer_id;
         */
        TransferDTO createdTransferDTO = null;
        String sql = "INSERT INTO transfer(sender_account_id, receiver_account_id, approve_status, amount)\n" +
                     "VALUES((SELECT account_id FROM account WHERE user_id = ?),(SELECT account_id FROM account WHERE user_id = ?),?,?)RETURNING transfer_id;";

        try{
            int newTransferId = jdbcTemplate.queryForObject(sql,Integer.class,
                                senderUserId,
                                receiverUserId,
                                newTransfer.getStatus(),
                                newTransfer.getAmount());
            if(newTransferId>0){
                createdTransferDTO = getTransferDTOByID(newTransferId);
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new RuntimeException("Unable to contact the database!", e);
        }catch (BadSqlGrammarException e){
            throw new RuntimeException("Bad SQL query: " + e.getSql()
                    +"\n"+e.getSQLException(), e);
        }catch (DataIntegrityViolationException e){
            throw new RuntimeException("Database Integrity Violation", e);
        }catch (NullPointerException e){
            throw new RuntimeException("Null value returned", e);
        }
        return createdTransferDTO;
    }

    @Override
    public TransferDTO createTransferWithAccounts(Transfer newTransfer, int senderAccountId, int receiverAccountId) {
        /* --create transfer (easiest way)
            INSERT INTO transfer(sender_account_id, receiver_account_id, approve_status, amount)
            VALUES(?,?,'*Pending*',?)RETURNING transfer_id;

         */

        TransferDTO createdTransferDTO = null;
        String sql = "INSERT INTO transfer(sender_account_id, receiver_account_id, approve_status, amount)\n" +
                     "VALUES(?,?,'*Pending*',?)RETURNING transfer_id;";

        try{
            int newTransferId = jdbcTemplate.queryForObject(sql,Integer.class,
                    senderAccountId,
                    receiverAccountId,
                    newTransfer.getAmount());
            if(newTransferId>0){
                createdTransferDTO = getTransferDTOByID(newTransferId);
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new RuntimeException("Unable to contact the database!", e);
        }catch (BadSqlGrammarException e){
            throw new RuntimeException("Bad SQL query: " + e.getSql()
                    +"\n"+e.getSQLException(), e);
        }catch (DataIntegrityViolationException e){
            throw new RuntimeException("Database Integrity Violation", e);
        }catch (NullPointerException e){
            throw new RuntimeException("Null value returned", e);
        }
        return createdTransferDTO;
    }





    @Override
    public TransferDTO updateTransferStatus(String status, int id) {
        /*  -- update transfer Status using id
            UPDATE transfer SET approve_status = ?
            WHERE transfer_id = ?;
         */
        String sql = "UPDATE transfer SET approve_status = ?\n" +
                     "WHERE transfer_id = ?;";

        TransferDTO updatedTransferDTO = null;

        try{
            int updatedRows = jdbcTemplate.update(sql,status,id);
            if(updatedRows>0){
                updatedTransferDTO = getTransferDTOByID(id);
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new RuntimeException("Unable to contact the database!", e);
        }catch (BadSqlGrammarException e){
            throw new RuntimeException("Bad SQL query: " + e.getSql()
                    +"\n"+e.getSQLException(), e);
        }catch (DataIntegrityViolationException e){
            throw new RuntimeException("Database Integrity Violation", e);
        }

        return updatedTransferDTO;
    }

        @Override
    public Transfer getTransferByID(int id) {
        Transfer retreivedTransfer = null;
        /*  SELECT transfer_id,sender_account_id,receiver_account_id,approve_status,amount
            FROM transfer
            WHERE transfer_id = ?;
        */

        String sql = "SELECT transfer_id,sender_account_id,receiver_account_id,approve_status,amount " +
                     "FROM transfer " +
                     "WHERE transfer_id = ?;";
        try{
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql,id);
            if(results.next()){
                retreivedTransfer = mapRowSetToTransfer(results);
            }
        }catch (CannotGetJdbcConnectionException e){
            System.out.println("Cannot connect to database!");
        }catch (BadSqlGrammarException e){
            System.out.println("Bad Query: " + e.getSql() +
                    "\n"+e.getSQLException());
        }catch (DataIntegrityViolationException e){
            System.out.println("Data Integrity Violation" + e.getMessage());
        }

        return retreivedTransfer;
    }

    @Override
    public List<TransferDTO> getPendingDTOs(String username) {
        List<TransferDTO> pendingTransferDTOs = new ArrayList<>();
        /*  -- Return all pending transfers associated with the logged in user
            SELECT transfer_id,amount ,t1.username AS from,t2.username AS to
            FROM transfer
            JOIN account AS a1 ON transfer.sender_account_id = a1.account_id
            JOIN account AS a2 ON transfer.receiver_account_id = a2.account_id
            JOIN tenmo_user AS t1 on a1.user_id = t1.user_id
            JOIN tenmo_user AS t2 on a2.user_id = t2.user_id
            WHERE approve_status ILIKE '%pending%'
            AND (sender_account_id=(SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = ?)
            OR receiver_account_id = (SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = ?))
            ORDER BY transfer_id;
         */
        String sql = "SELECT transfer_id,amount ,t1.username AS from,t2.username AS to\n" +
                     "FROM transfer\n" +
                     "JOIN account AS a1 ON transfer.sender_account_id = a1.account_id\n" +
                     "JOIN account AS a2 ON transfer.receiver_account_id = a2.account_id\n" +
                     "JOIN tenmo_user AS t1 on a1.user_id = t1.user_id\n" +
                     "JOIN tenmo_user AS t2 on a2.user_id = t2.user_id\n" +
                     "WHERE approve_status ILIKE '%pending%'\n" +
                     "AND (sender_account_id IN (SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = ?)\n" +
                     "OR receiver_account_id IN (SELECT account_id FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE tenmo_user.username = ?))\n" +
                     "ORDER BY transfer_id;";
        try{
            SqlRowSet results= jdbcTemplate.queryForRowSet(sql,username,username);
            while(results.next()){
                pendingTransferDTOs.add(mapSqlRowsetToDTO(results));
            }
        }catch (CannotGetJdbcConnectionException e){
            System.out.println("Cannot connect to database!");
        }catch (BadSqlGrammarException e){
            System.out.println("Bad Query: " + e.getSql() +
                    "\n"+e.getSQLException());
        }catch (DataIntegrityViolationException e){
            System.out.println("Data Integrity Violation" + e.getMessage());
        }

        return pendingTransferDTOs;
    }

    @Override
    @Transactional
    public TransferApprovalDTO completeTransaction(Transfer pendingTransfer) {

        String sql1 = "UPDATE account SET balance = balance - ? \n" +
                      "WHERE account_id = ?;";

        String sql2 = "UPDATE account SET balance = balance + ? \n" +
                      "WHERE account_id = ?;";

        try{
            // process the decrement
            int decrementedRowCount = jdbcTemplate.update(sql1, pendingTransfer.getAmount(), pendingTransfer.getSenderAccountId());
            // check to see if valid update was performed
            if(decrementedRowCount ==0 ){
                throw new RuntimeException("Expected an updated row. None were updated");
            }else if(decrementedRowCount >1){
                throw new RuntimeException("More than 1 row was updated.");
            }else{

            }
            //process the increment
            int incrementedRowCount = jdbcTemplate.update(sql2,pendingTransfer.getAmount(),pendingTransfer.getReceiverAccountId());
            //check to see if valid update was performed
            if(incrementedRowCount ==0){
                throw new RuntimeException("Expected an updated row. None were updated");
            }else if(incrementedRowCount >1){
                throw new RuntimeException("More than 1 row was updated.");}

        }catch (CannotGetJdbcConnectionException e){
            throw new RuntimeException("Unable to contact the database!", e);
        }catch (BadSqlGrammarException e){
            throw new RuntimeException("Bad SQL query: " + e.getSql()
                    +"\n"+e.getSQLException(), e);
        }catch (DataIntegrityViolationException e){
            throw new RuntimeException("Database Integrity Violation", e);
        }
        //If successful updates are performed update the status of the associated transfer
        updateTransferStatus("*Approved*",pendingTransfer.getTransfer_id());
        TransferDTO current = getTransferDTOByID(pendingTransfer.getTransfer_id());
        TransferApprovalDTO  updatedApprovalDTO = new TransferApprovalDTO();
        updatedApprovalDTO.setTransferId(pendingTransfer.getTransfer_id());
        updatedApprovalDTO.setAmount(pendingTransfer.getAmount());
        updatedApprovalDTO.setStatus(pendingTransfer.getStatus());
        updatedApprovalDTO.setApprove(true);
        updatedApprovalDTO.setFrom(current.getFrom());
        updatedApprovalDTO.setTo(current.getTo());

        return updatedApprovalDTO;
    }



    private TransferDTO mapSqlRowsetToDTO(SqlRowSet results){
        TransferDTO mappedTransferDTO = new TransferDTO();

        mappedTransferDTO.setTransferId(results.getInt("transfer_id"));
        mappedTransferDTO.setAmount(results.getBigDecimal("amount"));
        mappedTransferDTO.setFrom(results.getString("from"));
        mappedTransferDTO.setTo(results.getString("to"));

        return mappedTransferDTO;
    }

    private Transfer mapRowSetToTransfer (SqlRowSet results){
        Transfer mappedTransfer = new Transfer();
        mappedTransfer.setTransfer_id(results.getInt("transfer_id"));
        mappedTransfer.setSenderAccountId(results.getInt("sender_account_id"));
        mappedTransfer.setReceiverAccountId(results.getInt("receiver_account_id"));
        mappedTransfer.setAmount(results.getBigDecimal("amount"));
        mappedTransfer.setStatus(results.getString("approve_status"));
        return  mappedTransfer;
    }

    private TransferApprovalDTO mapRowSetToApprovalDTO (SqlRowSet results){
        TransferApprovalDTO mappedApprovalDTO = new TransferApprovalDTO();
        mappedApprovalDTO.setTransferId(results.getInt("transfer_id"));
        mappedApprovalDTO.setAmount(results.getBigDecimal("amount"));
        mappedApprovalDTO.setFrom(results.getString("from"));
        mappedApprovalDTO.setTo(results.getString("to"));
        mappedApprovalDTO.setStatus(results.getString("approve_status"));
        return mappedApprovalDTO;
    }


}