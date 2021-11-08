package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.User;
import be.ac.ulb.infof307.g03.viewsControllers.CollaboratorManagementViewController;
import be.ac.ulb.infof307.g03.viewsControllers.ViewLoaderSingleton;

import java.util.ArrayList;

/**
 * Handle the logic of the management of a collaborator.
 * This class will manage information about collaborators and is the listener of the {@link CollaboratorManagementViewController.ViewListener}.<br>
 * This will manage the interaction on the CollaboratorManagement.fxml view.
 */
public class CollaboratorManagementController implements CollaboratorManagementViewController.ViewListener, AddCollaboratorController.Listener {
    private final Project project;
    private CollaboratorManagementViewController collaboratorManagementViewController;
    private final Listener listener;
    private ArrayList<User> collaborators;

    /**
     * Constructor for the CollaboratorManagementController
     * @param listener listener of the controller
     * @param project the project
     */
    public CollaboratorManagementController(Listener listener, Project project) {
        this.project = project;
        this.listener = listener;
    }


    /**
     * Setup and show the CollaboratorManagementView
     * @throws NavigationException nav exception
     */
    public void show() throws NavigationException {
        collaboratorManagementViewController = (CollaboratorManagementViewController) ViewLoaderSingleton.getInstance().loadView("CollaboratorManagementView.fxml");
        collaboratorManagementViewController.setListener(this);

        setup();

    }

    /**
     * setup the view
     */
    public void setup() {
        try{
            collaborators = (ArrayList<User>) project.getAllCollaborators(Helper.getCurrentUser().getId());
            if (collaborators.size() > 0) {
                collaboratorManagementViewController.setCollaboratorTable(collaborators);
            }
        }catch (DaoException e){
            collaboratorManagementViewController.showError("An error occurred", e.getMessage(), false);
        }
    }

    @Override
    public void goBack() {
        try {
            listener.navigateBack();
        } catch (NavigationException e) {
            collaboratorManagementViewController.showError("An error occurred", e.getMessage(), false);
        }
    }

    @Override
    public void deleteCollaborator(User user) {
        try {
            project.removeCollaborator(user.getId());
            collaborators.remove(user);
            collaboratorManagementViewController.setCollaboratorTable(collaborators);
        }catch(DaoException e){
            collaboratorManagementViewController.showError("An error occurred", e.getMessage(), false);
        }
    }

    @Override
    public void onAddCollaborator() {
        AddCollaboratorController addCollaboratorController = new AddCollaboratorController(this, project);
        try {
            addCollaboratorController.show();
        } catch (NavigationException e) {
            collaboratorManagementViewController.showError("An error occurred", e.getMessage(), false);
        }
    }

    /**
     * This function is used to show the view of the listener when going back.
     * @throws NavigationException if the listener couldn't load the previous view.
     */
    @Override
    public void navigateBack() throws NavigationException {
        show();
    }

    public void setViewController(CollaboratorManagementViewController collaboratorManagementViewController) {
        this.collaboratorManagementViewController = collaboratorManagementViewController;
    }

    public interface Listener{
        /**
         * View navigator to the main menu.
         */
        void navigateBack() throws NavigationException;
    }
}
