package be.ac.ulb.infof307.g03.database.dao;

import be.ac.ulb.infof307.g03.database.Database;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.Task;
import be.ac.ulb.infof307.g03.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access class of the table task
 */
public class TaskDao {

    /**
     * Get all tasks for a project by his id
     * @param projectID The id of the project for which tasks have to be extracted
     * @return all tasks referred to the project
     */
    public static ArrayList<Task> getAllByProjectId(int projectID) throws DaoException {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            String sqlGet = "SELECT * FROM task where projectId=?";
            PreparedStatement ps= Database.getPreparedStatement(sqlGet);
            ps.setInt(1,projectID);
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                Task task = new Task(rs.getInt("id"),
                        rs.getString("description"),
                        rs.getInt("projectId"),
                        rs.getDate("startDate").toLocalDate(),
                        rs.getDate("endDate").toLocalDate(),
                        rs.getBoolean("isDone"));
                tasks.add(task);
            }
        }catch (SQLException e){
            throw new DaoException("Error : Cannot get the tasks for a project", e);
        }
        return tasks;

    }

    /**
     * Get all user for a task by his id
     * @param taskId the id of the task 
     * @return all tasks referred to the project
     */
    public static ArrayList<User> getAllUserByTaskId(int taskId) throws DaoException {
        ArrayList<User> users =new ArrayList<>();
        try {
            String sqlGet = "SELECT * FROM taskAssignment where taskId=?";
            PreparedStatement ps=Database.getPreparedStatement(sqlGet);
            ps.setInt(1,taskId);
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                int userId = rs.getInt("userId");
                String sql = "SELECT * FROM user where id = ?";
                PreparedStatement ps2 =Database.getPreparedStatement(sql);
                ps2.setInt(1,userId);
                ResultSet rs2 = ps2.executeQuery();
                User user = new User(rs2.getInt("id"),rs2.getString("userName"),rs2.getString("firstName"),rs2.getString("lastName"),rs2.getString("email"));
                users.add(user);

            }
        }catch (SQLException e){
            throw new DaoException("Error : Cannot get all the user assigned to a task", e);
        }
        return users;

    }


    /**
     * Insert a task in the DB
     * @param task the task to insert
     * @return the id of the inserted task
     */
    public static int insert(Task task) throws DaoException {
        int res=-1;
        try{
            String sqlInsert = "INSERT INTO task(projectId,description, startDate, endDate, isDone) VALUES(?,?,?,?,?)";
            PreparedStatement ps=Database.getPreparedStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1,task.getProjectId());
            ps.setString(2,task.getDescription());
            ps.setDate(3, Date.valueOf(task.getStartDate()));
            ps.setDate(4, Date.valueOf(task.getEndDate()));
            ps.setBoolean(5, task.isDone());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            Database.commitStatement();
            while(rs.next()){
                res = rs.getInt(1);
            }
        }catch (SQLException e){
            Database.rollbackStatement();
            throw new DaoException("Error : Cannot insert task", e);
        }
        return res;
    }

    /**
     * Insert task assignment in the DB
     * @param task the task to insert
     * @param user the user who's the task has been assigned to
     */
    public static void insertAssignedTask(Task task, User user) throws DaoException {
        try{
            String sqlInsert = "INSERT INTO taskAssignment(taskId,userId) VALUES(?,?)";
            PreparedStatement ps=Database.getPreparedStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1,task.getId());
            ps.setInt(2,user.getId());
            ps.executeUpdate();
            Database.commitStatement();
        }catch (SQLException e){
            Database.rollbackStatement();
            throw new DaoException("Error : Cannot insert a task assignment for a user", e);
        }
    }


    /**
     * Delete a task assignment from the DB
     * @param userId the id of the user you are deleting a task from
     * @param taskId the id of the task to delete
     */
    public static void deleteAssignment(int userId, int taskId) throws DaoException{
        try{
            String sql = "DELETE FROM taskAssignment WHERE userId = ? AND taskId = ?";
            PreparedStatement ps = Database.getPreparedStatement(sql);
            ps.setInt(1,userId);
            ps.setInt(2,taskId);
            ps.executeUpdate();
            Database.commitStatement();
        }catch(SQLException e){
            Database.rollbackStatement();
            throw new DaoException(e);
        }
    }

    /**
     * Delete all task assignment by a task id from the DB
     * @param taskId the id of the task
     */
    public static void deleteAllAssignmentByTaskId(int taskId) throws DaoException{
        try{
            String sql = "DELETE FROM taskAssignment WHERE taskId = ?";
            PreparedStatement ps = Database.getPreparedStatement(sql);
            ps.setInt(1,taskId);
            ps.executeUpdate();
            Database.commitStatement();
        }catch(SQLException e){
            Database.rollbackStatement();
            throw new DaoException(e);
        }
    }


    /**
     * Check if a user is assigned to a task.
     * @param taskId to check
     * @param userId to determine either he is assigned or not
     * @return true if the user is assigned, false otherwise
     * @throws DaoException DaoException
     */
    public static boolean isUserAssigned(int taskId,int userId) throws DaoException{
        String sql = "SELECT * FROM taskAssignment WHERE taskId = ? AND userId =?";
        PreparedStatement ps;
        try {
            ps = Database.getPreparedStatement(sql);
            ps.setInt(1,taskId);
            ps.setInt(2,userId);
            ResultSet rs = ps.executeQuery();
            return  rs.next();
        } catch(SQLException e){
            Database.rollbackStatement();
            throw new DaoException(e);
        }
    }


    /**
     * Get the task link to an id in the DB
     * @param id id of the task to get in the DB
     * @return the task linked to the id
     */
    public static Task getById(int id) throws  DaoException{
        Task task=null;
        try {
            String sqlGet = "SELECT * FROM task where id=?";
            PreparedStatement ps=Database.getPreparedStatement(sqlGet);
            ps.setInt(1,id);
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                    task=new Task(rs.getInt("id"),
                            rs.getString("description"),
                            rs.getInt("projectId"),
                            rs.getDate("startDate").toLocalDate(),
                            rs.getDate("endDate").toLocalDate(),
                            rs.getBoolean("isDone"));
            }
        }catch (SQLException e){
            throw new DaoException("Error : Cannot select the specific task", e);
        }
        return task;

    }

    /**
     * Delete a task from the DB
     * @param id of the task
     * @return true if the task is correctly deleted, false if there is no task to delete with the given id
     */
    public static boolean delete(int id) throws DaoException{
        if(getById(id) == null) return false;
        try{
            String sql = "DELETE FROM task WHERE id = ?";
            PreparedStatement ps = Database.getPreparedStatement(sql);
            ps.setInt(1,id);
            ps.executeUpdate();
            Database.commitStatement();
        }catch(SQLException e){
            Database.rollbackStatement();
            throw new DaoException("Error : Cannot delete this task", e);
        }
        return true;
    }

    /**
     * Update the description of the task given
     * @param task the task that you are updating its description
     */
    public static void updateDescription(Task task) throws DaoException{
        String sql = "UPDATE task SET description = ? WHERE id = ?;";
        try {
            PreparedStatement ps = Database.getPreparedStatement(sql);
            ps.setString(1,task.getDescription());
            ps.setInt(2,task.getId());
            ps.executeUpdate();
            Database.commitStatement();
        } catch (SQLException e) {
            Database.rollbackStatement();
            throw new DaoException("Error : Cannot update the description task", e);
        }
    }

    /**
     * give the number of task
     * @param project concerned project
     * @return the number of task concerning the project given or -1 if the project doesn't exist
     * @throws DaoException DaoException
     */
    public static int getNumberOfTask(Project project) throws DaoException {
        String getNumberOfTask = "SELECT COUNT(*) FROM Task where projectId = ?";
        try{
            PreparedStatement query = Database.getPreparedStatement(getNumberOfTask);
            query.setInt(1,project.getId());
            ResultSet rs=query.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
            return -1;
        }catch (SQLException e){
            throw new DaoException("Error : Cannot get the number of task of the project",e);
        }
    }

    /**
     * Update start date and end date for a given task
     * @param task task to update
     */
    public static void updateDates(Task task) throws DaoException{
        String sql = "UPDATE task SET startDate = ?, endDate = ? where id = ?";
        try {
            PreparedStatement ps = Database.getPreparedStatement(sql);
            ps.setDate(1, Date.valueOf(task.getStartDate()));
            ps.setDate(2, Date.valueOf(task.getEndDate()));
            ps.setInt(3, task.getId());
            ps.executeUpdate();
            Database.commitStatement();
        } catch (SQLException e) {
            Database.rollbackStatement();
            throw new DaoException("Error : while trying to update dates for a task", e);
        }

    }

    /**
     * Set the task to done
     * @param id Id of the task
     * @param isDone is done
     * @throws DaoException DaoException
     */
    public static void complete(int id, boolean isDone) throws DaoException{
        String sql = "UPDATE task SET isDone = ? where id = ?";
        try {
            PreparedStatement ps = Database.getPreparedStatement(sql);
            ps.setBoolean(1, isDone);
            ps.setInt(2, id);
            ps.executeUpdate();
            Database.commitStatement();
        } catch (SQLException e) {
            Database.rollbackStatement();
            throw new DaoException("Error : while trying to update isDone for a task", e);
        }
    }

    /**
     * give the number of tasks left of a project
     * @param project concerned project
     * @return the number of tasks left or -1 if the project doesn't exist
     * @throws DaoException DaoException
     */
    public static int getNumberOfTaskLeft(Project project) throws DaoException {
        String getNumberOfTaskLeft = "SELECT COUNT(*) FROM Task where projectId = ? AND isDone = 0";
        try{
            PreparedStatement query = Database.getPreparedStatement(getNumberOfTaskLeft);
            query.setInt(1,project.getId());
            ResultSet rs=query.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
            return -1;
        }catch (SQLException e){
            throw new DaoException("Error : Cannot get the number of task(s) left of the project",e);
        }
    }
    /**
     * Insert tasks in the database.
     * @param tasks a list of tasks to add.
     * @param projectId assign the project the task refer to
     * @throws DaoException DaoException
     */
    public static void insertTasks(List<Task> tasks, int projectId) throws DaoException {
        if(tasks != null){
            for(Task task : tasks){
                task.setProjectId(projectId);
                TaskDao.insert(task);
            }
        }
    }

}
