package be.ac.ulb.infof307.g03.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectTest {

    private static Project project1;
    private static Project project2;

    @BeforeAll
    static void setUp(){
        List<Task> taskP1 = new ArrayList<>(Arrays.asList(new Task(1,"Description 1",1,LocalDate.now(), LocalDate.now().plusDays(1), false), new Task(2, "Description", 1,LocalDate.now(), LocalDate.now().plusDays(1), false)));
        List<Task> taskP2 = new ArrayList<>(Arrays.asList(new Task(1,"Description changed",1,LocalDate.now(), LocalDate.now().plusDays(1), false), new Task(3, "Description", 1, LocalDate.now(), LocalDate.now().plusDays(1), false)));
        List<Tag> tag1 = new ArrayList<>(Arrays.asList(new Tag(1, "Tag 1"), new Tag(2, "Tag 2")));
        List<Tag> tag2 = new ArrayList<>(Arrays.asList(new Tag(1, "Tag 3"), new Tag(3, "Tag 2")));

        project1 = new ProjectBuilder().id(1).name("P1").description("test").dateEnd(LocalDate.now()).tasks(taskP1).tags(tag1).cryptPassword("a").build();
        project2 = new ProjectBuilder().id(1).name("P2").description("test").dateEnd(LocalDate.now()).tasks(taskP2).tags(tag2).cryptPassword("ab").build();
        Project childProject = new ProjectBuilder().id(2).name("Child1").description("Description child").dateEnd(LocalDate.now()).parent(project2).build();
        project2.addChild(childProject);
    }

    @Test
    public void testProjectBuilder(){
        Project project = new ProjectBuilder().name("test").dateEnd(LocalDate.now()).id(1).description("test").build();
        assertEquals(1, project.getId());
        assertEquals("test", project.getName());
    }

    @Test
    public void diff(){
        Map<String, Map<String, String>> map = project2.diff(project1);
        Map<String, String> deleted = map.get("deleted");
        Map<String, String> added = map.get("added");
        Map<String, String> differences = map.get("differences");

        assertEquals(2, deleted.keySet().size());       // deleted 1 task and 1 tag
        assertEquals(3, added.keySet().size());         // added 1 task, 1 tag and a child
        assertEquals(3, differences.keySet().size());   // diff on name, task description, tag description
    }

    @Test
    public void testPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches("a", project1.getPassword()));
        assertTrue(encoder.matches("ab", project2.getPassword()));
    }
}