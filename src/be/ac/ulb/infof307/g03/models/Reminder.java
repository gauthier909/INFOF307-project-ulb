package be.ac.ulb.infof307.g03.models;
import be.ac.ulb.infof307.g03.database.dao.ReminderDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;

import java.time.LocalDate;


public class Reminder {

    private int id;
    private final int projectId;
    private final int taskId;
    private LocalDate date;
    public static final int NOTATASK = -1; //in case where no task associated

    public Reminder(int projectId, int taskId, LocalDate date) {
        this.projectId = projectId;
        this.taskId = taskId;
        this.date = date;
    }

    public Reminder(int id,int projectId, int taskId, LocalDate date) {
        this.id = id;
        this.projectId = projectId;
        this.taskId = taskId;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public int getTaskId() {
        return taskId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString(){
        return "id: " + this.id + "projectId: " + this.projectId + "taskId: " + this.taskId + "date: "+ this.date.toString();
    }

    /**
     * insert this reminder in the database
     * @throws DaoException DaoException
     */
    public void insert() throws DaoException {
        int id = ReminderDao.insert(this);
        this.setId(id);

    }

    /**
     * Report the reminder for a day
     * @throws DaoException DaoException
     */
    public void snooze() throws DaoException {
        ReminderDao.snooze(this.getId());
    }

    /**
     * update this reminder in the database
     * @throws DaoException DaoException
     */
    public void update() throws DaoException{
        ReminderDao.update(this);
    }

}
