package be.ac.ulb.infof307.g03.database;

import be.ac.ulb.infof307.g03.BeforeAllTests;
import be.ac.ulb.infof307.g03.database.dao.TagDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.Tag;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TagDaoTest extends BeforeAllTests {

    private static Tag tag;
    private static int insertedId;

    @BeforeAll
    static void oneTimeSetup(){
        tag = new Tag("mock");
    }

    @Test
    @Order(2)
    void insert() throws  DaoException{
        insertedId = TagDao.insert(tag);
        assertNotNull(TagDao.getById(insertedId));
        assertThrows(NullPointerException.class,() -> TagDao.insert(null));
    }

    @Test
    @Order(4)
    void delete() throws  DaoException{
        assertTrue(TagDao.delete(insertedId));
        assertFalse(TagDao.delete(-14));
    }

    @Test
    @Order(5)
    void getAllAfterDelete() throws  DaoException{
        List<Tag> tags = TagDao.getAll(Helper.getCurrentUser().getId());
        assertEquals(tags.size(), 0);
    }

    @Test
    @Order(1)
    void getAllBeforeInsert() throws  DaoException{
        List<Tag> tags = TagDao.getAll(Helper.getCurrentUser().getId());
        assertEquals(tags.size(), 0);
    }

    @Test
    @Order(3)
    void getAllAfterInsert() throws  DaoException{
        List<Tag> tags = TagDao.getAll(Helper.getCurrentUser().getId());
        assertEquals(1, tags.size());
    }
}