package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.BeforeAllTests;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.viewsControllers.LoginViewController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;


class LoginControllerTest extends BeforeAllTests {
    private static LoginController loginController;

    @BeforeAll
    public static void oneTimeSetup() {
        WelcomeController welcomeController = mock(WelcomeController.class);
        loginController = new LoginController(welcomeController);
        LoginViewController loginViewController = mock(LoginViewController.class);
        loginController.setLoginViewController(loginViewController);
    }

    @Test
    void login() {
        try {
            assertTrue(loginController.login("Fred","strongpassword123"));
        } catch (DaoException e) {
            e.printStackTrace();
        }
        assertNotNull(Helper.getCurrentUser());
    }

    @AfterAll
    public static void Teardown() throws DaoException {
        Helper.getCurrentUser().logout();
        Helper.setCurrentUser(null);
    }
}
