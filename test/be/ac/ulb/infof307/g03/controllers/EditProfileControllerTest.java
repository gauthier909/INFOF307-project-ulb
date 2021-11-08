package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.BeforeAllTests;
import be.ac.ulb.infof307.g03.database.dao.user.UserGetterDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.User;
import be.ac.ulb.infof307.g03.viewsControllers.EditProfileViewController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;


class EditProfileControllerTest extends BeforeAllTests {

    private static EditProfileController controller;

    @BeforeAll
    static void setup() throws DaoException {
        HomeController homeController = mock(HomeController.class);
        EditProfileViewController editProfileViewController = mock(EditProfileViewController.class);

        controller = new EditProfileController(homeController);
        controller.setEditProfilViewController(editProfileViewController);

        User user = UserGetterDao.getUserFromDb("GianMarco", "groupe03LESBESTS");
        user.setFirstName("Gian");
        user.setLastName("Marco");
        Helper.setCurrentUser(user);
    }

    @AfterAll
    static void teardown(){
        Helper.setCurrentUser(null);
    }


    @Test
    @Order(1)
    void confirm() throws DaoException {
        User toUpdate = Helper.getCurrentUser();
        controller.confirm(toUpdate.getUsername(), "groupe03LESBESTS", "groupe03LESBESTS", toUpdate.getEmail(), toUpdate.getFirstName(), toUpdate.getLastName());
        User updated = UserGetterDao.getById(toUpdate.getId());
        assertEquals(updated, toUpdate);
    }

    @Test
    @Order(2)
    void confirmWrongUsername() throws DaoException {
        User toUpdate = Helper.getCurrentUser();
        controller.confirm("Fred", "groupe03LESBESTS", "groupe03LESBESTS", toUpdate.getEmail(), toUpdate.getFirstName(), toUpdate.getLastName());
        User notUpdated = UserGetterDao.getById(toUpdate.getId());
        assertEquals(notUpdated, toUpdate);
    }

    @Test
    @Order(3)
    void confirmWrongEmail() throws DaoException {
        User toUpdate = Helper.getCurrentUser();
        User fred = UserGetterDao.getUserFromDb("Fred", "strongpassword123");
        controller.confirm(toUpdate.getUsername(), "groupe03LESBESTS", "groupe03LESBESTS", fred.getEmail(), toUpdate.getFirstName(), toUpdate.getLastName());
        User notUpdated = UserGetterDao.getById(toUpdate.getId());
        assertEquals(notUpdated, toUpdate);
    }

    @Test
    @Order(4)
    void confirmModifyFields() throws  DaoException {
        User user = new User("updated", "updated@gmail.com");
        User toUpdate = Helper.getCurrentUser();
        controller.confirm(user.getUsername(), "groupe03LESBESTS", "groupe03LESBESTS", user.getEmail(), toUpdate.getFirstName(), toUpdate.getLastName());
        User updated = UserGetterDao.getById(toUpdate.getId());
        assertEquals(user.getUsername(), updated.getUsername());
        assertEquals(user.getEmail(), updated.getEmail());
        controller.confirm("GianMarco", "groupe03LESBESTS", "groupe03LESBESTS", "email@gmail.com", toUpdate.getFirstName(), toUpdate.getLastName());
    }
}