package be.ac.ulb.infof307.g03.controllers;


import be.ac.ulb.infof307.g03.database.dao.cloud.CloudDao;
import be.ac.ulb.infof307.g03.database.dao.cloud.DropboxDao;
import be.ac.ulb.infof307.g03.database.dao.cloud.GoogleDao;
import be.ac.ulb.infof307.g03.database.dao.project.ProjectDaoGetter;
import be.ac.ulb.infof307.g03.exceptions.CloudException;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.viewsControllers.ImportExportViewController;
import be.ac.ulb.infof307.g03.viewsControllers.ViewLoaderSingleton;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.*;
import java.util.List;

/**
 * Handle the logic of import and export of a project
 * This class is the listener of the {@link ImportExportViewController.ViewListener}.<br>
 * This will manage the interaction on the ImportExportView.fxml view.
 */
public class ImportExportController extends BaseController implements ImportExportViewController.ViewListener, FileSelectorController.Listener{
    private final Listener listener;
    private ImportExportViewController importExportViewController;
    private final State state;
    private CloudDao cloudAccess;

    @Override
    public void navigateBack() {
        goBack();
    }

    /**
     * Type of this controller IMPORT / EXPORT
     */
    public enum State{
        IMPORT,
        EXPORT
    }

    /**
     * Constructor for the ImportExportController
     * @param listener listener of the controller
     * @param state IMPORT - EXPORT
     */
    public ImportExportController(Listener listener, State state){
        this.listener = listener;
        this.state = state;
    }

    /**
     * Setup and show the ImportExportView.fxml
     * @throws NavigationException nav exception
     */
    public void show() throws NavigationException {
        importExportViewController = (ImportExportViewController) ViewLoaderSingleton.getInstance().loadView("ImportExportView.fxml");
        importExportViewController.setListener(this);

        final long maxExportSize = Helper.getMaxExportSize();
        final long directorySize = getDirectorySize(Helper.getCurrentUser().getUsername());
        importExportViewController.setProgressBar(maxExportSize, directorySize);
        if(maxExportSize <= directorySize && state.equals(State.EXPORT)) importExportViewController.disableLocalStorage();
    }

    @Override
    public void onLocal() {
        if(this.state == State.EXPORT)
            exportLocal();
        else
            importLocal();
    }

    /**
     * Import from local storage
     */
    private void importLocal() {
        List<File> selectedFiles = importExportViewController.openFileSelector();
        if (selectedFiles == null || selectedFiles.isEmpty())
            return;

        Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
        File temp = new File("./temp.json");

        try {
            for (File file: selectedFiles) {
                if (!file.isFile()) {
                    importExportViewController.showError("Failed", "Failed to import your project, please try again.", false);
                    return;
                }
                archiver.extract(file, temp.getParentFile());
                Project project = extractProjectFromJsonFile(temp.getPath());
                if(project.getPassword() == null) {
                    int existingProjectId = project.checkExistingProject();
                    if (existingProjectId >= 0) {
                        Project oldProject = ProjectDaoGetter.getProjectById(existingProjectId);
                        if (importExportViewController.askConfirm("Override","Existing project with this name and end date.\nDo you want to override ?") && oldProject != null) {
                            oldProject.overrideProjectFromName(project);
                        }
                    } else {
                        project.insertAfterImport();
                    }
                }else {
                    String password = importExportViewController.showPasswordDialog(project.getName());
                    String hashedPwd = project.getPassword();
                    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                    if(encoder.matches(password, hashedPwd)) project.insertAfterImport();
                    else importExportViewController.showError("Wrong password", "Wrong password !" );
                }
            }

        } catch (FileNotFoundException e) {
            importExportViewController.showError("Import", "File not valid", false);
        }catch (DaoException e) {
            importExportViewController.showError("Failed", "An error occurred", false);
        }catch (IOException e) {
            importExportViewController.showError("Failed", "Failed to import your project, please try again.", false);
        }
        temp.delete();
        goBack();
    }

    /**
     * Export from local storage
     */
    public void exportLocal(){
        FileSelectorController controller = new FileSelectorController(this,null,state);
        try {
            controller.show();
        }catch (Exception e){
            importExportViewController.showError("An error occurred", "Cannot show projects names", false);
        }
    }

    @Override
    public void onGoogleDrive() {
        try {
            cloudAccess = new GoogleDao();
            if (this.state == State.EXPORT)
                exportCloud(cloudAccess);
            else
                importCloud(cloudAccess);
        } catch(CloudException e){
            importExportViewController.showError("An error occurred",e.getMessage(), false);
        }
    }

    @Override
    public void onDropbox(){
        cloudAccess = new DropboxDao();
        if(this.state == State.EXPORT)
            exportCloud(cloudAccess);
        else
            importCloud(cloudAccess);
    }

    /**
     * Import a project to the cloud
     */
    private void importCloud(CloudDao cloudAccess) {
        FileSelectorController fileSelectorController = new FileSelectorController(this, cloudAccess, this.state);

        try {
            fileSelectorController.show();
        } catch (NavigationException e) {
            importExportViewController.showError("An error occurred", e.getMessage(), false);
        }
    }

    /**
     * Export projects to the cloud. This manage single and multiple export.
     */
    private void exportCloud(CloudDao cloudAccess) {
        FileSelectorController fileSelectorController = new FileSelectorController(this, cloudAccess, this.state);
        try {
            fileSelectorController.show();
        } catch (NavigationException e) {
            importExportViewController.showError("An error occurred",e.getMessage());
        }

    }

    @Override
    public void goBack(){
        try{
            listener.navigateBack();
        } catch (NavigationException e) {
            importExportViewController.showError("An error occurred", e.getMessage(), false);
        }
    }




    public interface Listener{
        /**
         * Navigate back
         * @throws NavigationException nav exception
         */
        void navigateBack() throws NavigationException;
    }
}
