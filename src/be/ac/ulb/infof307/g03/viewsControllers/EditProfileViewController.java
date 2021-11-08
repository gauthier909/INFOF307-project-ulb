package be.ac.ulb.infof307.g03.viewsControllers;

import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.User;
import be.ac.ulb.infof307.g03.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Class controlling the EditProfileView
 */
public class EditProfileViewController extends BaseViewController implements Initializable {
    @FXML
    private TextField username;
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField confirmPassword;
    @FXML
    private Label errorLabel;
    @FXML
    private Button confirmButton;
    @FXML
    private Button backButton;

    private ViewListener listener;

    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        backButton.setOnMouseClicked(event -> {
            try {
                listener.goBack();
            } catch (NavigationException e) {
                showError("An error occurred", e.getMessage(), false);
            }
        });
        confirmButton.setOnMouseClicked(event -> listener.confirm(
                username.getText(),
                password.getText(),
                confirmPassword.getText(),
                email.getText(),
                firstName.getText(),
                lastName.getText()));
        User currentUser = Helper.getCurrentUser();
        username.setText(currentUser.getUsername());
        firstName.setText(Utils.blankIfNull(currentUser.getFirstName())); //toString to avoid null values
        lastName.setText(Utils.blankIfNull(currentUser.getLastName()));
        email.setText(Utils.blankIfNull(currentUser.getEmail()));
        notEdit();
    }

    /**
     * Switch the edit project window back to non-edit mode. All text fields are disable.
     */
    public void notEdit(){
        username.setDisable(true);
        firstName.setDisable(true);
        lastName.setDisable(true);
        email.setDisable(true);
        password.setDisable(true);
        confirmPassword.setDisable(true);
        confirmButton.setVisible(false);
    }

    /**
     * Switch the edit project window to edit mode, all text fields are now editable.
     */
    public void edit() {
        username.setDisable(false);
        firstName.setDisable(false);
        lastName.setDisable(false);
        email.setDisable(false);
        password.setDisable(false);
        confirmPassword.setDisable(false);
        confirmButton.setVisible(true);
    }

    public void setErrorLabel(String text){
        errorLabel.setText(text);
        errorLabel.setVisible(true);
    }

    public interface ViewListener{

        void goBack() throws NavigationException;

        void confirm(String username, String password, String confirmPassword, String email, String firstName, String lastName);
    }
}
