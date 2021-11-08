package be.ac.ulb.infof307.g03.viewsControllers;

import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Class controlling the RegisterView
 */
public class RegisterViewController extends BaseViewController implements Initializable {

    private RegisterViewController.ViewListener listener;



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
    private Button backButton;
    @FXML
    private Button registerButton;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        backButton.setOnMouseClicked(event -> {
            try {
                listener.goBack();
            } catch (NavigationException e) {
                showError("An error occurred", e.getMessage(),false);
            }
        });
        registerButton.setOnMouseClicked(e-> listener.register(
                username.getText(),
                firstName.getText(),
                lastName.getText(),
                email.getText(),
                password.getText(),
                confirmPassword.getText()
        ));
    }

    public void setListener(RegisterViewController.ViewListener listener){
        this.listener = listener;
    }

    public void setWrongInfo(String text){
        errorLabel.setText(text);
        errorLabel.setVisible(true);
    }

    public interface ViewListener{
        void goBack() throws NavigationException;
        void register(String username,String firstName, String lastName, String email, String password, String confirmPassword);
    }
}
