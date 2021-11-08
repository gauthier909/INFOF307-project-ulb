package be.ac.ulb.infof307.g03.database;

import be.ac.ulb.infof307.g03.BeforeAllTests;
import be.ac.ulb.infof307.g03.database.dao.project.ProjectDaoGetter;
import be.ac.ulb.infof307.g03.database.dao.project.ProjectDaoSetter;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.ProjectBuilder;
import be.ac.ulb.infof307.g03.models.Project;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProjectDaoTest extends BeforeAllTests{

    private static Project childProject;
    private static Project parentProject;
    @BeforeAll
    public static void oneTimeSetup() {
        parentProject = new ProjectBuilder().name("test").description("test").dateEnd(LocalDate.now().plusDays(2)).startDate(LocalDate.now()).build();
        childProject = new ProjectBuilder().name("Test project").description("Project crée dans les tests").dateEnd(LocalDate.now().plusDays(2)).startDate(LocalDate.now()).parent(parentProject).build();
    }

    @Test
    @Order(1)
    public void TestInsert() throws DaoException {
        parentProject.save();
        Project createdProject = ProjectDaoGetter.getById(parentProject.getId());
        assertEquals(parentProject.getName(), createdProject.getName());
        assertEquals(parentProject.getId(), createdProject.getId());

        childProject.save();
        createdProject = ProjectDaoGetter.getById(childProject.getId());

        assertEquals(childProject.getName(), createdProject.getName());
        assertEquals(childProject.getId(), createdProject.getId());
        assertEquals(createdProject.getParent().getId(),parentProject.getId());
    }

    @Test
    @Order(2)
    public void TestGetById() throws  DaoException{
        Project project = ProjectDaoGetter.getById(childProject.getId());
        assertEquals(childProject.getId(), project.getId());
        assertEquals(parentProject.getId(), project.getParent().getId());
        assertNull(ProjectDaoGetter.getById(9999));
    }

    @Test
    @Order(3)
    public void TestGetProjectById() throws  DaoException{
        Project project = ProjectDaoGetter.getById(childProject.getId());
        assertEquals(childProject.getId(), project.getId());
        assertEquals(parentProject.getId(), project.getParent().getId());
        assertNull(ProjectDaoGetter.getById(9999));
    }

    @Test
    @Order(4)
    public void TestGetAll() throws DaoException {
        List<Project> projects = Helper.getCurrentUser().getProjects();
        assertEquals(1,projects.size());
        assertEquals(1,projects.get(0).getChildren().size());
    }

    @Test
    @Order(5)
    public void TestCheckExistingProject() throws DaoException {
        assertEquals(childProject.getId(), ProjectDaoGetter.checkExistingProject(childProject.getName(), childProject.getDateEnd()));
        assertEquals(-1, ProjectDaoGetter.checkExistingProject("prout", LocalDate.now()));
    }



    @Test
    @Order(6)
    public void TestDelete() throws  DaoException{
        assertTrue(parentProject.delete());
        assertNull(ProjectDaoGetter.getById(childProject.getId()));
        assertFalse(ProjectDaoSetter.delete(-1));
    }
}
