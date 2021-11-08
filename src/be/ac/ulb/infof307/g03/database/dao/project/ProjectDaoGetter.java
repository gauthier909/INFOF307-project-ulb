package be.ac.ulb.infof307.g03.database.dao.project;

import be.ac.ulb.infof307.g03.database.dao.BranchDao;
import be.ac.ulb.infof307.g03.database.Database;
import be.ac.ulb.infof307.g03.database.dao.TagDao;
import be.ac.ulb.infof307.g03.database.dao.TaskDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.models.ProjectBuilder;
import be.ac.ulb.infof307.g03.models.Project;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data access class of the table project (only reading acces)
 */
public class ProjectDaoGetter {
    /**
     * Get a project by his id. If the project has a parent, select also the parent
     * @param id project's id
     * @return the project with the given id
     */
    public static Project getById(int id)throws DaoException{
        Project project=null;
        try {
            PreparedStatement ps= Database.getPreparedStatement("SELECT * FROM project where id=?");
            ps.setInt(1,id);
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                if(rs.getInt("parentProject")!=0){
                    project = new ProjectBuilder()
                                .name(rs.getString("name"))
                                .startDate(rs.getDate("startDate").toLocalDate())
                                .dateEnd(rs.getDate("dateEnd").toLocalDate())
                                .id(rs.getInt("id"))
                                .parent(getById(rs.getInt("parentProject")))
                                .tags(TagDao.getByProjectId(id))
                                .description(rs.getString("description"))
                                .password("password").build();
                }else{
                    project = new ProjectBuilder()
                            .name(rs.getString("name"))
                            .startDate(rs.getDate("startDate").toLocalDate())
                            .dateEnd(rs.getDate("dateEnd").toLocalDate())
                            .id(rs.getInt("id"))
                            .tags(TagDao.getByProjectId(id))
                            .description(rs.getString("description"))
                            .password(rs.getString("password")).build();
                }
            }
            if(project != null) project.setTasks(TaskDao.getAllByProjectId(id));

        }catch (SQLException e){
            throw new DaoException("Error : Cannot get the project from his id", e);
        }
        return project;
    }


    /**
     * Check for an existing project in the database according to the unique fields of a project.
     * @param projectName the project name
     * @param endDate the end date of the project
     * @return the ID of the project in the DB or -1 if he is not in the DB
     * @throws DaoException  if an sqlException occurs.
     */
    public static int checkExistingProject(String projectName, LocalDate endDate) throws DaoException {
        String querySelect = "SELECT id FROM project WHERE dateEnd = ? and name = ?";
        try{
            PreparedStatement ps = Database.getPreparedStatement(querySelect);
            ps.setDate(1, java.sql.Date.valueOf(endDate));
            ps.setString(2, projectName);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt("id");
            }else{
                return -1;
            }
        }catch(SQLException e){
            throw  new DaoException("Error : Cannot get the project from his end date and name", e);
        }
    }


    /**
     * Get all the projects which are link to the user following a project hierarchy.
     * @return The list of project of the user
     * @throws DaoException DaoException
     */
    public static List<Project> getAllFromCurrentUser(int userId) throws DaoException {
        List<Project>userProjectsList=new ArrayList<>();
        Map<Integer,Project>allProject=getAllProject();
        String getUserProjectsId = "SELECT projectId FROM userProject WHERE userId = ? AND isRoot IS TRUE";
        PreparedStatement query = Database.getPreparedStatement(getUserProjectsId);
        try{
            query.setInt(1,userId);
            ResultSet rs=query.executeQuery();
            while(rs.next()){
                int idProject=rs.getInt("projectId");
                userProjectsList.add(allProject.get(idProject));
            }
        }catch (SQLException e){
            throw new DaoException("Error : cannot getUserProject",e);
        }
        return userProjectsList;
    }

    /**
     * Get all project in the database and map the project hierarchy of all the project. So the parents and children
     * projects are linked
     * @return Map that links a project id to the project object
     * @throws DaoException DaoException
     */
    private static Map<Integer,Project> getAllProject() throws DaoException {
        Map<Integer,Project>projectMap=new HashMap<>();
        String getAllProject = "SELECT * FROM project";
        PreparedStatement query = Database.getPreparedStatement(getAllProject);
        try {
            ResultSet rs=query.executeQuery();
            while (rs.next()){
                int idParent = rs.getInt("parentProject");
                int id = rs.getInt("id");
                Project p = new ProjectBuilder()
                        .id(id)
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .startDate(rs.getDate("startDate").toLocalDate())
                        .dateEnd(rs.getDate("dateEnd").toLocalDate())
                        .isGeetActivate(rs.getBoolean("isGeetActivate"))
                        .tags(TagDao.getByProjectId(id))
                        .parent(idParent==0 ? null : new ProjectBuilder().id(idParent).build())
                        .currentBranch(BranchDao.getBranchById(rs.getInt("currentBranch")))
                        .tasks(TaskDao.getAllByProjectId(id))
                        .password(rs.getString("password")).build();

                projectMap.put(p.getId(),p);
                if(idParent!=0){
                    projectMap.get(idParent).addChild(p);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error : cannot getProject",e);
        }
        return projectMap;
    }

    /**
     * get the list of all the children of the project corresponding to the given id
     * @param parentId the id of the parent we have to look after
     * @return list of children with the same parentId
     * @throws DaoException DaoException
     */
    public static List<Project> getChildrenList (int parentId) throws DaoException {
        List<Project> childrenList = new ArrayList<>();
        String getAllChildren = "SELECT * FROM project where parentProject = ?";
        PreparedStatement query = Database.getPreparedStatement(getAllChildren);
        try{
            query.setInt(1,parentId);
            ResultSet rs=query.executeQuery();
            while(rs.next()){
                int id=rs.getInt("id");
                childrenList.add(new ProjectBuilder()
                        .id(id)
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .dateEnd(rs.getDate("dateEnd").toLocalDate())
                        .startDate(rs.getDate("startDate").toLocalDate())
                        .tags(TagDao.getByProjectId(id))
                        .parent(getById(rs.getInt("parentProject")))
                        .children(getChildrenList(id))
                        .password(rs.getString("password")).build());
            }
        }catch (SQLException e){
            throw new DaoException("Error : Cannot get the children of a project",e);
        }

        return childrenList;
    }


    /**
     * get a project object taken in the table project with the correct id
     * @param projectId id of the project to return
     * @return return the project with the id given in parameters
     * @throws DaoException DaoException
     */
    public static Project getProjectById(int projectId) throws DaoException {
        String getProjectById = "SELECT * FROM project where id = ?";
        PreparedStatement query = Database.getPreparedStatement(getProjectById);
        try{
            query.setInt(1,projectId);
            ResultSet rs=query.executeQuery();
            if(rs.next()){
                return new ProjectBuilder()
                        .id(projectId)
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .dateEnd(rs.getDate("dateEnd").toLocalDate())
                        .startDate(rs.getDate("startDate").toLocalDate())
                        .tags(TagDao.getByProjectId(projectId))
                        .parent(getById(rs.getInt("parentProject")))
                        .children(getChildrenList(projectId))
                        .password(rs.getString("password"))
                        .isGeetActivate(rs.getBoolean("isGeetActivate"))
                        .build();
            }

        }catch (SQLException e){
            throw new DaoException("Error : Cannot get the children of a project",e);
        }
        return null;
    }

    /**
     * get all project (parent and children) by user id
     * @param userId id of the current user.
     * @return return list of projects of the current user
     * @throws DaoException DaoException
     */
    public static List<Project> getAllParentAndChildrenByUserId(int userId) throws DaoException {
        List<Project> listProjects = new ArrayList<>();
        String getProjects = "SELECT * FROM userProject where userId = ?";
        PreparedStatement query = Database.getPreparedStatement(getProjects);
        try {
            query.setInt(1, userId);
            ResultSet rs = query.executeQuery();
            while (rs.next()) {
                int projectId = rs.getInt("projectId");
                listProjects.add(getProjectById(projectId));
            }
        } catch (SQLException e) {
            throw new DaoException("Error : Cannot get the children of a project", e);
        }
        return listProjects;

    }

    /**
     * Give the number of collaborator of a project
     * @param project concerned project
     * @return the number of collaborator of the project or -1 if the project doesn't exist
     * @throws DaoException DaoException
     */
    public static int getNumberOfCollaborator(Project project) throws DaoException {
        String getNumberOfCollaborator = "SELECT COUNT(*) FROM userProject where projectId = ?";

        try{
            PreparedStatement query = Database.getPreparedStatement(getNumberOfCollaborator);
            query.setInt(1,project.getId());
            ResultSet rs=query.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
            return -1;
        }catch (SQLException e){
            throw new DaoException("Error : Cannot get the children of a project",e);
        }
    }
}
