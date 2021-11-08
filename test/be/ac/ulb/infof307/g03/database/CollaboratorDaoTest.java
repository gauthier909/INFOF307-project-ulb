package be.ac.ulb.infof307.g03.database;

import be.ac.ulb.infof307.g03.BeforeAllTests;
import be.ac.ulb.infof307.g03.database.dao.CollaboratorDao;
import be.ac.ulb.infof307.g03.database.dao.user.UserGetterDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.ProjectBuilder;
import be.ac.ulb.infof307.g03.models.User;
import be.ac.ulb.infof307.g03.models.Project;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CollaboratorDaoTest extends BeforeAllTests {
    private static User gian;
    private static User jaco;
    private static Project project;

    @BeforeAll
    static void setUp() throws DaoException {
        gian = UserGetterDao.getUserFromDb("GianMarco", "groupe03LESBESTS");
        jaco = UserGetterDao.getUserFromDb("Jacopo", "osef");
        project = new ProjectBuilder().id(0).name("G3").description("test").dateEnd(LocalDate.now()).build();
        project.save();
    }

    @Test
    @Order(1)
    void insertCollaborator() {
        assertDoesNotThrow(() -> CollaboratorDao.insertCollaborator(gian.getId(), project.getId()));
        assertDoesNotThrow(() -> CollaboratorDao.insertCollaborator(jaco.getId(), project.getId()));
        assertThrows(DaoException.class, () -> CollaboratorDao.insertCollaborator(999999999,999999999));
    }

    @Test
    @Order(2)
    void getAllCollaborators() {
        try {
            ArrayList<User> collaborators = CollaboratorDao.getAllCollaborators(project.getId(), Helper.getCurrentUser().getId());
            assertEquals(2, collaborators.size());
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(3)
    void removeCollaborator() {
        try {
            CollaboratorDao.removeCollaborator(jaco.getId(), project.getId());
            assertEquals(1, CollaboratorDao.getAllCollaborators(project.getId(), Helper.getCurrentUser().getId()).size());
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(4)
    void isCollaborator() {
        try {
            assertTrue(CollaboratorDao.isCollaborator(gian.getId(), project.getId()));
            assertFalse(CollaboratorDao.isCollaborator(jaco.getId(), project.getId()));
            assertTrue(CollaboratorDao.isCollaborator(Helper.getCurrentUser().getId(), project.getId()));
        } catch (DaoException e) {
            e.printStackTrace();
        }
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