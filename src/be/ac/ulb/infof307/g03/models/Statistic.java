package be.ac.ulb.infof307.g03.models;

import be.ac.ulb.infof307.g03.exceptions.DaoException;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * class representing the statistics and containing the projects they are reffering to in List projects
 */
public class Statistic {
    @Expose
    private int numberOfCollaborator;

    @Expose
    private int numberOfTasks;

    @Expose
    private int numberOfAssignedTasks;

    @Expose
    private String estimatedTime;

    @Expose
    private String realTime;

    @Expose
    private int numberOfTaskLeft;

    @Expose
    private int numberOfAssignedCollaborators;

    @Expose
    private Map<Integer, Integer> numberOfCollabPerTask;


    private final List<Project> projects;


    public Statistic(List<Project> project) throws DaoException{
        projects = new ArrayList<>(project);
        this.setEstimatedTime();
        this.setNumberOfAssignedCollaborators();
        this.setNumberOfCollaborator();
        this.setNumberOfTasks();
        this.setNumberOfTasksLeft();
        this.setNumberOfAssignedTasks();
        this.setNumberOfCollabPerTask();
    }

    /**
     * Return the number of assigned collaborators to at least one task per project.
     *
     * @throws DaoException dao exception
     */
    private void setNumberOfAssignedCollaborators() throws DaoException {
        Set<User> userList = new HashSet<>();
        for (Project project : projects) {
            if (project.getTasks() != null){
                for (Task task : project.getTasks()) {
                    userList.addAll(task.getAllUserByTaskId());
                }
            }

        }
        numberOfAssignedCollaborators = userList.size();
    }

    public int getNumberOfAssignedCollaborators() { return numberOfAssignedCollaborators; }

    /**
     * Set the number of collaborator linked to the projects
     *
     * @throws DaoException dao exception
     */
    private void setNumberOfCollaborator() throws DaoException {
        int numberOfCollaborator = 0;
        for (Project project : projects) {
            numberOfCollaborator += project.getNumberOfCollaborator();
        }
        this.numberOfCollaborator = numberOfCollaborator;
    }

    public int getNumberOfCollaborator() {
        return numberOfCollaborator;
    }

    /**
     * Set the number of tasks per projects
     *
     * @throws DaoException dao exception
     */
    private void setNumberOfTasks() throws DaoException {
        int numberOfTask = 0;
        for (Project project : projects) {
            numberOfTask += project.getNumberOfTask();
        }
        this.numberOfTasks = numberOfTask;
    }

    public int getNumberOfTasks() {
        return numberOfTasks;
    }

    /**
     * Set the number of task that has an assignment
     * @throws DaoException dao exception
     */
    private void setNumberOfAssignedTasks() throws DaoException {
        int numberOfAssignedTask = 0;
        for (Project project : projects) {
            if (project.getTasks() != null) {
                for (Task task : project.getTasks()) {
                    if (task.getAllUserByTaskId().size() > 0) {
                        numberOfAssignedTask++;
                    }
                }
            }
        }
        this.numberOfAssignedTasks = numberOfAssignedTask;
    }

    public int getNumberOfAssignedTasks() {
        return numberOfAssignedTasks;
    }

    public int getNumberOfNonAssignedTasks() {
        return numberOfTasks - numberOfAssignedTasks;
    }

    /**
     * Set the estimated and real time for all projects
     */
    private void setEstimatedTime(){
        LocalDate minStartDate = LocalDate.MAX;
        LocalDate maxEndDate = LocalDate.MIN;
        for(Project project : projects){
            LocalDate startDate = project.getStartDate();
            LocalDate endDate = project.getDateEnd();
            if(minStartDate.compareTo(startDate) > 0){
                minStartDate = startDate;
            }
            if(maxEndDate.compareTo(endDate) < 0){
                maxEndDate = endDate;
            }
        }

        this.estimatedTime = maxEndDate.toEpochDay() - minStartDate.toEpochDay() + " days";
        this.realTime = LocalDate.now().toEpochDay() - minStartDate.toEpochDay() + " / " + this.estimatedTime;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public String getRealTime() {
        return realTime;
    }

    public int getNumberOfTaskLeft() {
        return numberOfTaskLeft;
    }

    /**
     * Set the number of tasks left per projects
     *
     * @throws DaoException dao exception
     */
    private void setNumberOfTasksLeft() throws DaoException {
        int numberOfTaskLeft = 0;
        for (Project project : projects) {
            numberOfTaskLeft += project.getNumberOfTaskLeft();
        }
        this.numberOfTaskLeft = numberOfTaskLeft;
    }

    /**
     * Set the number of collaborator for each task
     * Create a map containing the id of the collaborator as key and
     * the number of collaborator(s) for the given task as the value
     * @throws DaoException dao exception
     */
    private void setNumberOfCollabPerTask() throws DaoException{
        this.numberOfCollabPerTask = new HashMap<>();
        for(Project project : projects){
            if (project.getTasks() != null) {
                for (Task task : project.getTasks()) {
                    int numberOfCollab = task.getAllUserByTaskId().size();
                    numberOfCollabPerTask.put(task.getId(), numberOfCollab);
                }
            }
        }
    }
    public Map<Integer, Integer> getNumberOfCollabPerTask() {
        return numberOfCollabPerTask;
    }

    /**
     * Export raw stat data to .csv format
     * @param file destination to write data into.
     */
    public void exportCsv(File file) throws IOException{
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getPath()));
        writeIntoCsv(bufferedWriter, "Number of collaborator", String.valueOf(numberOfCollaborator));
        writeIntoCsv(bufferedWriter, "Number of tasks", String.valueOf(numberOfTasks));
        writeIntoCsv(bufferedWriter, "Number of assigned tasks", String.valueOf(numberOfAssignedTasks));
        writeIntoCsv(bufferedWriter, "Estimated time", String.valueOf(estimatedTime));
        writeIntoCsv(bufferedWriter, "Real time", String.valueOf(realTime));
        writeIntoCsv(bufferedWriter, "Number of task(s) left", String.valueOf(numberOfTaskLeft));
        writeIntoCsv(bufferedWriter, "Number of assigned collaborators", String.valueOf(numberOfAssignedCollaborators));
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    /**
     * Write a line inside a csv
     * @param writer the buffered writer link to the csv
     * @param column the column's name
     * @param line line
     * @throws IOException input output exception
     */
    private void writeIntoCsv(BufferedWriter writer,String column, String line) throws IOException {
        writer.write(column + "," + line);
        writer.newLine();
    }

    /**
     * Export raw stat data to .json format
     * @param file destination to write data into.
     */
    public void exportJson(File file) throws IOException {
        Gson gson = new Gson().newBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation().create();
        PrintWriter printWriter = new PrintWriter(file.getPath());
        printWriter.println(gson.toJson(this));
        printWriter.flush();
        printWriter.close();
    }
}
