package be.ac.ulb.infof307.g03.viewsControllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Class controlling the ChangeDateView
 */
public class ChangeDateViewController extends BaseViewController implements Initializable {
    @FXML
    private Button returnButton;
    @FXML
    private Label taskName;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Button changeDateButton;


    private ViewListener listener;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        returnButton.setOnAction(event -> listener.goBack());
        changeDateButton.setOnAction(event -> listener.onChangeDate(startDatePicker.getValue(), endDatePicker.getValue()));


}
    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    public void setStartDate(LocalDate startDate) {
        startDatePicker.setValue(startDate);
    }

    public void setEndDate(LocalDate endDate) {
        endDatePicker.setValue(endDate);
    }

    public void setMaxAvailableDateForPicker(LocalDate endDateProject) {
        LocalDate today = LocalDate.now();
        startDatePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(today) < 0|| date.compareTo(endDateProject) > 0 );
            }
        });
        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(today) < 0 || date.compareTo(endDateProject) > 0);
            }
        });
    }

    public interface ViewListener{
        /**
         * View navigator to the task View.
         */
        void goBack();
        /**
         * Change date of current task.
         * @param startDate localDate representing the new startDate
         * @param endDate localDate representing the new endDate
         */
        void onChangeDate(LocalDate startDate, LocalDate endDate);
        }

}
