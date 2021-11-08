package be.ac.ulb.infof307.g03.models;

import be.ac.ulb.infof307.g03.database.dao.BranchDao;
import be.ac.ulb.infof307.g03.database.dao.VersionDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;

import java.util.List;

/**
 * Class representing a branch
 */
public class Branch {
    private int id;
    private final String branchName;
    private final int projectId;
    private Version version;

    /**
     * Constructor of a branch
     * @param branchName name of the branch
     * @param projectId id of the project associated to the branch
     */
    public Branch(String branchName,int projectId) {
        this.branchName = branchName;
        this.projectId = projectId;
    }

    /**
     * Constructor of a branch
     * @param id id of the branch
     * @param branchName name of the branch
     * @param projectId id of the project associated to the branch
     */
    public Branch(int id,String branchName,int projectId) {
        this.id = id;
        this.branchName = branchName;
        this.projectId = projectId;
    }

    /**
     * Get the id of the branch
     * @return id of the branch
     */
    public int getId() {
        return id;
    }

    /**
     * Set the id of the branch
     * @param id value of the id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the name of the branch
     * @return name of the branch
     */
    public String getBranchName() {
        return branchName;
    }

    /**
     * Get the id of the project
     * @return id of the project
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * Get the version associated to the branch
     * @return version associated to the branch
     */
    public Version getVersion() {
        return this.version;
    }

    /**
     * Set the version associated to the branch
     * @param version version associated to the branch
     */
    public void setVersion(Version version){
        this.version = version;
    }


    /**
     * Delete branch in database and switch all user who were on that branch to newBranch
     * @param newBranch branch to switch old users to
     * @throws DaoException in case of Database exception
     */
    public void delete(Branch newBranch) throws DaoException {
        this.switchBranchForEveryone(newBranch);
        BranchDao.removeBranch(this.getId());
    }

    /**
     * Switch the branch for everyone
     * @param newBranch the branch to witch on
     * @throws DaoException DaoException
     */
    private void switchBranchForEveryone(Branch newBranch) throws DaoException {
        BranchDao.switchBranchForEveryone(newBranch, this);
    }

    /**
     * Create a branch
     * @return The branch created
     * @throws DaoException DaoException
     */
    public Branch create() throws DaoException {
        int id = BranchDao.createBranch(this);
        this.setId(id);
        return this;
    }

    /**
     * get all the version of this branch
     * @return a list of Version
     * @throws DaoException DaoException
     */
    public List<Version> getAllVersions() throws DaoException {
        return VersionDao.getVersionsFromBranch(this);
    }

    /**
     * update the version
     * @param version Version to update
     * @throws DaoException DaoException
     */
    public void updateVersion(Version version) throws DaoException {
        BranchDao.updateVersion(this, version);
    }
}
