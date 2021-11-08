package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.models.User;
import be.ac.ulb.infof307.g03.utils.Utils;
import be.ac.ulb.infof307.g03.viewsControllers.RegisterViewController;
import be.ac.ulb.infof307.g03.viewsControllers.ViewLoaderSingleton;

/**
 * Handle the logic of registering a user.
 * This class  is the listener of the {@link RegisterViewController.ViewListener}.<br>
 * This will manage the interaction on the RegisterView.fxml view.
 */
public class RegisterController implements RegisterViewController.ViewListener{

    private final Listener listener;
    private RegisterViewController registerViewController;

    /**
     * Constructor of the RegisterController
     * @param listener of this class
     */
    public RegisterController(Listener listener) {
        this.listener=listener;
    }

    /**
     * Setup and show the RegisterView.fxml
     * @throws NavigationException nav exception
     */
    public void show() throws NavigationException {
        registerViewController = (RegisterViewController) ViewLoaderSingleton.getInstance().loadView("RegisterView.fxml");
        registerViewController.setListener(this);
    }

    /**
     * Register a new user
     * Verify the given data for the new user
     */
    @Override
    public void register(String username, String firstName, String lastName, String email, String password, String confirmPassword) {
        String errors = Utils.checkValidity(username,
                password,
                confirmPassword,
                email,
                firstName,
                lastName);


        if (errors.length() != 0){
            registerViewController.setWrongInfo(errors);
        }
        else{
            boolean userInDB = false;
            User user = new User(username, firstName, lastName, email);

            try {
                userInDB = user.isUserInDB();
            } catch (DaoException e) {
                registerViewController.showError("An error occurred", e.getMessage(), false);
            }
            boolean emailInDB = false;
            try {
                emailInDB = user.isEmailInDB();
            } catch (DaoException e) {
                registerViewController.showError("An error occurred", e.getMessage(), false);
            }
            if(!userInDB && !emailInDB){

                try {
                    user.register(password);
                } catch (DaoException e) {
                    registerViewController.showError("An error occurred", e.getMessage(), false);
                }
                listener.signIn();
            }
            else{
                if (userInDB) errors += "Username already exists.\n";
                if (emailInDB) errors += "Email already exists.\n";
                registerViewController.setWrongInfo(errors);
            }
            }
        }

    @Override
    public void goBack() throws  NavigationException{
            this.listener.navigateBack();
    }

    public void setViewController(RegisterViewController registerViewController) {
        this.registerViewController = registerViewController;
    }


    public interface Listener{
        /**
         * Sign in
         */
        void signIn();

        /**
         * Navigate back
         * @throws NavigationException nav exception
         */
        void navigateBack() throws NavigationException;
    }
}
