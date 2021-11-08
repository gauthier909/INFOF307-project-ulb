package be.ac.ulb.infof307.g03.controllers;
import be.ac.ulb.infof307.g03.database.dao.project.ProjectDaoGetter;
import be.ac.ulb.infof307.g03.database.dao.ReminderDao;
import be.ac.ulb.infof307.g03.database.dao.TaskDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.*;

import java.io.File;
import java.io.IOException;

import be.ac.ulb.infof307.g03.viewsControllers.*;

import java.time.LocalDate;
import java.util.*;

/**
 * This is the main window of our application and therefore have access to the listeners of every other windows of the
 * application that is callable from the home controller.
 * This class the listener of the {@link HomeViewController.ViewListener}.<br>
 * This will manage the interaction on the HomeView.fxml view.
 */
public class HomeController extends BaseController implements HomeViewController.ViewListener,
        EditProjectController.Listener ,
        TaskController.Listener,
        EditProfileController.Listener,
        CalendarController.Listener ,
        AddCollaboratorController.Listener,
        CollaboratorManagementController.Listener,
        ImportExportController.Listener,
        BranchController.Listener,
        VersionController.Listener,
        StatisticProjectController.Listener {


    private HomeViewController homeViewController;

    private final Stack<List<Project>> projectStack;

    private List<Project> currentProjectList;

    /**
     * Private field to determine if a user already seen the different recall project alert.
     */
    private static boolean showProjectRecallAlert = true;


    /**
     * Constructor of the HomeController
     */
    public HomeController() {
        projectStack = new Stack<>();
    }


    /**
     * Setup and load the HomeView.fxml
     * @throws NavigationException nav exception
     */
    public void show() throws NavigationException {
        homeViewController = (HomeViewController) ViewLoaderSingleton.getInstance().loadView("HomeView.fxml");
        homeViewController.setListener(this);
        setup();
    }
    public void setup(){
        showNotifications();
        try {
            List<Project> projectList = Helper.getCurrentUser().getProjects();
            currentProjectList = new ArrayList<>(projectList);
        } catch (DaoException e) {
            homeViewController.showError("An error occurred", e.getMessage());
        }

        homeViewController.insertProjects(currentProjectList);
        homeViewController.setBackButtonState(!projectStack.isEmpty());

        if(showProjectRecallAlert) {
            showRecallAlert();
            showProjectRecallAlert = false;
        }
        homeViewController.setProjectButtonsStatus(!currentProjectList.isEmpty());
    }

    /**
     * execute the logic to show the recall alerts
     */
    private void showRecallAlert() {
        Map<Project, Reminder> projectReminderMap= new HashMap<>();
        Map<Task, Reminder> taskReminderMap= new HashMap<>();
        List<Project> recallProjects = new ArrayList<>();
        List<Reminder> reminders;
        List<Project> userProjects;
        List<Task> recallTask = new ArrayList<>();
        try {
            userProjects = ProjectDaoGetter.getAllParentAndChildrenByUserId(Helper.getCurrentUser().getId());
            reminders = ReminderDao.getAllReminder(userProjects);

            if(!reminders.isEmpty()){
                for(Reminder r : reminders){
                    if(r.getDate().getDayOfYear() == LocalDate.now().getDayOfYear()){
                        if(r.getTaskId()<0) {
                            Optional<Project> project = userProjects.stream().filter(p -> p.getId() == r.getProjectId()).findFirst();
                            recallProjects.add(project.get());
                            projectReminderMap.put(project.get(), r);
                        }else{
                            Task task = TaskDao.getById(r.getTaskId());
                            recallTask.add(task);
                            taskReminderMap.put(task, r);
                        }
                    }
                }
            }
        } catch (DaoException e) {
        homeViewController.showError("An error occurred", e.getMessage());
        }
        if (!recallTask.isEmpty()){
            for(Task t : recallTask){
                if(homeViewController.recallTaskAlert(t)){
                    try {
                        taskReminderMap.get(t).snooze();
                    } catch (DaoException e) {
                        homeViewController.showError("An error occurred", e.getMessage());
                    }
                }
            }
        }
        if (!recallProjects.isEmpty()){
            for(Project p : recallProjects){
                if(homeViewController.recallProjectAlert(p)){
                    try {
                        projectReminderMap.get(p).snooze();
                    } catch (DaoException e) {
                        homeViewController.showError("An error occurred", e.getMessage());
                    }

                }
            }
        }
    }

    /**
     * Show collaboration demand notifications and collaboration acceptation
     */
    private void showNotifications(){
        try {
            List<Notification> notifications = Helper.getCurrentUser().getAllNotification();
            for(Notification notification : notifications){
                if(notification.getType().equals(Notification.NotificationType.ASK_COLLAB)){
                    showCollaborationDemandNotification(notification);
                }else{
                    showCollaborationAcceptationNotification(notification);
                }
            }
        } catch (DaoException e) {
            homeViewController.showError("An error occurred", e.getMessage());
        }
    }


    /**
     * Show collaboration acceptation notifications
     * @param notification current notification
     */
    private void showCollaborationAcceptationNotification(Notification notification) throws DaoException {
        homeViewController.showAcceptedCollaboration(notification.getMessage());
        notification.delete();
    }

    /**
     * Show collaboration demand notifications
     * @param notification current notification
     */
    private void showCollaborationDemandNotification(Notification notification) throws DaoException {
        if(homeViewController.showCollaborationDemand(notification.getMessage())){
            // insert the user as a collaborator of the children and the parent

            Project project = notification.getAssociatedProject();
            project.insertCollaborator(Helper.getCurrentUser().getId());
            notification.AcceptNotification(Helper.getCurrentUser());
        }
        notification.delete();
    }


    @Override
    public void onTask(Project project){
        TaskController taskController = new TaskController(this, project);
        try{
            taskController.show();
        }catch(NavigationException e){
            homeViewController.showError("An error occurred", e.getMessage());
        }
    }

    /**
     * Load the children of the current project to display them in our ListView
     * @param parentProject The parent project
     */
    @Override
    public void loadChildOfProject(Project parentProject) {
        List<Project> children = parentProject.getChildren();
        if (children == null) {
            homeViewController.showInformation("This project has no children");
            return;
        }
        projectStack.push(new ArrayList<>(currentProjectList));
        currentProjectList.clear();
        currentProjectList.addAll(children);
        homeViewController.insertProjects(currentProjectList);
        homeViewController.setBackButtonState(true);
    }

    /**
     * Go back
     * @throws NavigationException nav exception
     */
    @Override
    public void navigateBack() throws NavigationException{
        show();
    }

    @Override
    public void onCreateProject(){
        createEditProjectController(EditProjectController.State.CREATE, null,null);
    }

    @Override
    public void onCreateSubProject(Project project){
        createEditProjectController(EditProjectController.State.CREATE, project ,null);
    }

    @Override
    public void onEditMode(Project project, Reminder reminder){
        createEditProjectController(EditProjectController.State.UPDATE, project,reminder);
    }


    @Override
    public void onCollaborator(Project project) {
        CollaboratorManagementController collaboratorManagementController = new CollaboratorManagementController(this, project);
        try {
            collaboratorManagementController.show();
        } catch (NavigationException e) {
            homeViewController.showError("An error occurred", e.getMessage());
        }
    }

    @Override
    public void onStatsButton(Project project){
        StatisticProjectController statisticProjectController = new StatisticProjectController(this, Collections.singletonList(project));
        try {
            statisticProjectController.show();
        } catch (NavigationException e) {
            homeViewController.showError("An error occurred", e.getMessage());
        }
    }

    @Override
    public void onGlobalStatsButton() {
        List<Project> finalProjects = new ArrayList<>();
        getAllProjectAndTheirChildren(currentProjectList, finalProjects);
        StatisticProjectController statisticProjectController = new StatisticProjectController(this, finalProjects);
        try {
            statisticProjectController.show();
        } catch (NavigationException e) {
            homeViewController.showError("An error occurred", e.getMessage());
        }
    }

    /**
     * Unpack the hierarchy of project to get all of them in one list
     * @param projects all projects packed
     * @param finalProjects all projects unpacked
     */
    private void getAllProjectAndTheirChildren(List<Project> projects, List<Project> finalProjects){
        for(Project project : projects){
            finalProjects.add(project);
            if(project.getChildren() != null) getAllProjectAndTheirChildren(project.getChildren(),finalProjects);
        }
    }

    /**
     * Create the edit project controller which one create the associated view
     * @param state state between CREATE,READ,UPDATE,DELETE
     * @param project Project to modify
     */
    public void createEditProjectController(EditProjectController.State state, Project project,Reminder reminder){
        EditProjectController editProjectController = new EditProjectController(this, state, project,reminder);
        try{
            editProjectController.show();
        }catch (NavigationException e){
            homeViewController.showError("An error occurred", e.getMessage());
        }
    }


    /**
     * Create the welcomeController which one is able to create the associated view
     */
    public void createWelcomeController(){
        try {
            WelcomeController welcomeController = new WelcomeController();
            welcomeController.show();
        } catch (NavigationException e) {
            homeViewController.showError("An error occurred", e.getMessage());
        }
    }



    @Override
    public void logout(){
        if (homeViewController.askConfirm("Confirm","Are you sure you want to logout ?\n (Non Committed changes will be deleted)")) {
            try {
                Helper.deleteTempGeetFile();
                Helper.getCurrentUser().logout();
            } catch (DaoException e) {
                homeViewController.showError("An error occurred", e.getMessage());
            }
            createWelcomeController();
            showProjectRecallAlert = true;
        }
    }

    @Override
    public void onEditProfile(){
        EditProfileController editProfileController = new EditProfileController(this);
        try {
            editProfileController.show();
        } catch (NavigationException e) {
            homeViewController.showError("An error occurred", e.getMessage());
        }
    }

    @Override
    public void onCalendar() {
        CalendarController calendarController= new CalendarController(this);
        try{
            calendarController.show();
        }catch (NavigationException e) {
            homeViewController.showError("An error occurred", e.getMessage());
        }
    }

    @Override
    public void onExportProject() {
        ImportExportController importExportController = new ImportExportController(this, ImportExportController.State.EXPORT);
        try{
            importExportController.show();
        }catch (NavigationException e) {
            homeViewController.showError("An error occurred", e.getMessage());
        }
    }

    @Override
    public void backToRootProject() {
        while(!projectStack.isEmpty())
            currentProjectList = projectStack.pop();
        homeViewController.insertProjects(currentProjectList);
        homeViewController.setBackButtonState(false);
    }

    @Override
    public void onInitGeet(Project currentProject) {
        try{
            currentProject.initGeet();
        } catch (DaoException e) {
            homeViewController.showError("An error occurred", e.getMessage());
        }
        homeViewController.disableInitGeet();
        homeViewController.setGeetButtonStatus(true);
        homeViewController.setCurrentBranchLabel("Current branch: " + currentProject.getCurrentBranch().getBranchName());
        homeViewController.setCurrentVersionLabel("Current version: ");
    }



    @Override
    public void onBranch(Project currentProject) {
        BranchController branchController = new BranchController(this, currentProject);
        try{
            branchController.show();
        }catch (NavigationException e) {
            homeViewController.showError("An error occurred", e.getMessage());
        }
    }

    @Override
    public void onVersion(Project currentProject) {
        VersionController versionController = new VersionController(this, currentProject);
        try{
            versionController.show();
        }catch (NavigationException e) {
            homeViewController.showError("An error occurred", e.getMessage());
        }
    }


    @Override
    public void onImportProject(){
        ImportExportController importExportController = new ImportExportController(this, ImportExportController.State.IMPORT);
        try{
            importExportController.show();
        }catch (NavigationException e) {
            homeViewController.showError("An error occurred", e.getMessage());
        }
    }

    @Override
    public void backToParentProject() {
        if(!projectStack.isEmpty()) {
            currentProjectList = projectStack.pop();
            homeViewController.insertProjects(currentProjectList);
            homeViewController.setBackButtonState(!projectStack.isEmpty());
        }
    }

    @Override
    public Branch getBranchByProjectId(Project project){
        return project.getCurrentBranch();
    }

    @Override
    public void onAddGeet(Project currentProject){
        try {
            File dir = new File(Helper.GEET_TMP);
            if (!dir.exists())dir.mkdirs();
            Helper.saveProjectToJson(currentProject, Helper.GEET_TMP + currentProject.getCurrentBranch().getBranchName() + "_" + currentProject.getId() + ".json");

        } catch (IOException | DaoException e) {
            homeViewController.showError("An error occurred", e.getMessage());
        }
        homeViewController.setAddButtonStatus(false);
    }

    @Override
    public void onCommit(Project project) {
        String commitMessage = homeViewController.alertCommitMessage();
        if(commitMessage == null) return;
        try{
            commitProject(project,commitMessage);
        } catch (IOException|DaoException e) {
            homeViewController.showError("An error occurred", e.getMessage());
        }
        try {
            show();
        } catch (NavigationException e) {
            homeViewController.showError("An error occurred", e.getMessage());
        }
    }

    @Override
    public void onPopupPassword(Project currentProject){
        String password = homeViewController.showPasswordDialog(currentProject.getName());
        if(password != null){
            if (!password.isEmpty()) currentProject.setPassword(password);
            homeViewController.displayProjects();
            try {
                currentProject.update();
            } catch (DaoException e) {
                homeViewController.showError("An error occurred", e.getMessage());
            }
        }
    }

    @Override
    public Reminder getProjectReminder(int projectId) {
        try {
            return ReminderDao.getByProjectId(projectId);
        } catch (DaoException e) {
            homeViewController.showError("An error occurred", e.getMessage());
        }
        return null;
    }

    @Override
    public void onHelp() {
        MarkdownViewController md = new MarkdownViewController("HELP.md");
        md.show();

    }

}