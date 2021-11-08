package be.ac.ulb.infof307.g03.viewsControllers;

import be.ac.ulb.infof307.g03.database.dao.TaskDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.Task;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Class controlling the TaskView
 */
public class TaskViewController extends BaseViewController implements Initializable {

    @FXML
    private CheckBox checkGoogleCalendar;

    @FXML
    private Spinner<Integer> reminderSpinner;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Spinner<Integer> spinner;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private Label currentProjectName;

    @FXML
    private TextArea taskDescription;

    @FXML
    private TableView<Task> taskTable;

    @FXML
    private TableColumn<Task, String> descriptionColumn;

    @FXML
    private Button addTaskButton;

    @FXML
    private Button returnButton;

    private ViewListener listener;

    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    public void setCurrentProjectName(String projectName){
        currentProjectName.setText("Project : " + projectName);
    }

    /**
     * changeRowsColor updates the color of every row to green if the task is done otherwise does nothing
     */
    private void changeRowsColor() {
        taskTable.setRowFactory(tv -> new TableRow<Task>() {
            @Override
            protected void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || !item.isDone())
                    setStyle("");
                else setStyle("-fx-background-color: #90ee90;");
            }
        });
    }

    public void setTasks(List<Task> tasks){
        taskTable.setItems(FXCollections.observableList(tasks));
        changeRowsColor();
        taskTable.refresh();
        enableEditForTaskColumn();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addTaskButton.setOnMouseClicked(event -> {
            String description = taskDescription.getText();
            listener.onAddTask(description, startDatePicker.getValue(), endDatePicker.getValue(), reminderSpinner.getValue(), checkGoogleCalendar.isSelected());
        });
        returnButton.setOnMouseClicked(event -> {
            try {
                listener.goBack();
            } catch (NavigationException e) {
                showError("An error occurred", e.getMessage(), false);
            }
        });
        populateTaskDescriptionColumn();
        populateTaskStartDateColumn();
        populateTaskEndDateColumn();
        populateAssignmentColumn();
        taskTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        addDeleteButtonToTable();
        addDoneButtonToTable();
        addAssignButtonToTable();
        addEditDateButtonToTable();

        startDatePicker.setOnAction(event -> {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue() ;
            if(endDate != null){
            if (startDate.compareTo(endDate) > 0) endDatePicker.setValue(startDate);
            setSpinner(startDate,endDate);
            long diff = ChronoUnit.DAYS.between(startDate, endDate);
            if (spinner.getValueFactory() != null) spinner.getValueFactory().setValue((int)diff);}

        });

        endDatePicker.setOnAction(event -> {
            long diff = ChronoUnit.DAYS.between(startDatePicker.getValue(),  endDatePicker.getValue());
            if (spinner.getValueFactory() != null) spinner.getValueFactory().setValue((int)diff);
        });

        spinner.setOnMouseClicked(event -> {
            LocalDate startDate = startDatePicker.getValue();
            endDatePicker.setValue(startDate.plusDays(spinner.getValue()));
        });
    }


    public void setDefaultDate(LocalDate endDate){
        LocalDate startDate = LocalDate.now();
        startDatePicker.setValue(startDate);

        startDatePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0|| date.compareTo(endDate) > 0 );
            }
        });
        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0 || date.compareTo(endDate) > 0);
            }
        });
        endDatePicker.setValue(endDate);
        setSpinner(startDate,endDate);
    }

    /**
     * Create a new spinner with the desired start date and end date
     */
    private void setSpinner(LocalDate startDate, LocalDate endDate){
        long diff = ChronoUnit.DAYS.between(startDate, endDate);
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,(int)diff,1));
        spinner.getValueFactory().setValue((int)diff);
        reminderSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,(int)diff,1));
    }


    /**
     * populate the column of description tasks and add it to the tableView
     */
    private void populateTaskDescriptionColumn() {
        taskTable.setPlaceholder(new Label("This project does not contain any task"));
        descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("Description"));
        taskTable.getColumns().add(descriptionColumn);
    }

    /**
     * populate the start date column for tasks and add it to the tableView
     */
    private void populateTaskStartDateColumn() {

        TableColumn<Task, LocalDate> startDateColumn = new TableColumn<>("Start date");
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        taskTable.getColumns().add(startDateColumn);
    }

    /**
     * populate the end date column for tasks and add it to the tableView
     */
    private void populateTaskEndDateColumn() {
        TableColumn<Task, LocalDate> endDateColumn = new TableColumn<>("End date");
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        taskTable.getColumns().add(endDateColumn);
    }

    /**
     * populate the column of assignment tasks and add it to the tableView
     */
    private void populateAssignmentColumn() {
        TableColumn<Task, ImageView> userAssignedColumn = new TableColumn<>("Assigned");
        userAssignedColumn.setCellValueFactory(cellData -> {
            Task task = cellData.getValue();
            try {
                if (TaskDao.isUserAssigned(task.getId(), Helper.getCurrentUser().getId())) {
                    ImageView img = new ImageView(new Image(("be/ac/ulb/infof307/g03/views/images/check_icon.png")));
                    img.setFitHeight(25);
                    img.setFitWidth(25);
                    return new SimpleObjectProperty<>(img);
                }
            } catch (DaoException e) {
                showError("An error occurred", e.getMessage(), false);
            }
            return new SimpleObjectProperty<>(new ImageView());
        });
        taskTable.getColumns().add(userAssignedColumn);
    }

    /**
     * add the edit date button to table
     */
    private void addEditDateButtonToTable(){
        TableColumn<Task, Void> editDateColumn = new TableColumn<>();
        Callback<TableColumn<Task, Void>, TableCell<Task, Void>> cellFactory = new Callback<TableColumn<Task, Void>, TableCell<Task, Void>>() {
            @Override
            public TableCell<Task, Void> call(TableColumn<Task, Void> param) {
                return new TableCell<Task, Void>() {
                    private final Button btn = new Button("Edit dates");
                    {

                        btn.setOnAction((ActionEvent event) -> {
                            Task task = getTableView().getItems().get(getIndex());
                            listener.onEditDates(task);
                        });
                    }
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) setGraphic(null);
                        else setGraphic(btn);
                    }
                };
            }
        };
        editDateColumn.setCellFactory(cellFactory);
        taskTable.getColumns().add(editDateColumn);
    }

    /**
     * Add delete button to the task's tableView
     * set the action listener to the button
     */
    private void addDeleteButtonToTable() {
        TableColumn<Task, Void> actionColumn = new TableColumn<>();
        Callback<TableColumn<Task, Void>, TableCell<Task, Void>> cellFactory = new Callback<TableColumn<Task, Void>, TableCell<Task, Void>>() {
            @Override
            public TableCell<Task, Void> call(TableColumn<Task, Void> param) {
                return new TableCell<Task, Void>() {
                    private final Button btn = new Button("Delete task");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Task task = getTableView().getItems().get(getIndex());
                            if (askConfirm("Confirm", "Are you sure you want to delete this task ?")) {
                                listener.deleteTask(task);
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
        taskTable.getColumns().add(actionColumn);
    }

    /**
     * Add done button to the task's tableView
     * set the action listener to the button
     */
    private void addDoneButtonToTable() {
        TableColumn<Task, Void> actionColumn = new TableColumn<>();
        Callback<TableColumn<Task, Void>, TableCell<Task, Void>> cellFactory = new Callback<TableColumn<Task, Void>, TableCell<Task, Void>>() {
            @Override
            public TableCell<Task, Void> call(TableColumn<Task, Void> param) {
                return new TableCell<Task, Void>() {
                    private final Button btn = new Button( "Set (un)done");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Task task = getTableView().getItems().get(getIndex());
                            listener.completeTask(task);
                            changeRowsColor();
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
        taskTable.getColumns().add(actionColumn);
    }


    /**
     * Add Assignment button to the task's tableView
     * set the action listener to the button
     */
    private void addAssignButtonToTable() {
        TableColumn<Task, Void> actionColumn = new TableColumn<>();
        Callback<TableColumn<Task, Void>, TableCell<Task, Void>> cellFactory = new Callback<TableColumn<Task, Void>, TableCell<Task, Void>>() {
            @Override
            public TableCell<Task, Void> call(TableColumn<Task, Void> param) {
                return new TableCell<Task, Void>() {
                    private final Button btn = new Button("Assign");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Task task = getTableView().getItems().get(getIndex());
                            listener.onAssignTask(task);
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
        taskTable.getColumns().add(actionColumn);
    }

    /**
     * Permit the edit of a task
     */
    private void enableEditForTaskColumn() {
        taskTable.setEditable(true);

        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setOnEditCommit(event -> {
            Task task = event.getTableView().getItems().get(event.getTablePosition().getRow());
            if (event.getNewValue().trim().isEmpty()) {
                event.getTableView().getItems().set(event.getTablePosition().getRow(), task);
                return;
            }
            task.setDescription(event.getNewValue());
            listener.modifyTask(task);
        });


    }

    /**
     * Show a error
     * @param s error message to show
     */
    public void showError(String s) {
        showError("An error occurred", s, false);
    }

    /**
     * Clear the description
     */
    public void clearDescription() {
        taskDescription.clear();
    }


    public interface ViewListener{
        /**
         *  Insert a new task for a specific project
         */
        void onAddTask(String description, LocalDate startDate, LocalDate endDate, int reminder, boolean isGoogleSelected);
        /**
         * Go back to the previous view
         */
        void goBack() throws NavigationException;

        /**
         * Delete the task in the DB
         * @param task task to delete
         */
        void deleteTask(Task task);

        /**
         * Show the assign view
         * @param task task to assign
         */
        void onAssignTask(Task task);

        void onEditDates(Task task);

        void modifyTask(Task task);

        void completeTask(Task task);
    }
}
