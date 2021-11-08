package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.viewsControllers.CalendarViewController;
import be.ac.ulb.infof307.g03.viewsControllers.ViewLoaderSingleton;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handle the logic behind the calendar.
 * This class is the listener of the {@link CalendarViewController.ViewListener}.<br>
 * This will manage the interaction on the CalendarWrapperView.fxml view.
 */
public class CalendarController implements CalendarViewController.ViewListener {
    private final Listener listener;

    /**
     * Constructor for the CalendarController
     * @param listener listener for the controller
     */
    public CalendarController(Listener listener) {
        this.listener=listener;
    }

    /**
     * Setup and Show the CalendarWrapperView.fxml
     * @throws NavigationException if a {@link IOException} occurs
     */
    public void show() throws NavigationException {
        CalendarViewController calendarViewController = (CalendarViewController) ViewLoaderSingleton.getInstance().loadView("CalendarWrapperView.fxml");
        calendarViewController.setListener(this);

        List<Project> projects = new ArrayList<>();
        try {
            projects = Helper.getCurrentUser().getProjects();
        }catch (DaoException e){
            calendarViewController.showError("An error occurred", e.getMessage(), false);
        }
        calendarViewController.setProjects(projects);
    }

    @Override
    public void goBack() throws NavigationException {
        listener.navigateBack();
    }

    public interface Listener{
        /**
         * GO back
         * @throws NavigationException nav exception
         */
        void navigateBack() throws NavigationException;
    }
}
