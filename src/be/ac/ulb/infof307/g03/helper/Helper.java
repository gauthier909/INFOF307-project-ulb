package be.ac.ulb.infof307.g03.helper;

import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.Task;
import be.ac.ulb.infof307.g03.models.User;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class with useful methods which helps us!
 */
public class Helper {

    public static final String GEET_TMP = "Geet/tmp/";
    private static User currentUser = null;
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    public static User getCurrentUser(){
        return currentUser;
    }

    private static final long maxExportSize = 1000;
    public static long getMaxExportSize() {
        return maxExportSize;
    }

    /**
     * Delete a directory
     * @param file the directory to delete
     */
    public static boolean deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if(!deleteDir(f))
                    return false;
            }
        }
        return file.delete();
    }

    /**
     * delete geet temp Files of user
     */
    public static boolean deleteTempGeetFile(){
        File folder = new File(GEET_TMP);
        return Helper.deleteDir(folder);
    }

    /**
     * Delete the tempGeet file of a project
     * @param project concerned project
     */
    public static boolean deleteTempGeetFile(Project project){
        File file = new File(Helper.GEET_TMP + project.getCurrentBranch().getBranchName() + "_" + project.getId() + ".json");
        return file.delete();
    }

    /**
     * Check if the tempGeet file of a project exists
     * @param project concerned project
     * @return true if the tempGeet file exists else false
     */
    public static boolean isTempGeetFileExists(Project project){
        return new File(Helper.GEET_TMP + project.getCurrentBranch().getBranchName() + "_" + project.getId() + ".json").exists();
    }

    /**
     * Save a project object to a JSON file
     * @param project project to save
     * @throws IOException IOException
     */
    public static void saveProjectToJson(Project project, String fileName) throws IOException, DaoException {
        PrintWriter writer;
        Gson gson = new Gson();
        File temp = new File(fileName);

        List<Object> toJsonExport = new ArrayList<>();

        Map<Integer, List<Integer>> userProject = new HashMap<>();
        Map<Integer, List<Integer>> taskAssignment = new HashMap<>();
        getAllUserProject(project, userProject, taskAssignment);

        toJsonExport.add(project);
        toJsonExport.add(userProject);
        toJsonExport.add(taskAssignment);

        writer = new PrintWriter(temp);
        writer.println(gson.toJson(toJsonExport));
        writer.close();
    }

    /**
     * Get all user for a project to save properly to json
     * @param project the project
     * @param userProject the map which map a project id to a list of users that have this id
     * @throws DaoException DaoException
     */
    private static void getAllUserProject(Project project, Map<Integer, List<Integer>> userProject, Map<Integer, List<Integer>> taskAssignment) throws DaoException {
        List<Integer> collaborators = project.getAllCollaborators().stream().map(User::getId).collect(Collectors.toList());
        userProject.put(project.getId(), collaborators);
        if (project.getTasks() != null){
            for (Task task: project.getTasks()){
                taskAssignment.put(task.getId(), task.getAllUserByTaskId().stream().map(User::getId).collect(Collectors.toList()));
            }
        }

        if (project.getChildren() != null){
            for (Project child: project.getChildren()){ getAllUserProject(child, userProject, taskAssignment); }
        }
    }


}