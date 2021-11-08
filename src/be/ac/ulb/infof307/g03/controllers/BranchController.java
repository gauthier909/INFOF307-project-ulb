package be.ac.ulb.infof307.g03.controllers;
import be.ac.ulb.infof307.g03.database.dao.VersionDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.*;
import be.ac.ulb.infof307.g03.viewsControllers.BranchViewController;
import be.ac.ulb.infof307.g03.viewsControllers.ViewLoaderSingleton;
import com.google.gson.JsonArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handle the logic of branching on a project.
 * This class is the listener of the {@link BranchViewController.ViewListener}.<br>
 * This will manage the interaction on the BranchView.fxml view.
 */
public class BranchController extends BaseController implements BranchViewController.ViewListener{
    private final Project project;
    private BranchViewController branchViewController;
    private final Listener listener;
    private List<Branch> branches;

    /**
     * Merge tags from projectFromOtherBranch to the current project
     * @param projectFromOtherBranch merged branch
     * @throws DaoException DaoException
     */
    private void mergeTags(Project project, Project projectFromOtherBranch) throws DaoException {
        List<Tag> tagsModified = new ArrayList<>(project.getTags());
        for (Tag tag : tagsModified) {
            if (!projectFromOtherBranch.getTags().contains(tag)) {
                project.deleteTag(tag);
            }
        }

        for(Tag tag: projectFromOtherBranch.getTags()){
            if(project.getTags() != null && project.getTags().stream().noneMatch(t -> t.getName().equals(tag.getName()))){
                tag.insert();
                project.addTag(tag);
            }
        }
    }

    /**
     * Merge tasks from projectFromOtherBranch to the current project
     * @param projectFromOtherBranch merged branch
     * @throws DaoException DaoException
     */
    private void mergeTasks(Project project, Project projectFromOtherBranch) throws DaoException {
        List<Task> tasksModify = new ArrayList<>(project.getTasks());
        for (Task task: tasksModify) {
            if (!projectFromOtherBranch.getTasks().contains(task)) {
                task.delete();
                project.removeTask(task);
            }
        }

        for (Task task: projectFromOtherBranch.getTasks()) {
            if (!project.getTasks().contains(task)) {
                task.insert();
                project.addTask(task);
            }
        }
    }

    /**
     * Principal method to merge a project from mergedProject to currentProject
     * @param currentProject currentProject which will be merged from mergedProject
     * @param mergedProject mergedProject which will merge into currentProject
     * @throws DaoException DaoException
     */
    private void merge(Project currentProject, Project mergedProject) throws DaoException {
        mergeTasks(currentProject, mergedProject);
        mergeTags(currentProject, mergedProject);

        if (mergedProject.getChildren() != null) {
            for (Project child : mergedProject.getChildren()) {
                if (currentProject.getChildren() == null) currentProject.setChildren(new ArrayList<>());
                if (!currentProject.getChildren().contains(child)) {
                    child.setParent(currentProject);
                    child.insert();
                    currentProject.addChild(child);
                } else {
                    int indexOfChild = currentProject.getChildren().indexOf(child);
                    if (indexOfChild != -1) {
                        Project childFromCurrentProject = currentProject.getChildren().get(indexOfChild);
                        merge(childFromCurrentProject, child);
                    }
                }
            }
        }

        if (currentProject.getChildren() != null) {
            List<Project> children = new ArrayList<>(currentProject.getChildren());
            for (Project child : children) {
                if (mergedProject.getChildren() == null || (mergedProject.getChildren() != null && !mergedProject.getChildren().contains(child))) {
                    child.delete();
                    currentProject.getChildren().remove(child);
                }
            }
        }
    }

    /**
     * Constructor for the CollaboratorManagementController
     * @param listener listener for the controller
     * @param project the project
     */
    public BranchController(Listener listener, Project project) {
        this.listener = listener;
        this.project = project;
    }

    /**
     * Setup and show the CollaboratorManagementView
     * @throws NavigationException nav exception
     */
    public void show() throws NavigationException {
        branchViewController = (BranchViewController) ViewLoaderSingleton.getInstance().loadView("BranchView.fxml");
        branchViewController.setListener(this);

        setup();
    }

    /**
     * Setup the view
     */
    public void setup() {
        try{
            branches = project.getAllBranches();
            if (!branches.isEmpty()) {
                branchViewController.setBranchTable(branches);
            }
        }catch (DaoException e){
            branchViewController.showError("An error occurred", e.getMessage(), false);
        }
    }

    @Override
    public void goBack() {
        try {
            listener.navigateBack();
        } catch (NavigationException e) {
            branchViewController.showError("An error occurred", e.getMessage(), false);
        }
    }

