package be.ac.ulb.infof307.g03.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private static Task task1;
    private static Task task2;

    @BeforeAll
    static void setUp(){
        task1 = new Task(1, "Description", 1, LocalDate.now(), LocalDate.now().plusDays(1), false);
        task2 = new Task(1, "Description diff", 1, LocalDate.now().minusDays(1), LocalDate.now().plusDays(2), false);
    }

    @Test
    void diff() {
        Map<String, String> map1 = task1.diff(task2);
        Map<String, String> map2 = task1.diff(task1);
        assertEquals(3, map1.keySet().size());
        assertEquals(0, map2.keySet().size());
    }
}