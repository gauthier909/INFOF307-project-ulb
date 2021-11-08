package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.Task;
import be.ac.ulb.infof307.g03.models.User;
import be.ac.ulb.infof307.g03.viewsControllers.AssignTaskViewController;
import be.ac.ulb.infof307.g03.viewsControllers.ViewLoaderSingleton;

import java.io.IOException;
import java.util.List;


/**
 * Handle the logic of assigning a task to a project.
 * This class is the listener of the {@link AssignTaskViewController.ViewListener}.<br>
 * This will manage the interaction on the AssignTaskView.fxml view.
 */
public class AssignTaskController implements AssignTaskViewController.ViewListener{
    /**
     * {@link TaskController.Listener} referring to & implemented by the {@link TaskController}.
     */
    private final Listener listener;

    /**
     * This class is the listener of the {@link AssignTaskViewController}.
     */
    private AssignTaskViewController assignTaskViewController;

    /**
     * The current {@link Task} on which we will add a user assignment.
     */
    private final Task task;

    /**
     * A list of user that are already assigned to the task.
     */
    private List<User> userListAssigned;

    /**
     * Constructor for the AddCollaboratorController.
     * @param listener reference to the {@link TaskController}.
     * @param task the {@link Project} on which we add collaborator.
     */
    public AssignTaskController(Listener listener, Task task){
        this.listener = listener;
        this.task = task;
    }


    /**
     * Loading our view and setting our {@link AssignTaskViewController} listener.
     * @throws NavigationException if a {@link IOException} occurs.
     */
    public void show() throws NavigationException {
        assignTaskViewController = (AssignTaskViewController) ViewLoaderSingleton.getInstance().loadView("AssignTaskView.fxml");
        assignTaskViewController.setListener(this);
        assignTaskViewController.setCurrentTaskName(task.getDescription());

        setup();

    }

    /**
     * setup the view.
     * Load/Setting up the lists of users.
     */
    public void setup() {
        try {
            userListAssigned = task.getAllUserByTaskId();
            assignTaskViewController.setAssignedUsers(userListAssigned);
        }catch(DaoException e){
            assignTaskViewController.showError("An error occurred", e.getMessage(), false);
        }

        try {
            Project project = task.getMyProject();
            /*
              A list of user that can be assigned to the task.
             */
            List<User> userList = project.getUserByProject();
            assignTaskViewController.SetUsers(userList);
        } catch (DaoException e) {
            assignTaskViewController.showError("An error occurred", e.getMessage(), false);
        }
    }


    /**
     * This function is used by the {@link AssignTaskViewController} to delete a user assignment to the task.
     * @param user on which we want to remove the task assignment.
     */
    @Override
    public void deleteUser(User user){
        try {
            user.deleteAssignedTask(this.task.getId());
        } catch (DaoException e) {
            assignTaskViewController.showError("An error occurred", e.getMessage(), false);
        }
        userListAssigned.removeIf(u -> user.getId() == u.getId());
        assignTaskViewController.setAssignedUsers(userListAssigned);
    }

    /**
     * This function is called by the returnButton in the {@link AssignTaskViewController} to go back to the TaskView.fxml.
     * @throws NavigationException if the listener couldn't load the previous view.
     */
    @Override
    public void goBack() throws NavigationException {
        listener.navigateBack();
    }

    /**
     * This function is called by the assignButton to assign a task to a user.
     * @param user on which we add a task assignment.
     */
    @Override
    public void onAssignTask(User user) {
        try {
            user.insertAssignedTask(task);
            assignTaskViewController.setAssignedUsers(task.getAllUserByTaskId());
        }catch(DaoException e){
            assignTaskViewController.showError("An error occurred", e.getMessage(), false);
        }
    }

    public void setAssignTaskViewController(AssignTaskViewController viewController){
        this.assignTaskViewController = viewController;
    }

    public interface Listener{
        /**
         * This function is used to show the view of the listener when going back from the assign task view.
         * @throws NavigationException if the listener couldn't load the previous view.
         */
        void navigateBack() throws NavigationException;
    }
}
