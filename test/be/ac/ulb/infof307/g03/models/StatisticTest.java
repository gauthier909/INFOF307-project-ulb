package be.ac.ulb.infof307.g03.models;

import be.ac.ulb.infof307.g03.BeforeAllTests;
import be.ac.ulb.infof307.g03.database.dao.CollaboratorDao;
import be.ac.ulb.infof307.g03.database.dao.user.UserGetterDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.helper.Helper;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * We only test getters but this makes sense because all getters refers to attribute set by private methods in the class constructor
 */
class StatisticTest extends BeforeAllTests {
    private static Project project1;
    private static Project project2;
    private static Statistic stats;

    @BeforeAll
    public static void oneTimeSetup() {

        project1 = new ProjectBuilder().name("test").description("test").dateEnd(LocalDate.now().plusDays(2)).startDate(LocalDate.now()).build();
        project2 = new ProjectBuilder().name("Test project").description("Project created in tests").dateEnd(LocalDate.now().plusDays(2)).startDate(LocalDate.now()).build();
        try {
            User gian = UserGetterDao.getUserFromDb("GianMarco", "groupe03LESBESTS");
            project1.save();
            project2.save();

            Task task1 = new Task("test task1", project1.getId(), LocalDate.now(), LocalDate.now().plusDays(1), true);
            Task task2 = new Task("test task2", project1.getId(), LocalDate.now(), LocalDate.now().plusDays(1), false);
            Task task3 = new Task("test task3", project2.getId(), LocalDate.now(), LocalDate.now().plusDays(1), false);

            task1.insert();
            task2.insert();
            task3.insert();

            ArrayList<Task> taskListProject1 = new ArrayList<>();

            taskListProject1.add(task1);
            taskListProject1.add(task2);

            ArrayList<Task> taskListProject2 = new ArrayList<>();
            taskListProject2.add(task3);

            project1.setTasks(taskListProject1);
            project2.setTasks(taskListProject2);

            gian.insertAssignedTask(task1);
            Helper.getCurrentUser().insertAssignedTask(task3);

            CollaboratorDao.insertCollaborator(gian.getId(), project1.getId());
            CollaboratorDao.insertCollaborator(gian.getId(), project2.getId());

            List<Project> projectList = new ArrayList<>();
            projectList.add(project1);
            projectList.add(project2);
            stats = new Statistic(projectList);
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

    @Test
    public void getEstimatedTime(){
        assertEquals("2 days", stats.getEstimatedTime());
    }

    @Test
    public void getRealTime(){
        assertEquals("0 / 2 days", stats.getRealTime());
    }

    @Test
    public void getNumberOfAssignedCollaborators(){
        assertEquals(2, stats.getNumberOfAssignedCollaborators());
    }

    @Test
    public void getNumberOfCollaborator(){
        assertEquals(4, stats.getNumberOfCollaborator()); //if you're in 2 projects you count for 2 collaborator
    }
    @Test
    public void getNumberOfTasks(){
        assertEquals(3, stats.getNumberOfTasks());
    }
    @Test
    public void getNumberOfAssignedTasks(){
        assertEquals(2, stats.getNumberOfAssignedTasks());

    }
    @Test
    public void getNumberOfNonAssignedTasks(){
        assertEquals(1, stats.getNumberOfNonAssignedTasks());
    }

    @Test
    public void getNumberOfTaskLeft(){
        assertEquals(2, stats.getNumberOfTaskLeft());
    }
    @Test
    public void getNumberOfCollabPerTask(){
        Map<Integer, Integer> map = new HashMap<>();
        map.put(1,1);
        map.put(2,0);
        map.put(3,1);
        assertEquals(map, stats.getNumberOfCollabPerTask());
    }



}