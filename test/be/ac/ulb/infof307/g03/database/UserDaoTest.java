package be.ac.ulb.infof307.g03.database;

import be.ac.ulb.infof307.g03.BeforeAllTests;
import be.ac.ulb.infof307.g03.database.dao.user.UserGetterDao;
import be.ac.ulb.infof307.g03.database.dao.user.UserSetterDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.models.ProjectBuilder;
import be.ac.ulb.infof307.g03.models.User;
import be.ac.ulb.infof307.g03.models.Project;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDaoTest extends BeforeAllTests {
    private static User fred;
    private static Project project;

    @BeforeAll
    public static void oneTimeSetUp() throws DaoException {
        try {
            project = new ProjectBuilder()
                    .name("name")
                    .dateEnd(LocalDate.now())
                    .build();
            project.save();
            int projectId = project.getId();
            String insertUserProject1 ="INSERT OR IGNORE INTO userProject VALUES(1,"+projectId+",NULL, true);";
            String insertUserProject2 ="INSERT OR IGNORE INTO userProject VALUES(2,"+projectId+",NULL, true);";
            Database.getPreparedStatement(insertUserProject1).executeUpdate();
            Database.getPreparedStatement(insertUserProject2).executeUpdate();
            Database.commitStatement();
        }catch(SQLException err) {
            Database.rollbackStatement();
        }
    }

    @Test
    @Order(1)
    void testGetUser() throws DaoException {
        User gianMarco = UserGetterDao.getUserFromDb("GianMarco", "groupe03LESBESTS");
        fred = UserGetterDao.getUserFromDb("Fred", "strongpassword123");
        User jacopo = UserGetterDao.getUserFromDb("Jacopo", "fakepwd");

        assertNotNull(gianMarco);
        assertNotNull(fred);
        assertNull(jacopo);

        jacopo = UserGetterDao.getUserFromDb("Jacopo", "osef");
        assertNotNull(jacopo);
    }

    @Test
    @Order(2)
    void registerUser() {
        try {
            UserSetterDao.registerUser("julien","password", "julien@mail.be","Julien","");
            assertNotNull(UserGetterDao.getUserFromDb("julien","password"));
            assertThrows(DaoException.class,() -> UserSetterDao.registerUser("julien","password", "juju@mail.be","Julien",""));
            assertThrows(DaoException.class,() -> UserSetterDao.registerUser("juju","password", "julien@mail.be","Julien",""));
        } catch (DaoException e) {
            e.printStackTrace();
        }
        try {
            Database.getPreparedStatement("DELETE FROM user WHERE username='julien'").executeUpdate();
            Database.commitStatement();
            assertNull(UserGetterDao.getUserFromDb("julien","password"));
        } catch (SQLException | DaoException e) {
            try {
                Database.rollbackStatement();
            } catch (DaoException daoException) {
                daoException.printStackTrace();
            }
            e.printStackTrace();
        }
    }
    
    @Test
    @Order(3)
    void checkLoginInfo() throws DaoException {
        assertNull(UserGetterDao.getUserFromDb("fakeUser", "fakePassword"));
        assertNull(UserGetterDao.getUserFromDb("GianMarco", "fakePassword"));
        assertNotNull(UserGetterDao.getUserFromDb("Fred","strongpassword123"));
    }

    @Test
    @Order(4)
    void setUserConnection() {
        try {
            UserSetterDao.setUserConnection(fred.getId(), true);
            assertEquals(1,UserGetterDao.checkIfConnected());
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(5)
    void setUserDisconnected() {
        try {
            UserSetterDao.setUserConnection(fred.getId(), false);
            assertEquals(0, UserGetterDao.checkIfConnected());
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(6)
    void getUserByProject() {
        try{
        List<User> list;
        list =UserGetterDao.getUserByProject(project.getId());
        assertEquals(2,list.size());}
        catch (DaoException e) {
            e.printStackTrace();
        }

    }

    @Test
    @Order(7)
    void getIdFromUsername() {
        try {
            int userId = UserGetterDao.getIdFromUsername("Fred");
            int userId2= UserGetterDao.getIdFromUsername("rien");
            assertEquals(1,userId);
            assertEquals(-1,userId2);
        } catch (DaoException e) {
            e.printStackTrace();
        }

    }


    @Test
    @Order(8)
    void isUserInDB() {
        try {
            boolean user = UserGetterDao.isUserInDB("Fred");
            boolean user2 = UserGetterDao.isUserInDB("Maxou");
            assertTrue(user);
            assertFalse(user2);
        } catch (DaoException e) {
            e.printStackTrace();
        }

    }

    @Test
    @Order(9)
    void isEmailInDB() {
        try {
            boolean email = UserGetterDao.isEmailInDB("e@e.be");
            boolean email1 = UserGetterDao.isEmailInDB("a@a.ba");
            assertTrue(email);
            assertFalse(email1);
        } catch (DaoException e) {
            e.printStackTrace();
        }

    }

    @AfterAll
    static void tearDown(){
        try {
            project.delete();
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

}
