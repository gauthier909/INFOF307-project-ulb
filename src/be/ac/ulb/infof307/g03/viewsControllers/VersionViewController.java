package be.ac.ulb.infof307.g03.viewsControllers;

import be.ac.ulb.infof307.g03.models.Version;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Class controlling the VersionView
 */
public class VersionViewController extends BaseViewController implements Initializable {

    @FXML
    private TableView<Version> versionTable;

    @FXML
    private Button returnButton;

    private ViewListener listener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        returnButton.setOnAction(event -> listener.goBack());

        versionTable.setPlaceholder(new Label("No versions yet (try to commit something)"));
        TableColumn<Version, String> versionColumn = new TableColumn<>("commitMessage");
        versionColumn.setCellValueFactory(new PropertyValueFactory<>("commitMessage"));
        versionTable.getColumns().add(versionColumn);
        addButtonsToTable();
        versionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void addButtonsToTable() {
        TableColumn<Version, Void> colBtn = new TableColumn<>("Manage");
        Callback<TableColumn<Version, Void>, TableCell<Version, Void>> cellFactory = new Callback<TableColumn<Version, Void>, TableCell<Version, Void>>() {
            @Override
            public TableCell<Version, Void> call(final TableColumn<Version, Void> param) {
                return new TableCell<Version, Void>() {
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttonWrapper = new HBox();
                            buttonWrapper.setSpacing(5);
                            Button revertButton = new Button("Revert");
                            Button removeButton = new Button("Remove");
                            Button diffButton = new Button("Diff");
                            Version version = getTableView().getItems().get(getIndex());
                            if(listener.getCurrentVersion().getId() == version.getId()) {
                                revertButton.setDisable(true);
                                removeButton.setDisable(true);
                                diffButton.setDisable(true);
                            }
                            revertButton.setOnAction((ActionEvent event) -> listener.onRevertVersion(getTableView().getItems().get(getIndex())));
                            removeButton.setOnAction((ActionEvent event) -> listener.onRemoveVersion(getTableView().getItems().get(getIndex())));
                            diffButton.setOnAction((ActionEvent event) -> listener.onDiffVersion(getTableView().getItems().get(getIndex())));
                            buttonWrapper.getChildren().addAll(revertButton, removeButton, diffButton);
                            setGraphic(buttonWrapper);
                        }
                    }
                };
            }
        };

        colBtn.setCellFactory(cellFactory);
        versionTable.getColumns().add(colBtn);
    }

    public void setVersionTable(List<Version> Versions) {
        versionTable.setItems(FXCollections.observableList(Versions));
        versionTable.refresh();
    }

    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    public interface ViewListener{
        /**
         * View navigator to the main menu.
         */
        void goBack();

        /**
         * Revert to selected version
         * @param version selected version
         */
        void onRevertVersion(Version version);

        /**
         * Get the current version for the current branch's project
         * @return the current version
         */
        Version getCurrentVersion();

        /**
         * Remove the selected version
         * @param version selected version
         */
        void onRemoveVersion(Version version);

        /**
         * Show the difference between 2 version
         * @param otherVersion The version to compare with the current one
         */
        void onDiffVersion(Version otherVersion);
    }
}