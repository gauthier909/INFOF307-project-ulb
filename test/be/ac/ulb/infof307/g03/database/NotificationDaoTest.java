package be.ac.ulb.infof307.g03.database;

import be.ac.ulb.infof307.g03.BeforeAllTests;
import be.ac.ulb.infof307.g03.database.dao.NotificationDao;
import be.ac.ulb.infof307.g03.database.dao.user.UserGetterDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.ProjectBuilder;
import be.ac.ulb.infof307.g03.models.Notification;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.User;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NotificationDaoTest extends BeforeAllTests {
    private static User gian;
    private static User jaco;
    private static Project project;

    @BeforeAll
    static void setUp() throws DaoException {
        gian = UserGetterDao.getUserFromDb("GianMarco", "groupe03LESBESTS");
        jaco = UserGetterDao.getUserFromDb("Jacopo", "osef");
        project = new ProjectBuilder().id(0).name("G3").description("test").dateEnd(LocalDate.now()).build();
        project.save();
    }

    @Test
    @Order(1)
    void createNotification() {
        assertThrows(DaoException.class, () -> NotificationDao.createNotification(99999, gian, project, Notification.NotificationType.ASK_COLLAB));
        try {
            NotificationDao.createNotification(Helper.getCurrentUser().getId(), gian, project, Notification.NotificationType.ASK_COLLAB);
        } catch (DaoException e) {
            e.printStackTrace();
        }

    }

    @Test
    @Order(3)
    void getAllNotification() {
        try {
            assertEquals(1, NotificationDao.getAllNotification(Helper.getCurrentUser().getId()).size());
            assertEquals(new ArrayList<>(), NotificationDao.getAllNotification(99999)); // empty list is returned for non existing user
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(2)
    void isNotificationPending() throws DaoException {
        assertTrue(NotificationDao.isNotificationPending(gian.getId(), Helper.getCurrentUser().getId(), project.getId()));
        assertFalse(NotificationDao.isNotificationPending(gian.getId(), jaco.getId(), project.getId()));
    }

    @Test
    @Order(4)
    void deleteNotification() {
        try {
            NotificationDao.deleteNotification(NotificationDao.getAllNotification(Helper.getCurrentUser().getId()).get(0));
            assertEquals(0, NotificationDao.getAllNotification(Helper.getCurrentUser().getId()).size());
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