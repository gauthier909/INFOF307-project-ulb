package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.BeforeAllTests;
import be.ac.ulb.infof307.g03.database.dao.project.ProjectDaoGetter;
import be.ac.ulb.infof307.g03.database.dao.project.ProjectDaoSetter;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.models.ProjectBuilder;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.Reminder;
import be.ac.ulb.infof307.g03.models.Task;
import be.ac.ulb.infof307.g03.viewsControllers.EditProjectViewController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;


import static org.junit.jupiter.api.Assertions.*;

class EditProjectControllerTest extends BeforeAllTests {

    private static Project project;
    private static Task task;
    private static EditProjectController controller;

    @BeforeAll
    static void setup() {
        try {
            HomeController homeController = mock(HomeController.class);
            EditProjectViewController editProfileViewController = mock(EditProjectViewController.class);
            project = new ProjectBuilder().name("Test").description("Test").dateEnd(LocalDate.of(2022,12,31)).build();
            project.insert();

            task = new Task("Task 1", project.getId(), LocalDate.now(), LocalDate.now(), false);
            task.insert();
            project.addTask(task);

            Reminder reminder = new Reminder(project.getId(), task.getId(), LocalDate.now());

            controller = new EditProjectController(homeController, EditProjectController.State.CREATE, project, reminder);
            controller.setViewController(editProfileViewController);
            controller.setup();

        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void teardown() {
        try {
            project.delete();
            task.delete();
        }catch (DaoException e){
            e.printStackTrace();
        }
    }


    @Test
    @Order(1)
    void checkData() {
        assertFalse(controller.checkData(project.getName(), project.getDescription(), project.getDateEnd()));

        assertTrue(controller.checkData(null, project.getDescription(), project.getDateEnd()));
        project.setName("");
        assertTrue(controller.checkData(project.getName(), project.getDescription(), project.getDateEnd()));
        project.setName("Test");

        assertTrue(controller.checkData(project.getName(), project.getDescription(), null));
        project.setDateEnd(LocalDate.of(1990, 12, 12));
        assertTrue(controller.checkData(project.getName(), project.getDescription(), project.getDateEnd()));
        project.setDateEnd(LocalDate.now());

        assertTrue(controller.checkData(project.getName(), null, project.getDateEnd()));
        project.setDescription("");
        assertTrue(controller.checkData(project.getName(), project.getDescription(), project.getDateEnd()));
        project.setDescription("Test");
    }

    @Test
    @Order(2)
    void createProject() {
        try {
            controller.createProject("New Project", "Other Desc", project.getDateEnd(), 1, false);
            int insertedId1 = ProjectDaoGetter.checkExistingProject("New Project", project.getDateEnd());
            assertEquals(project.getId() + 1, insertedId1);

            controller.createProject("Another New Project", "Another Desc", project.getDateEnd(), 1, false);
            int insertedId2 = ProjectDaoGetter.checkExistingProject("Another New Project", project.getDateEnd());
            assertEquals(project.getId() + 2, insertedId2);

            ProjectDaoSetter.delete(insertedId1);
            ProjectDaoSetter.delete(insertedId2);

        }catch (NavigationException | DaoException e){
            e.printStackTrace();
        }
    }

    @Test
    @Order(3)
    void updateProject()  {
        try {
            controller.updateProject("TEST UPDATE", project.getDescription(), project.getDateEnd(), 1);
            Project updated = ProjectDaoGetter.getProjectById(project.getId());

            assert updated != null;
            assertEquals(updated.getName(), project.getName());
            assertEquals(updated.getDescription(), project.getDescription());
            assertEquals(updated.getDateEnd(), project.getDateEnd());

        } catch (NavigationException | DaoException e) {
            e.printStackTrace();
        }
    }
}