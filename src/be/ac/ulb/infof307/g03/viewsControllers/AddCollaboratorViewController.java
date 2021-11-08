package be.ac.ulb.infof307.g03.viewsControllers;

import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.models.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Class controlling the AddCollaboratorView
 */
public class AddCollaboratorViewController extends BaseViewController implements Initializable {

    private ViewListener listener;
    @FXML
    private TextField collaboratorUsername;

    @FXML
    private Label labelProject;

    @FXML
    private Label feedbackMessage;

    @FXML
    private Button returnButton;

    @FXML
    private Button addCollaboratorButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addCollaboratorButton.setOnMouseClicked(event -> listener.onAskCollaborator(collaboratorUsername.getText()));

        returnButton.setOnMouseClicked(event -> {
            try {
                listener.goBack();
            } catch (NavigationException e) {
                showError("An error occurred", "Couldn't switch view.", false);
            }
        });
    }

    /**
     * Overloading with default color boolean so the messages are red by default.
     * Handle error messages and positive feedback.
     * @param feedback String : message to show
     */
    public void showVisualFeedBack(String feedback, boolean positiveFeedback) {
        if (positiveFeedback) feedbackMessage.setTextFill(Color.web("#50C878", 0.8));
        if (!(positiveFeedback)) feedbackMessage.setTextFill(Color.web("#eb0000", 0.8));
        feedbackMessage.setText(feedback);
    }

    public void setListener(ViewListener listener){ this.listener = listener;}

    public void setProjectName(String name) {
        labelProject.setText(name);
    }

    public interface ViewListener{
        /**
         * This function is used by the returnButton to go back to the CollaboratorManagementView.fxml.
         * @throws NavigationException if the listener couldn't load the previous view.
         */
        void goBack() throws NavigationException;

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
        void onAskCollaborator(String notifiedUsername);
    }
}
