package be.ac.ulb.infof307.g03.database;

import be.ac.ulb.infof307.g03.BeforeAllTests;
import be.ac.ulb.infof307.g03.database.dao.ReminderDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.ProjectBuilder;
import be.ac.ulb.infof307.g03.models.Reminder;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReminderDaoTest extends BeforeAllTests {
    private static Project project1;
    private static Project project2;
    @BeforeAll
    static void setUp(){
        project1 = new ProjectBuilder().name("test").description("test").dateEnd(LocalDate.now()).build();
        project2 = new ProjectBuilder().name("test").description("test").dateEnd(LocalDate.now()).build();
        try {
            project1.save();
            project2.save();
        } catch (DaoException e) {
            e.printStackTrace();
        }

    }

    @Test
    @Order(1)
    void insert() {
        Reminder testReminder = new Reminder(project1.getId(),Reminder.NOTATASK,LocalDate.now());
        try {
            ReminderDao.insert(testReminder);
            List<Project> projectList = new ArrayList<>();
            projectList.add(project1);
            assertEquals(1,ReminderDao.getAllReminder(projectList).size());
        } catch (DaoException e) {
            e.printStackTrace();
        }

    }
    @Test
    @Order(2)
    void getByProjectId() {
        try {
            assertNotNull(ReminderDao.getByProjectId(project1.getId()));
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(3)
    void getAllReminder() {
        Reminder testReminder = new Reminder(project2.getId(),Reminder.NOTATASK,LocalDate.now());
        try {
            ReminderDao.insert(testReminder);
            List<Project> projectList = new ArrayList<>();
            projectList.add(project1);
            projectList.add(project2);
            assertEquals(2,ReminderDao.getAllReminder(projectList).size());
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(4)
    void snooze() {
        try {
            List<Project> projectList = new ArrayList<>();
            projectList.add(project1);
            Reminder reminderTest = ReminderDao.getAllReminder(projectList).get(0);
            LocalDate currentDate = reminderTest.getDate();
            ReminderDao.snooze(reminderTest.getId());
            assertEquals(currentDate.plusDays(1), ReminderDao.getAllReminder(projectList).get(0).getDate());
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(5)
    void update() {
        try {
            List<Project> projectList = new ArrayList<>();
            projectList.add(project1);
            Reminder testReminder = ReminderDao.getAllReminder(projectList).get(0);
            LocalDate currentDate = testReminder.getDate();
            currentDate = currentDate.plusDays(4);
            testReminder.setDate(currentDate);
            ReminderDao.update(testReminder);
            assertEquals(currentDate, ReminderDao.getAllReminder(projectList).get(0).getDate());
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }



    @AfterAll
    static void tearDown(){
        try {
            project1.delete();
            project2.delete();
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }
}