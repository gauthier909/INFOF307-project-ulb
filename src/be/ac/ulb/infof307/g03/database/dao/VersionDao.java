package be.ac.ulb.infof307.g03.database.dao;

import be.ac.ulb.infof307.g03.database.Database;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.models.Branch;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.User;
import be.ac.ulb.infof307.g03.models.Version;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access class of the table version
 */
public class VersionDao {

    public static void initGeet(Project project) throws DaoException {
        Branch master = new Branch("Master",project.getId());
        PreparedStatement ps = Database.getPreparedStatement("INSERT INTO branch(branch_name,project_id) VALUES (?,?);",Statement.RETURN_GENERATED_KEYS);
        try {
            ps.setString(1, master.getBranchName());
            ps.setInt(2,project.getId());
            ps.executeUpdate();
            ResultSet rsIDBranch = ps.getGeneratedKeys();
            int idBranch;
            if (rsIDBranch.next()) {
                idBranch = rsIDBranch.getInt(1);
            } else {
                throw new DaoException("Error : Cannot create branch", new SQLException());
            }
            master.setId(idBranch);
            for(User user : project.getAllCollaborators()){
                BranchDao.switchBranch(user,master);
            }
            project.setCurrentBranch(master);

            project.setGeetActivate(true);
            project.update();
            Database.commitStatement();
        } catch (SQLException e) {
            Database.rollbackStatement();
            project.setGeetActivate(false);
            throw new DaoException("Error : Cannot update project", e);
        }
    }

    /**
     * Commit the changes
     * @param project concerned project
     * @param message commit message
     * @return the last version
     * @throws DaoException DaoException
     */
    public static Version commit(Project project, String message) throws DaoException {
        String query = "INSERT INTO version(timestamp, commit_message, branch, project) VALUES(?,?,?,?);";
        String updateCurrentVersion =  "UPDATE branch SET current_version = ? WHERE  id=? ";

        Version version;
        try{
            PreparedStatement ps = Database.getPreparedStatement(query, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement ps2 = Database.getPreparedStatement(updateCurrentVersion);

            Timestamp time = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(1, time);
            ps.setString(2, message);
            ps.setInt(3, project.getCurrentBranch().getId());
            ps.setInt(4, project.getId());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                version = new Version(rs.getInt(1),time.getTime(),message,project.getCurrentBranch().getId(),project.getId());

                // update the current version for the given branch
                ps2.setInt(1, version.getId());
                ps2.setInt(2, project.getCurrentBranch().getId());
                ps2.executeUpdate();
            } else {
                throw new DaoException("Error : Cannot create branch", new SQLException());
            }
            Database.commitStatement();
        }catch(SQLException e){
            Database.rollbackStatement();
            throw new DaoException("Error : Cannot commit these changes", e);
        }
        return version;
    }

    /**
     * Get all versions related to a branch, sorted by chronological order
     * @param branch selected branch
     * @return list of versions
     * @throws DaoException in case of database exception
     */
    public static List<Version> getVersionsFromBranch(Branch branch) throws DaoException{
        String query = "SELECT * FROM version WHERE branch = ? AND project = ? ORDER BY timestamp DESC;";
        List<Version> versions = new ArrayList<>();
        try{
            PreparedStatement ps = Database.getPreparedStatement(query);
            ps.setInt(1, branch.getId());
            ps.setInt(2, branch.getProjectId());
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Version v = new Version(rs.getInt(1), rs.getTimestamp(2).getTime(), rs.getString(3), rs.getInt(4), rs.getInt(5));
                versions.add(v);
            }
        }catch(SQLException e){
            throw new DaoException("Error : Cannot load version from branch", e);
        }
        return versions;
    }

    /**
     * Get a version based on an id
     * @param versionId id of the version
     * @return version if found else null
     * @throws DaoException in case of database exception
     */
    public static Version getVersionById(int versionId) throws DaoException{
        String query = "SELECT * FROM version WHERE id = ?;";
        try{
            PreparedStatement ps = Database.getPreparedStatement(query);
            ps.setInt(1, versionId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return new Version(rs.getInt(1), rs.getTimestamp(2).getTime(), rs.getString(3), rs.getInt(4), rs.getInt(5));
            }
        }catch(SQLException e){
            throw new DaoException("Error : Cannot load version from branch", e);
        }
        return null;
    }

    /**
     * Delete a version in the database
     * @param versionId id of the version
     * @throws DaoException in case of database exception
     */
    public static void deleteVersion(int versionId) throws DaoException{
        String query = "DELETE FROM version WHERE id = ?;";
        try{
            PreparedStatement ps = Database.getPreparedStatement(query);
            ps.setInt(1, versionId);
            ps.executeUpdate();
            Database.commitStatement();
        }catch(SQLException e){
            Database.rollbackStatement();
            throw new DaoException("Error: connot delete version", e);
        }
    }

    /**
     * add the changes
     * @param version version to add on
     * @param branch the branch to add on
     * @throws DaoException DaoException
     */
    public static void addVersion(Version version, Branch branch) throws DaoException{
        String query = "INSERT INTO version(timestamp, commit_message, branch, project) VALUES(?,?,?,?);";

        String updateCurrentVersion =  "UPDATE branch SET current_version = ? WHERE  id=? ";

        try{
            PreparedStatement ps = Database.getPreparedStatement(query, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement ps2 = Database.getPreparedStatement(updateCurrentVersion);

            ps.setTimestamp(1, new Timestamp(version.getTimestamp()));
            ps.setString(2, version.getCommitMessage());
            ps.setInt(3, branch.getId());
            ps.setInt(4, version.getProjectId());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                // update the current version for the given branch
                ps2.setInt(1, rs.getInt(1));
                ps2.setInt(2, branch.getId());
                ps2.executeUpdate();
            } else {
                throw new DaoException("Error : Cannot create branch", new SQLException());
            }
            Database.commitStatement();
        }catch(SQLException e){
            Database.rollbackStatement();
            throw new DaoException("Error : Cannot commit these changes", e);
        }

    }

}
