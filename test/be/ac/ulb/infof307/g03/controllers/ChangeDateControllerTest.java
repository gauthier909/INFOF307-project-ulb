package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.BeforeAllTests;
import be.ac.ulb.infof307.g03.database.dao.user.UserGetterDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.ProjectBuilder;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.Task;
import be.ac.ulb.infof307.g03.models.User;
import be.ac.ulb.infof307.g03.viewsControllers.ChangeDateViewController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ChangeDateControllerTest extends BeforeAllTests {

    private static ChangeDateController changeDateController;
    private static Project project;
    private static Task task;

    private static LocalDate start2;
    private static LocalDate end2;

    @BeforeAll
    static void setup() throws DaoException {

        User fred = UserGetterDao.getUserFromDb("Fred", "strongpassword123");

        Helper.setCurrentUser(fred);

        project = new ProjectBuilder().name("Projet1").description("MY_PROJECT").dateEnd(LocalDate.now()).build();
        project.insert();

        LocalDate start1 = LocalDate.of(2020, 10, 12);
        LocalDate end1 = LocalDate.of(2020, 10, 14);

        start2 =  LocalDate.of(2020, 10, 12);
        end2 =  LocalDate.of(2020, 10, 12);

        task = new Task("tache1", project.getId(), start1, end1, false);
        task.insert();

        ChangeDateViewController changeDateViewController = mock(ChangeDateViewController.class);
        TaskController taskController = mock(TaskController.class);

        changeDateController = new ChangeDateController(taskController, task, LocalDate.now());
        changeDateController.setViewController(changeDateViewController);
    }

    @AfterAll
    static void teardown() throws DaoException {
        project.delete();
        Helper.setCurrentUser(null);
    }

    @Test
    void onChangeDate() {
        changeDateController.onChangeDate(start2, end2);
        assertEquals(task.getEndDate().compareTo(start2), 0);
        assertEquals(task.getEndDate().compareTo(end2), 0);

    }
}