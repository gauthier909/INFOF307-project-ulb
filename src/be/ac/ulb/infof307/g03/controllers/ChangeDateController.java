package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.models.Task;
import be.ac.ulb.infof307.g03.viewsControllers.ChangeDateViewController;
import be.ac.ulb.infof307.g03.viewsControllers.ViewLoaderSingleton;

import java.time.LocalDate;

/**
 * Handle the logic of modifying a date for a task in a project.
 * This class is the listener of the {@link ChangeDateViewController.ViewListener}.<br>
 * This will manage the interaction on the ChangeDateView.fxml view.
 */
public class ChangeDateController extends BaseController implements ChangeDateViewController.ViewListener{

    private final LocalDate endDateProject;
    private final Task task;
    private ChangeDateViewController changeDateViewController;
    private final Listener listener;

    public ChangeDateController(Listener listener, Task task, LocalDate endDateProject){
        this.listener = listener;
        this.task = task;
        this.endDateProject = endDateProject;
    }


    public void show() throws NavigationException {
        changeDateViewController = (ChangeDateViewController) ViewLoaderSingleton.getInstance().loadView("ChangeDateView.fxml");
        changeDateViewController.setListener(this);
        changeDateViewController.setStartDate(task.getStartDate());
        changeDateViewController.setEndDate(task.getEndDate());
        changeDateViewController.setMaxAvailableDateForPicker(endDateProject);
    }

    @Override
    public void goBack() {
        try {
            listener.navigateBack();
        } catch (NavigationException e) {
            changeDateViewController.showError("Navigation error", e.getMessage());
        }
    }

    @Override
    public void onChangeDate(LocalDate startDate, LocalDate endDate) {
        if(startDate.compareTo(endDate) > 0) {
            changeDateViewController.showError("Date error", "start date cannot be after the end date.");
            return;
        }
        task.setStartDate(startDate);
        task.setEndDate(endDate);
        try {
            task.updateDates();
            goBack();
        } catch (DaoException e) {
            changeDateViewController.showError("Update error", e.getMessage());
        }
    }

    public void setViewController(ChangeDateViewController changeDateViewController) {
        this.changeDateViewController = changeDateViewController;
    }

    public interface Listener{
        /**
         * Go back
         * @throws NavigationException nav exception
         */
        void navigateBack() throws NavigationException;
    }


}
