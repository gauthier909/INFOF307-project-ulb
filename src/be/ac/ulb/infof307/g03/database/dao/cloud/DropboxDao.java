package be.ac.ulb.infof307.g03.database.dao.cloud;

import be.ac.ulb.infof307.g03.exceptions.CloudException;
import be.ac.ulb.infof307.g03.models.User;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.WriteMode;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the class used to interacted directly with the cloud service dropbox.
 * Extends the interface CloudDAO
 */
public class DropboxDao implements CloudDao{
    /**
     * The token used to create a connexion with dropbox
     */
    private static final String ACCESS_TOKEN = "8rK32609pjkAAAAAAAAAAat_kkyoL1MC_ksFOHxoL9B7c5QNrj1GGmDaQhiWCrR_";
    private final DbxClientV2 client;

    /**
     * Constructor for the DropboxDao
     */
    public DropboxDao()
    {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("").build();
        this.client = new DbxClientV2(config, ACCESS_TOKEN);
    }

    /**
     * Get list of all user's projects given in parameter
     * @param user current user
     * @return list of projects
     */
    @Override
    public List<String> getProjectNames(User user) throws CloudException
    {
        ArrayList<String> projects = new ArrayList<>();
        addUserFolder(user);
        try {

            ListFolderResult result = client.files().listFolder("/" + user.getUsername());
            for (Metadata metadata : result.getEntries()) {
                projects.add(metadata.getName());
            }
        } catch (DbxException e) {
            throw new CloudException("Failed to fetch project names. Please try again.",e);
        }

        return projects;
    }
    /**
     * Add folder in dropbox root corresponding to the user
     * @param user current user
     */
    public void addUserFolder(User user) throws CloudException {
        try {
            ListFolderResult result = client.files().listFolder("");
            for (Metadata metadata : result.getEntries()) {
                if(metadata.getName().equals(user.getUsername())){
                    return ;
                }
            }
            client.files().createFolderV2("/" + user.getUsername());

        } catch (DbxException e) {
            throw new CloudException("Couldn't create user folder",e);
        }
    }

    /**
     * Add project to the user's dropbox folder
     * @param project project to add
     * @param user current user
     */
    @Override
    public void addProject(File project, User user) throws CloudException
    {

        try (InputStream in = new FileInputStream(project)) {
            client.files().uploadBuilder("/" + user.getUsername() + "/" + project.getName()).uploadAndFinish(in);
        }catch (Exception e) {
            throw new CloudException("Failed to upload project. Please try again.",e);
        }
    }

    /**
     * Download the project from dropbox
     * @param user current user
     * @param projectName name of the project
     * @param project File path where the file will be downloaded
     */
    @Override
    public void getProject(User user, String projectName, File project) throws CloudException
    {

        try {

            OutputStream outputStream = new FileOutputStream(project);
            client.files().downloadBuilder("/" + user.getUsername() + "/" + projectName).download(outputStream);
        } catch (DbxException | IOException e) {
            throw new CloudException("Failed to fetch project from the cloud. Please try again.",e);
        }
    }

    /**
     * Check if a project already exists on the drive
     * @param user current user
     * @param projectName project to add
     * @return true if the project is already in the drive, false if not
     */
    @Override
    public boolean checkIfProjectExist(User user, String projectName) throws CloudException{
        List<String> projectList = getProjectNames(user);
        for (String project : projectList){
            if(project.equals(projectName)) return true;
        }
        return false;
    }

    /**
     * Update an existing project to the user's google drive folder
     * @param user current user
     * @param project project to add
     */
    @Override
    public void updateProject(String projectName, User user, File project) throws CloudException {
        try (InputStream in = new FileInputStream(project)) {
            client.files().uploadBuilder("/" + user.getUsername() + "/" + project.getName()).withMode(WriteMode.OVERWRITE).uploadAndFinish(in);
        }catch (Exception e) {
            throw new CloudException("Failed to update project. Please try again.",e);
        }
    }
}
