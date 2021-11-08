package be.ac.ulb.infof307.g03.database.dao.cloud;

import be.ac.ulb.infof307.g03.exceptions.CloudException;
import be.ac.ulb.infof307.g03.models.User;
import java.io.File;
import java.util.List;

/**
 * This is the public interface to manipulate information with cloud services
 */
public interface CloudDao {

    /**
     * Get a list of project names according to the user
     * @param user to get project names
     * @return list of string with the projects name
     * @throws CloudException if an {@link Exception} is thrown
     */
    List<String> getProjectNames(User user) throws CloudException;

    /**
     * Add project to a user cloud
     * @param project a File with the project
     * @param user the user who add to the cloud.
     * @throws CloudException if an {@link Exception} is thrown
     */
    void addProject(File project, User user) throws CloudException;

    /**
     * Get a project from the cloud.
     * @param user the user who ask the project
     * @param projectName the project name
     * @param project the file where the project will be stored
     * @throws CloudException if an {@link Exception} is thrown
     */
    void getProject(User user, String projectName, File project) throws CloudException;

    /**
     * Check if a user have a project according to the name
     * @param user the user
     * @param projectName the project name
     * @return true if the user have a project on the cloud matching the project name, false otherwise
     * @throws CloudException if an {@link Exception} is thrown
     */
    boolean checkIfProjectExist(User user, String projectName) throws CloudException;

    /**
     * Update a project on the cloud with the name and for this user
     * @param projectName the project name to update
     * @param user the user who update
     * @param project the new project updated
     * @throws CloudException if an {@link Exception} is thrown
     */
    void updateProject(String projectName, User user, File project) throws CloudException;
}
