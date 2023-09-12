package com.techelevator.dao;


import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.Username;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcUserDaoTests extends BaseDaoTests{

    private JdbcUserDao sut;
    private static final User USER_1 = new User(1101,"kevin","$2a$10$EPV7k.ntx6zIQaDiVF1ptuFeUCkwUkQnDq17fUHhPojTxIG/0xis6", true);
    private static final User USER_2 = new User(1102,"chris","$2a$10$bnnRv/C9XDXlRA9.paxgbODzi4n5fw/D06WI2AWyoMmGY75MwZeoG", true);
    private static final User USER_3 = new User(1103,"eric","$2a$10$/OMTuByaTxKtyqXN.m9hnezEv9DdhCaB9Jnnmnc7yh6ODp1KV8Cz.", true);
    private static final User USER_4 = new User(1104,"thwin","$2a$10$QB.eOvz/SYiltE.g8ghNPu.jW23vKIu5cjLSfGeFYOn/f.6UJi1su", true);




    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcUserDao(jdbcTemplate);

    }

    @Test
    public void createNewUser() {
//        Tests sut.create AND sut.findByUsername
        boolean userCreated = sut.create("TEST_USER","test_password");
        Assert.assertTrue(userCreated);
        User user = sut.findByUsername("TEST_USER");
        Assert.assertEquals("TEST_USER", user.getUsername());
    }

    @Test
    public void findIdByUsername() {
        int expectedId = sut.findIdByUsername("kevin");
        int expectedId2 = sut.findIdByUsername("thwin");

        Assert.assertEquals(1101, expectedId);
        Assert.assertEquals(1104, expectedId2);
    }

    @Test
    public void findAllTest() {
        List<User> actualResult = sut.findAll();
        User actual1 = actualResult.get(0);
        User actual2 = actualResult.get(1);
        User actual3 = actualResult.get(2);
        User actual4 = actualResult.get(3);

        assertUsersMatch(USER_1, actual1);
        assertUsersMatch(USER_2, actual2);
        assertUsersMatch(USER_3, actual3);
        assertUsersMatch(USER_4, actual4);
    }

    @Test
    public void findOtherUsersTest() {
        List<Username> expectedResult1 = new ArrayList<>();
        List<Username> expectedResult2 = new ArrayList<>();

        Username username1 = new Username(USER_1.getUsername());  //kevin
        Username username2 = new Username(USER_2.getUsername());  //chris
        Username username3 = new Username(USER_3.getUsername());  //eric
        Username username4 = new Username(USER_4.getUsername());  //thwin

        expectedResult1.add(username1);  // kevin
        expectedResult1.add(username2);  // chris
        expectedResult1.add(username3);  // eric

        expectedResult2.add(username2);  // chris
        expectedResult2.add(username3);  // eric
        expectedResult2.add(username4);  // thwin


        List<Username> actual1 = sut.findOtherUsers(username4.getUsername());  //thwin
        List<Username> actual2 = sut.findOtherUsers(username1.getUsername());  //kevin

        for (int i = 0; i < 3; i++) {
            assertUsernameDTOsMatch(expectedResult1.get(i), actual1.get(i));
        }
        for (int i = 0; i < 3; i++) {
            assertUsernameDTOsMatch(expectedResult2.get(i), actual2.get(i));
        }

    }

    private void assertUsersMatch(User expected, User actual) {
        Assert.assertEquals(expected.getUsername(), actual.getUsername());
        Assert.assertEquals(expected.getPassword(), actual.getPassword());
        Assert.assertEquals(expected.getId(), actual.getId());
        Assert.assertEquals(expected.isActivated(), actual.isActivated());
    }

    private void assertUsernameDTOsMatch(Username expected, Username actual) {
        Assert.assertEquals(expected.getUsername(), actual.getUsername());
    }
}
