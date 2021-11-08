package be.ac.ulb.infof307.g03.database;

import be.ac.ulb.infof307.g03.exceptions.DaoException;

import java.sql.SQLException;

/**
 * CLass creating all the table of the database
 */
public class CreateTables {
    private final static String createUserTableSQL = "CREATE TABLE IF NOT EXISTS user(" +
            "id INTEGER PRIMARY KEY, " +
            "userName VARCHAR(30) NOT NULL UNIQUE, " +
            "password VARCHAR(30) NOT NULL, " +
            "firstName VARCHAR(30) NULL, " +
            "lastName VARCHAR(30) NULL, " +
            "email VARCHAR(30) NULL UNIQUE, " +
            "isConnected BOOLEAN NOT NULL DEFAULT 0" +
            ");";

    private final static String createProjectTableSql = "CREATE TABLE IF NOT EXISTS project(" +
            "id INTEGER PRIMARY KEY," +
            "name VARCHAR(30) NOT NULL," +
            "description VARCHAR(255) NULL," +
            "startDate DATETIME NOT NULL," +
            "dateEnd DATETIME NOT NULL,"+
            "parentProject INTEGER NULL,"+
            "currentBranch INTEGER NULL,"+
            "password VARCHAR(100) NULL,"+
            "isGeetActivate BOOLEAN NOT NULL DEFAULT 0,"+
            "FOREIGN KEY(parentProject) REFERENCES project(id) ON DELETE CASCADE,"+
            "FOREIGN KEY(currentBranch) REFERENCES branch(id)"+
            ");";

    private final static String createBranchTableSql = "CREATE TABLE IF NOT EXISTS branch("+
            "id INTEGER PRIMARY KEY," +
            "branch_name VARCHAR(30),"+
            "project_id INTEGER NOT NULL,"+
            "current_version INTEGER DEFAULT NULL,"+
            "FOREIGN KEY(project_id) REFERENCES project(id) ON DELETE CASCADE"+
            ");";

    private final static String createVersionTableSql = "CREATE TABLE IF NOT EXISTS version("+
            "id INTEGER PRIMARY KEY," +
            "timestamp DATETIME NOT NULL,"+
            "commit_message VARCHAR(30),"+
            "branch INTEGER NOT NULL,"+
            "project INTEGER NOT NULL,"+
            "FOREIGN KEY(branch) REFERENCES branch(id) ON DELETE CASCADE,"+
            "FOREIGN KEY(project) REFERENCES project(id) ON DELETE CASCADE"+
            ");";

    private final static String createNotificationTableSql = "CREATE TABLE IF NOT EXISTS notification(" +
            "id integer primary key," +
            "message text not null," +
            "type text not null," +
            "sender_id integer null," +
            "receiver_id integer not null," +
            "project_id integer not null," +
            "foreign key (sender_id) references user(id) ON DELETE CASCADE," +
            "foreign key (receiver_id) references user(id) ON DELETE CASCADE," +
            "foreign key (project_id) references project(id) ON DELETE CASCADE" +
            ");";


    private final static String createUserProjectTableSql = "CREATE TABLE IF NOT EXISTS userProject("+
            "userId INTEGER NOT NULL,"+
            "projectId INTEGER NOT NULL,"+
            "currentBranch INTEGER NULL,"+
            "isRoot BOOLEAN NOT NULL,"+
            "PRIMARY KEY (userId,projectId),"+
            "FOREIGN KEY(userId) REFERENCES user(id) ON DELETE CASCADE,"+
            "FOREIGN KEY(projectId) REFERENCES project(id) ON DELETE CASCADE,"+
            "FOREIGN KEY(currentBranch) REFERENCES branch(id)"+
            ");";

    private final static String createReminderTableSql = "CREATE TABLE IF NOT EXISTS reminder("+
            "id INTEGER PRIMARY KEY,"+
            "projectId INTEGER NOT NULL,"+
            "taskId INTEGER NULL,"+
            "date DATETIME NOT NULL,"+
            "FOREIGN KEY(projectId) REFERENCES project(id) ON DELETE CASCADE,"+
            "FOREIGN KEY(taskId) REFERENCES task(id) ON DELETE CASCADE"+
            ");";

