package be.ac.ulb.infof307.g03.viewsControllers;
import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.models.Tag;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Class controlling the EditProjectView
 */
public class EditProjectViewController extends BaseViewController implements Initializable {

    @FXML
    private Label nameLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private TextField nameField;
    @FXML
    private TextField descriptionField;
    @FXML
    public DatePicker dateEnd;
    @FXML
    private Label errorLabel;
    @FXML
    private Button updateButton;
    @FXML
    private Button createButton;
    @FXML
    private ComboBox<Tag> tagsBox;
    @FXML
    private Button addTagButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private TextField nameTagInput;
    @FXML
    private Image removeImg;
    @FXML
    private HBox tagsPane;
    @FXML
    private Button backButton;
    @FXML
    private Spinner<Integer> reminderSpinner;
    @FXML
    private CheckBox checkCalendar;

    private ViewListener listener;

    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createButton.setOnMouseClicked(event -> {
            String name = nameField.getText();
            String description = descriptionField.getText();
            LocalDate date = dateEnd.getValue();
            int reminderDate = reminderSpinner.getValue();
            try {
                listener.createProject(name, description, date, reminderDate, checkCalendar.isSelected());
            } catch (NavigationException e) {
                showError("An error occurred", e.getMessage(), false);
            }
        });
        updateButton.setOnMouseClicked(event -> {
            String name = nameField.getText();
            String description = descriptionField.getText();
            LocalDate date = dateEnd.getValue();
            try {
                int reminderDate = reminderSpinner.getValue();
                listener.updateProject(name, description, date , reminderDate);
            } catch (NavigationException e) {
                showError("An error occurred", e.getMessage(), false);
            }
        });
        backButton.setOnMouseClicked(event -> {
            try {
                listener.goBack();
            } catch (NavigationException e) {
                showError("An error occurred", e.getMessage(), false);
            }
        });
        tagsBox.setOnAction(event -> {
            listener.onSelectTagsBox(tagsBox.getValue());
            Platform.runLater(() -> tagsBox.getSelectionModel().select(-1));
        });
        tagsBox.setConverter(new StringConverter<Tag>() {
            @Override
            public String toString(Tag tag) {
                return tag.getName();
            }

            @Override
            public Tag fromString(String name) {
                return tagsBox.getItems().stream().filter(tag -> tag.getName().equals(name)).findFirst().orElse(null);
            }
        });
        addTagButton.setOnMouseClicked(event -> {
            String tagName = nameTagInput.getText();
            listener.addNewTag(tagName);
        });
        deleteButton.setOnMouseClicked(event -> {
            try {
                listener.deleteProject();
            } catch (NavigationException e) {
                showError("An error occurred", e.getMessage(), false);
            }
        });
        nameLabel.setText("Name: ");
        descriptionLabel.setText("Description: ");
        tagsPane.setStyle("-fx-border-color: #F1F1F1;" +
                "          -fx-border-width: 1px;" +
                "          -fx-border-radius: 10;" +
                "          -fx-border-insets: 5");
        tagsPane.setSpacing(10);
        removeImg = new Image("images/remove.png");
    }

    /**
     * Switch the edit project window to edit mode, all text fields are now editable.
     */
    public void edit(){
        nameField.setDisable(false);
        descriptionField.setDisable(false);
        dateEnd.setDisable(false);
        tagsBox.setDisable(false);
        addTagButton.setDisable(false);
        nameTagInput.setDisable(false);
        updateButton.setDisable(false);
        reminderSpinner.setDisable(false);
        tagsPane.getChildren().forEach(btn ->btn.setDisable(false));
    }


    /**
     * Create a new spinner with the desired start date and end date
     */
    public void setSpinner(LocalDate startDate, LocalDate endDate){
        long diff = ChronoUnit.DAYS.between(startDate, endDate);
        reminderSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,(int)diff,1));
    }

    /**
     * Switch the the edit project window back to non-edit mode. All text fields are disable.
     */
    public void notEdit(){
        reminderSpinner.setDisable(true);
        nameField.setDisable(true);
        descriptionField.setDisable(true);
        dateEnd.setDisable(true);
        tagsBox.setDisable(true);
        addTagButton.setDisable(true);
        nameTagInput.setDisable(true);
        updateButton.setDisable(true);
        tagsPane.getChildren().forEach(btn -> btn.setDisable(true));
    }

    public void setTags(List<Tag> tags) {
        tagsBox.setItems(FXCollections.observableList(tags));
    }

    public void displayError(String title, String message){
        showError(title, message, false);
    }

    public void setViewForUpdate(String name, String description, LocalDate endDate) {
        createButton.setVisible(false);
        nameField.setText(name);
        descriptionField.setText(description);
        dateEnd.setValue(endDate);
        notEdit();
    }

    public void setViewForCreate(){
        editButton.setVisible(false);
        deleteButton.setVisible(false);
        updateButton.setVisible(false);
    }

    /**
     * Add the tag's name into a button which is injected inside a HBox
     * Each button can be pressed to remove the tag from the HBox
     */
    public void addTagButton(Tag tag, boolean isEdit) {
        ImageView removeImgView = new ImageView(removeImg);
        removeImgView.setFitWidth(16);
        removeImgView.setFitHeight(16);
        Button button = new Button(tag.getName(), removeImgView);
        button.setDisable(isEdit);
        button.setPrefHeight(20);
        button.setContentDisplay(ContentDisplay.RIGHT);
        button.setOnAction(event -> {
            tagsPane.getChildren().remove(button);
            listener.removeTagFromSelectedTagList(tag);
        });
        tagsPane.getChildren().add(button);
    }

    public void setErrorLabel(String text){
        errorLabel.setText(text);
    }

    public void clearNameTagInput() {
        nameTagInput.clear();
    }

    public interface ViewListener{
        /**
         * remove a tag from the choice's list
         * @param tag tag to remove
         */
        void removeTagFromSelectedTagList(Tag tag);
        /**
         * Create a project with all information filled by user, if all required information are given.
         * Otherwise an error message is shown to the user.
         * Once a project is correctly created, it is added to the database.
         * @param name project name
         * @param description project description
         * @param date project end date
         */
        void createProject(String name, String description, LocalDate date, int reminderDate, boolean isCheckedGoogle) throws NavigationException;
        /**
         * Update information of a project in the database.
         * Once information has been updated, go back to the main page.
         * @param name project name
         * @param description project description
         * @param date project end date
         */
        void updateProject(String name, String description, LocalDate date , int reminderValue) throws NavigationException;

        /**
         * Return to the previous view
         */
        void goBack() throws NavigationException;

        void onSelectTagsBox(Tag tag);
        /**
         * Create new tag with the corresponding button
         * Verify if the new tag's name already exists in the tagsList.
         * @param tagName name of new Tag
         */
        void addNewTag(String tagName);
        /**
         * Ask the confirmation when trying to delete a project.
         */
        void deleteProject() throws NavigationException;
    }
}
