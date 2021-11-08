package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.User;
import be.ac.ulb.infof307.g03.utils.Utils;
import be.ac.ulb.infof307.g03.viewsControllers.EditProfileViewController;
import be.ac.ulb.infof307.g03.viewsControllers.ViewLoaderSingleton;

import java.io.File;
import java.io.IOException;

/**
 * Handle the logic of editing the profile of the user
 * This class is the listener of the {@link EditProfileViewController.ViewListener}.<br>
 * This will manage the interaction on the EditProfileView.fxml view.
 */
public class EditProfileController implements EditProfileViewController.ViewListener{
    private EditProfileViewController editProfileViewController;
    private final Listener listener;

    /**
     * Constructor for the EditProfileController
     * @param listener listener of th controller
     */
    public EditProfileController(Listener listener) {
        this.listener = listener;
    }

    /**
     * Setup and show the EditProfileView.fxml
     * @throws NavigationException nav exception
     */
    public void show() throws NavigationException {
        editProfileViewController = (EditProfileViewController) ViewLoaderSingleton.getInstance().loadView("EditProfileView.fxml");
        editProfileViewController.setListener(this);
    }


    @Override
    public void goBack() throws NavigationException {
        listener.navigateBack();
    }

    @Override
    public void confirm(String username, String password, String confirmPassword, String email, String firstName, String lastName) {
        String errors = Utils.checkValidity(username,
                password,
                confirmPassword,
                email,
                firstName,
                lastName);


        if (errors.length() != 0) {
            editProfileViewController.setErrorLabel(errors);
        } else {
            boolean userInDB = false;
            boolean emailInDB = false;

            User currentUser = Helper.getCurrentUser();
            if (!(username.equals(currentUser.getUsername()))) {
                try {
                    userInDB = currentUser.isUserInDB(username);
                } catch (DaoException e) {
                    editProfileViewController.showError("An error occurred", e.getMessage(), false);
                }
            }
            if (!(email.equals( currentUser.getEmail()))) {
                try {
                    emailInDB = currentUser.isEmailInDB(email);
                } catch (DaoException e) {
                    editProfileViewController.showError("An error occurred", e.getMessage(), false);
                }
            }

            if (!userInDB && !emailInDB) {
                try {
                    String oldUsername = currentUser.getUsername();
                    currentUser.modifyUser(username,password,email,firstName,lastName);
                    currentUser.modifyAll(username,firstName,lastName,email);
                    renameUserFolder(oldUsername,currentUser.getUsername());
                } catch (DaoException e) {
                    editProfileViewController.showError("An error occurred", e.getMessage(), false);
                }
                editProfileViewController.notEdit();
            } else {
                if (userInDB) errors += "Username already exists.\n";
                if (emailInDB) errors += "Email already exists.\n";
                editProfileViewController.setErrorLabel(errors);
            }
        }
    }

    /**
     * Rename the user folder
     * @param oldUsername old username
     * @param newUsername new username
     */
    private boolean renameUserFolder(String oldUsername,String newUsername){
        File sourceFile = new File("users/"+"user_"+oldUsername);
        File destFile = new File("users/"+"user_"+newUsername);
        if(!sourceFile.exists())
            return false;
        return sourceFile.renameTo(destFile);
    }

    public void setEditProfilViewController(EditProfileViewController editProfileViewController) {
        this.editProfileViewController = editProfileViewController;
    }

    public interface Listener{
        /**
         * Go back
         * @throws NavigationException nav exception
         */
        void navigateBack() throws NavigationException;
    }
}
