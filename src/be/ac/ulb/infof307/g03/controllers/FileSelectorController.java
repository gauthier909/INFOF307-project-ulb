package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.database.dao.cloud.CloudDao;
import be.ac.ulb.infof307.g03.database.dao.project.ProjectDaoGetter;
import be.ac.ulb.infof307.g03.exceptions.CloudException;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.exceptions.LocalExportException;
import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.User;
import be.ac.ulb.infof307.g03.viewsControllers.FileSelectorViewController;
import com.google.gson.Gson;
import be.ac.ulb.infof307.g03.viewsControllers.ViewLoaderSingleton;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handle the logic of selecting a file for an import or an export.
 * This class is the listener of the {@link FileSelectorViewController.ViewListener}.<br>
 * This will manage the interaction on the FileSelectorView.fxml view.
 */
public class FileSelectorController extends BaseController implements FileSelectorViewController.ViewListener {
    private final Listener listener;
    private FileSelectorViewController fileSelectorViewController;
    private final CloudDao cloudAccess;
    private final ImportExportController.State state;

    /**
     * Constructor for the CloudController
     * @param listener listener for the controller
     */
    public FileSelectorController(Listener listener, CloudDao cloudAccess, ImportExportController.State state) {
        this.listener = listener;
        this.state = state;
        this.cloudAccess = cloudAccess;
    }

    /**
     * Setup and show the FileSelectorView.fxml
     * @throws NavigationException nav exception
     */
    public void show() throws NavigationException {
        fileSelectorViewController = (FileSelectorViewController) ViewLoaderSingleton.getInstance().loadView("FileSelectorView.fxml");
        fileSelectorViewController.setListener(this);
        fileSelectorViewController.setButton(state);
        showProjects();
    }

    /**
     * Show the different projects for the current user FROM THE CLOUD !
     */
    public void showProjects() {
        try {
            if(state == ImportExportController.State.IMPORT) {
                List<String> projects = cloudAccess.getProjectNames(Helper.getCurrentUser());
                fileSelectorViewController.showImportProjects(projects);
            }
            if(state == ImportExportController.State.EXPORT){
                List<Project> projects = Helper.getCurrentUser().getProjects();
                Map<String, Project> mapProjects = new HashMap<>();
                for(Project p : projects){
                    mapProjects.put(p.getName()+"-"+p.getDateEnd().toString(), p); //unique key for each project
                }
                fileSelectorViewController.setProjectsMap(mapProjects);
                fileSelectorViewController.showExportProjects();
            }

        } catch (CloudException | DaoException e) {
            fileSelectorViewController.showError("An error occurred",e.getMessage(),false);
            goBack();
        }

    }

    @Override
    public void importProjects(List<String> projectsToImport){
        List<File> imported = new ArrayList<>();

        for(String s : projectsToImport) {
            File f = new File(s);
            try {
                cloudAccess.getProject(Helper.getCurrentUser(), s, f);
            } catch (CloudException e) {
                fileSelectorViewController.showError("Failed", e.getMessage(), false);
                goBack();
                return;
            }
            imported.add(f);
        }
        for (File selectedFile: imported) {
            Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
            File temp = new File("./temp.json");
            Project project;
            try {
                archiver.extract(selectedFile, temp.getParentFile());
                project = extractProjectFromJsonFile(temp.getPath());
                if (project.getPassword() == null) {
                    int existingProjectId = project.checkExistingProject();
                    if (existingProjectId >= 0) {
                        Project oldProject = ProjectDaoGetter.getProjectById(existingProjectId);
                        if (fileSelectorViewController.askConfirm("Override", "Existing project with this name and end date.\nDo you want to override ?") && oldProject != null) {
                            oldProject.overrideProjectFromName(project);
                        }
                    } else {
                        project.insertAfterImport();
                    }
                } else {
                    String password = fileSelectorViewController.showPasswordDialog(project.getName());
                    String hashedPwd = project.getPassword();
                    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                    if(encoder.matches(password, hashedPwd)) project.insertAfterImport();
                    else fileSelectorViewController.showError("Wrong password", "Wrong password !" );
                }
            }catch (FileNotFoundException e) {
                fileSelectorViewController.showError("Import", "File not valid", false);
                break;
            }catch (DaoException e) {
                fileSelectorViewController.showError("Failed", "An error occurred", false);
            }catch (IOException e) {
                fileSelectorViewController.showError("Failed", "Failed to import your project, please try again.", false);
                break;
            }
            temp.delete();
        }
        goBack();
    }
    /**
     * Export projects one by one
     * @param projects the projects to export
     */
    @Override
    public void exportProjects(List<Project> projects){
        for(Project project : projects){
            String fileName = project.getName()+"-"+project.getDateEnd().toString()+".tar.gz";
            try {
                export(project, fileName);
            }catch (LocalExportException e){
                fileSelectorViewController.showError("Export Error",e.getMessage(),false);
                goBack();
                return;
            }
            catch (IOException e){
                fileSelectorViewController.showError("Export Error",e.getMessage(),false);
            }
        }
        fileSelectorViewController.showSuccess("Export file(s)", "export successful");
        goBack();
    }

