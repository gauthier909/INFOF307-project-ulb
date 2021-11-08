package be.ac.ulb.infof307.g03.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagTest {

    private static Tag tag1;
    private static Tag tag2;

    @BeforeAll
    static void setUp(){
        tag1 = new Tag(1, "Description");
        tag2 = new Tag(1, "Description diff");
    }

    @Test
    void diff() {
        assertNotNull(tag1.diff(tag2));
        assertNull(tag1.diff(tag1));
    }
}