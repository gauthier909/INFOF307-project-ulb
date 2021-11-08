package be.ac.ulb.infof307.g03.viewsControllers;

import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.models.User;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.StringConverter;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Class controlling the AssignTaskView
 */
public class AssignTaskViewController extends BaseViewController implements Initializable {
    ViewListener listener;

    @FXML
    private Label taskName;

    @FXML
    private ComboBox<User> collaboratorBox;

    @FXML
    private TableView<User> tableUserAssigned;

    @FXML
    private Button assignButton;

    @FXML
    private Button returnButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        assignButton.setOnMouseClicked(event -> listener.onAssignTask(collaboratorBox.getSelectionModel().getSelectedItem()));

        returnButton.setOnMouseClicked(event -> {
            try {
                listener.goBack();
            } catch (NavigationException e) {
                showError("An error occurred", "Couldn't switch view.", false);
            }
        });


        TableColumn<User, String> userColumn = new TableColumn<>("Users Assigned");
        userColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        tableUserAssigned.getColumns().add(userColumn);
        tableUserAssigned.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        addDeleteButtonToTable();


        collaboratorBox.setConverter(new StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user.getUsername();
            }

            @Override
            public User fromString(String name) {
                return collaboratorBox.getItems().stream().filter(user -> user.getUsername().equals(name)).findFirst().orElse(null);
            }
        });
    }

    /**
     * Add delete button to the assign Task's tableView
     * set the action listener to the button
     */
    private void addDeleteButtonToTable() {
        TableColumn<User,Void> actionColumn = new TableColumn<>();
        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = new Callback<TableColumn<User, Void>, TableCell<User, Void>>() {
            @Override
            public TableCell<User, Void> call(TableColumn<User, Void> param) {
                return new TableCell<User, Void>() {
                    private final Button btn = new Button("Delete");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            User user = getTableView().getItems().get(getIndex());
                            if (askConfirm("Confirm", "Are you sure you want to delete this user from this task?")) {
                                listener.deleteUser(user);

                            }
                        });
                    }
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
        actionColumn.setCellFactory(cellFactory);
        tableUserAssigned.getColumns().add(actionColumn);
    }

    public void setAssignedUsers(List<User> list){
        tableUserAssigned.setItems(FXCollections.observableList(list));
        tableUserAssigned.refresh();
    }

    public void SetUsers(List<User> list){
        collaboratorBox.itemsProperty().setValue(FXCollections.observableList(list));
    }

    public void setListener(ViewListener listener){ this.listener = listener;}

    public void setCurrentTaskName(String name) {
        this.taskName.setText(name);
    }

    public interface ViewListener{

        /**
         * This function is used by the returnButton to go back to the TaskView.fxml.
         * @throws NavigationException if the listener couldn't load the previous view.
         */
        void goBack() throws NavigationException;

        /**
         * This function is called by the assignButton to assign a task to a user.
         * @param user on which we add a task assignment.
         */
        void onAssignTask(User user);

        /**
         * This function is used by the {@link AssignTaskViewController} to delete a user assignment to the task.
         * @param user on which we want to remove the task assignment.
         */
        void deleteUser(User user);
    }
}
