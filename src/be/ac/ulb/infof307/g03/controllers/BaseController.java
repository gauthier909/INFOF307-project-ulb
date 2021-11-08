package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.models.Project;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * This is a non-instantiable class which regroup useful methods that can be used through all controllers.
 */
class BaseController {
    protected String geetPath = "Geet/";
    protected BaseController() {}

    /**
     * convert a json File to a JsonElement to be read
     * @param fileName String corresponding to path of the json file
     * @return jsonObject : jsonObject created by getting the json file from fileName path
     */
    public JsonElement convertFileToJSONElement (String fileName) throws FileNotFoundException {
        JsonElement jsonElement;
        jsonElement = JsonParser.parseReader(new FileReader(fileName));
        return jsonElement;
    }

    /**
     * convert a json File to a JsonArray to be read
     * @param fileName String corresponding to path of the json file
     * @return jsonObject : jsonObject created by getting the json file from fileName path
     */
    public JsonArray convertFileToJSONArray (String fileName) throws FileNotFoundException {
        JsonArray jsonArray;
        jsonArray = JsonParser.parseReader(new FileReader(fileName)).getAsJsonArray();
        return jsonArray;
    }

    /**
     * Save a project object to a compressed JSON file
     * @param content content to save (Json object as string)
     * @param file File location to save archive
     */
    public void saveProjectToArchive(String content, File file) throws IOException {
        PrintWriter writer;
        File temp = new File("./temp.json");
        writer = new PrintWriter(temp);
        writer.println(content);
        writer.close();
        Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
        archiver.create(file.getName(), file.getParentFile(), temp);
        temp.delete();
    }

    public Project extractProjectFromJsonFile(String path) throws FileNotFoundException{
        Gson gson = new Gson();
        return gson.fromJson(convertFileToJSONElement(path), Project.class);
    }
    public Project extractProjectFromJsonElement(JsonElement jsonElement){
        Gson gson = new Gson();
        return gson.fromJson(jsonElement, Project.class);
    }

    public void commitProject(Project project, String commitMessage) throws IOException, DaoException {
        String pathname = geetPath+project.getId()+"/"+project.getCurrentBranch().getBranchName()+"/";
        File folder = new File(pathname);
        folder.mkdirs();

        project.getCurrentBranch().setVersion(project.commit(commitMessage));
        File sourceFile = new File(Helper.GEET_TMP + project.getCurrentBranch().getBranchName() + "_" + project.getId() + ".json");
        File destFile = new File(folder.getCanonicalPath()+"/"+project.getCurrentBranch().getVersion().getTimestamp()+".json");
        if (!sourceFile.renameTo(destFile))
            throw new IOException("Could not commit project");
    }


    /**
     * Create a directory
     * @param username create directory with username
     */
        public void createDir(String username ){

        File file = new File("users/"+"user_"+username);
        file.mkdirs();
    }
    /**
     * Get the size of a directory
     * @param username name of the directory
     * @return Return size in byte of a directory
     */
    public long getDirectorySize(String username) {
        long size;
        // need close Files.walk
        try (Stream<Path> walk = Files.walk(Paths.get("users/"+"user_" + username))) {
            size = walk
                    .filter(Files::isRegularFile)
                    .mapToLong(p -> {
                        try {
                            return Files.size(p);
                        } catch (IOException e) {
                            return -1;
                        }
                    })
                    .sum();

        } catch (IOException e) {
            return -1;
        }
        return size;
    }

    public void setGeetPath(String geetPath) {
        this.geetPath = geetPath;
    }
}
