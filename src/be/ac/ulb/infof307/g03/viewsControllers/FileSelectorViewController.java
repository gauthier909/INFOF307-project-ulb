package be.ac.ulb.infof307.g03.viewsControllers;

import be.ac.ulb.infof307.g03.controllers.ImportExportController;
import be.ac.ulb.infof307.g03.models.Project;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Class controlling the FileSelectorView
 */
public class FileSelectorViewController extends BaseViewController implements Initializable {

    @FXML
    Button importExportButton;

    @FXML
    Button cancelButton;

    @FXML
    ListView<String> projectList;

    private ObservableList<String> projects;

    private Map<String,Project> projectsMap;

    private ViewListener controller;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancelButton.setOnAction(event -> controller.goBack());
    }

    /**
     * Add to the listView all the projects given in parameter
     * @param projects to show on the ListView
     */
    public void showImportProjects(List<String> projects){
        this.projects = FXCollections.observableList(projects);
        showProjects();
    }

    /**
     * Setter for project Map that maps project names to project objects
     * @param projectsMap project Map
     */
    public void setProjectsMap(Map<String,Project> projectsMap) {
        this.projectsMap = projectsMap;
    }
    /**
     *Show projects to be exported for selection
     */
    public void showExportProjects(){
        List<String> projectNames = new ArrayList<>(projectsMap.keySet());
        this.projects = FXCollections.observableList(projectNames);
        showProjects();
    }


    /**
     * Configuration of the listView
     */
    private void showProjects(){
        projectList.setItems(projects);
        projectList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void setListener(ViewListener listener) {
        this.controller = listener;
    }

    public void setButton(ImportExportController.State state) {
        if (state == ImportExportController.State.IMPORT) {
            importExportButton.setOnMouseClicked(event -> {
                ObservableList<String> selected = projectList.getSelectionModel().getSelectedItems();
                controller.importProjects(selected);
            });
            importExportButton.setText("Import Projects");
        }else{
            importExportButton.setText("Export Projects");
            importExportButton.setOnMouseClicked(event -> {
                ObservableList<String> selected = projectList.getSelectionModel().getSelectedItems();
                List<Project> projects = new ArrayList<>(selected).stream().map(projectName -> projectsMap.get(projectName)).collect(Collectors.toList());
                controller.exportProjects(projects);
            });}
    }       

    public interface ViewListener {
        void importProjects(List<String> projects);
        void goBack();
        void exportProjects(List<Project> selected);
    }
}
