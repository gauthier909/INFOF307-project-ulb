package be.ac.ulb.infof307.g03.database;

import be.ac.ulb.infof307.g03.BeforeAllTests;
import be.ac.ulb.infof307.g03.database.dao.VersionDao;
import be.ac.ulb.infof307.g03.database.dao.project.ProjectDaoGetter;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.models.ProjectBuilder;
import be.ac.ulb.infof307.g03.models.Version;
import org.junit.jupiter.api.*;
import be.ac.ulb.infof307.g03.models.Project;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VersionDaoTest extends BeforeAllTests {

    private static Project project;

    @BeforeAll
    static void setUp(){
        project = new ProjectBuilder().name("test").description("test").dateEnd(LocalDate.now()).build();
        try {
            project.save();
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(1)
    void initGeet() {
        try{
            project.initGeet();
            Project test = ProjectDaoGetter.getProjectById(project.getId());
            assert test != null;
            assertEquals(true, test.getGeetActivate());
        }catch(DaoException e){
            e.printStackTrace();
        }
    }

    @Test
    @Order(2)
    void commit() {
        try {
            Version version = project.commit("Test");
            project.getCurrentBranch().setVersion(version);
            assertEquals(1, project.getCurrentBranch().getAllVersions().size());
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(3)
    void getVersionsFromBranch() {
        try {
            assertEquals(1, VersionDao.getVersionsFromBranch(project.getCurrentBranch()).size());
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(4)
    void getVersionById() {
        try {
            Version res = VersionDao.getVersionById(project.getCurrentBranch().getVersion().getId());
            assert res != null;
            assertEquals(project.getCurrentBranch().getVersion().getId(), res.getId());
            assertNull(VersionDao.getVersionById(99999));
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(5)
    void addVersion() {
        Version version = new Version(2, 0, "test", project.getCurrentBranch().getId(), project.getId());
        try {
            VersionDao.addVersion(version, project.getCurrentBranch());
            int res = project.getCurrentBranch().getAllVersions().size();
            assertEquals(2, res);
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(6)
    void deleteVersion() {
        int id = project.getCurrentBranch().getVersion().getId();
        try {
            VersionDao.deleteVersion(id);
            assertNull(VersionDao.getVersionById(id));
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