    private final static String createTaskTableSql = "CREATE TABLE IF NOT EXISTS task("+
            "id INTEGER PRIMARY KEY," +
            "description VARCHAR(255)," +
            "projectId INTEGER," +
            "startDate DATETIME NOT NULL,"+
            "endDate DATETIME NOT NULL,"+
            "isDone BOOLEAN NOT NULL DEFAULT 0,"+
            "FOREIGN KEY(projectId) REFERENCES project(id) ON DELETE CASCADE"+
            ");";

    private final static  String createTaskAssignmentTableSql = "CREATE TABLE IF NOT EXISTS taskAssignment("+
            "taskId INTEGER NOT NULL,"+
            "userId INTEGER NOT NULL,"+
            "PRIMARY KEY (taskId,userId),"+
            "FOREIGN KEY(userId) REFERENCES user(id) ON DELETE CASCADE,"+
            "FOREIGN KEY(taskId) REFERENCES task(id) ON DELETE CASCADE"+
            ");";

    private final static String createTagTableSql = "CREATE TABLE IF NOT EXISTS tag(" +
            "id INTEGER PRIMARY KEY," +
            "name VARCHAR(20) NOT NULL" +
            ");";

    private final static String createTagUserTableSql = "CREATE TABLE IF NOT EXISTS tag_user(" +
            "tag_user_id INTEGER PRIMARY KEY," +
            "id_user INTEGER," +
            "id_tag INTEGER," +
            "FOREIGN KEY(id_tag) REFERENCES tag(id) ON DELETE CASCADE," +
            "FOREIGN KEY(id_user) REFERENCES user(id) ON DELETE CASCADE" +
            ");";

    private final static String createProjectTagTableSql = "CREATE TABLE IF NOT EXISTS tag_project(" +
            "tag_project_id INTEGER PRIMARY KEY," +
            "id_project INTEGER," +
            "id_tag INTEGER," +
            "FOREIGN KEY(id_tag) REFERENCES tag(id)," +
            "FOREIGN KEY(id_project) REFERENCES project(id) ON DELETE CASCADE" +
            ");";

    /**
     * Create the user table inside the DB
     * @throws SQLException SQLException
     */
    public static void createUserTable() throws SQLException, DaoException {
        Database.getPreparedStatement(createUserTableSQL).executeUpdate();
    }


    /**
     * Create the tag table inside the DB
     * @throws SQLException SQLException
     */
    public static void createTagsTable() throws SQLException, DaoException{
        Database.getPreparedStatement(createTagTableSql).executeUpdate();
        Database.getPreparedStatement(createTagUserTableSql).executeUpdate();
        Database.getPreparedStatement(createProjectTagTableSql).executeUpdate();
    }

    /**
     * Create the project table, branch table and version table inside the DB
     * @throws SQLException SQLException
     */
    public static void createProjectTable() throws SQLException, DaoException{
        Database.getPreparedStatement(createProjectTableSql).executeUpdate();
        Database.getPreparedStatement(createBranchTableSql).executeUpdate();
        Database.getPreparedStatement(createVersionTableSql).executeUpdate();
    }

    /**
     * Create the task table inside the DB
     * @throws SQLException SQLException
     */
    public static void createTaskTable() throws SQLException, DaoException{
        Database.getPreparedStatement(createTaskTableSql).executeUpdate();
    }

    /**
     * Create the user project table inside the DB
     * @throws SQLException SQLException
     */
    public static void createUserProjectTable() throws SQLException, DaoException{
        Database.getPreparedStatement(createUserProjectTableSql).executeUpdate();
    }

    /**
     * Create the notification table in the DB
     * @throws DaoException DaoException
     * @throws SQLException SQLException
     */
    public static void createNotificationTable() throws DaoException, SQLException {
        Database.getPreparedStatement(createNotificationTableSql).executeUpdate();
    }

    /**
     * Create the assign task table in the DB
     * @throws SQLException SQLException
     * @throws DaoException DaoException
     */
    public static  void createAssignTaskTable() throws SQLException, DaoException{
        Database.getPreparedStatement(createTaskAssignmentTableSql).executeUpdate();
    }

    /**
     * Create the reminder table in the DB
     * @throws SQLException SQLException
     * @throws DaoException DaoException
     */
    public static void createReminderTable()  throws SQLException, DaoException{
        Database.getPreparedStatement(createReminderTableSql).executeUpdate();
    }


}
