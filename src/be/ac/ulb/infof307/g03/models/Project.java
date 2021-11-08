package be.ac.ulb.infof307.g03.models;

import be.ac.ulb.infof307.g03.database.dao.*;
import be.ac.ulb.infof307.g03.database.dao.project.ProjectDaoGetter;
import be.ac.ulb.infof307.g03.database.dao.project.ProjectDaoSetter;
import be.ac.ulb.infof307.g03.database.dao.user.UserGetterDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.utils.Utils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.*;

/**
 * Class representing a Project, contains information about the project as well as its children.
 */
public class Project {
    private int id;
    private String name;
    private String description;
    private final LocalDate startDate;
    private LocalDate dateEnd;
    private Boolean isGeetActivate;
    /**
     * List of project's children
     */
    private List<Project> children;
    private List<Tag> tags;
    private List<Task> tasks;
    /**
     * The project's parent; null if this is a root project
     */
    private Project parent;
    /**
     * The branch the project is using currently
     */
    private Branch currentBranch;

    private String password;


    /**
     * Constructor who create a project using a project builder
     * @param build the project builder
     */
    protected Project(ProjectBuilder build){
        this.id = build.id;
        this.name = build.name;
        this.description = build.description;
        this.startDate = build.startDate;
        this.dateEnd = build.dateEnd;
        this.isGeetActivate = build.isGeetActivate;
        this.children = build.children;
        this.tags = build.tags;
        this.tasks = build.tasks;
        this.parent = build.parent;
        this.currentBranch = build.currentBranch;
        this.password = build.password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return name.equals(project.name) && dateEnd.equals(project.dateEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dateEnd);
    }

    /**
     * Create the project in the database
     * @throws DaoException an exception that can be raised if an error occurred while trying to insert the new project
     */
    public void save() throws DaoException{
        this.id = ProjectDaoSetter.insert(this);
    }

    /**
     * Delete the project from the database
     * @return true if the project has been deleted
     * @throws DaoException an exception that can be raised if an error occurred while trying to insert the new project
     */
    public boolean delete() throws DaoException{
        return ProjectDaoSetter.delete(this.id);
    }

    /**
     * Update a project in the database
     * @throws DaoException  an exception who can be raised if an error occurred while trying to update the project
     */
    public void update() throws DaoException{
        ProjectDaoSetter.update(this);
    }

    /**
     * Init the geet tracking for the project
     * @throws DaoException an exception that can be raised if an error occurred while trying to insert the new project
     */
    public void initGeet() throws DaoException{
        VersionDao.initGeet(this);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", dateEnd=" + dateEnd +
                ", parent=" + parent +
                '}';
    }

