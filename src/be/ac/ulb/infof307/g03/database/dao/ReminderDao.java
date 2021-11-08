package be.ac.ulb.infof307.g03.database.dao;

import be.ac.ulb.infof307.g03.database.Database;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.models.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReminderDao {

    /**
     * Insert
     * new reminder, and associate with the taskId or projectId
     * @param reminder new reminder
     * @return id of created reminder; -1 else
     */
    public static int insert(Reminder reminder) throws DaoException {
        int res=-1;
        String sqlInsert = "INSERT INTO reminder(projectId,taskId,date) VALUES(?,?,?)";
        PreparedStatement ps = Database.getPreparedStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
        try{
            if(reminder.getTaskId()==Reminder.NOTATASK){
                ps.setNull(2, Types.NULL);
            }else{
                ps.setInt(2, reminder.getTaskId());
            }
            ps.setInt(1,reminder.getProjectId());
            ps.setDate(3, Date.valueOf(reminder.getDate()));

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                res=rs.getInt(1);
            }
            Database.commitStatement();
        }catch (SQLException e){
            Database.rollbackStatement();
            throw new DaoException("Error : Cannot insert reminder in database", e);
        }
        return res;
    }


    /**
     * Retrieve all reminder associated to a user
     * @param currentUserProjects list of the projects associated to the user
     * @return a list of reminders linked to the project list
     * @throws DaoException DaoException
     */
    public static List<Reminder> getAllReminder(List<Project> currentUserProjects) throws DaoException{
        String sql = "SELECT * FROM reminder WHERE projectId = ?";
        List<Reminder> reminders = new ArrayList<>();
        try {
            for (Project project : currentUserProjects) {
                PreparedStatement ps = Database.getPreparedStatement(sql);
                ps.setInt(1, project.getId());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    rs.getInt("taskId");
                    if(rs.wasNull()) {
                        reminders.add(new Reminder(rs.getInt("id"),
                                rs.getInt("projectId"),
                                Reminder.NOTATASK,
                                rs.getDate("date").toLocalDate()));
                    }else{
                        reminders.add(new Reminder(rs.getInt("id"),
                                rs.getInt("projectId"),
                                rs.getInt("taskId"),
                                rs.getDate("date").toLocalDate()));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error : Could not retrieve Reminders", e);
        }
        return reminders;
    }


    /**
     * Report the reminder of a project to tomorrow
     * @param reminderId, the project to report
     *
     */
    public static void snooze(int reminderId) throws DaoException{
        String sql = "UPDATE reminder SET date = ? WHERE id = ?;";
        try {
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            PreparedStatement ps = Database.getPreparedStatement(sql);
            ps.setDate(1,java.sql.Date.valueOf(tomorrow));
            ps.setInt(2,reminderId);
            ps.executeUpdate();
            Database.commitStatement();
        } catch (SQLException e) {
            Database.rollbackStatement();
            throw new DaoException("Error : Cannot update the description task", e);
        }
    }


    /**
     * Update object Reminder in database
     * @param reminder, the reminder to update
     */
    public static void update(Reminder reminder) throws DaoException{
        String sql = "UPDATE reminder SET date = ? WHERE id = ?;";
        try {
            PreparedStatement ps = Database.getPreparedStatement(sql);
            ps.setDate(1,java.sql.Date.valueOf(reminder.getDate()));
            ps.setInt(2,reminder.getId());
            ps.executeUpdate();
            Database.commitStatement();
        } catch (SQLException e) {
            Database.rollbackStatement();
            throw new DaoException("Error : Cannot update the description task", e);
        }
    }


    /**
     * Get the Reminder link to a project in the DB
     * @param projectId id of the reminder's project to get in the DB
     * @return the reminder linked to the projectId
     */
    public static Reminder getByProjectId(int projectId) throws  DaoException{
        Reminder reminder=null;
        try {
            String sqlGet = "SELECT * FROM reminder WHERE projectId=? AND taskId IS NULL";
            PreparedStatement ps=Database.getPreparedStatement(sqlGet);
            ps.setInt(1,projectId);
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                reminder=new Reminder(rs.getInt("id"),
                        rs.getInt("projectId"),
                        Reminder.NOTATASK,
                        rs.getDate("date").toLocalDate()
                    );
            }
        }catch (SQLException e){
            throw new DaoException("Error : Cannot select the specific reminder", e);
        }
        return reminder;
    }
}
