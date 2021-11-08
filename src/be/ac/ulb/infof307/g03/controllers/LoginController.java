package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.database.dao.user.UserGetterDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.User;
import be.ac.ulb.infof307.g03.viewsControllers.LoginViewController;
import be.ac.ulb.infof307.g03.viewsControllers.ViewLoaderSingleton;

/**
 * Handle the logic of the login of a user
 * This class is the listener of the {@link LoginViewController.ViewListener}.<br>
 * This will manage the interaction on the LoginView.fxml view.
 */
public class LoginController extends BaseController implements LoginViewController.ViewListener{

    private final Listener listener;
    private LoginViewController loginViewController;

    /**
     * Constructor for a LoginController
     * @param listener the listener of this controller
     */
    public LoginController(Listener listener) {this.listener = listener;}

    /**
     * Setup and show the LoginView.fxml
     * @throws NavigationException nav exception
     */
    public void show() throws NavigationException {
        loginViewController = (LoginViewController) ViewLoaderSingleton.getInstance().loadView("LoginView.fxml");
        loginViewController.setListener(this);
    }

    /**
     * View navigator to the welcome menu.
     */
    @Override
    public void goBack() {
        try{
            this.listener.navigateBack();
        } catch (NavigationException e) {
            loginViewController.showError("An error occurred", e.getMessage(), false);
        }
    }

    /**
     * Login the user to the application, if information given match with an existing user.
     * Else, an error message is shown to the user.
     * @param username Username
     * @param password User password
     */
    @Override
    public boolean login(String username, String password) throws DaoException {
        if (UserGetterDao.checkIfConnected() == 1) {
            loginViewController.setWrongInfo("A user is already connected.");
            return false;
        }
        User currentUser = new User(username);
        currentUser = currentUser.loadFromDB(password);
        if (currentUser == null) {
            loginViewController.setWrongInfo("Invalid password or username.");
            return false;
        }
        currentUser.login();
        Helper.setCurrentUser(currentUser);
        createDir(currentUser.getUsername());
        return true;
    }

    /**
     * Load home controller
     */
    @Override
    public void loadHomeController(){
        HomeController controller = new HomeController();
        try {
            controller.show();
        } catch (NavigationException e) {
            loginViewController.showError("An error occurred", e.getMessage(), false);
        }
    }

    public void setLoginViewController(LoginViewController loginViewController) {
        this.loginViewController = loginViewController;
    }

    public interface Listener{
        /**
         * Navigate to the welcome view
         * @throws NavigationException nav exception
         */
        void navigateBack() throws NavigationException;
    }

}
