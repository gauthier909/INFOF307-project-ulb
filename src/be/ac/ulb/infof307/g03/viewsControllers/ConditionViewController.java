package be.ac.ulb.infof307.g03.viewsControllers;

import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * Class controlling the ConditionView
 */
public class ConditionViewController extends BaseViewController implements Initializable {
    @FXML
    private CheckBox checkBox;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button nextButton;

    @FXML
    private Button returnButton;


    private ViewListener listener;

    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        checkBox.setOnAction(event -> nextButton.setDisable(!checkBox.isSelected()));

        returnButton.setOnAction(event -> {
            try {
                listener.goBack();
            } catch (NavigationException e) {
                showError("An error occurred", e.getMessage(), false);
            }
        });

        nextButton.setOnAction(event -> listener.toRegisterView());
        Text t = new Text();
        InputStream is = getClass().getClassLoader().getResourceAsStream("assets/TermsAndConditions.txt");
        if (is != null) {
            try(Scanner s = new Scanner(is).useDelimiter("\n")){
                while (s.hasNext()) {
                    if (s.hasNextInt()) { // check if next token is an int
                        t.setText(t.getText() + s.nextInt() + "\n"); // display the found integer
                    } else {
                        t.setText(t.getText() + s.next() + "\n"); // else read the next token
                    }
                }
            } catch (IllegalStateException e) {
                showError("An error occurred", e.getMessage(), false);
                t.setStyle("-fx-color-label-visible: #ff0000");
            }
        }
        scrollPane.setContent(t);
        nextButton.setDisable(true);
    }

    public interface ViewListener{
        /**
         * Navigate back to the main view
         */
        void goBack() throws NavigationException;
        /**
         * View navigation to register view.
         */
        void toRegisterView();
    }
}
