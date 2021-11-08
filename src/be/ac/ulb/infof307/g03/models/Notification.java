package be.ac.ulb.infof307.g03.models;

import be.ac.ulb.infof307.g03.database.dao.NotificationDao;
import be.ac.ulb.infof307.g03.database.dao.project.ProjectDaoGetter;
import be.ac.ulb.infof307.g03.exceptions.DaoException;

/**
 * Class representing a notification
 */
public class Notification {
    private final int id;
    private final String message;
    private final NotificationType type;
    private final int senderId;
    private final int receiverId;
    private final int projectId;

    /**
     * Constructor for notification
     * @param id the id of the notification
     * @param message the message of the notification
     * @param type the type of the notification
     * @param receiverId the id of the receiving user
     * @param senderId the id of the sender user
     * @param projectId the id of the project
     */
    public Notification(int id, String message, String type, int receiverId, int senderId, int projectId) {
        this.id = id;
        this.message = message;
        this.type = type.equals(NotificationType.ASK_COLLAB.toString()) ? NotificationType.ASK_COLLAB : NotificationType.ACCEPT_COLLAB;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.projectId = projectId;
    }

    /**
     * Get the message of the notification
     * @return message of the notication
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the type of a notification
     * @return type of the notification
     */
    public NotificationType getType() {
        return type;
    }

    /**
     * Return the id of the notification
     * @return id of the notification
     */
    public int getId() { return id; }

    /**
     * Return the id of the user that sends the notification
     * @return id of the sender
     */
    public int getSenderId() {
        return senderId;
    }

    /**
     * Get a the project's id
     * @return id of the project
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * Delete the notification for the user
     * @throws DaoException DaoException
     */
    public void delete() throws DaoException {
        NotificationDao.deleteNotification(this);
    }

    /**
     * Get associated project to a project id
     * @return the associated project
     * @throws DaoException DaoException
     */
    public Project getAssociatedProject() throws DaoException {
        return ProjectDaoGetter.getById(this.getProjectId());
    }

    /**
     * Create a notification
     * @param sender user that needs to receive the notification
     * @throws DaoException DaoException
     */
    public void AcceptNotification(User sender) throws DaoException {
        NotificationDao.createNotification(this.getSenderId(), sender, this.getAssociatedProject(), Notification.NotificationType.ACCEPT_COLLAB);
    }

    public int getReceiverId() {
        return receiverId;
    }

    /**
     * Type of the notification which is either an invitation or an accept (if the user has accepted to be a collaborator of the project)
     */
    public enum NotificationType{
        ASK_COLLAB ("ASK_COLLAB"),
        ACCEPT_COLLAB ("ACCEPT_COLLAB");

        private final String type;

        NotificationType(String type){
            this.type= type;
        }

        public String toString(){
            return this.type;
        }

    }
}
