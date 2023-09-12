package com.techelevator.tenmo.dao;

import com.techelevator.dao.BaseDaoTests;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class JdbcTransferDaoTest extends BaseDaoTests {

    private JdbcTransferDao sut;
    private JdbcAccountDao accountDao;
    private static final Transfer TRANSFER_1 = new Transfer(3101,2101,2102, new BigDecimal("500.00"),"*Approved*");
    private static final Transfer TRANSFER_2 = new Transfer(3102,2104,2103, new BigDecimal("20000.00"), "*Approved*");
    private static final Transfer TRANSFER_3 = new Transfer(3103, 2103,2101, new BigDecimal("1531.52"), "*Pending*");
    private static final Transfer TRANSFER_4 = new Transfer(3104, 2101,2102, new BigDecimal("500.00"), "*Rejected*");

    private static final TransferDTO TRANSFER_DTO_1 = new TransferDTO(3101, new BigDecimal("500.00"), "kevin","chris");
    private static final TransferDTO TRANSFER_DTO_2 = new TransferDTO(3102, new BigDecimal("20000.00"), "thwin","eric");
    private static final TransferDTO TRANSFER_DTO_3 = new TransferDTO(3103, new BigDecimal("1531.52"), "eric","kevin");
    private static final TransferDTO TRANSFER_DTO_4 = new TransferDTO(3104, new BigDecimal("500.00"), "kevin","chris");

    private Account ACCOUNT_1 = new Account(2101,1101,new BigDecimal("1000.00"));
    private Account ACCOUNT_2 = new Account(2102,1102,new BigDecimal("2000.00"));
    private Account ACCOUNT_3 = new Account(2103,1103,new BigDecimal("3000.00"));
    private Account ACCOUNT_4 = new Account(2104,1104,new BigDecimal("4000.00"));
    private Transfer TESTtransfer1;
    private Transfer TESTtransfer2;
    private TransferDTO TEST_TransferDTO1;
    private TransferDTO TEST_TransferDTO2;


    //ToDO add extra tests for the new methods(multi accounts)
    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcTransferDao(jdbcTemplate);
        accountDao = new JdbcAccountDao(jdbcTemplate);

        TESTtransfer1 = new Transfer(3001, 2103, 2104, new BigDecimal("100.00"), "*Approved*");
        TESTtransfer2 = new Transfer(3002, 2104, 2103, new BigDecimal("250.00"), "*Approved*");

        TEST_TransferDTO1 = new TransferDTO(3001, new BigDecimal("100.00"), "eric", "thwin");
        TEST_TransferDTO2 = new TransferDTO(3002, new BigDecimal("250.00"), "thwin", "eric");
    }

    @Test
    public void getTransferDTOsByUsername() {
        String username = "kevin";
        List<TransferDTO> expected = new ArrayList<>();
        expected.add(TRANSFER_DTO_1);
        expected.add(TRANSFER_DTO_3);
        expected.add(TRANSFER_DTO_4);
        List<TransferDTO> actual = sut.getTransferDTOsByUsername(username);

        assertTransferDTOsMatch(TRANSFER_DTO_1, actual.get(0));
        assertTransferDTOsMatch(TRANSFER_DTO_3, actual.get(1));
        assertTransferDTOsMatch(TRANSFER_DTO_4, actual.get(2));


        username = "chris";
        List<TransferDTO> expected2 = new ArrayList<>();
        expected2.add(TRANSFER_DTO_1);
        expected2.add(TRANSFER_DTO_4);

        List<TransferDTO> actual2 = sut.getTransferDTOsByUsername(username);
        assertTransferDTOsMatch(TRANSFER_DTO_1, actual2.get(0));
        assertTransferDTOsMatch(TRANSFER_DTO_4, actual2.get(1));

    }

    @Test
    public void getTransferDTOByID() {
        TransferDTO expected1 = TRANSFER_DTO_1;
        TransferDTO expected2 = TRANSFER_DTO_2;

        TransferDTO actual1 = sut.getTransferDTOByID(3101);
        TransferDTO actual2 = sut.getTransferDTOByID(3102);

        assertTransferDTOsMatch(actual1, expected1);
        assertTransferDTOsMatch(actual2, expected2);
    }

    @Test
    public void createTransfer() {
        TransferDTO actual = sut.createTransfer(TESTtransfer1, 1103, 1104);
        TransferDTO actual2 = sut.createTransfer(TESTtransfer2, 1104, 1103);

        assertTransferDTOsMatch(TEST_TransferDTO1, actual);
        assertTransferDTOsMatch(TEST_TransferDTO2, actual2);
    }

    @Test
    public void updateTransferStatus() {
        String approved = "*Approved*";
        String rejected = "*Rejected*";

        TransferDTO updatedTransferDTO = sut.updateTransferStatus(approved, 3103);
        String updatedTransferStatus = sut.getTransferByID(3103).getStatus();

        Assert.assertEquals(approved, updatedTransferStatus);

        updatedTransferDTO = sut.updateTransferStatus(rejected, 3103);
        updatedTransferStatus = sut.getTransferByID(3103).getStatus();

        Assert.assertEquals(rejected, updatedTransferStatus);
    }

    @Test
    public void completeTransaction() {
        BigDecimal account1Balance = ACCOUNT_1.getBalance();
        BigDecimal account2Balance = ACCOUNT_2.getBalance();
        BigDecimal finalBalance1 = account1Balance.subtract(TRANSFER_4.getAmount());
        BigDecimal finalBalance2 = account2Balance.add(TRANSFER_4.getAmount());

        sut.completeTransaction(TRANSFER_4);
        BigDecimal actualBalance1 = accountDao.findAccountById(2101).getBalance();
        BigDecimal actualBalance2 = accountDao.findAccountById(2102).getBalance();

        Assert.assertEquals(finalBalance1, actualBalance1);
        Assert.assertEquals(finalBalance2, actualBalance2);
    }

    @Test
    public void getTransferByID() {
        Transfer actual = sut.getTransferByID(3101);
        Transfer actual2 = sut.getTransferByID(3102);

        assertTransfersMatch(TRANSFER_1, actual);
        assertTransfersMatch(TRANSFER_2, actual2);
    }

    @Test
    public void getPendingDTOs() {
        List<TransferDTO> actual = sut.getPendingDTOs("eric");
        List<TransferDTO> actual2 = sut.getPendingDTOs("kevin");

        for(TransferDTO item: actual) {
            assertTransferDTOsMatch(TRANSFER_DTO_3, item);
        }
        for(TransferDTO item: actual) {
            assertTransferDTOsMatch(TRANSFER_DTO_3, item);
        }
    }

    private void assertTransfersMatch(Transfer expected, Transfer actual) {
        Assert.assertEquals(expected.getTransfer_id(), actual.getTransfer_id());
        Assert.assertEquals(expected.getSenderAccountId(), actual.getSenderAccountId());
        Assert.assertEquals(expected.getReceiverAccountId(), actual.getReceiverAccountId());
        Assert.assertEquals(expected.getAmount(), actual.getAmount());
        Assert.assertEquals(expected.getStatus(), actual.getStatus());
    }

    private void assertTransferDTOsMatch(TransferDTO expected, TransferDTO actual) {
        Assert.assertEquals(expected.getTransferId(), actual.getTransferId());
        Assert.assertEquals(expected.getAmount(), actual.getAmount());
        Assert.assertEquals(expected.getTo(), actual.getTo());
        Assert.assertEquals(expected.getFrom(), actual.getFrom());
    }

}