package be.ac.ulb.infof307.g03.models;

import be.ac.ulb.infof307.g03.database.dao.*;
import be.ac.ulb.infof307.g03.database.dao.project.ProjectDaoGetter;
import be.ac.ulb.infof307.g03.database.dao.user.UserGetterDao;
import be.ac.ulb.infof307.g03.database.dao.user.UserSetterDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;

import java.util.List;
import java.util.Objects;

/**
 * Class representing a user
 */
public class User {

    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    /**
     * User constructor
     * @param id the id of the user
     * @param username the username of the user
     * @param firstName the firstname of the user
     * @param lastName the lastname of the user
     * @param email the email of the user
     */
    public User(int id, String username, String firstName, String lastName, String email) {
        this.id= id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public User(String username, String firstName, String lastName, String email) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * User constructor
     * @param username the username of a user
     */
    public User(String username){
        this.username = username;
    }

    /**
     * User constructor
     * @param username username of the user
     * @param email email of the user
     */
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public User(int id){
        this.id = id;
    }

    /**
     * Get all projects from the current user.
     * @return the list containing all the projects
     * @throws DaoException DaoException
     */
    public List<Project> getProjects() throws DaoException { return ProjectDaoGetter.getAllFromCurrentUser(this.getId()); }

    /**
     * Set username
     * @param username value of username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Set firstName
     * @param firstName value of firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Sets lastName
     *
     * @param lastName value of lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Set email
     * @param email value of email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get username
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get firstName
     * @return firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Get lastName
     * @return lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Get email
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set id
     */
    public void setId(int id){ this.id = id;}

    /**
     * Get id
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Modify the data of the user
     * @param username the username of the user that could have been modified
     * @param firstName the firstname of the user that could have been modified
     * @param lastName the lastname of the user that could have been modified
     * @param email the email of the user that could have been modified
     */
    public void modifyAll(String username, String firstName, String lastName, String email){
        this.setUsername(username);
        this.setEmail(email);
        this.setFirstName(firstName);
        this.setLastName(lastName);
    }

    /**
     * Delete assigned tasks
     * @param taskId id of the task
     * @throws DaoException DaoException
     */
    public void deleteAssignedTask(int taskId) throws DaoException {
        TaskDao.deleteAssignment(this.getId(), taskId);
    }

    /**
     * Insert a new assigned task
     * @param task task to be assigned
     * @throws DaoException DaoException
     */
    public void insertAssignedTask(Task task) throws DaoException {
        TaskDao.insertAssignedTask(task,this);
    }

    /**
     * Check if a user is in the database
     * @return true or false depending if the user is in the database or not
     * @throws DaoException DaoException
     */
    public boolean isUserInDB() throws DaoException {
        return UserGetterDao.isUserInDB(this.username);
    }

    /**
     * Check if a given username is in the database
     * @param username username to check
     * @return true or false depending if the username is in the database or not
     * @throws DaoException DaoException
     */
    public boolean isUserInDB(String username) throws DaoException {
        return UserGetterDao.isUserInDB(username);
    }

    /**
     * Check if an user is in the database
     * @return true or false depending if the user is in the database or not
     * @throws DaoException DaoException
     */
    public boolean isEmailInDB() throws DaoException {
        return UserGetterDao.isEmailInDB(this.email);
    }

    /**
     * Check if an email is in the database
     * @param email email to check
     * @return true or false depending if the email is in the database or not
     * @throws DaoException DaoException
     */
    public boolean isEmailInDB(String email) throws DaoException {
        return UserGetterDao.isEmailInDB(email);
    }

    /**
     * Modify an user's information
     * @param username username to be modified
     * @param password password to be modified
     * @param email email to be modified
     * @param firstName firstName to be modified
     * @param lastName lastName to be modified
     * @throws DaoException DaoException
     */
    public void modifyUser(String username, String password, String email, String firstName, String lastName) throws DaoException {
        UserSetterDao.modifyUser(this, username,password,email,firstName,lastName);
    }

    /**
     * Get all notifications of the project
     * @return all notifications of the project
     * @throws DaoException dao exception
     */
    public List<Notification> getAllNotification() throws DaoException {
        return NotificationDao.getAllNotification(this.getId());

    }

    /**
     * Logout the user
     * @throws DaoException DaoException
     */
    public void logout() throws DaoException {
        UserSetterDao.setUserConnection(this.getId(),false);

    }

    /**
     * Load an user from the database
     * @param password password of the user
     * @return the user found
     * @throws DaoException DaoException
     */
    public User loadFromDB(String password) throws DaoException {
        return UserGetterDao.getUserFromDb(this.username, password);
    }

    /**
     * Login the user
     * @throws DaoException DaoException
     */
    public void login() throws DaoException {
        UserSetterDao.setUserConnection(this.getId(),true);
    }

    public int getIdFromUsername() throws DaoException {
        return UserGetterDao.getIdFromUsername(this.getUsername());

    }

    public boolean isCollaborator(Project project) throws DaoException {
        return CollaboratorDao.isCollaborator(this.getId(), project.getId());
    }

    public boolean isNotificationPending(int senderId, Project project) throws DaoException {
        return NotificationDao.isNotificationPending(senderId,this.getId(), project.getId());
    }

    public void createNotification(User notifierUser, Project project, Notification.NotificationType askCollab) throws DaoException {
        NotificationDao.createNotification(notifierUser.getId(),this, project, askCollab);
    }

    public void checkout(Branch branch) throws DaoException {
        BranchDao.switchBranch(this, branch);
    }

    public List<Tag> getAllTags() throws DaoException {
        return TagDao.getAll(this.getId());
    }

    /**
     * Register this user
     * @param password the password
     * @throws DaoException DaoException
     */
    public void register(String password) throws DaoException {
        UserSetterDao.registerUser(this.getUsername(),
                password,
                this.getEmail(),
                this.getFirstName(),
                this.getLastName());
    }

    /**
     * Link the current user to the project id
     * @param id project id
     * @throws DaoException dao exception
     */
    public void linkToProject(int id, boolean isParent) throws DaoException{
        UserSetterDao.linkToProject(this, id, isParent);
    }
}
