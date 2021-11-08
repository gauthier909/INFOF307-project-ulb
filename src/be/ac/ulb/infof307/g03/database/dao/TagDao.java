package be.ac.ulb.infof307.g03.database.dao;

import be.ac.ulb.infof307.g03.database.Database;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.Tag;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access class of the table tag
 */
public class TagDao {
    /**
     * Insert
     * new tag, and associate the tag with the currentUser
     * @param tag new tag
     * @return id of created tag; -1 else
     */
    public static int insert(Tag tag) throws DaoException{
        int res=-1;
        try{

            String sqlInsert = "INSERT INTO tag(name) VALUES(?)";
            PreparedStatement ps = Database.getPreparedStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,tag.getName());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                res=rs.getInt(1);
            }
            if(res!=-1){//tag create -> insert tag in intermediate table
                ps = Database.getPreparedStatement("INSERT INTO tag_user(id_user,id_tag) VALUES(?,?)");
                ps.setInt(1, Helper.getCurrentUser().getId());
                ps.setInt(2,res);
                ps.executeUpdate();
            }
            Database.commitStatement();
        }catch (SQLException e){
            Database.rollbackStatement();
            throw new DaoException("Error : Cannot insert tag in database", e);
        }
        return res;
    }

    /**
     * check if the id of the tag already exist
     * @param name name of the tag
     * @return the id of the tag if the name of the tag already exist ; else return -1
     * @throws DaoException if an {@link SQLException} occurs
     */
    public static int getIdIfNameExist(String name) throws DaoException {
        PreparedStatement ps = Database.getPreparedStatement("SELECT id FROM tag WHERE name = ?");
        try {
            ps.setString(1,name);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                return rs.getInt("id");
            }else{
                return 0;
            }
        } catch (SQLException e) {
            throw new DaoException("Error while trying to get a tag from its name", e);
        }
    }

    /**
     * Insert a new tag, and associate the tag with a given project
     * @param tagID new tag
     * @param projectID id of the project
     * @throws DaoException DaoException
     */
    public static void insertToProject(int tagID, int projectID) throws DaoException {
        try{
            PreparedStatement ps = Database.getPreparedStatement("INSERT INTO tag_project(id_project,id_tag) VALUES(?,?)");
            ps.setInt(1, projectID);
            ps.setInt(2,tagID);
            ps.executeUpdate();
            Database.commitStatement();
        }catch (SQLException e){
            Database.rollbackStatement();
            throw new DaoException("Error : Cannot add tag to project", e);
        }
    }

    /**
     * Delete a new tag associated to a given project
     * @param tagID id of the deleted tag
     * @param projectID id of the project
     * @throws DaoException DaoException
     */
    public static void deleteFromProject(int tagID, int projectID) throws DaoException{
        if (getById(tagID) == null) { return;}
        try{
            PreparedStatement ps = Database.getPreparedStatement("DELETE FROM tag_project WHERE id_project = ? AND id_tag = ?");
            ps.setInt(1,projectID);
            ps.setInt(2,tagID);
            ps.executeUpdate();
            Database.commitStatement();
        }catch (SQLException e){
            Database.rollbackStatement();
            throw new DaoException("Error : Cannot delete tag from project", e);
        }
    }

    /**
     * Get all tags referred to a project's id
     * @param id of the project
     * @return tags linked to a given project
     * @throws DaoException DaoException
     */
    public static ArrayList<Tag> getByProjectId(int id) throws DaoException{
        ArrayList<Tag> tags = new ArrayList<>();
        try{
            PreparedStatement ps = Database.getPreparedStatement("SELECT t.id, t.name FROM tag t " +
                    "JOIN tag_project tp on t.id = tp.id_tag " +
                    "WHERE tp.id_project = ?;");
            ps.setInt(1, id);
            ResultSet audiRs4 = ps.executeQuery();
            while(audiRs4.next()){
                tags.add(new Tag(audiRs4.getInt("id"), audiRs4.getString("name")));
            }
        }catch (SQLException e){
            throw new DaoException("Error : Cannot get all tags from a project", e);
        }
        return tags;
    }

    /**
     * Get a tag by id
     * @param id of the tag
     * @return the tag with the given id
     */
    public static Tag getById(int id) throws DaoException{
        Tag tag = null;
        try{
            PreparedStatement ps = Database.getPreparedStatement("SELECT * from tag where id =?");
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                tag = new Tag(rs.getString("name"));
            }
        }catch(SQLException e){
            throw new DaoException("Error : Cannot select tag by id", e);
        }
        return tag;
    }

    /**
     * Delete a tag in DB
     * @param id of the tag
     * @return true if tag has been deleted; false else
     */
    public static boolean delete(int id) throws  DaoException{
        if(getById(id) == null) return false;
        try {
            PreparedStatement ps = Database.getPreparedStatement("DELETE FROM tag_user where id_tag = ?");
            ps.setInt(1, id);
            ps.executeUpdate();

            ps = Database.getPreparedStatement("DELETE FROM tag WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();

            Database.commitStatement();
        } catch (SQLException e) {
            Database.rollbackStatement();
            throw new DaoException("Error : Cannot delete tag from database", e);
        }
        return true;
    }

    /**
     * Get all the tags linked to the current user
     * @return List of all the tags of a user
     */
    public static List<Tag> getAll(int userId) throws DaoException {
        List<Tag> tags = new ArrayList<>();
        try {
            PreparedStatement ps = Database.getPreparedStatement("SELECT * FROM tag LEFT JOIN tag_user ON tag.id = tag_user.id_tag where tag_user.id_user = ?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                tags.add(new Tag(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            throw new DaoException("Error : Cannot get user tags", e);
        }
        return tags;
    }

    /**
     * Manage the insert in tag_project SQL table when inserting a project
     * @param tags The list of tags to insert
     * @param projectId id of project
     * @throws DaoException throwing DaoException create on the recursive call to TaskDao.insert
     */
    public static void insertTags(List<Tag> tags, int projectId) throws DaoException {
        if(tags != null){
            for(Tag tag : tags){
                if(tag.getId() == -1){
                    tag.setId(TagDao.insert(tag));
                }
                TagDao.insertToProject(tag.getId(),projectId);
            }
        }
    }
}