    /**
     * Export project to user directory if allowed size is not exceeded
     * @param project project to export
     * @param selectedFile file to export to
     */
    private void exportLocalProject(Project project,File selectedFile) throws LocalExportException {
        Gson gson = new Gson();
        //check if allowed size has been exceeded (- length of current file if overwriting)
        if(Helper.getMaxExportSize()<=(getDirectorySize(Helper.getCurrentUser().getUsername()))){
            throw new LocalExportException("Not enough space on disk, delete files or upload to cloud");
        }
        try {
            saveProjectToArchive(gson.toJson(project), selectedFile);
        } catch (IOException e) {
            fileSelectorViewController.showError("Failed", "Failed to export your project, please try again.", false);
        }
    }


    /**
     * Manage the true export of a project
     * @param project the project to export
     */
    private void export(Project project,String fileName) throws LocalExportException, IOException {
        User currentUser = Helper.getCurrentUser();
        Gson gson = new Gson();
        File selectedFile = new File("users/user_" + currentUser.getUsername() + "/" + fileName);
        if (checkIfProjectExists(Helper.getCurrentUser(), fileName)) {
            if (fileSelectorViewController.askConfirm("Confirm", "File " + fileName + "exists. Overwrite?")) {
                if (cloudAccess == null) exportLocalProject(project, selectedFile);
                else exportExistingProject(selectedFile, currentUser, gson.toJson(project), fileName);
            }
        }else{
            if (cloudAccess == null) exportLocalProject(project, selectedFile);
            else exportNonExistingProject(selectedFile, currentUser, gson.toJson(project));
        }
        if(cloudAccess !=null) selectedFile.delete();
    }


    private void exportNonExistingProject(File projectToExport, User currentUser, String content) {
        try {
            saveProjectToArchive(content, projectToExport);
            cloudAccess.addProject(projectToExport, currentUser);
        } catch (CloudException | IOException e) {
            fileSelectorViewController.showError("An error occurred", e.getMessage(), false);
        }
    }

    private void exportExistingProject(File projectToExport, User currentUser, String content, String projectName) {
        try {
            saveProjectToArchive(content, projectToExport);
            cloudAccess.updateProject(projectName, currentUser, projectToExport);
        } catch (CloudException | IOException e) {
            fileSelectorViewController.showError("An error occurred", e.getMessage(), false);
        }
    }

    private boolean checkIfProjectExists(User user, String fileName) {
        if (cloudAccess == null) return new File("users/user_" + user.getUsername() + "/" + fileName).exists();
        try {
            return cloudAccess.checkIfProjectExist(user, fileName);
        } catch (CloudException e) {
            fileSelectorViewController.showError("An error occurred", e.getMessage(), false);
        }
        return false;
    }

    /**
     * To home menu.
     */
    @Override
    public void goBack() {
        listener.navigateBack();
    }

    public interface Listener{
        void navigateBack();
    }
}
