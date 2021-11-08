package be.ac.ulb.infof307.g03.database.dao;

import be.ac.ulb.infof307.g03.database.Database;
import be.ac.ulb.infof307.g03.database.dao.user.UserGetterDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

/**
 * Data access class handling the collaboration
 */
public class CollaboratorDao {
    /**
     * Get all the collaborators for a given projectID
     * @param projectId current project
     * @param userId current userId to exclude from getter
     * @return the list of collaborators
     */
    public static ArrayList<User> getAllCollaborators(int projectId, int userId) throws DaoException {
        ArrayList<User> collaborators= new ArrayList<>();
        try{
            String sql = "SELECT * FROM userProject WHERE projectId = ? and userId != ?";
            PreparedStatement query= Database.getPreparedStatement(sql);
            query.setInt(1,projectId);
            query.setInt(2,userId);
            ResultSet rs = query.executeQuery();
            while (rs.next()){
                User user = UserGetterDao.getById(rs.getInt("userId"));
                collaborators.add(user);
            }
            return collaborators;
        }catch(SQLException e){
            throw new DaoException("Error : Cannot retrieves all the collaborators for a specific project", e);
        }
    }

    /**
     * Get all the collaborators for a given projectID
     * @param projectId current project
     * @return the list of collaborators
     */
    public static ArrayList<User> getAllCollaborators(int projectId) throws DaoException {
        ArrayList<User> collaborators= new ArrayList<>();
        try{
            String sql = "SELECT * FROM userProject WHERE projectId = ?";
            PreparedStatement query= Database.getPreparedStatement(sql);
            query.setInt(1,projectId);
            ResultSet rs = query.executeQuery();
            while (rs.next()){
                User user = UserGetterDao.getById(rs.getInt("userId"));
                collaborators.add(user);
            }
            return collaborators;
        }catch(SQLException e){
            throw new DaoException("Error : Cannot retrieves all the collaborators for a specific project", e);
        }
    }

    /**
     *  remove a given collaborator from a specific project
     * @param userID the user id
     * @param projectID the project id
     */
    public static void removeCollaborator(int userID, int projectID) throws DaoException{
        try {
            String sql = "DELETE FROM userProject where userId = ? AND projectId = ?";
            PreparedStatement ps = Database.getPreparedStatement(sql);
            ps.setInt(1, userID);
            ps.setInt(2, projectID);
            ps.executeUpdate();
            Database.commitStatement();
        } catch (SQLException e) {
            Database.rollbackStatement();
            throw new DaoException("Error : Cannot delete a collaborator from project", e);
        }
    }

    /**
     * Check either a used is a collaborator on a project.
     * @param userId the user to check
     * @param projectId the project on which he is maybe collaborator
     * @return true if he is collaborator, false otherwise
     * @throws DaoException DaoException
     */
    public static boolean isCollaborator(int userId, int projectId) throws DaoException {
        String sql = "SELECT COUNT(*) as isCollab FROM userProject WHERE projectId = ? and userId = ?";
        PreparedStatement query= Database.getPreparedStatement(sql);
        try{
            query.setInt(1,projectId);
            query.setInt(2,userId);
            ResultSet rs = query.executeQuery();
            return rs.getInt("isCollab") > 0;
        }catch(SQLException e){
            throw new DaoException("Error : Cannot check if user is collaborator", e);
        }
    }

    /**
     * Manage the insertion of a collaborator in userProject SQL table
     * @param userId id of the user we are inserting
     * @param projectId id of the project we are inserting
     * @throws DaoException DaoException
     */
    public static void insertCollaborator(int userId, int projectId) throws DaoException {
        String sql = "INSERT INTO userProject(userId,projectId,isRoot) VALUES (?,?,?);";
        try {
            PreparedStatement ps = Database.getPreparedStatement(sql);
            ps.setInt(1,userId);
            ps.setInt(2,projectId);
            ps.setBoolean(3, true);
            ps.executeUpdate();
            Database.commitStatement();
        } catch (SQLException e) {
            Database.rollbackStatement();
            throw new DaoException("Error : Cannot insert collaborator", e);
        }
    }
}

