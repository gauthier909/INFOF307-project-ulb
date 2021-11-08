package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.viewsControllers.ConditionViewController;
import be.ac.ulb.infof307.g03.viewsControllers.ViewLoaderSingleton;

/**
 * This class is the listener of the {@link ConditionViewController.ViewListener}.<br>
 * This will manage the interaction on the ConditionView.fxml view.
 */
public class ConditionController implements ConditionViewController.ViewListener {
    private final Listener listener;

    /**
     * Constructor for the ConditionController
     * @param listener the listener for this controller
     */
    public ConditionController(Listener listener) {
        this.listener = listener;
    }

    /**
     * Setup and show the ConditionView.fxml
     * @throws NavigationException nav exception
     */
    public void show() throws NavigationException {
        ConditionViewController conditionViewController = (ConditionViewController) ViewLoaderSingleton.getInstance().loadView("ConditionView.fxml");
        conditionViewController.setListener(this);
    }

    @Override
    public void goBack() throws NavigationException {
        listener.navigateBack();
    }

    @Override
    public void toRegisterView(){
        listener.signUp();
    }

    public interface Listener{
        /**
         * Navigate to the previous view
         * throw NavigationException
         */
        void navigateBack() throws NavigationException;

        /**
         * Navigate to the register view after accepting conditions
         */
        void signUp();
    }
}
