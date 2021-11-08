package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.BeforeAllTests;
import be.ac.ulb.infof307.g03.database.dao.BranchDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.Branch;
import be.ac.ulb.infof307.g03.models.ProjectBuilder;
import be.ac.ulb.infof307.g03.models.Project;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class HomeControllerTest extends BeforeAllTests {
    private static Project project;
    private static Branch branch;
    private static HomeController homeController;
    private final static LocalDate date = LocalDate.now();

    @BeforeAll
    static void beforeAll(){
        try {
            project = new ProjectBuilder().name("name").description("description").dateEnd(date).build();
            project.save();
            branch = new Branch(1, "branchName", project.getId());
        } catch (DaoException e) {
            e.printStackTrace();
        }
        homeController = new HomeController();
    }

    @AfterAll
    static void afterAll(){
        try {
            project.delete();
            BranchDao.removeBranch(branch.getId());
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getBranchByProjectId(){
        try {
            project.setGeetActivate(true);
            project.setCurrentBranch(branch);
            BranchDao.createBranch(branch);
            project.update();
            BranchDao.switchBranch(Helper.getCurrentUser(), branch);
            assertEquals(branch.getBranchName(), homeController.getBranchByProjectId(project).getBranchName());
            assertEquals(branch.getId(), homeController.getBranchByProjectId(project).getId());
            assertEquals(branch.getProjectId(), homeController.getBranchByProjectId(project).getProjectId());
        } catch (DaoException exception) {
            exception.printStackTrace();
        }
    }
}