package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.BeforeAllTests;
import be.ac.ulb.infof307.g03.database.dao.user.UserGetterDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.ProjectBuilder;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.User;
import be.ac.ulb.infof307.g03.viewsControllers.CollaboratorManagementViewController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CollaboratorManagementControllerTest extends BeforeAllTests {

    private static CollaboratorManagementController collaboratorManagementController;
    private static Project project;
    private static User collaborator;
    private static User fred;

    @BeforeAll
    static void setup() throws DaoException {

        fred = UserGetterDao.getUserFromDb("Fred", "strongpassword123");

        Helper.setCurrentUser(fred);

        // Fred insert the current project
        project = new ProjectBuilder().name("Projet1").description("MY_PROJECT").dateEnd(LocalDate.now()).build();
        project.insert();

        collaborator = UserGetterDao.getUserFromDb("GianMarco", "groupe03LESBESTS");

        project.insertCollaborator(collaborator.getId());

        HomeController homeController = mock(HomeController.class);
        CollaboratorManagementViewController collaboratorManagementViewController = mock(CollaboratorManagementViewController.class);
        collaboratorManagementController = new CollaboratorManagementController(homeController, project);
        collaboratorManagementController.setViewController(collaboratorManagementViewController);
        collaboratorManagementController.setup();
    }

    @AfterAll
    static void teardown() throws DaoException {
        project.delete();
        Helper.setCurrentUser(null);
    }


    @Test
    void deleteCollaborator() throws DaoException {
        assertEquals(2, project.getAllCollaborators().size());
        collaboratorManagementController.deleteCollaborator(collaborator);
        assertEquals(1, project.getAllCollaborators().size());
        assertEquals(fred.getId(), project.getAllCollaborators().get(0).getId());
    }
}