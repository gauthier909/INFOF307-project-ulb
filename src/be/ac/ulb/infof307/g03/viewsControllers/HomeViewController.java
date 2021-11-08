package be.ac.ulb.infof307.g03.viewsControllers;

import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.Branch;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.Reminder;
import be.ac.ulb.infof307.g03.models.Tag;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Class controlling the HomeView
 */
public class HomeViewController extends BaseViewController implements Initializable {

    @FXML
    private ListView<Project> projectList;
    @FXML
    private Button globalStatsButton;
    @FXML
    private Button goBackButton;
    @FXML
    private Button homeButton;
    @FXML
    private Label projectNameLabel;
    @FXML
    private Label projectDescriptionLabel;
    @FXML
    private Label projectDateLabel;
    @FXML
    private Label tagsLabel;
    @FXML
    private Button createSubProjectButton;
    @FXML
    private Button viewTaskButton;
    @FXML
    private Button exportButton;
    @FXML
    private Button editButton;
    @FXML
    private Button manageCollabButton;
    @FXML
    private Button statsButton;
    @FXML
    private Button createProjectButton;
    @FXML
    private Button importButton;
    @FXML
    private Button calendarButton;
    @FXML
    private Button profileButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button initButton;
    @FXML
    private Button addButton;
    @FXML
    private Button branchButton;
    @FXML
    private Button versionButton;
    @FXML
    private Button commitButton;
    @FXML
    private Button helpButton;
    @FXML
    private Label currentBranchLabel;
    @FXML
    private Label currentVersionLabel;
    @FXML
    private Button passwordButton;
    @FXML
    private Label passwordLabel;

    private HomeViewController.ViewListener listener;
    private ObservableList<Project> projectsObservableList;
    private static int loadedProjectIndex = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createProjectButton.setOnMouseClicked(event -> listener.onCreateProject());

        profileButton.setOnMouseClicked(event -> listener.onEditProfile());

        calendarButton.setOnMouseClicked(event -> listener.onCalendar());

        logoutButton.setOnMouseClicked(event -> listener.logout());

        exportButton.setOnAction((ActionEvent event) -> listener.onExportProject());

        importButton.setOnMouseClicked(event -> listener.onImportProject());

        goBackButton.setOnMouseClicked(event -> listener.backToParentProject());

        homeButton.setOnMouseClicked(event -> listener.backToRootProject());

        globalStatsButton.setOnAction(event -> listener.onGlobalStatsButton());

        helpButton.setOnAction(event -> listener.onHelp());