    /**
     * Add a child to the project
     * @param project the child to insert
     */
    public void addChild(Project project){
        if(this.children==null){
            this.children = new ArrayList<>();
        }
        this.children.add(project);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Project getParent() {
        return parent;
    }

    public void setParent(Project parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get the hash of the password of the project
     * @return the hash of the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the hash of the password of the project
     *
     */
    public void setPassword(String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.password = encoder.encode(password);
    }

    /**
     * Set the description of the project
     * @param description new description of the project
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(LocalDate dateEnd) {
        this.dateEnd = dateEnd;
    }

    public List<Tag> getTags() {
        if(this.tags == null)
            this.tags = new ArrayList<>();
        return tags;
    }

    public void setTasks(List<Task> tasks){
        this.tasks = tasks;
    }

    public void addTask(Task task){
        if (this.tasks == null)
            this.tasks = new ArrayList<>();
        this.tasks.add(task);
    }

    public List<Task> getTasks(){
        return this.tasks;
    }

    public List<Project> getChildren() { return this.children;}

    public void setChildren(List<Project> children) { this.children = children;}

    public Boolean getGeetActivate() { return isGeetActivate;}

    public void setGeetActivate(Boolean geetActivate) {isGeetActivate = geetActivate; }

    public Branch getCurrentBranch() { return currentBranch;}

    public void setCurrentBranch(Branch currentBranch) {this.currentBranch = currentBranch;}

    /**
     * Get a list of users associated to the project
     * @return users associated to the project
     * @throws DaoException DaoException
     */
    public List<User> getUserByProject() throws DaoException {
        return UserGetterDao.getUserByProject(this.getId());
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Get the list of collaborators associated to the project (i.e. all the users associated to the project besides the user itself)
     * @param userId id of the user
     * @return list of collaborators
     * @throws DaoException DaoException
     */
    public List<User> getAllCollaborators(int userId) throws DaoException {
        return CollaboratorDao.getAllCollaborators(this.getId(), userId);
    }

    /**
     * Get the list of collaborators associated to the project
     * @return list of collaborators
     * @throws DaoException DaoException
     */
    public List<User> getAllCollaborators() throws DaoException {
        return CollaboratorDao.getAllCollaborators(this.getId());
    }

    /**
     * Remove a collaborator from the project
     * @param userId id of the user to be removed
     * @throws DaoException DaoException
     */
    public void removeCollaborator(int userId) throws DaoException {
        CollaboratorDao.removeCollaborator(userId, this.getId());
    }

    /**
     *  Check for an existing project in the database according to the unique fields of a project.
     * @return true or false depending if the project exists or not
     * @throws DaoException DaoException
     */
    public int checkExistingProject() throws DaoException {
        return ProjectDaoGetter.checkExistingProject(this.getName(), this.getDateEnd());
    }

    /**
     * Override a project with his new information
     * @param newProject new project that overrides the old one
     * @throws DaoException DaoException
     */
    public void overrideProjectFromName(Project newProject) throws DaoException {
        ProjectDaoSetter.overrideProjectFromName(this, newProject);
    }


    /**
     * Insert a project after import and check if the tags name already exist.
     * @throws DaoException DaoException
     */
    public void insertAfterImport() throws DaoException {
        ProjectDaoSetter.insertAfterImport(this);
    }

    /**
     * Insert a collaborator to a project
     * @param userId id of the project
     * @throws DaoException DaoException
     */
    public void insertCollaborator(int userId) throws DaoException {
        CollaboratorDao.insertCollaborator(userId, this.getId());
    }


    public List<Task> getAllTasks() throws DaoException {
        return TaskDao.getAllByProjectId(this.getId());
    }

    /**
     * Commit current Branch on latest version
     * @param commitMessage commit message
     */
    public Version commit(String commitMessage) throws DaoException {
        return VersionDao.commit(this, commitMessage);
    }

    public List<Branch> getAllBranches() throws DaoException {
        return BranchDao.getAllProjectBranches(this.getId());
    }

    public List<Tag> getAllTags() throws DaoException {
        return TagDao.getByProjectId(this.getId());
    }

    public void insert() throws DaoException {
        int id = ProjectDaoSetter.insert(this);
        this.setId(id);
    }

    public void addTag(Tag tag) throws DaoException {
        TagDao.insertToProject(tag.getId(), this.getId());
        if (this.tags == null)
            this.tags = new ArrayList<>();
        this.tags.add(tag);
    }

    public void deleteTag(Tag tag) throws DaoException {
        TagDao.deleteFromProject(tag.getId(), this.getId());
        this.tags.removeIf(t->t.getId() == tag.getId());
    }

    /**
     * Create a map with all the differences between 2 projects
     * @param otherProject the project to compare with
     * @return map with all the differences
     */
    public Map<String, Map<String, String>> diff(Project otherProject){
        Map<String, Map<String, String>> ret = new HashMap<>();
        ret.put("differences", new HashMap<>());
        ret.put("added", new HashMap<>());
        ret.put("deleted", new HashMap<>());
        if(!name.equals(otherProject.name))
            ret.get("differences").put("name", name + ", " + otherProject.name);
        if(!description.equals(otherProject.description))
            ret.get("differences").put("description", description + ", " + otherProject.description);
        if(dateEnd.compareTo(otherProject.dateEnd) != 0)
            ret.get("differences").put("dateEnd", dateEnd + ", " + otherProject.dateEnd);

        if(children != null){
            mergeDiffMap(ret, checkChildrenDiff(otherProject.children));
        }

        if(tags != null){
            mergeDiffMap(ret, checkTagsDiff(otherProject.tags));
        }

        if(tasks != null){
            mergeDiffMap(ret, checkTasksDiff(otherProject.tasks));
        }
        return ret;
    }

    /**
     * Merging differences + added + deleted map into the diffMap
     * @param diffMap the map containing differencesMap, addedMap, deletedMap
     * @param newDiffMap the diff map returned by the check function
     */
    private void mergeDiffMap(Map<String, Map<String, String>> diffMap, Map<String, Map<String, String>> newDiffMap){
        newDiffMap.forEach((key, value) -> {
            switch(key){
                case "added":
                    diffMap.get("added").putAll(value);
                    break;
                case "deleted":
                    diffMap.get("deleted").putAll(value);
                    break;
                default:
                    diffMap.get("differences").putAll(value);
                    break;
            }
        });
    }

    /**
     * Checking the differences between tags
     * @param otherTags tags of the other project
     * @return Map containing the differences between children + added and removed tags
     */
    private Map<String, Map<String, String>> checkTagsDiff(List<Tag> otherTags){
        List<Integer> idVisited = new ArrayList<>();
        Map<String, String> differences = new HashMap<>();
        Map<String, String> deleted = new HashMap<>();
        Map<String, String> added = new HashMap<>();
        if(otherTags != null){
            for(Tag tag : tags){
                for(Tag otherTag: otherTags){
                    if(tag.getId() == otherTag.getId()){
                        idVisited.add(tag.getId());
                        String tmp = tag.diff(otherTag);
                        if(tmp != null)
                            differences.put("tag " + tag.getId(), tmp);
                    }

                }
            }
            for(Tag tag : otherTags){
                if(!idVisited.contains(tag.getId())) deleted.put("tag "+ tag.getId(), tag.getName());
            }
        }
        for(Tag tag: tags){
            if(!idVisited.contains(tag.getId())) added.put("tag " + tag.getId(), tag.getName());
        }
        Map<String, Map<String,String>> res = new HashMap<>();
        res.put("differences", differences);
        res.put("deleted", deleted);
        res.put("added", added);
        return res;
    }

    /**
     * Checking the differences between tasks
     * @param otherTasks tasks of the other project
     * @return Map containing the differences between children + added and removed tasks
     */
    private Map<String, Map<String, String>> checkTasksDiff(List<Task> otherTasks){
        Map<String, String> differences = new HashMap<>();
        Map<String, String> deleted = new HashMap<>();
        Map<String, String> added = new HashMap<>();
        List<Integer> idVisited = new ArrayList<>();

        if(otherTasks != null){
            for(Task task : tasks){
                for(Task otherTask: otherTasks){
                    if(task.getId() == otherTask.getId()){
                        idVisited.add(task.getId());
                        differences.putAll(task.diff(otherTask));
                    }
                }
            }

            for(Task task : otherTasks){
                if(!idVisited.contains(task.getId())) deleted.put("Task "+task.getId(), task.getDescription());
            }
        }
        for(Task task: tasks){
            if(!idVisited.contains(task.getId())) added.put("Task " + task.getId(), task.getDescription());
        }
        Map<String, Map<String,String>> res = new HashMap<>();
        res.put("differences", differences);
        res.put("deleted", deleted);
        res.put("added", added);
        return res;
    }

    /**
     * Checking the differences between children between 2 projects
     * @param otherChildren children for the other project
     * @return Map containing the differences between children + added and removed children
     */
    private Map<String, Map<String,String>> checkChildrenDiff(List<Project> otherChildren) {
        Map<String, String> differences = new HashMap<>();
        List<Integer> idVisited = new ArrayList<>();
        Map<String, String> deleted = new HashMap<>();
        Map<String, String> added = new HashMap<>();
        if(otherChildren != null){
            for (Project child: children){
                for(Project otherChild: otherChildren){
                    if(child.getId() == otherChild.getId()){
                        idVisited.add(otherChild.getId());
                        Map<String, Map<String, String>> tempDiff = child.diff(otherChild);
                        if(!tempDiff.get("differences").isEmpty()){
                            differences.put("children " + child.getId(), Utils.convertWithStream(tempDiff.get("differences")));
                        }
                        if(!tempDiff.get("added").isEmpty()){
                            added.put("children " + child.getId(), Utils.convertWithStream(tempDiff.get("added")));
                        }
                        if(!tempDiff.get("deleted").isEmpty()){
                            deleted.put("children " + child.getId(), Utils.convertWithStream(tempDiff.get("deleted")));
                        }
                    }
                }
            }
            for(Project otherChild : otherChildren){
                if(!idVisited.contains(otherChild.getId())){
                    deleted.put("children " + otherChild.getId(), otherChild.getName());
                }
            }
        }
        for(Project child: children){
            if(!idVisited.contains(child.getId())) added.put("children " + child.getId(), child.getName());
        }
        Map<String, Map<String,String>> res = new HashMap<>();
        res.put("differences", differences);
        res.put("deleted", deleted);
        res.put("added", added);
        return res;
    }

    /**
     * Get the number of collaborator of this project
     * @return the number of collaborator of this project or -1 if this project doesn't exist in the database
     * @throws DaoException DaoException
     */
    public int getNumberOfCollaborator() throws DaoException {
        return ProjectDaoGetter.getNumberOfCollaborator(this);
    }

    /**
     * Get the number of task of this project
     * @return the number of task on this project or -1 if this project doesn't exist in the database
     * @throws DaoException DaoException
     */
    public int getNumberOfTask() throws DaoException {
        return TaskDao.getNumberOfTask(this);
    }

    /**
     * Delete a task from a project
     * @param task task to be deleted
     */
    public void removeTask(Task task) {
        tasks.remove(task);
    }

    /**
     * Return the number of task(s) left for the project
     * @return number of task(s) left
     */
    public int getNumberOfTaskLeft() throws DaoException {
        return TaskDao.getNumberOfTaskLeft(this);
    }
}
