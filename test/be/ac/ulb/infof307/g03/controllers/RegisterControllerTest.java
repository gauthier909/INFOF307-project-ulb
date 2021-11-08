package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.BeforeAllTests;
import be.ac.ulb.infof307.g03.database.dao.user.UserGetterDao;
import be.ac.ulb.infof307.g03.database.dao.user.UserSetterDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.models.User;
import be.ac.ulb.infof307.g03.viewsControllers.RegisterViewController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

import static org.junit.jupiter.api.Assertions.*;

class RegisterControllerTest extends BeforeAllTests {

    private static  RegisterController controller;
    private static  User userAlreadyInDB;


    @BeforeAll
    static void setup(){
        try {
            WelcomeController welcomeController = mock(WelcomeController.class);
            RegisterViewController registerViewController = mock(RegisterViewController.class);
            controller = new RegisterController(welcomeController);
            controller.setViewController(registerViewController);

            userAlreadyInDB = new User("DbMan", "Testfn", "Testln", "test@test.be");
            userAlreadyInDB.register("My-Pwd");
            userAlreadyInDB = UserGetterDao.getUserFromDb("DbMan", "My-Pwd");

        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void teardown(){
        try {
            UserSetterDao.deleteUser(userAlreadyInDB.getUsername());
            UserSetterDao.deleteUser("Another register");
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    void register()  {
        controller.register(userAlreadyInDB.getUsername(),
                userAlreadyInDB.getFirstName(),
                userAlreadyInDB.getLastName(),
                userAlreadyInDB.getEmail(),
                "Other-pwd",
                "Other-pwd");

        try {
            assertTrue(UserGetterDao.isEmailInDB(userAlreadyInDB.getEmail()));
            assertTrue(UserGetterDao.isUserInDB(userAlreadyInDB.getUsername()));
            assertEquals(userAlreadyInDB.getId(), UserGetterDao.getIdFromUsername(userAlreadyInDB.getUsername()));
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }
}