        projectList.setCellFactory(new ProjectCellFactory());
        projectList.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue!=null) {
                loadProjectInfo(newValue);
                loadProjectButton(newValue);
                loadedProjectIndex = projectList.getSelectionModel().getSelectedIndex();
            }
        });
    }

    public void setListener(HomeViewController.ViewListener listener) {
        this.listener = listener;
    }


    /**
     * Enable or disable project navigation buttons
     * @param state state
     */
    public void setBackButtonState(boolean state){
        goBackButton.setDisable(!state);
        homeButton.setDisable(!state);
    }

    /**
     * Set the branch's label
     * @param label string
     */
    public void setCurrentBranchLabel(String label){
        currentBranchLabel.setText(label);
    }

    /**
     * SEt the version's label
     * @param label string
     */
    public void setCurrentVersionLabel(String label){
        currentVersionLabel.setText(label);
    }

    /**
     * Set the list of project to show
     * @param projects to show on the homeView
     */
    public void insertProjects(List<Project> projects){
        this.projectsObservableList = FXCollections.observableList(projects);
        displayProjects();
    }

    /**
     * Fill the ListView of projects and manage the display of all information relative to a project
     */
    public void displayProjects() {
        projectList.setItems(projectsObservableList);
        projectList.getSelectionModel().select(loadedProjectIndex);
    }

    /**
     * disable geet init button
     */
    public void disableInitGeet(){
        initButton.setDisable(true);
    }

    /**
     * enable geet add button
     */
    public void setGeetButtonStatus(boolean status){
        addButton.setDisable(!status);
        branchButton.setDisable(!status);
        versionButton.setDisable(!status);
    }

    public void setAddButtonStatus(boolean status){
        addButton.setDisable(!status);
        commitButton.setDisable(status);
    }


    public void setProjectButtonsStatus(boolean status){
        initButton.setVisible(status);
        branchButton.setVisible(status);
        versionButton.setVisible(status);
        addButton.setVisible(status);
        commitButton.setVisible(status);
        passwordButton.setVisible(status);
        createSubProjectButton.setVisible(status);
        viewTaskButton.setVisible(status);
        editButton.setVisible(status);
        manageCollabButton.setVisible(status);
        statsButton.setVisible(status);
        currentBranchLabel.setVisible(status);
        currentVersionLabel.setVisible(status);
        exportButton.setDisable(!status);
        calendarButton.setDisable(!status);
        globalStatsButton.setDisable(!status);

    }

    /**
     * Link the button of a project to the project
     * @param currentProject The current Project
     */
    private void loadProjectButton(Project currentProject) {
        if(currentProject.getGeetActivate()){
            Branch currentBranch =listener.getBranchByProjectId(currentProject);
            currentProject.setCurrentBranch(currentBranch);
            initButton.setDisable(true);
            branchButton.setDisable(false);
            versionButton.setDisable(false);
            commitButton.setDisable(!Helper.isTempGeetFileExists(currentProject));
            setGeetButtonStatus(true);
            if(currentProject.getCurrentBranch() == null) {
                addButton.setDisable(true);
            }
            else{
                addButton.setDisable(Helper.isTempGeetFileExists(currentProject));
            }
        } else{
            initButton.setDisable(false);
            branchButton.setDisable(true);
            versionButton.setDisable(true);
            commitButton.setDisable(true);
            setGeetButtonStatus(false);
        }

        // as we can only import root projects, it doesn't make sense to set a password to a child project
        if (currentProject.getParent() != null) {
            passwordButton.setDisable(true);
        }

        initButton.setOnAction((ActionEvent event) -> listener.onInitGeet(currentProject));
        addButton.setOnAction(event -> {
            listener.onAddGeet(currentProject);
            //to be sure to enable the commit button
            commitButton.setDisable(false);
        });
        passwordButton.setOnMouseClicked(event -> listener.onPopupPassword(currentProject));
        versionButton.setOnAction(event -> listener.onVersion(currentProject));
        branchButton.setOnAction(event -> listener.onBranch(currentProject));
        commitButton.setOnAction(event -> {
            listener.onCommit(currentProject);
            commitButton.setDisable(true);
            addButton.setDisable(false);
        });

        createSubProjectButton.setOnAction((ActionEvent event) -> listener.onCreateSubProject(currentProject));

        viewTaskButton.setOnAction((ActionEvent event) -> listener.onTask(currentProject));

        editButton.setOnAction((ActionEvent event) -> listener.onEditMode(currentProject,listener.getProjectReminder(currentProject.getId())));

        manageCollabButton.setOnAction((ActionEvent event) -> listener.onCollaborator(currentProject));

        statsButton.setOnAction((ActionEvent event)-> listener.onStatsButton(currentProject));
    }

    /**
     * Load information relative to a project in the description area
     * @param currentProject The loaded Project
     */
    private void loadProjectInfo(Project currentProject) {
            // rendering the infos of the main project
            if (currentProject.getGeetActivate()) {
                Branch currentBranch = listener.getBranchByProjectId(currentProject);
                currentProject.setCurrentBranch(currentBranch);
                setCurrentBranchLabel("Current branch: " + currentProject.getCurrentBranch().getBranchName());
                String versionMsg = "";
                if (currentProject.getCurrentBranch().getVersion() != null) {
                    versionMsg += currentProject.getCurrentBranch().getVersion().getId();
                }
                setCurrentVersionLabel("Current version: " + versionMsg);
            } else if (currentProject.getParent() != null) {
                setCurrentBranchLabel("");
                setCurrentVersionLabel("");
            } else {
                setCurrentBranchLabel("This project has no branch yet");
                setCurrentVersionLabel("This project has no version yet");
            }

            passwordLabel.setText("Project locked : " + (currentProject.getPassword() == null ? "false" : "true"));
            projectNameLabel.setText("Project : " + currentProject.getName());
            projectDateLabel.setText("Date of end : " + currentProject.getDateEnd().toString());

            StringBuilder tagsContent = new StringBuilder("Tags : ");
            for (Tag tag : currentProject.getTags()) {
                tagsContent.append(tag.getName()).append("  ");
            }
            tagsLabel.setText(tagsContent.toString());

            projectDescriptionLabel.setText("Description : " + currentProject.getDescription());
    }


    private class ProjectCellFactory implements Callback<ListView<Project>, ListCell<Project>> {
        @Override
        public ListCell<Project> call(ListView<Project> listview)
        {
            return new ProjectCell();
        }
    }


    private class ProjectCell extends ListCell<Project> {
        @Override
        public void updateItem(Project project, boolean empty) {
            super.updateItem(project, empty);

            int index = this.getIndex();
            String name;
            
            if (project != null && !empty) {
                LocalDate today = LocalDate.now(ZoneId.systemDefault());
                name = (index + 1) + ". " + project.getName();
                setText(name);

                if (today.isAfter(project.getDateEnd())) {
                    setStyle("-fx-background-color: #FFCCCB;");
                }
                setGraphic(null);
                setOnMouseClicked(event -> {
                    if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                        listener.loadChildOfProject(project);
                    }
                });
            }
            else {
                setText("");   // <== clear the now empty cell.
                setGraphic(null);
            }
        }
    }

    public interface ViewListener{
        /**
         * Call the view navigator and handle error messages.
         */
        void onCreateProject();

        /**
         * Create the EditProfileController which one is able to create the associated view
         */
        void onEditProfile();
        /**
         * Create the CalendarController which one is able to create the associated view
         */
        void onCalendar();
        /**
         * Ask confirmation of the logout
         * If the user confirm, disconnect the user
         */
        void logout();
        /**
         * Show the importExportController
         */
        void onImportProject();

        /**
         * Open the view to create a new sub project
         * @param parent the current project which is going to be the parent
         */
        void onCreateSubProject(Project parent);
        /**
         * View navigator to task viewer.
         * @param project Project that you want to visualize the tasks.
         */
        void onTask(Project project);
        /**
         * Go to Export Project View
         */
        void onExportProject();
        void onEditMode(Project project, Reminder reminder);
        /**
         * Call the view navigator and handle error messages.
         * @param project current project
         */
        void onCollaborator(Project project);

        /**
         * Load the children of the current project to display them in our ListView
         * @param parentProject The parent project
         */
        void loadChildOfProject(Project parentProject);

        /**
         * Navigate back to parent Project
         */
        void backToParentProject();

        /**
         * Navigate back to root Project
         */
        void backToRootProject();

        /**
         * navigate to the stats view
         */
        void onStatsButton( Project currentProject);

        /**
         * init versioning project
         * @param currentProject the parent project
         */
        void onInitGeet(Project currentProject);

        /**
         * init versioning project
         * @param currentProject the parent project
         */
        void onAddGeet(Project currentProject);
        /**
         * open Branch View
         * @param currentProject the parent project
         */
        void onBranch(Project currentProject);

        /**
         * get the branch of the project
         * @param project project
         * @return Branch
         */
        Branch getBranchByProjectId(Project project);

        /**
         * Commit current project
         * @param currentProject current project
         */
        void onCommit(Project currentProject);

        void onVersion(Project currentProject);

        /**
         * Navigate to the stats view for all projects
         */
        void onGlobalStatsButton();

        void onPopupPassword(Project currentProject);

        Reminder getProjectReminder(int projectId);

        /**
         * Open Help view
         */
        void onHelp();

    }


}

