package be.ac.ulb.infof307.g03.database;

import be.ac.ulb.infof307.g03.BeforeAllTests;
import be.ac.ulb.infof307.g03.database.dao.TaskDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.models.ProjectBuilder;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.Task;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskDaoTest extends BeforeAllTests {
    private static Project project;
    private static Task task;
    private static int insertedId;


    @BeforeAll
    static void setUp(){
        project = new ProjectBuilder().name("test").description("test").dateEnd(LocalDate.now()).build();
        try {
            project.save();
            task = new Task("description", project.getId(), LocalDate.now(), LocalDate.now().plusDays(1), false);
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(1)
    void insert() throws DaoException {
        insertedId = TaskDao.insert(task);
        task.setId(insertedId);
        assertNotNull(TaskDao.getById(insertedId));
        assertThrows(NullPointerException.class,() -> TaskDao.insert(null));
    }

    @Test
    @Order(2)
    void complete() throws DaoException{
        task.complete();
        assertTrue(task.isDone());
    }


    @Test
    @Order(4)
    void delete() throws  DaoException{
        assertTrue(TaskDao.delete(insertedId));
        assertFalse(TaskDao.delete(-14));
    }

    @Test
    @Order(3)
    void updateDates() throws DaoException{
        LocalDate beforeChangeStartDate = task.getStartDate();
        LocalDate beforeChangeEndDate = task.getEndDate();
        task.setStartDate(LocalDate.now().plusDays(1));
        task.setEndDate(LocalDate.now().plusDays(3));
        task.updateDates();
        task = TaskDao.getById(task.getId());
        assertEquals(1, task.getStartDate().compareTo(beforeChangeStartDate));
        assertEquals(2, task.getEndDate().compareTo(beforeChangeEndDate));
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
