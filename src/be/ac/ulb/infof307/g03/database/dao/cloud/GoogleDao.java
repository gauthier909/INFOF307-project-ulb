package be.ac.ulb.infof307.g03.database.dao.cloud;

import be.ac.ulb.infof307.g03.exceptions.CloudException;
import be.ac.ulb.infof307.g03.models.User;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.*;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This is the class used to interacted directly with the cloud service google drive.
 * Extends the interface CloudDAO
 */
public class GoogleDao implements CloudDao {
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/drive https://www.googleapis.com/auth/calendar");
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static Drive driveService = null;
    private static Calendar calendar;

    /**
     * Create Drive object and connect to Google Drive API
     * @throws CloudException of a {@link GeneralSecurityException} occurs
     */
    public GoogleDao() throws CloudException {
        try {
            NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT)).setApplicationName("A new way to excel")
                    .build();
            calendar = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT)).setApplicationName("A new way to excel")
                    .build();

        } catch (GeneralSecurityException | IOException e) {
            throw new CloudException("Failed to connect to Cloud",e);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {

        // Load client secrets.
        InputStream in = GoogleDao.class.getClassLoader().getResourceAsStream("assets/credentials.json");
        if (in == null) {
            throw new FileNotFoundException("Resource not found: credentials.json");
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Get list of all user's projects given in parameter
     * @param user current user
     * @return list of projects
     */
    @Override
    public List<String> getProjectNames(User user) throws CloudException{
        List<String> projects = new ArrayList<>();
        String folderId;
        try {
            folderId = getFolderId(user);
            if (folderId == null) folderId = addUserFolder(user);

            String pageToken = null;
            do {
                FileList result = driveService.files().list()
                        .setQ("mimeType='application/tar+gzip'and parents in '"+folderId+"' and trashed = false")
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageToken(pageToken)
                        .execute();
                projects.addAll(result.getFiles().stream().map(File::getName).collect(Collectors.toList()));
                pageToken = result.getNextPageToken();
            } while (pageToken != null);
        } catch (IOException e) {
            throw new CloudException("Failed to fetch project names. Please try again.",e);
        }
        return projects;
    }

    /**
     * Add folder in google drive root corresponding to the user
     * @param user current user
     */
    private static String addUserFolder(User user) throws CloudException {
        File fileMetadata = new File();
        fileMetadata.setName(user.getUsername());
        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        File file;
        try {
            file = driveService.files().create(fileMetadata)
                    .setFields("id")
                    .execute();
        } catch (IOException e) {
            throw new CloudException("Couldn't create user folder",e);
        }
        return file.getId();
    }

    /**
     * Get Folder Id corresponding to the user's folder
     * @param user current user
     * @return String containing folder ID
     */
    private static String getFolderId(User user) throws IOException {
        String pageToken = null;
        Optional<File> userFile;
        String userFolderId = null;
        do {
            FileList result = driveService.files().list()
                    .setQ("mimeType='application/vnd.google-apps.folder' and trashed = false")
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name)")
                    .setPageToken(pageToken)
                    .execute();
            userFile = result.getFiles().stream().filter(file->file.getName().equals(user.getUsername())).findFirst();
            if(userFile.isPresent()) userFolderId = userFile.get().getId();
            pageToken = result.getNextPageToken();
        } while (pageToken != null && userFile.isPresent());
        return userFolderId;
    }

    /**
     * Add project to the user's google drive folder
     * @param project project to add
     * @param user current user
     */
    @Override
    public void addProject(java.io.File project, User user) throws CloudException {
        try {
            String folderId = getFolderId(user);
            if (folderId == null) folderId = addUserFolder(user);
            File fileMetadata = new File();
            fileMetadata.setName(project.getName());
            fileMetadata.setParents(Collections.singletonList(folderId));
            FileContent mediaContent = new FileContent("application/tar+gzip", project);
                driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id, parents")
                    .execute();
        } catch (IOException e) {
            throw new CloudException("Failed to upload project. Please try again.",e);
        }
    }

    /**
     * Update an existing project to the user's google drive folder
     * @param user current user
     * @param project project to add
     */
    @Override
    public void updateProject(String projectName, User user, java.io.File project) throws CloudException {
        Optional<File> userFile;
        try {
            String folderId = getFolderId(user);
            if (folderId == null) folderId = addUserFolder(user);
            String pageToken = null;
            do {
                FileList result = driveService.files().list()
                        .setQ("mimeType='application/tar+gzip'and parents in '"+folderId+"' and trashed = false")
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageToken(pageToken)
                        .execute();
                userFile = result.getFiles().stream().filter(file->file.getName().equals(projectName)).findFirst();
                if(userFile.isPresent()) {
                    File newMetadata = new File();
                    newMetadata.setName(userFile.get().getName());
                    newMetadata.setParents(userFile.get().getParents());
                    FileContent mediaContent = new FileContent("application/tar+gzip", project);
                    driveService.files().update(userFile.get().getId(), newMetadata, mediaContent)
                            .execute();
                }
                pageToken = result.getNextPageToken();
            } while (pageToken != null);
        } catch (IOException e) {
            throw new CloudException("Failed to update project. Please try again.",e);
        }
    }

    /**
     * Check if a project already exists on the drive
     * @param user the user to check if a project exists
     * @param projectName the project name to check
     * @return true if it exists false otherwise
     */
    @Override
    public boolean checkIfProjectExist(User user, String projectName) throws CloudException {
        Optional<File> userFile;
        try {
            String folderId = getFolderId(user);
            if (folderId == null) return false;
            String pageToken = null;
            do {
                FileList result = driveService.files().list()
                        .setQ("mimeType='application/tar+gzip'and parents in '"+folderId+"' and trashed = false")
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageToken(pageToken)
                        .execute();
                userFile = result.getFiles().stream().filter(file->file.getName().equals(projectName)).findFirst();
                if(userFile.isPresent()) return true;
                pageToken = result.getNextPageToken();
            } while (pageToken != null);
        } catch (IOException e) {
            throw new CloudException("Failed to check Cloud. Please try again",e);
        }
        return false;
    }

    /**
     * Download the project from google drive
     * @param user current user
     * @param projectName name of the project
     * @param project File path where the file will be downloaded
     */
    @Override
    public void getProject(User user, String projectName, java.io.File project) throws CloudException {
        Optional<File> userFile;
        String userFileId =null;
        try {
        String folderId = getFolderId(user);
        if (folderId == null) folderId = addUserFolder(user);
        String pageToken = null;
        do {
            FileList result = driveService.files().list()
                    .setQ("mimeType='application/tar+gzip'and parents in '"+folderId+"' and trashed = false")
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name)")
                    .setPageToken(pageToken)
                    .execute();
            userFile = result.getFiles().stream().filter(file->file.getName().equals(projectName)).findFirst();
            if(userFile.isPresent()) userFileId = userFile.get().getId();
            pageToken = result.getNextPageToken();
        } while (pageToken != null && userFile.isPresent());
        OutputStream outputStream = new FileOutputStream(project);

            driveService.files().get(userFileId)
                    .executeMediaAndDownloadTo(outputStream);
        } catch (IOException e) {
            throw new CloudException("Failed to fetch project from the cloud. Please try again.",e);
        }
    }

    /**
     * Add a project to the google calendar
     * @param name Name of the project to add
     * @param desc description of the project to add
     * @param date Date of the end of the project to add
     * @throws IOException IOException
     */
    public void addToCalendar(String name, String desc, LocalDate date) throws IOException {
        Event event = new Event()
                .setSummary(name)
                .setDescription(desc);

        DateTime begin = new DateTime(new Date());
        EventDateTime beginDate = new EventDateTime().setDateTime(begin);

        DateTime end = convertLocalDateToDateTime(date);
        EventDateTime endDateGoogle = new EventDateTime().setDateTime(end);

        event.setEnd(endDateGoogle);
        event.setStart(beginDate);

        String calendarId = "primary";

        calendar.events().insert(calendarId, event).execute();

    }

    /**
     * Add a task to the google calendar
     * @param name Name of the task to add
     * @param start Date of the start of the task
     * @param end Date of the end of the task
     * @throws IOException IOException
     */
    public void addToCalendar(String name, LocalDate start, LocalDate end) throws IOException {
        Event event = new Event()
                .setSummary(name);

        DateTime startDate = convertLocalDateToDateTime(start);
        EventDateTime beginDate = new EventDateTime().setDateTime(startDate);

        DateTime endDate = convertLocalDateToDateTime(end);
        EventDateTime endDateGoogle = new EventDateTime().setDateTime(endDate);

        event.setEnd(endDateGoogle);
        event.setStart(beginDate);

        event.setColorId("11");

        String calendarId = "primary";

        calendar.events().insert(calendarId, event).execute();

    }

    /**
     * Convert a LocalDate to a DateTime
     * @param localDate THe LocalDate to convert
     * @return a DateTime obtained with the LocalDate
     */
    private DateTime convertLocalDateToDateTime(LocalDate localDate){
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Date date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
        return new DateTime(date);
    }
}
