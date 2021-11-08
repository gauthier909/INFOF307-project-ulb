package be.ac.ulb.infof307.g03.models;

import be.ac.ulb.infof307.g03.database.dao.BranchDao;
import be.ac.ulb.infof307.g03.database.dao.VersionDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;

/**
 * Class representing a version
 */
public class Version {
    private int id;
    private final long timestamp;
    private String commitMessage;
    private final int branch;
    private final int projectId;

    /**
     * Cunstructor of a version
     * @param timestamp timestamp of the version
     * @param branch branch associated to the version
     * @param projectId id of the project associated to the version
     */
    public Version(long timestamp, int branch, int projectId) {
        this.timestamp = timestamp;
        this.branch = branch;
        this.projectId = projectId;
    }

    /**
     * Constructor of a version
     * @param id id of the version
     * @param timestamp timestamp of the version
     * @param commitMessage message of the commit
     * @param branch branch associated to the version
     * @param projectId id of the project associated to the version
     */
    public Version(int id, long timestamp, String commitMessage, int branch, int projectId) {
        this.id = id;
        this.timestamp = timestamp;
        this.commitMessage = commitMessage;
        this.branch = branch;
        this.projectId = projectId;
    }

    /**
     * Get the id of the version
     * @return id of the version
     */
    public int getId() {
        return id;
    }

    /**
     * Get the timestamp of the version
     * @return timestamp of the version
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Get the commit message
     * @return message of the commit
     */
    public String getCommitMessage() {
        return commitMessage;
    }

    /**
     * Get the branch associated to the version
     * @return branch associated to the version
     */
    public int getBranch() {
        return branch;
    }

    /**
     * Get the project's id associated to the version
     * @return project's id
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * Stringify a version to rend the commit message and the id of the version
     * @return string to render
     */
    public String toString(){
        return this.getCommitMessage() + " " + this.getId();
    }

    public Branch getMyBranch() throws DaoException {
       return BranchDao.getBranchById(this.getBranch());
    }

    /**
     * Delete this version in the Database
     * @throws DaoException DaoException
     */
    public void delete() throws DaoException {
        VersionDao.deleteVersion(this.getId());
    }
}

