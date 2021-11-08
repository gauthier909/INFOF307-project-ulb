package be.ac.ulb.infof307.g03.models;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.List;

/**
 * A builder to create the project
 */
public class ProjectBuilder {
    public String password;
    protected int id;
    protected String name;
    protected String description;
    protected LocalDate dateEnd;
    protected LocalDate startDate;
    protected Boolean isGeetActivate=false;
    protected List<Project> children;
    protected List<Tag> tags;
    protected List<Task> tasks;
    protected Project parent;
    protected Branch currentBranch;

    /**
     * Set the name of the project
     * @param name the new name
     * @return Builder
     */
    public ProjectBuilder name(String name){
        this.name = name;
        return this;
    }

    /**
     * Set the date of the end of the project
     * @param dateEnd the new dateEnd
     * @return Builder
     */
    public ProjectBuilder dateEnd(LocalDate dateEnd){
        this.dateEnd = dateEnd;
        return this;
    }

    /**
     * Set the start date for the project
     * @param startDate start date
     * @return Builder
     */
    public ProjectBuilder startDate(LocalDate startDate){
        this.startDate = startDate;
        return this;
    }

    /**
     * Set the id of the project
     * @param id the new id
     * @return Builder
     */
    public ProjectBuilder id(int id){
        this.id = id;
        return this;
    }

    /**
     * Set the description of the project
     * @param description the new description
     * @return Builder
     */
    public ProjectBuilder description(String description){
        this.description = description;
        return this;
    }

    /**
     * Set the isGeetActivate for the project
     * @param isGeetActivate the new isGeetActivate
     * @return Builder
     */
    public ProjectBuilder isGeetActivate(boolean isGeetActivate){
        this.isGeetActivate = isGeetActivate;
        return this;
    }

    /**
     * Set the children list for the project
     * @param children the new children list
     * @return Builder
     */
    public ProjectBuilder children(List<Project> children){
        this.children = children;
        return this;
    }

    /**
     * Set the tags list for the project
     * @param tags the new tags list
     * @return Builder
     */
    public ProjectBuilder tags(List<Tag> tags){
        this.tags = tags;
        return this;
    }

    /**
     * Set the tasks list for the project
     * @param tasks the new tasks list
     * @return Builder
     */
    public ProjectBuilder tasks(List<Task> tasks){
        this.tasks = tasks;
        return this;
    }

    /**
     * Set the parent of the project
     * @param parent the new parent
     * @return Builder
     */
    public ProjectBuilder parent(Project parent){
        this.parent = parent;
        return this;
    }

    /**
     * Set the current branch of the project
     * @param currentBranch the new current branch
     * @return Builder
     */
    public ProjectBuilder currentBranch(Branch currentBranch){
        this.currentBranch = currentBranch;
        return this;
    }

    /**
     * Set the password
     * @param password password of a project
     * @return Builder
     */
    public ProjectBuilder password(String password){
        this.password = password;
        return this;
    }


    /**
     * Hash the password before setting it to increase security of export and database consulting
     * @param pwd password of a project
     * @return Builder
     */
    public ProjectBuilder cryptPassword(String pwd){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.password = encoder.encode(pwd);
        return this;
    }

    /**
     * Create the project with the builder values
     * @return Project
     */
    public Project build(){
        return new Project(this);
    }
}

