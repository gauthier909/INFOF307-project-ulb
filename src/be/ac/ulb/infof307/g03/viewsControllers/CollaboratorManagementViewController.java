package be.ac.ulb.infof307.g03.viewsControllers;

import be.ac.ulb.infof307.g03.models.User;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Class controlling the CollaboratorManagementView
 */
public class CollaboratorManagementViewController extends BaseViewController implements Initializable {

    @FXML
    private TableView<User> collaboratorTable;

    @FXML
    private Button returnButton;

    @FXML
    private Button addCollaboratorButton;

    private ViewListener listener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        returnButton.setOnAction(event -> listener.goBack());
        addCollaboratorButton.setOnAction(event -> listener.onAddCollaborator());
        collaboratorTable.setPlaceholder(new Label("This project does not contain any collaborator"));
        TableColumn<User, String> usernameColumn = new TableColumn<>("Collaborator");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        collaboratorTable.getColumns().add(usernameColumn);
        addButtonsToTable();
        collaboratorTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void addButtonsToTable() {
        TableColumn<User, Void> colBtn = new TableColumn<>("Manage");
        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = new Callback<TableColumn<User, Void>, TableCell<User, Void>>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<User, Void>() {
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttonWrapper = new HBox();
                            buttonWrapper.setSpacing(5);
                            Button removeCollaboratorBtn = new Button("Remove");
                            removeCollaboratorBtn.setOnAction((ActionEvent event) -> listener.deleteCollaborator(getTableView().getItems().get(getIndex())));
                            buttonWrapper.getChildren().add(removeCollaboratorBtn);
                            setGraphic(buttonWrapper);
                        }
                    }
                };
            }
        };
        colBtn.setCellFactory(cellFactory);
        collaboratorTable.getColumns().add(colBtn);
    }

    public void setCollaboratorTable(ArrayList<User> collaborators) {
        collaboratorTable.setItems(FXCollections.observableList(collaborators));
        collaboratorTable.refresh();
    }

    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    public interface ViewListener{
        /**
         * View navigator to the main menu.
         */
        void goBack();
        void deleteCollaborator(User user);
        /**
         * Call the view navigator and handle error messages.
         */
        void onAddCollaborator();
    }
}