    @Override
    public void onDeleteBranch(Branch branch) {
        try {
            // Delete the 'branche' and change it by 'master'
            branch.delete(branches.get(0));
            branches.remove(branch);
            branchViewController.setBranchTable(branches);
            if(project.getCurrentBranch().getId() == branch.getId()){
                project.setCurrentBranch(branches.get(0));
                Helper.getCurrentUser().checkout(branches.get(0));
            }
        }catch(DaoException e) {
            branchViewController.showError("An error occurred", e.getMessage(), false);
        }

        //Delete the version file
        File directory = new File(geetPath + project.getId() + "/" + branch.getBranchName());
        if (directory.exists()) {
            Helper.deleteDir(directory);
        }
        goBack();
   }

    @Override
    public void onAddBranch(String branchName) {
        try {
            Branch branch = new Branch(branchName, project.getId());
            branch = branch.create();
            branches.add(branch);
            branchViewController.setBranchTable(branches);

            String pathname = geetPath+project.getId()+"/"+branch.getBranchName()+"/";
            File folder = new File(pathname);
            folder.mkdirs();
            Version version = project.getCurrentBranch().getVersion();
            String pathCurrentProject = geetPath+project.getId()+"/"+project.getCurrentBranch().getBranchName()+"/"+ version.getTimestamp()+".json";
            pathname += version.getTimestamp()+".json";

            File toCopy = new File(pathCurrentProject);
            File copyTo = new File(pathname);

            Files.copy(toCopy.toPath(), copyTo.toPath());
            VersionDao.addVersion(version, branch);

        }catch(DaoException | IOException e){
            branchViewController.showError("An error occurred", e.getMessage());
        }
    }

    @Override
    public void onCheckoutBranch(Branch branch) {
        try {
            if (Helper.isTempGeetFileExists(project)) {
                if (branchViewController.askConfirm("Checkout", "Do you want to commit your changes before the checkout ?")) {
                    try {
                        commitProject(project, "force commit on checkout");
                    } catch (IOException e) {
                        branchViewController.showError("An error occurred", "Failed to commit project");
                    }
                } else {
                    Helper.deleteTempGeetFile(project);
                }
            }
            Version lastVersion = VersionDao.getVersionsFromBranch(branch).get(0);
            // replace the old version with the new one

            File directory = new File(geetPath + project.getId() + "/" + branch.getBranchName());
            if (!directory.exists()) return;
            String path = directory + "/" + lastVersion.getTimestamp() + ".json";
            JsonArray jsonArray = convertFileToJSONArray(path);
            Project projectToReplace = extractProjectFromJsonElement(jsonArray.get(0));
            project.overrideProjectFromName(projectToReplace);
            project.setCurrentBranch(branch);
            Helper.getCurrentUser().checkout(branch);
        } catch (DaoException | IOException e) {
            branchViewController.showError("An error occurred", e.getMessage());
        }
        goBack();
    }

    @Override
    public Branch getCurrentBranch(){
        return project.getCurrentBranch();
    }

    @Override
    public void onMergeBranch(Branch branch){
        try {
            String timeStamp = Long.toString(branch.getVersion().getTimestamp());
            String projectId = Integer.toString(branch.getProjectId());
            String path = geetPath + projectId + "/" + branch.getBranchName() + "/" + timeStamp + ".json";
            Project projectFromOtherBranch = extractProjectFromJsonElement(convertFileToJSONArray(path).get(0));
            Map<String, String> differences = this.project.diff(projectFromOtherBranch).get("differences");
            if (differences.isEmpty()) {
                merge(project, projectFromOtherBranch);
                String previousProjectBranchName = this.project.getCurrentBranch().getBranchName();
                project.commit("Merge from " + branch.getBranchName() + " into " + previousProjectBranchName);
                branchViewController.showInformation("Merge from " + branch.getBranchName() + " into " + previousProjectBranchName);
            } else {
                StringBuilder finalString = new StringBuilder();
                finalString.append("Differences : \n");
                differences.forEach((key, value) -> finalString.append("\t").append(key).append(" :\t ").append(value).append("\n"));
                finalString.append("Please change values of the current branch to fit the branch to be merged");
                branchViewController.showInformation(finalString.toString());
            }
        }catch(DaoException | FileNotFoundException e){
            branchViewController.showError("Error", e.getMessage());
        }
    }

    public void setBranchViewController(BranchViewController controller) {
        this.branchViewController = controller;
    }

    public interface Listener{
        /**
         * View navigator to the main menu.
         */
        void navigateBack() throws NavigationException;
    }
}

