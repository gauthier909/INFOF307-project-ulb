package be.ac.ulb.infof307.g03.database.dao.user;

import be.ac.ulb.infof307.g03.database.Database;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.models.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Data access class of the table user (only writing/updating access)
 */
public class UserSetterDao {
    /**
     * Insert new user in database
     * @param username username for user
     * @param password password for user
     * @param email email for user
     * @param firstName first name for user
     * @param lastName last name for user
     */
    public static void registerUser(String username, String password, String email,
                                       String firstName, String lastName) throws DaoException {
        //register in DB
        String insertUser = "INSERT INTO user VALUES(?, ?, ?, ?,?,?, 0);";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        try {
            PreparedStatement query = Database.getPreparedStatement(insertUser);
            query.setString(2, username);
            query.setString(3, encoder.encode(password));

            if (firstName.equals("")) query.setNull(4, java.sql.Types.NULL);
            else query.setString(4, firstName);

            if (lastName.equals("")) query.setNull(5, java.sql.Types.NULL);
            else query.setString(5, lastName);

            if (email.equals("")) query.setNull(6, java.sql.Types.NULL);
            else query.setString(6, email);

            query.executeUpdate();
            Database.commitStatement();
        } catch (SQLException e) {
            Database.rollbackStatement();
            throw new DaoException("Error : Cannot insert a user", e);
        }
    }

    /**
     * Modify user values in database
     * @param currentUser User object corresponding to the current user
     * @param username new username for user
     * @param password new password for user
     * @param email new email for user
     * @param firstName new first name for user
     * @param lastName new last name for user
     */
    public static void modifyUser(User currentUser, String username, String password, String email,
                                  String firstName, String lastName)throws DaoException{
        String updateTable = "UPDATE user SET userName = ?, password = ?, email = ?, firstName = ?, lastName = ? WHERE id = ?;";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        try {
            PreparedStatement update = Database.getPreparedStatement(updateTable);
            update.setString(1, username);
            update.setString(2, encoder.encode(password));

            if (email.equals("")) update.setNull(3, java.sql.Types.NULL);
            else update.setString(3, email);

            if (firstName.equals("")) update.setNull(4, java.sql.Types.NULL);
            else update.setString(4, firstName);

            if (lastName.equals("")) update.setNull(5, java.sql.Types.NULL);
            else update.setString(5, lastName);

            update.setInt(6, currentUser.getId());
            update.executeUpdate();
            Database.commitStatement();
        } catch(SQLException e){
            Database.rollbackStatement();
            throw new DaoException("Error : Cannot update this user", e);
        }
    }

    /**
     * Update connected flag in the database to connected for given userName
     * @param id: id for given user
     * @param connecting: true if you want to connect, false if you want to disconnect
     */
    public static void setUserConnection(int id, boolean connecting)throws DaoException{
        String updateTable;
        if(connecting) updateTable = "UPDATE user SET isConnected = 1 WHERE id = ?;";
        else updateTable = "UPDATE user SET isConnected = 0 WHERE id = ?;";

        try {
            PreparedStatement update = Database.getPreparedStatement(updateTable);
            update.setInt(1,id);
            update.executeUpdate();
            Database.commitStatement();
        }
        catch(SQLException e){
            Database.rollbackStatement();
           throw new DaoException("Error :  Cannot set the user connection status", e);
        }
    }

    /**
     * Link the user to the project
     * @param user the user to be linked
     * @param id the project id
     */
    public static void linkToProject(User user, int id, boolean isParent) throws DaoException{
        String sql = "INSERT INTO userProject (userId,projectId,isRoot) VALUES (?,?,?)";
        try {
            PreparedStatement ps = Database.getPreparedStatement(sql);
            ps.setInt(1, user.getId());
            ps.setInt(2,id);
            ps.setBoolean(3, isParent);
            ps.executeUpdate();
            Database.commitStatement();
        } catch (SQLException e) {
            Database.rollbackStatement();
            throw new DaoException("Error while trying to link a user to a project", e);
        }
    }

    /**
     * Delete a user from the database according to the username of the user we want to delete
     * @param username of a user to delete
     * @throws DaoException in case of Database exception
     */
    public static void deleteUser(String username) throws DaoException {
        String sql = "DELETE FROM user WHERE username = ?;";
        try{
            PreparedStatement ps = Database.getPreparedStatement(sql);
            ps.setString(1, username);
            ps.executeUpdate();
            Database.commitStatement();
        }catch (SQLException e){
            Database.rollbackStatement();
            throw new DaoException("Error while deleting a user from the db", e);
        }
    }
}
