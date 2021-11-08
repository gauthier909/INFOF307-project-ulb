package be.ac.ulb.infof307.g03.controllers;


import be.ac.ulb.infof307.g03.database.dao.cloud.GoogleDao;
import be.ac.ulb.infof307.g03.database.dao.project.ProjectDaoGetter;
import be.ac.ulb.infof307.g03.database.dao.TagDao;
import be.ac.ulb.infof307.g03.exceptions.CloudException;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.ProjectBuilder;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.Reminder;
import be.ac.ulb.infof307.g03.models.Tag;
import be.ac.ulb.infof307.g03.viewsControllers.EditProjectViewController;
import be.ac.ulb.infof307.g03.viewsControllers.ViewLoaderSingleton;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handle the logic of editing a project
 * This class is the listener of the {@link EditProjectViewController.ViewListener}.<br>
 * This will manage the interaction on the EditProjectView.fxml view.
 */
public class EditProjectController implements EditProjectViewController.ViewListener {
    private final State state;
    private final Project project;
    private final Reminder reminder;
    private ArrayList<Tag> tagsListChoice;
    private final ArrayList<Tag> tagsDeleted;
    private List<Tag> tagsList;
    private EditProjectViewController editProjectViewController;
    private final Listener listener;

    /**
     * Constructor for the EditProjectController
     * @param listener listener of the controller
     * @param state to determine either the window is in Insert mode or edit mode (Insert, Update)
     * @param project the project
     */
    public EditProjectController(Listener listener,  State state, Project project,Reminder reminder){
        this.listener = listener;
        this.state = state;
        this.project = project;
        this.reminder = reminder;
        tagsListChoice = new ArrayList<>();
        tagsDeleted = new ArrayList<>();
    }


    /**
     * Setup and show the EditProjectView.fxml
     * @throws NavigationException nav exception
     */
    public void show() throws NavigationException {
        editProjectViewController = (EditProjectViewController) ViewLoaderSingleton.getInstance().loadView("EditProjectView.fxml");
        editProjectViewController.setListener(this);
        setup();
    }

    /**
     * Setup the view
     */
    public void setup() {
        try {
            tagsList = Helper.getCurrentUser().getAllTags();
        } catch (DaoException e) {
            editProjectViewController.showError("An error occurred", e.getMessage(), false);
        }
        editProjectViewController.setTags(tagsList);

        if(this.state.equals(State.UPDATE)){
            editProjectViewController.setViewForUpdate(project.getName(), project.getDescription(), project.getDateEnd());
            editProjectViewController.setSpinner(LocalDate.now(),project.getDateEnd());
            try {
                tagsListChoice = (ArrayList<Tag>) project.getAllTags();
            } catch (DaoException e) {
                editProjectViewController.showError("An error occurred", e.getMessage(), false);
            }
            try {

                project.getAllTags().forEach(tag -> editProjectViewController.addTagButton(tag,true));
            } catch (DaoException e) {
                editProjectViewController.showError("An error occurred", e.getMessage(), false);
            }
        }else {
            editProjectViewController.setSpinner(LocalDate.now(),LocalDate.now().plusDays(1000));
            editProjectViewController.setViewForCreate();
        }
    }

    /**
     * Check if a project has a name, a description and a valid date.
     * @param name String name of the project
     * @param description String with the description of the project
     * @param date LocalDate check the validity of the project date
     * @return true if data error,else false
     */
    boolean checkData(String name, String description, LocalDate date){
        if(name == null || name.length()==0){
            editProjectViewController.setErrorLabel("Project name is empty.");
            return true;
        } else if(description == null || description.length()==0){
            editProjectViewController.setErrorLabel("Description is empty.");
            return true;
        } else if(date==null || date.compareTo(LocalDate.now())<0){
            editProjectViewController.setErrorLabel("The date given is invalid.");
            return true;
        }
        return false;
    }

    @Override
    public void removeTagFromSelectedTagList(Tag tag) {
        tagsListChoice.remove(tag);
        tagsDeleted.add(tag);
    }

