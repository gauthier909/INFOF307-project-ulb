package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.BeforeAllTests;
import be.ac.ulb.infof307.g03.database.dao.user.UserGetterDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.ProjectBuilder;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.Task;
import be.ac.ulb.infof307.g03.models.User;
import be.ac.ulb.infof307.g03.viewsControllers.AssignTaskViewController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;


import static org.junit.jupiter.api.Assertions.*;

class AssignTaskControllerTest extends BeforeAllTests {


    private static AssignTaskController assignTaskController;
    private static User assignedUser;
    private static Project project;
    private static Task task;

    @BeforeAll
    static void setup() throws DaoException {
        TaskController taskController = mock(TaskController.class);
        AssignTaskViewController assignTaskViewController = mock(AssignTaskViewController.class);

        User fred = UserGetterDao.getUserFromDb("Fred", "strongpassword123");

        Helper.setCurrentUser(fred);

        project = new ProjectBuilder().name("Projet1").description("MY_PROJECT").dateEnd(LocalDate.now()).build();
        project.insert();

        task = new Task("tache1", project.getId(), LocalDate.now(), LocalDate.now(), false);
        task.insert();

        assignTaskController = new AssignTaskController(taskController, task);
        assignTaskController.setAssignTaskViewController(assignTaskViewController);
        assignTaskController.setup();

        assignedUser = UserGetterDao.getUserFromDb("GianMarco", "groupe03LESBESTS");

        project.addTask(task);
        project.update();
    }

    @AfterAll
    static void teardown() throws DaoException {
        project.delete();
        Helper.setCurrentUser(null);
    }


    @Test
    @Order(1)
    void onAssignTask() throws DaoException {
        assignTaskController.onAssignTask(assignedUser);
        assertFalse(task.getAllUserByTaskId().isEmpty());
    }

    @Test
    @Order(2)
    void deleteUser() throws DaoException {
        assignTaskController.deleteUser(assignedUser);
        assertTrue(task.getAllUserByTaskId().isEmpty());
    }
}