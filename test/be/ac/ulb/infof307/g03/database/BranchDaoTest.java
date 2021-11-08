package be.ac.ulb.infof307.g03.database;

import be.ac.ulb.infof307.g03.BeforeAllTests;
import be.ac.ulb.infof307.g03.database.dao.BranchDao;
import be.ac.ulb.infof307.g03.database.dao.VersionDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.models.Branch;
import be.ac.ulb.infof307.g03.models.ProjectBuilder;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.Version;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BranchDaoTest extends BeforeAllTests {

    private static Project project;
    private static Branch branch;

    @BeforeAll
    static void setUp(){
        project = new ProjectBuilder().name("test").description("test").dateEnd(LocalDate.now()).build();
        try {
            project.save();
            branch = new Branch("saxo",project.getId());
            project.initGeet();
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(1)
    void createBranch() {
        try {
            int res = BranchDao.createBranch(branch);
            branch.setId(res);
            assertNotNull(BranchDao.getBranchById(branch.getId()));
            assertThrows(DaoException.class, () -> BranchDao.createBranch(new Branch("guitare",999)));
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(2)
    void getBranchById() {
        try {
            assertEquals("saxo", BranchDao.getBranchById(branch.getId()).getBranchName());
            assertNull(BranchDao.getBranchById(999999999));
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(3)
    void getAllProjectBranches() {
        try {
            assertEquals(2,BranchDao.getAllProjectBranches(project.getId()).size());
            assertEquals(0,BranchDao.getAllProjectBranches(99999).size());
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateVersion(){
        try {
            VersionDao.commit(project,"test1");
            VersionDao.commit(project,"test2");
            List<Version> versions = VersionDao.getVersionsFromBranch(project.getCurrentBranch());
            assertEquals(versions.get(0).getCommitMessage(),"test2");
            BranchDao.updateVersion(project.getCurrentBranch(),versions.get(1));
            Branch testBranch = BranchDao.getBranchById(project.getCurrentBranch().getId());
            assertEquals(testBranch.getVersion().getId(),versions.get(1).getId());
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }



    @Test
    @Order(4)
    void removeBranch() {
        try {
            BranchDao.removeBranch(branch.getId());
            assertEquals(1, BranchDao.getAllProjectBranches(project.getId()).size());
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