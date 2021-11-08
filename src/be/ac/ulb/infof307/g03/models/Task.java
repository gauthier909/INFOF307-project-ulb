package be.ac.ulb.infof307.g03.models;

import be.ac.ulb.infof307.g03.database.dao.project.ProjectDaoGetter;
import be.ac.ulb.infof307.g03.database.dao.TaskDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing a task
 */
public class Task {
    private int id;
    private int projectId;
    private boolean isDone;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Task constructor
     *
     * @param id          the id of the task
     * @param description the description of the task
     * @param projectId   the id of the project the task is assigned to
     * @param startDate   the start date of a task, initially on the start date of a project
     * @param endDate     the end date of the task, initially on the end date of project
     * @param isDone     true if the task has been completed else false
     */
    public Task(int id, String description, int projectId, LocalDate startDate, LocalDate endDate, boolean isDone) {
        this.id = id;
        this.description = description;
        this.projectId = projectId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isDone = isDone;
    }

    /**
     * Task constructor without id
     *
     * @param description the description of the task
     * @param projectId   the id of the project the task is assigned to
     * @param startDate   the start date of a task, initially on the start date of a project
     * @param endDate     the end date of the task, initially on the end date of project
     * @param isDone     true if the task has been completed else false
     */
    public Task(String description, int projectId, LocalDate startDate, LocalDate endDate, boolean isDone) {
        this.description = description;
        this.projectId = projectId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isDone = isDone;
    }

    /**
     * Return the difference between 2 tasks
     *
     * @param other task to compare with
     * @return Map of String containing fields that differs between the 2 tasks
     */
    public Map<String, String> diff(Task other) {
        Map<String, String> map = new HashMap<>();
        if (!description.equals(other.description)) map.put("Task " + id + " description", description + ", " + other.description);
        if (startDate.compareTo(other.startDate) != 0) map.put("Task " + id + " startDate", startDate + ", " + other.startDate);
        if (endDate.compareTo(other.endDate) != 0) map.put("Task " + id + " endDate", endDate + ", " + other.endDate);
        return map;
    }

    /**
     * Get the id of the task
     *
     * @return id of the task
     */
    public int getId() {
        return id;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * Set the id of the task
     *
     * @param id id to be set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the description of the task
     *
     * @return description of the task
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the id of the project
     *
     * @return id of the project
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * Set the id of the project
     *
     * @param projectId id of the project
     */
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    /**
     * Set the description of the project
     *
     * @param description description of the project
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get all users associated to the id of the task
     *
     * @return list of users associated to the task
     * @throws DaoException DaoException
     */
    public List<User> getAllUserByTaskId() throws DaoException {
        return TaskDao.getAllUserByTaskId(this.getId());
    }

    /**
     * Get the project associated to the task
     *
     * @return project
     * @throws DaoException DaoException
     */
    public Project getMyProject() throws DaoException {
        return ProjectDaoGetter.getProjectById(this.getProjectId());
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    /**
     * Insert a new task and set his id
     *
     * @throws DaoException DaoException
     */
    public void insert() throws DaoException {
        int taskId = TaskDao.insert(this);
        this.setId(taskId);
    }

    /**
     * Delete a task
     *
     * @throws DaoException DaoException
     */
    public void delete() throws DaoException {
        TaskDao.delete(this.getId());
    }

    /**
     * Complete a task i.e. the task is now done
     */
    public void complete() throws DaoException {
        TaskDao.complete(this.getId(), !isDone());
        setDone(!isDone());
    }

    /**
     * Delete all the assignments for the task
     * @throws DaoException DaoException
     */
    public void deleteAllAssignment() throws DaoException {
        TaskDao.deleteAllAssignmentByTaskId(this.getId());
    }

    /**
     * Update the description of the task
     * @throws DaoException DaoException
     */
    public void updateDescription() throws DaoException {
        TaskDao.updateDescription(this);
    }

    /**
     * Update the dates of this task
     * @throws DaoException DaoException
     */
    public void updateDates() throws DaoException{
        TaskDao.updateDates(this);
    }

    public LocalDate getStartDate() { return startDate; }

    public LocalDate getEndDate() { return endDate; }
}
