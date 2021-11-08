package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.database.dao.cloud.GoogleDao;
import be.ac.ulb.infof307.g03.exceptions.CloudException;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.Reminder;
import be.ac.ulb.infof307.g03.models.Task;
import be.ac.ulb.infof307.g03.viewsControllers.TaskViewController;
import be.ac.ulb.infof307.g03.viewsControllers.ViewLoaderSingleton;
import javafx.collections.FXCollections;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Handle all the logic related to tasks
 * This class is the listener of the {@link TaskViewController.ViewListener}.<br>
 * This will manage the interaction on the TaskView.fxml view.
 */
public class TaskController implements TaskViewController.ViewListener, AssignTaskController.Listener, ChangeDateController.Listener {

    private final Project project;
    private ArrayList<Task> tasks;
    private TaskViewController taskViewController;
    private final Listener listener;

    /**
     * Constructor of the TaskController
     * @param listener listener of the controller
     * @param project project which have tasks
     */
    public TaskController(Listener listener, Project project){
        this.project = project;
        this.listener = listener;
    }

    /**
     * Show the TaskView.fxml of a project
     * @throws NavigationException nav exception
     */
    public void show() throws NavigationException{
        taskViewController = (TaskViewController) ViewLoaderSingleton.getInstance().loadView("TaskView.fxml");
        taskViewController.setListener(this);

        taskViewController.setCurrentProjectName(project.getName());
        taskViewController.setDefaultDate(project.getDateEnd());
        try {
            tasks = (ArrayList<Task>) project.getAllTasks();
        } catch (DaoException e) {
            taskViewController.showError("An error occurred", e.getMessage(), false);
        }
        taskViewController.setTasks(FXCollections.observableList(tasks));

    }


    @Override
    public void onAddTask(String description, LocalDate startDate, LocalDate endDate, int reminderValue, boolean isGoogleSelected) {
        if (description == null || description.trim().isEmpty()) return;
        if(isGoogleSelected) addToGoogleCalendar(description, startDate, endDate);
        Task task = new Task(description, project.getId(), startDate, endDate, false);
        try {
            task.insert();
            tasks.add(task);
            taskViewController.setTasks(tasks);
            taskViewController.clearDescription();
            Reminder reminder = new Reminder(project.getId(), task.getId(), endDate.minusDays(reminderValue));
            reminder.insert();
        } catch (DaoException e) {
            taskViewController.showError("An error occurred", e.getMessage(), false);
        }

    }

    /**
     * Add the task to the google calendar through the GoogleDao class
     * @param description description of the task to add
     * @param startDate Date of the start of the task
     * @param endDate Date of the end of the task
     */
    private void addToGoogleCalendar(String description, LocalDate startDate, LocalDate endDate){
        try {
            GoogleDao googleDao = new GoogleDao();
            googleDao.addToCalendar(description, startDate, endDate);
        } catch (CloudException | IOException e) {
            taskViewController.showError("An error occurred", e.getMessage(), false);
        }
    }

    @Override
    public void goBack() throws NavigationException {
        listener.navigateBack();
    }

    /**
     * Navigate back
     * @throws NavigationException of a {@link IOException} occurs
     */
    @Override
    public void navigateBack() throws NavigationException {
        show();
    }

    /**
     * Delete a task from the database and all the assignments to this task
     * @param task task to delete
     */
    @Override
    public void deleteTask(Task task) {
        try {
            task.deleteAllAssignment();
            task.delete();
        } catch (DaoException e) {
            taskViewController.showError("An error occurred", e.getMessage(), false);
        }
        tasks.removeIf(t -> task.getId() == t.getId());
        taskViewController.setTasks(tasks);
    }

    /**
     * Show the assign task view
     * @param task task to assign
     */
    @Override
    public void onAssignTask(Task task){
        AssignTaskController controller = new AssignTaskController(this,task);
        try {
            controller.show();
        } catch (NavigationException e) {
            taskViewController.showError("An error occurred", "Error when switching windows", false);
        }
    }

    @Override
    public void onEditDates(Task task) {
        ChangeDateController ChangeDateController = new ChangeDateController(this,task, project.getDateEnd());
        try {
            ChangeDateController.show();
        } catch (NavigationException e) {
            taskViewController.showError("An error occurred", e.getMessage());
        }
    }



    /**
     * Update the description of a task
     * @param task to update
     */
    @Override
    public void modifyTask(Task task){
        tasks.stream().filter(t -> task.getId() == t.getId()).findFirst().ifPresent(currentTask -> currentTask.setDescription(task.getDescription()));
        try{
            task.updateDescription();
        } catch (DaoException e) {
            taskViewController.showError("An error occurred",e.getMessage(),false);
        }
    }

    @Override
    public void completeTask(Task task){
        try {
            task.complete();
        } catch (DaoException e) {
            taskViewController.showError("An error occurred", e.getMessage(), false);
        }
    }

    public interface Listener{
        /**
         * Navigate back
         * @throws NavigationException nav exception
         */
        void navigateBack() throws NavigationException;
    }
}
