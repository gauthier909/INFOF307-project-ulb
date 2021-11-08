package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.Notification;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.User;
import be.ac.ulb.infof307.g03.viewsControllers.AddCollaboratorViewController;
import be.ac.ulb.infof307.g03.viewsControllers.ViewLoaderSingleton;

import java.io.IOException;

/**
 * Handle the logic of adding a collaborator in a project.
 * This class is the listener of the {@link AddCollaboratorViewController.ViewListener}.<br>
 * This will manage the interaction on the AddCollaboratorView.fxml view.
 */
public class AddCollaboratorController implements AddCollaboratorViewController.ViewListener {
    /**
     * {@link Listener} referring to & implemented by the {@link CollaboratorManagementController}.
     */
    private final Listener listener;

    /**
     * This class is the listener of the {@link AddCollaboratorViewController}.
     */
    private AddCollaboratorViewController addCollaboratorViewController;

    /**
     * The current {@link Project} on which we will add a collaborator.
     */
    private final Project project;

    /**
     * Constructor for the AddCollaboratorController.
     * @param listener reference to the {@link CollaboratorManagementController}.
     * @param project the {@link Project} on which we add collaborator.
     */
    public AddCollaboratorController(Listener listener,Project project){
        this.listener = listener;
        this.project = project;
    }

    /**
     * Loading our view and setting our {@link AddCollaboratorViewController} listener.
     * @throws NavigationException if a {@link IOException} occurs.
     */
    public void show() throws NavigationException {
        addCollaboratorViewController = (AddCollaboratorViewController) ViewLoaderSingleton.getInstance().loadView("AddCollaboratorView.fxml");
        addCollaboratorViewController.setListener(this);

        addCollaboratorViewController.setProjectName(project.getName());
    }

    /**
     * This is the listener action for the add collaborator button.
     * This will try to add a {@link User} as collaborator to the current project.
     * First this will try to get the id of the user with notifiedUserName.<br>
     * Before sending a collaboration request it will checks that :<br>
     *
     *  - the current user cannot add himself as collaborator.<br>
     *  - the notifiedUserName match a user.<br>
     *  - the notifiedUserName is not already a collaborator.<br>
     *  - A notification hasn't already been sent to the user for the same project.<br>
     *
     *  In case of error, a message is displayed, otherwise the collaboration request is sent.
     *
     * @param notifiedUsername the userName to notify.
     */
    @Override
    public void onAskCollaborator(String notifiedUsername){
        try {
            User notifierUser = new User(notifiedUsername);
            int notifiedUserId = notifierUser.getIdFromUsername();
            notifierUser.setId(notifiedUserId);

            if (Helper.getCurrentUser().getUsername().equals(notifiedUsername)) {
                addCollaboratorViewController.showVisualFeedBack("You can't add yourself",false);
                return;
            }
            if (!notifierUser.isUserInDB()) {
                addCollaboratorViewController.showVisualFeedBack("This username doesn't exist",false);
                return;
            }

            if (notifierUser.isCollaborator(project)) {
                addCollaboratorViewController.showVisualFeedBack("This user is already a collaborator",false);
                return;
            }

            if(notifierUser.isNotificationPending(Helper.getCurrentUser().getId(), project)){
                addCollaboratorViewController.showVisualFeedBack("A notification has already been sent to this user.",false);
                return;
            }

            Helper.getCurrentUser().createNotification(notifierUser, project, Notification.NotificationType.ASK_COLLAB);

            addCollaboratorViewController.showVisualFeedBack("The invitation has been sent.", true);
        } catch(DaoException e){
            addCollaboratorViewController.showError("An error occurred", e.getMessage(), false);
        }

    }


    /**
     * This function is used by the returnButton to go back to the CollaboratorManagementView.fxml.
     * @throws NavigationException if the listener couldn't load the previous view.
     */
    @Override
    public void goBack() throws NavigationException {
        listener.navigateBack();
    }

    public void setAddCollaboratorViewController(AddCollaboratorViewController controller){
        this.addCollaboratorViewController = controller;
    }

    /**
     * This interface is implemented by the {@link CollaboratorManagementController} listener.
     */
    public interface Listener{
        /**
         * This function is used to show the view of the listener when going back from the add collaborator view.
         * @throws NavigationException if the listener couldn't load the previous view.
         */
        void navigateBack() throws NavigationException;
    }

}
