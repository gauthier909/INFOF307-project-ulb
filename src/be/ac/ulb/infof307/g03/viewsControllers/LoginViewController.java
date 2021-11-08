package be.ac.ulb.infof307.g03.viewsControllers;

import be.ac.ulb.infof307.g03.exceptions.DaoException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Class controlling the LoginView
 */
public class LoginViewController extends BaseViewController implements Initializable {
    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Label wrongInfo;

    @FXML
    private Button loginButton;

    @FXML
    private Button backButton;

    private ViewListener listener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginButton.setOnMouseClicked(event -> {
            try {
                if (listener.login(username.getText(), password.getText())) listener.loadHomeController();
            }catch(DaoException e){
                showError("An error occurred", e.getMessage(), false);
            }
            });
        backButton.setOnMouseClicked(event -> listener.goBack());
        }

    public void setListener(ViewListener listener){
        this.listener = listener;
    }

    public void setWrongInfo(String text){
        wrongInfo.setText(text);
        wrongInfo.setVisible(true);
    }

    public interface ViewListener{
        boolean login(String username, String password) throws DaoException;
        void loadHomeController();
        void goBack();
    }
}
