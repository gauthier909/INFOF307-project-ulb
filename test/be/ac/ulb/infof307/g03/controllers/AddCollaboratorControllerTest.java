package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.BeforeAllTests;
import be.ac.ulb.infof307.g03.database.dao.user.UserGetterDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.ProjectBuilder;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.User;
import be.ac.ulb.infof307.g03.viewsControllers.AddCollaboratorViewController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AddCollaboratorControllerTest extends BeforeAllTests {

    private static Project project;
    private static AddCollaboratorController addCollaboratorController;

    @BeforeAll
    public static void oneTimeSetup() throws DaoException {
        User gianMarco = UserGetterDao.getUserFromDb("GianMarco", "groupe03LESBESTS");
        Helper.setCurrentUser(gianMarco);
        project = new ProjectBuilder().name("test").description("test").dateEnd(LocalDate.now().plusDays(2)).startDate(LocalDate.now()).build();
        project.insert();

        CollaboratorManagementController controller = mock(CollaboratorManagementController.class);
        addCollaboratorController = new AddCollaboratorController(controller,project);
        AddCollaboratorViewController addCollaboratorViewController = mock(AddCollaboratorViewController.class);
        addCollaboratorController.setAddCollaboratorViewController(addCollaboratorViewController);
    }


    @Test
    void onAskCollaborator() throws DaoException {
        User fred = UserGetterDao.getUserFromDb("Fred", "strongpassword123");
        addCollaboratorController.onAskCollaborator(fred.getUsername());
        assertNotNull(fred.getAllNotification());
    }

    @AfterAll
    public static void oneTimeTeardown() throws DaoException {
        project.delete();
        Helper.setCurrentUser(null);
    }
}