    @Override
    public void createProject(String name, String description, LocalDate date, int reminderDate, boolean isCheckedGoogle) throws NavigationException {
        if(checkData(name, description, date)) return;
        if(isCheckedGoogle) { addGoogleCalendar(name, description, date); }
        Project newProject;
        Reminder reminder;
        if(state.equals(State.CREATE)){
            if(project == null){ //CreateProject
                newProject = new ProjectBuilder().name(name).dateEnd(date).description(description).tags(tagsListChoice).build();
            }else{ // Create sub project
                //Add tag if the tag is in the parent project
                project.getTags().forEach(rootTag -> {
                    if (tagsListChoice.stream().noneMatch(t -> t.getName().equals(rootTag.getName())))
                        tagsListChoice.add(rootTag);
                });
                newProject = new ProjectBuilder().name(name).dateEnd(date).description(description).tags(tagsListChoice).parent(project).build();
                project.addChild(newProject);
            }
            try {
                int existingProjectId = newProject.checkExistingProject();
                if (existingProjectId >= 0) {
                    Project oldProject = ProjectDaoGetter.getProjectById(existingProjectId);
                    if (editProjectViewController.askConfirm("Override","Existing project with this name and end date.\nDo you want to override ?") && oldProject != null) {
                        oldProject.overrideProjectFromName(newProject);
                    }
                } else {
                    newProject.insert();
                    reminder = new Reminder(newProject.getId(),Reminder.NOTATASK,newProject.getDateEnd().minusDays(reminderDate));
                    reminder.insert();
                }
            } catch (DaoException e) {
                editProjectViewController.showError("An error occurred", e.getMessage(), false);
            }
        }
        listener.navigateBack();

    }

    @Override
    public void updateProject(String name, String description, LocalDate date , int reminderValue) throws NavigationException {

        if(checkData(name, description, date)) return;
        project.setName(name);
        project.setDescription(description);
        project.setDateEnd(date);
        LocalDate reminderDate = date.minusDays(reminderValue);
        reminder.setDate(reminderDate);
        try {
            project.update();
            reminder.update();
        } catch (DaoException e) {
            editProjectViewController.showError("An error occurred", e.getMessage(), false);
        }
        if (project.getTags() != null) {
            for(Tag projectTag : project.getTags()){
                tagsListChoice.removeIf(tag -> tag.getId() == projectTag.getId());
            }
        }

        tagsListChoice.forEach(tag -> {
            try {
                if(tag.getId() == -1) tag.setId(TagDao.insert(tag));
                project.addTag(tag);
            } catch (DaoException e) {
                editProjectViewController.displayError("An error occurred", e.getMessage());
            }

        });

        tagsDeleted.forEach(tag -> {
            try {
                project.deleteTag(tag);
            } catch (DaoException e) {
                editProjectViewController.displayError("Failed", "Failed to delete the tag from the project, please try again.");
            }
        });
        listener.navigateBack();
    }

    @Override
    public void goBack() throws NavigationException {
        listener.navigateBack();
    }

    @Override
    public void onSelectTagsBox(Tag tag) {
        if(tag != null && tagsListChoice.stream().noneMatch(t -> t.getId() == tag.getId())){
            addSelectedTag(tag);
        }
    }

    @Override
    public void addNewTag(String tagName) {
        if(!tagName.isEmpty()){
            final Tag tag = new Tag(tagName);
            Optional<Tag> optionalTag = tagsList.stream().filter(t -> t.getName().equals(tag.getName())).findAny();

            if(optionalTag.isPresent()) tag.setId(optionalTag.get().getId());
            else tag.setId(-1);

            addSelectedTag(tag);
        }
    }

    /**
     * Put the tag in a selectedTagsList
     * Add the tag's name into a button which is injected inside a HBox
     * Each button can be pressed to remove the tag from the HBox
     * @param tag selected tag
     */
    public void addSelectedTag(Tag tag){
        tagsListChoice.add(tag);
        tagsDeleted.remove(tag);
        editProjectViewController.clearNameTagInput();
        editProjectViewController.addTagButton(tag,false);
    }

    @Override
    public void deleteProject() throws NavigationException {
        if(editProjectViewController.askConfirm("Confirm","Warning : the project and all the subprojects will be deleted")){
            try {
                boolean isProjectDeleted = project.delete();
                if (isProjectDeleted) Helper.deleteDir(new File("./Geet/" + project.getId()));
            } catch (DaoException e) {
                editProjectViewController.showError("An error occurred", e.getMessage(), false);
            }
            listener.navigateBack();
        }
    }

    public void setViewController(EditProjectViewController editProjectViewController) {
        this.editProjectViewController = editProjectViewController;
    }

    /**
     * Add the project to google calendar through the googleDao class
     * @param name Name of the project to add
     * @param desc Description of the project to add
     * @param date date of the end of the project to add
     */
    private void addGoogleCalendar(String name, String desc, LocalDate date){
        try {
            GoogleDao googleDao = new GoogleDao();
            googleDao.addToCalendar(name, desc, date);
        } catch (CloudException | IOException e) {
            editProjectViewController.showError("An error occurred", e.getMessage(), false);
        }

    }

    public interface Listener{
        /**
         * Go to the previous view
         * @throws NavigationException nav exception
         */
        void navigateBack() throws NavigationException;
    }

    public enum State { //crud enum
        CREATE,UPDATE
    }
}
