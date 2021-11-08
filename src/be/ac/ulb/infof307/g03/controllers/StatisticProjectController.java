package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.Statistic;
import be.ac.ulb.infof307.g03.viewsControllers.StatisticProjectViewController;
import be.ac.ulb.infof307.g03.viewsControllers.ViewLoaderSingleton;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Handle the logic to show statistics for the projects of a user
 * This class is the listener of the {@link StatisticProjectViewController.ViewListener}.<br>
 * This will manage the interaction on the StatisticProjectView.fxml view.
 */
public class StatisticProjectController implements StatisticProjectViewController.ViewListener {
    
    private final Listener listener;
    private final List<Project> projects;
    private StatisticProjectViewController statisticProjectViewController;

    private Statistic st;

    /**
     * Constructor of the StatistiqueProjectController
     * @param listener of this class
     */
    public StatisticProjectController(StatisticProjectController.Listener listener, List<Project> projects) {
        this.listener=listener;
        this.projects = projects;
    }

    /**
     * Setup and show the StatisticProjectView.fxml
     * @throws NavigationException nav exception
     */
    public void show() throws NavigationException {
        statisticProjectViewController = (StatisticProjectViewController) ViewLoaderSingleton.getInstance().loadView("StatisticProjectView.fxml");
        statisticProjectViewController.setListener(this);
        statisticProjectViewController.setStage(ViewLoaderSingleton.getInstance().getStage());
        this.loadData();
    }

    /**
     * Load the data
     */
    private void loadData(){
        try {
            st = new Statistic(projects);
        } catch (DaoException e) {
            statisticProjectViewController.showError("Error", e.getMessage());
        }
        statisticProjectViewController.setData();
    }

    @Override
    public Statistic getStatistics(){
        return st;
    }

    @Override
    public void exportCsv(File file) throws IOException {
        if(file == null) return;
        st.exportCsv(file);
    }

    @Override
    public void exportJson(File file) throws IOException {
        if(file == null) return;
        st.exportJson(file);
    }


    @Override
    public void goBack() throws NavigationException {
        this.listener.navigateBack();
    }

    public interface Listener{

        /**
         * Navigate back
         * @throws NavigationException nav exception
         */
        void navigateBack() throws NavigationException;
    }
}
