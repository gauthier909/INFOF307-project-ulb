package be.ac.ulb.infof307.g03.database.dao;

import be.ac.ulb.infof307.g03.database.Database;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.models.Notification;
import be.ac.ulb.infof307.g03.models.Notification.NotificationType;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access class of the table notification
 */
public class NotificationDao {

    private static final String askCollaboratorMsg = "User %s invited you to join project : %s";
    private static final String acceptedMsg = "User %s accepted to join project : %s";

    /**
     * Create a notification for a collaboration
     * @param receiverId if of the receiver of the collaboration
     * @param sender the sender of the request
     * @param project the project concern
     */
    public static void createNotification(int receiverId, User sender, Project project, NotificationType type) throws DaoException {
        String sql = "INSERT INTO notification(message, type, sender_id, receiver_id, project_id) VALUES (?,?,?,?,?)";
        PreparedStatement ps = Database.getPreparedStatement(sql);
        try{
            ps.setInt(3, sender.getId());
            ps.setInt(4, receiverId);
            ps.setInt(5, project.getId());
            if(type.equals(NotificationType.ASK_COLLAB)){
                ps.setString(1, String.format(askCollaboratorMsg, sender.getUsername(), project.getName()));
                ps.setString(2, NotificationType.ASK_COLLAB.toString());
            }else{
                ps.setString(1, String.format(acceptedMsg, sender.getUsername(), project.getName()));
                ps.setString(2, NotificationType.ACCEPT_COLLAB.toString());
            }
            ps.executeUpdate();
            Database.commitStatement();
        } catch (SQLException e){
            Database.rollbackStatement();
            throw new DaoException("Error while trying to create a notification",e);
        }
    }

    /**
     * Retrieve all notifications associated to a user
     * @param userId id of the associated user
     * @return a list of notification linked to the user
     * @throws DaoException DaoException
     */
    public static List<Notification> getAllNotification(int userId) throws DaoException {
        String sql = "SELECT * FROM notification where receiver_id = ?";
        PreparedStatement ps = Database.getPreparedStatement(sql);
        List<Notification> notifications = new ArrayList<>();
        try{
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                notifications.add(new Notification(rs.getInt("id"),
                        rs.getString("message"),
                        rs.getString("type"),
                        rs.getInt("receiver_id"),
                        rs.getInt("sender_id"),
                        rs.getInt("project_id")));
            }
        }catch(SQLException e){
            throw new DaoException("Error while trying to get notification",e);
        }
        return notifications;
    }

    /**
     * Check if a notification is pending.
     * @param senderId the id of the user that should have sent the notification
     * @param receiverId the id of the user that should have received the notification
     * @param projectId the id of the project that should have been notified
     * @return the value of the notification flag;
     * @throws DaoException DaoException
     */
    public static boolean isNotificationPending(int senderId, int receiverId ,int projectId) throws DaoException{
        String sql = "SELECT COUNT(*) AS notified FROM notification WHERE sender_id = ? AND receiver_id = ? AND project_id = ?";
        PreparedStatement query = Database.getPreparedStatement(sql);
        try {
            query.setInt(1,senderId);
            query.setInt(2,receiverId);
            query.setInt(3,projectId);
            ResultSet rs = query.executeQuery();
            if (rs.next()) {
                return rs.getInt("notified") > 0;
            }
        }catch (SQLException e){
            throw new DaoException("Error : Cannot check pending collaboration", e);
        }
        return false;
    }


    /**
     * Delete a notification from the database
     * @param notification The Notification object that has been handled
     * @throws DaoException if @{@link SQLException} occurs
     */
    public static void deleteNotification(Notification notification) throws DaoException {
        String sql = "DELETE FROM notification WHERE id=?";
        PreparedStatement ps = Database.getPreparedStatement(sql);
        try {
            ps.setInt(1, notification.getId());
            ps.executeUpdate();
            Database.commitStatement();
        } catch (SQLException e) {
            Database.rollbackStatement();
            throw new DaoException("Error,failed deleting notification from database",e);
        }
    }
}
