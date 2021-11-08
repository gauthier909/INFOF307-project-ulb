package be.ac.ulb.infof307.g03.database.dao;

import be.ac.ulb.infof307.g03.database.Database;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.models.Branch;
import be.ac.ulb.infof307.g03.models.User;
import be.ac.ulb.infof307.g03.models.Version;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access class of the table branch
 */
public class BranchDao {
    /**
     * Retrun the branch with the id given in paramater
     * @param id id of the branch
     * @return The branch with the id
     * @throws DaoException if an exception occurs
     */
    public static Branch getBranchById(int id) throws DaoException {
        Branch branch = null;
        String getBranch = "SELECT * FROM branch WHERE id = ?";
        try {
            PreparedStatement ps= Database.getPreparedStatement(getBranch);
            ps.setInt(1,id);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                branch = new Branch(rs.getInt("id"),
                        rs.getString("branch_name"),
                        rs.getInt("project_id"));
                branch.setVersion(VersionDao.getVersionById(rs.getInt("current_version")));
            }
        }catch (SQLException e){
            throw new DaoException("Error : Cannot get the branch from id", e);
        }
        return branch;
    }


    /**
     * Return a list of branch where a project is associate with
     * @param projectId id of the project
     * @return list of branch with the project
     * @throws DaoException if an exception occurs
     */
    public static List<Branch> getAllProjectBranches(int projectId) throws DaoException {
        List<Branch> branches = new ArrayList<>();
        String getBranch = "SELECT * FROM branch WHERE project_id=? ";
        try {
            PreparedStatement ps=Database.getPreparedStatement(getBranch);
            ps.setInt(1,projectId);
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                Branch branch = new Branch(rs.getInt("id"),
                        rs.getString("branch_name"),
                        rs.getInt("project_id"));
                branch.setVersion(VersionDao.getVersionById(rs.getInt("current_version")));
                branches.add(branch);
            }

        }catch (SQLException e){
            throw new DaoException("Error : Cannot get the branches from project id", e);
        }
        return branches;

    }

    /**
     * Delete a branch
     * @param branchId the id of the branch to delete
     * @throws DaoException if an exception occurs
     */
    public static void removeBranch(int branchId) throws DaoException {
        String delBranch = "DELETE from branch WHERE id = ?";

        try {
            PreparedStatement ps = Database.getPreparedStatement(delBranch);
            ps.setInt(1,branchId);
            ps.executeUpdate();
            Database.commitStatement();

        }catch (SQLException e){
            Database.rollbackStatement();
            throw new DaoException("Error : Cannot delete branch", e);
        }
    }

    /**
     * Create a branch
     * @param branch the branch object to add in DB
     * @return id of the branch created
     * @throws DaoException if an exception occurs
     */
    public static int createBranch(Branch branch) throws DaoException {
        String addBranch = "INSERT into branch(branch_name,project_id) values(?,?)";
        try {
            PreparedStatement ps=Database.getPreparedStatement(addBranch, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,branch.getBranchName());
            ps.setInt(2,branch.getProjectId());
            ps.executeUpdate();
            Database.commitStatement();
            ResultSet rsIDBranch = ps.getGeneratedKeys();
            int idBranch;
            if (rsIDBranch.next()) {
                idBranch = rsIDBranch.getInt(1);
            } else {
                throw new DaoException("Error : Cannot create branch", new SQLException());
            }
            return idBranch;
        }catch (SQLException e){
            Database.rollbackStatement();
            throw new DaoException("Error : Cannot delete branch", e);
        }
    }

    /**
     * Change the current branch to another one for all users
     * @param newBranch the new branch to switch
     * @param previousBranch the previous branch
     * @throws DaoException if an exception occurs
     */
    public static void switchBranchForEveryone(Branch newBranch, Branch previousBranch ) throws DaoException {
        String switchBranch = "UPDATE userProject SET currentBranch = ? WHERE currentBranch = ?;";
        try {
            PreparedStatement ps=Database.getPreparedStatement(switchBranch);
            ps.setInt(1,newBranch.getId());
            ps.setInt(2,previousBranch.getId());
            ps.executeUpdate();
            Database.commitStatement();
        }catch (SQLException e){
            Database.rollbackStatement();
            throw new DaoException("Error : Cannot delete branch", e);
        }
    }

    /**
     * Change the current branch to another one for the current user
     * @param currentUser the current user
     * @param newBranch the new branch to switch
     * @throws DaoException if an exception occurs
     */
    public static void switchBranch(User currentUser, Branch newBranch) throws DaoException {
        String switchBranchUserProject = "UPDATE userProject SET currentBranch = ? WHERE projectId=? AND userId = ?;";
        String switchBranchProject =  "UPDATE project SET currentBranch = ? WHERE  id=? ";
        try {
            PreparedStatement ps=Database.getPreparedStatement(switchBranchUserProject);
            PreparedStatement ps2=Database.getPreparedStatement(switchBranchProject);
            ps.setInt(1,newBranch.getId());
            ps.setInt(2,newBranch.getProjectId());
            ps.setInt(3,currentUser.getId());
            ps.executeUpdate();

            ps2.setInt(1,newBranch.getId());
            ps2.setInt(2,newBranch.getProjectId());
            ps2.executeUpdate();
            Database.commitStatement();
        }catch (SQLException e){
            Database.rollbackStatement();
            throw new DaoException("Error : Cannot delete branch", e);
        }
    }

    /**
     * Change the current branch version to a new one
     * @param branch the branch of the version
     * @param newVersion the new version
     * @throws DaoException if an exception occurs
     */
    public static void updateVersion(Branch branch, Version newVersion) throws DaoException {
        String switchBranchUserProject = "UPDATE branch SET current_version = ? WHERE id = ?;";
        try{
            PreparedStatement ps=Database.getPreparedStatement(switchBranchUserProject);
            ps.setInt(1,newVersion.getId());
            ps.setInt(2,branch.getId());
            ps.executeUpdate();
            Database.commitStatement();
        } catch(SQLException e) {
            Database.rollbackStatement();
            throw new DaoException("Error : Cannot delete branch", e);
    }
    }
}
