package be.ac.ulb.infof307.g03.models;


import be.ac.ulb.infof307.g03.database.dao.TagDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;

/**
 * Class representing a tag
 */
public class Tag {
    private int id;
    private final String name;

    /**
     * Tag constructor
     * @param name of the tag
     */
    public Tag(String name) {
        this.name = name;
    }

    /**
     * Tag constructor
     * @param id the id of the tag
     * @param name of the tag
     */
    public Tag(int id, String name){
        this.id = id;
        this.name = name;
    }

     /**
     * Return the difference between 2 tags
     * @param other tag to comapre with
     * @return string of the 2 name tag if different or null if equals
     */
    public String diff(Tag other){
        if(!name.equals(other.name))
            return name + ", " + other.name;
        return null;
    }

    /**
     * Get the name of the tag
     * @return name of the tag
     */
    public String getName() {
        return name;
    }

    /**
     * Insert tag in database and set id to given id
     */
    public void insert() throws DaoException {
        this.id = TagDao.insert(this);
    }


    /**
     * Get the id of the tag
     * @return id of the tag
     */
    public int getId(){
        return id;
    }

    /**
     * Set the id of the tag
     * @param id value of id
     */
    public void setId(int id){
        this.id = id;
    }
}
