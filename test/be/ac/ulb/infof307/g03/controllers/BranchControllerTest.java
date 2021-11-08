package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.BeforeAllTests;
import be.ac.ulb.infof307.g03.database.dao.user.UserGetterDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.*;
import be.ac.ulb.infof307.g03.models.Tag;
import be.ac.ulb.infof307.g03.viewsControllers.BranchViewController;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BranchControllerTest extends BeforeAllTests {
    private static Project project;
    private static BranchController branchController;
    private static Branch branch1;
    private static Branch branch2;

    @BeforeAll
    public static void oneTimeSetup() throws DaoException, IOException {
        User gianMarco = UserGetterDao.getUserFromDb("GianMarco", "groupe03LESBESTS");
        Helper.setCurrentUser(gianMarco);
        project = new ProjectBuilder().name("test").description("test").dateEnd(LocalDate.now().plusDays(2)).startDate(LocalDate.now()).build();
        project.insert();
        project.initGeet();
        project.getCurrentBranch().setVersion(project.commit("test"));
        File dir = new File(Helper.GEET_TMP);
        if (!dir.exists()) dir.mkdirs();
        Helper.saveProjectToJson(project,Helper.GEET_TMP + project.getCurrentBranch().getBranchName() + "_" + project.getId() + ".json");
        HomeController controller = mock(HomeController.class);
        branchController = new BranchController(controller,project);
        BranchViewController branchViewController = mock(BranchViewController.class);
        branchController.setBranchViewController(branchViewController);
        branchController.setGeetPath("testGeet/");
        branchController.setup();
        branchController.commitProject(project,"test");
    }

    @Test
    @Order(1)
    void onAddBranch() throws DaoException {
        branchController.onAddBranch("branch2");
        assertNotNull(project.getCurrentBranch());
        assertEquals(project.getAllBranches().size(),2);
        List<Branch> branches = project.getAllBranches();
        branch1 = branches.get(0);
        branch2 = branches.get(1);
        assertEquals(branch2.getBranchName(),"branch2");
    }

    @Test
    @Order(2)
    void onCheckoutBranch() {
        branchController.onCheckoutBranch(branch2);
        assertEquals(project.getCurrentBranch().getId(),branch2.getId());
    }

    @Test
    @Order(3)
    void onMergeBranch() throws DaoException, IOException {
        Tag tag = new Tag("test");
        tag.insert();
        project.addTag(tag);
        Task task = new Task("tache1", project.getId(), LocalDate.now(), LocalDate.now(), false);
        task.insert();
        project.addTask(task);
        project.getCurrentBranch().setVersion(project.commit("test"));
        Helper.saveProjectToJson(project,Helper.GEET_TMP + project.getCurrentBranch().getBranchName() + "_" + project.getId() + ".json");
        branchController.commitProject(project,"test");
        branchController.onCheckoutBranch(branch1);
        branchController.onMergeBranch(branch2);
        assertEquals(project.getCurrentBranch().getId(),branch1.getId());
        assertEquals(project.getAllTags().size(),1);
        assertEquals(project.getAllTags().get(0).getName(),"test");
        assertEquals(project.getAllTasks().size(),1);
        assertEquals(project.getAllTasks().get(0).getId(), task.getId());
    }

    @Test
    @Order(4)
    void onDeleteBranch() throws DaoException {
        branchController.onDeleteBranch(branch2);
        assertEquals(project.getAllBranches().size(),1);
        assertEquals(project.getAllBranches().get(0).getId(),branch1.getId());
    }

    @AfterAll
    public static void oneTimeTeardown() throws DaoException {
        project.delete();
        Helper.setCurrentUser(null);
    }
}