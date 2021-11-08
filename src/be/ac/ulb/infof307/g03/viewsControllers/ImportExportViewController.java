package be.ac.ulb.infof307.g03.viewsControllers;

import be.ac.ulb.infof307.g03.helper.Helper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Class controlling the ImportExportView
 */
public class ImportExportViewController extends BaseViewController implements Initializable {
    @FXML
    private Button returnButton;

    @FXML
    private Button localStorageButton;

    @FXML
    private Button dropboxButton;

    @FXML
    private Button googleDriveButton;

    @FXML
    private ProgressBar localProgressBar;

    @FXML
    private Label localProgressLabel;

    private ViewListener listener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        localStorageButton.setOnAction(event -> listener.onLocal());
        dropboxButton.setOnAction(event -> listener.onDropbox());
        returnButton.setOnAction(event -> listener.goBack());
        googleDriveButton.setOnAction(event -> listener.onGoogleDrive());
    }

    /**
     * Create the file chooser dialog
     * @return the file chooser
     */
    public FileChooser createFileChooserDialog() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select a tar.gz File");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Tar.gz archive", "*.tar.gz")
        );
        return fc;
    }

    /**
     * Open the file select dialog.
     * @return selected file or null
     */
    public List<File> openFileSelector(){
        FileChooser fc = createFileChooserDialog();
        fc.setInitialDirectory(new File("users/user_"+ Helper.getCurrentUser().getUsername()));
        return fc.showOpenMultipleDialog(new Stage());
    }

    public void setProgressBar(Long maxStorage, Long currentStorage){
        localProgressBar.setProgress((double)currentStorage/(double)maxStorage);
        localProgressLabel.setText(currentStorage+"/"+maxStorage+" bytes");
    }

    public void setListener(ViewListener listener){
        this.listener = listener;
    }

    public void disableLocalStorage() {
        localStorageButton.setDisable(true);
        localProgressLabel.setTextFill(Color.color(1, 0, 0));
    }


    public interface ViewListener{
        void onLocal();
        void onDropbox();
        void onGoogleDrive();
        void goBack();
    }
}
