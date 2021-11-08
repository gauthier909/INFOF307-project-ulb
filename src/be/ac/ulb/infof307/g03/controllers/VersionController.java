package be.ac.ulb.infof307.g03.controllers;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.models.Branch;
import be.ac.ulb.infof307.g03.models.Version;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.viewsControllers.VersionViewController;
import be.ac.ulb.infof307.g03.viewsControllers.ViewLoaderSingleton;
import com.google.gson.JsonArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * Handle the logic of the versioning of a project
 * This class is the listener of the {@link VersionViewController.ViewListener}.<br>
 * This will manage the interaction on the VersionView.fxml view.
 */
public class VersionController extends BaseController implements VersionViewController.ViewListener{
    private final Project project;
    private VersionViewController VersionViewController;
    private final Listener listener;

    /**
     * Constructor for the CollaboratorManagementController
     * @param listener listener for the controller
     * @param project the project
     */
    public VersionController(Listener listener, Project project) {
        this.listener = listener;
        this.project = project;
    }

    /**
     * Setup and show the CollaboratorManagementView
     * @throws NavigationException nav exception
     */
    public void show() throws NavigationException {
        VersionViewController = (VersionViewController) ViewLoaderSingleton.getInstance().loadView("VersionView.fxml");
        VersionViewController.setListener(this);

        try{
            List<Version> versions = project.getCurrentBranch().getAllVersions();
            if (!versions.isEmpty()) {
                VersionViewController.setVersionTable(versions);
            }
        }catch (DaoException e){
            VersionViewController.showError("An error occurred", e.getMessage(), false);
        }
    }

    @Override
    public Version getCurrentVersion() {
        return project.getCurrentBranch().getVersion();
    }

    @Override
    public void goBack() {
        try {
            listener.navigateBack();
        } catch (NavigationException e) {
            VersionViewController.showError("An error occurred", e.getMessage(), false);
        }
    }

    @Override
    public void onRevertVersion(Version version) {
        Branch currentBranch = project.getCurrentBranch();

        // replace the old version with the new one
        File directory = new File(geetPath + project.getId() + "/" + currentBranch.getBranchName());
        if (!directory.exists()) return;
        String path = directory + "/" + version.getTimestamp() + ".json";
        try {
            JsonArray jsonArray = convertFileToJSONArray(path);
            Project projectToReplace = extractProjectFromJsonElement(jsonArray.get(0));
            project.overrideProjectFromName(projectToReplace);
        } catch (FileNotFoundException | DaoException e) {
            VersionViewController.showError("An error occurred", e.getMessage(), false);
        }

        // update the current version for the branch
        try {
            currentBranch.updateVersion(version);
        } catch(DaoException e) {
            VersionViewController.showError("An error occurred", e.getMessage(), false);
        }

        goBack();

    }

    @Override
    public void onRemoveVersion(Version version){
        Branch versionBranch;
        try {
            versionBranch = version.getMyBranch();
        } catch (DaoException e) {
            VersionViewController.showError("An error occurred", e.getMessage());
            return;
        }

        //Delete the version file
        File versionFile = new File(geetPath + project.getId() + "/" + versionBranch.getBranchName() + "/" + version.getTimestamp() + ".json");
        if (!versionFile.exists()){
            VersionViewController.showError("An error occurred", "File doesn't exist");
            return;
        }
        if(!versionFile.delete()){
            VersionViewController.showError("An error occurred", "Error while trying to delete the file");
            return;
        }

        //Delete in the database
        try {
            version.delete();
        } catch (DaoException e) {
            VersionViewController.showError("An error occurred", e.getMessage());
        }

        goBack();
    }

    @Override
    public void onDiffVersion(Version otherVersion){
        String path;
        try {
            path = geetPath + otherVersion.getProjectId() + "/" + otherVersion.getMyBranch().getBranchName() + "/" + otherVersion.getTimestamp() + ".json";
            JsonArray jsonArray = convertFileToJSONArray(path);
            Project otherProject = extractProjectFromJsonElement(jsonArray.get(0));
            Map<String, Map<String, String>> diff = project.diff(otherProject);
            String finalString = "Diff between project : " + project.getName() + " and project : " + otherProject.getName() + "\n\n" +
                    diffMapToString(diff);
            VersionViewController.showInformation(finalString);
        } catch (DaoException | FileNotFoundException e) {
            VersionViewController.showError("An error occurred", e.getMessage());
        }
    }

    /**
     * Express in a string the difference between 2 versions
     * @param diff Map of differences
     * @return a string of the format :
     * 'differences: ...
     * added: ...
     * deleted: ...'
     */
    private String diffMapToString(Map<String, Map<String, String>> diff){
        StringBuilder finalString = new StringBuilder();
        Map<String, String> differences = diff.get("differences");
        Map<String, String> added = diff.get("added");
        Map<String, String> deleted = diff.get("deleted");
        finalString.append("Differences : \n");
        differences.forEach((key, value) -> finalString.append("\t").append(key).append(" :\t ").append(value).append("\n"));
        finalString.append("Added : \n");
        added.forEach((key, value) -> finalString.append("\t").append(key).append(" :\t ").append(value).append("\n"));
        finalString.append("Deleted : \n");
        deleted.forEach((key, value) -> finalString.append("\t").append(key).append(" :\t ").append(value).append("\n"));
        return finalString.toString();
    }

    public interface Listener{
        /**
         * View navigator to the main menu.
         */
        void navigateBack() throws NavigationException;
    }
}

