package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.viewsControllers.ViewLoaderSingleton;
import be.ac.ulb.infof307.g03.viewsControllers.WelcomeViewController;

import java.io.IOException;

/**
 * This class is the listener of the {@link WelcomeViewController.ViewListener}.<br>
 * This will manage the interaction on the WelcomeView.fxml view.
 */
public class WelcomeController implements WelcomeViewController.ViewListener, LoginController.Listener, RegisterController.Listener, ConditionController.Listener {
    private WelcomeViewController welcomeViewController;

    public WelcomeController(){}

    /**
     * Loading our view and setting our {@link WelcomeViewController} listener.
     * @throws NavigationException if a {@link IOException} occurs.
     */
    public void show() throws NavigationException{
        welcomeViewController = (WelcomeViewController) ViewLoaderSingleton.getInstance().loadView("WelcomeView.fxml");
        welcomeViewController.setListener(this);
    }

    /**
     * View navigator to the login menu
     */
    @Override
    public void signIn(){
        LoginController controller = new LoginController(this);
        try {
            controller.show();
        } catch (NavigationException e) {
            welcomeViewController.showError("An error occurred", e.getMessage(), false);
        }
    }

    @Override
    public void createConditionController() {
        ConditionController controller = new ConditionController(this);
        try {
            controller.show();
        } catch (NavigationException e) {
            welcomeViewController.showError("An error occurred", e.getMessage(), false);
        }
    }

    @Override
    public void navigateBack() throws NavigationException {
        show();
    }

    @Override
    public void signUp() {
        RegisterController registerController = new RegisterController(this);
        try {
            registerController.show();
        } catch (NavigationException e) {
            welcomeViewController.showError("An error occurred", e.getMessage(), false);
        }
    }

    /**
     * display an error message.
     * @param title Title of the error
     * @param message Message of the error
     */
    public void displayError(String title, String message){
        welcomeViewController.showError(title, message, true);
    }

}
