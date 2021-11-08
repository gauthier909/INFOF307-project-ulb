package be.ac.ulb.infof307.g03.database.dao.user;

import be.ac.ulb.infof307.g03.database.Database;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.models.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access class of the table user (only reading acces)
 */
public class UserGetterDao {

    /**
     * Check if a given username is already used by a user in the database
     * @param username username to check
     * @return boolean : true if the username is used
     */
    public static boolean isUserInDB(String username) throws DaoException{
        //check if username already in DB
        String isInDB = " SELECT userName FROM user WHERE userName = ?;";

        try {
            PreparedStatement query = Database.getPreparedStatement(isInDB);
            query.setString(1, username);
            ResultSet queryResult = query.executeQuery();
            return queryResult.next();
        } catch (SQLException e) {
            throw new DaoException("Error : Cannot check if the username already exists", e);
        }
    }

    /**
     * Check if a given email is already used by a user in the database
     * @param email email to check
     * @return boolean : true if the email is used
     */
    public static boolean isEmailInDB(String email) throws DaoException {
        //check if email already in DB
        String isInDB = " SELECT email FROM user WHERE email = ?;";

        try {
            PreparedStatement query = Database.getPreparedStatement(isInDB);
            query.setString(1, email);
            ResultSet queryResult = query.executeQuery();
            return  queryResult.next();

        } catch (SQLException e) {
            throw new DaoException("Error : Cannot check if the email already exists", e);
        }
    }

    /**
     * Check if a user is already connected.
     * @return int : 1 if connected, else 0. -1 for errors.
     */
    public static int checkIfConnected() throws DaoException{
        String selectConnected = "SELECT isConnected FROM user WHERE isConnected = 1;";
        try {
            ResultSet queryResult = Database.getPreparedStatement(selectConnected).executeQuery();
            return queryResult.next()? 1:0;
        }
        catch(SQLException e){
            throw new DaoException("Error : Cannot check if the user is already connected", e);
        }
    }


    /**
     * Get a user from the database, for login
     * @param username username of given user
     * @param password password of given user
     * @return User : User object corresponding to current User
     */
    public static User getUserFromDb(String username, String password) throws DaoException{
        User user = null;
        /*
        * For given username and password, check if combination is present in the database
         * returns 1 if information is correct, else 0, or -1 in case of an exception
         */
        String checkUser = " SELECT id,userName,firstName,lastName,email, password FROM user WHERE userName = ?;";
        try {

            PreparedStatement query = Database.getPreparedStatement(checkUser);
            query.setString(1,username);
            ResultSet queryResult = query.executeQuery();
            if(queryResult.next()){
                String hashedPwd = queryResult.getString("password");
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                if (encoder.matches(password, hashedPwd)) {
                    user = new User(queryResult.getInt("id"),queryResult.getString("userName"), queryResult.getString("firstName"),
                            queryResult.getString("lastName"), queryResult.getString("email"));
                }
            }

        }
        catch(SQLException e){
            throw new DaoException("Error :  Cannot get the user from the database", e);
        }
        return user;
    }

    /**
     * Get the ID of a user in DB.
     * @param username username of given user
     * @return int : id of the user we search; -1 if no user has been found.
     */
    public static int getIdFromUsername(String username) throws DaoException {
        String sql = " SELECT id AS userId FROM user WHERE userName = ?;";
        try {
            PreparedStatement query = Database.getPreparedStatement(sql);
            query.setString(1,username);
            ResultSet rs = query.executeQuery();
            if(rs.next())
                return rs.getInt("userId");
        }catch(SQLException e) {
            throw new DaoException("Error : Cannot retrieves the user id from the database", e);
        }
        return -1;
    }

    /**
     * Get a user by his id.
     * @param id id of the user we search
     * @throws DaoException if a {@link SQLException} occurs
     * @return User if a user correspond to this id; else null
     */
    public static User getById(int id) throws DaoException{
        User retUser = null;

        try {
            PreparedStatement ps = Database.getPreparedStatement("SELECT * FROM user where id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                retUser = new User(rs.getInt("id"),
                        rs.getString("userName"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"));
            }
        }catch(SQLException e){
            throw new DaoException("Error : Cannot get user from his id",e);
        }
        return retUser;
    }


    /**
     * Get all collaborator by projectId
     * @param projectID: id for given project
     * @return List of all collaborators
     */
    public static List<User> getUserByProject(int projectID) throws DaoException{
        ArrayList<User> usersList = new ArrayList<>();

        try {
            String sqlGet = "SELECT * FROM userProject where projectId=?";
            PreparedStatement ps=Database.getPreparedStatement(sqlGet);
            ps.setInt(1,projectID);
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                int userId = rs.getInt("userId");
                String sql = "SELECT * FROM user where id = ?";
                PreparedStatement ps2 =Database.getPreparedStatement(sql);
                ps2.setInt(1,userId);
                ResultSet rs2 = ps2.executeQuery();
                User user = new User(rs2.getInt("id"),rs2.getString("userName"),rs2.getString("firstName"),rs2.getString("lastName"),rs2.getString("email"));
                usersList.add(user);
            }
        }catch (Exception e){
            throw new DaoException("Error : Cannot retrieves all the collaborators to a project", e);
        }
        return usersList;
    }

}

