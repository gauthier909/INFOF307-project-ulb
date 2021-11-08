package be.ac.ulb.infof307.g03.database;

import be.ac.ulb.infof307.g03.exceptions.DaoException;

import java.sql.*;

/**
 * Class handling the connection to the database
 */
public class Database {

    private static Connection connection = null;
    private static String dbPath = "jdbc:sqlite:";

    /**
     * Create the different tables that are used for this application.
     * @param databaseName the file name that will be used to store the data
     * @throws DaoException if an {@link SQLException} occurs.
     */
    public static void init(String databaseName) throws DaoException{
        dbPath += databaseName;

        try {
            CreateTables.createUserTable();
            CreateTables.createNotificationTable();
            CreateTables.createProjectTable();
            CreateTables.createTagsTable();
            CreateTables.createTaskTable();
            CreateTables.createUserProjectTable();
            CreateTables.createAssignTaskTable();
            CreateTables.createReminderTable();

        }catch(SQLException e){
            throw new DaoException("Error : Cannot instantiate Database", e);
        }
    }

    /**
     * This is used to get the connection to the database pointed by 'dbPath'.
     * @return connection
     */
    private static Connection getConnection() throws SQLException {
        if(connection == null) {
            try {
                Class.forName("java.sql.DriverManager");
                connection = DriverManager.getConnection(dbPath);
                connection.createStatement().execute("PRAGMA foreign_keys = ON");
                connection.setAutoCommit(false);
            } catch (Exception e) {
                throw new SQLException("Error : Cannot get the database connection", e);
            }
        }
        return connection;
    }

    /**
     * This is used to get a preparedStatement from a {@link Connection} according to a SQL statement
     * @param sql the sql statement
     * @return a {@link PreparedStatement}
     * @throws DaoException if an {@link SQLException} occurs
     */
    public static PreparedStatement getPreparedStatement(String sql) throws DaoException{
        try {
            return getConnection().prepareStatement(sql);
        }catch (SQLException e){
            throw new DaoException("Error : Cannot get the prepared statement", e);
        }
    }

    /**
     * This is used to get a preparedStatement from a {@link Connection} according to a SQL statement
     * @param sql the sql statement
     * @param s s.
     * @return a {@link PreparedStatement}
     * @throws DaoException if an {@link SQLException} occurs
     */
    public static PreparedStatement getPreparedStatement(String sql, int s) throws DaoException {
        try {
            return getConnection().prepareStatement(sql,s);
        } catch (SQLException e) {
            throw new DaoException("Error : Cannot get the prepared statement", e);
        }
    }

    /**
     * This function is used to commit a statement to a database. Must be used for insert, update, delete
     * @throws DaoException is an {@link SQLException} occurs.
     */
    public static void commitStatement() throws DaoException {
        try {
            getConnection().commit();
        }catch (SQLException e){
            throw new DaoException("Error : Cannot commit statement", e);
        }
    }

    /**
     * This function is used to rollback a statement in case of error during a query.
     * @throws DaoException if an {@link SQLException} occurs.
     */
    public static void rollbackStatement() throws DaoException{
        try {
            getConnection().rollback();
        }catch(SQLException e){
            throw new DaoException("Error : Cannot rollback statement", e);
        }

    }

    /**
     * Close the connection.
     * @throws DaoException if an {@link SQLException} occurs.
     */
    public static void closeConnection() throws DaoException{
        if(connection != null){
            try {
                connection.close();
            }catch(SQLException e){
                throw new DaoException("Error : Cannot close the database connection", e);
            }
        }
    }

}
