package be.ac.ulb.infof307.g03.viewsControllers;

import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.Task;
import javafx.scene.control.*;
import javafx.scene.layout.Region;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * view controller class for the 'pop up' windows
 */
class BaseViewController{
    protected  BaseViewController(){}

    public void showInformation(String information){
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Information");
        alert.setContentText(information);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.getButtonTypes().setAll(ButtonType.OK);
        alert.showAndWait();
    }

    /**
     * Displays notification of a collaboration demand.
     * @param message message to show on popup
     * @return boolean to indicate choice (yes or no)
     */
    public boolean showCollaborationDemand(String message){
        ButtonType yesButton = ButtonType.YES;
        ButtonType noButton = ButtonType.NO;
        return alertCreatorAndHandler("Notification", message, yesButton, noButton);
    }

    /**
     * Get the choice of the user in a binary choice pop-up.
     * @param alertTitle Title of alert box
     * @param alertMsg Message in alert box
     * @param yesButton Ok Button
     * @param noButton Cancel buttons
     * @return true if choice is positive; else false
     */
    private boolean alertCreatorAndHandler(String alertTitle, String alertMsg, ButtonType yesButton, ButtonType noButton){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION); //credits: https://tagmycode.com/snippet/5207/yes-no-cancel-dialog-in-javafx
        alert.setTitle(alertTitle);
        alert.setContentText(alertMsg);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        AtomicBoolean retVal = new AtomicBoolean(false);
        alert.getButtonTypes().setAll(yesButton, noButton);
        alert.showAndWait().ifPresent(type -> {
            if ((type == yesButton)) {
                retVal.set(true);
            }
        });
        return retVal.get();
    }

    public String alertCommitMessage(){
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Commit message");
        dialog.setHeaderText("Enter a commit message :");

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public String showPasswordDialog(String projectName){
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Password");
        dialog.setHeaderText("Enter a password for the project : " + projectName);

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    /**
     * Displays notification of a collaboration demand.
     * @param message message to show on popup
     */
    public void showAcceptedCollaboration(String message){
        String alertTitle = "Notification";
        Alert alert = new Alert(Alert.AlertType.NONE); //credits: https://tagmycode.com/snippet/5207/yes-no-cancel-dialog-in-javafx
        alert.setTitle(alertTitle);
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.getButtonTypes().setAll(ButtonType.OK);
        alert.showAndWait();
    }

    public boolean recallTaskAlert(Task task){
        String alertTitle = "Recall";
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(alertTitle);
        String taskNames = "Today's reminder for task : \n";
        taskNames +=  "  " + task.getDescription() + "\n";
        alert.setContentText(taskNames);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        ButtonType change = new ButtonType("Report to tomorrow", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(ButtonType.OK,change);
        Optional<ButtonType> result = alert.showAndWait();
        return result.get().equals(change);
    }

    public boolean recallProjectAlert(Project project){
        String alertTitle = "Recall";
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(alertTitle);
        String projectNames = "Today's reminder for projects : \n";
        projectNames +=  "  " + project.getName() + "\n";
        alert.setContentText(projectNames);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        ButtonType change = new ButtonType("Report to tomorrow", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(ButtonType.OK,change);
        Optional<ButtonType> result = alert.showAndWait();
        return result.get().equals(change);
    }

    /**
     * Displays notification of a success demand.
     * @param title of the alert
     * @param msg content to display
     */
    public void showSuccess(String title, String msg){
        Alert alert = new Alert(Alert.AlertType.NONE); //credits: https://tagmycode.com/snippet/5207/yes-no-cancel-dialog-in-javafx
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.getButtonTypes().setAll(ButtonType.OK);
        alert.showAndWait();
    }


    /**
     * Open a Javafx error window that will block other windows until accepted
     * @param errorTitle String corresponding to title of the error window
     * @param errorMsg String corresponding to the error message shown on the window
     * @param closeApp boolean if true, this error may close the app, otherwise do nothing
     */
    public void showError(String errorTitle, String errorMsg, boolean closeApp) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(errorTitle);
        errorAlert.setContentText(errorMsg);
        errorAlert.showAndWait();
        if(closeApp){
            System.exit(-1);
        }
    }

    /**
     * Open a Javafx error window that will block other windows until accepted
     * @param errorTitle String corresponding to title of the error window
     * @param errorMsg String corresponding to the error message shown on the window
     */
    public void showError(String errorTitle, String errorMsg) {
        showError(errorTitle, errorMsg, false);
    }

    /**
     * Open a Javafx confirmation window that will block other windows until accepted or rejected
     * @param confirmMsg String corresponding to message shown on the confirmation window
     * @return boolean to indicate whether user has accepted or rejected the confirmation
     */
    public boolean askConfirm(String alertTitle, String confirmMsg){
        ButtonType okButton = ButtonType.OK;
        ButtonType noButton = ButtonType.CANCEL;
        return alertCreatorAndHandler(alertTitle, confirmMsg, okButton, noButton);
    }
}
