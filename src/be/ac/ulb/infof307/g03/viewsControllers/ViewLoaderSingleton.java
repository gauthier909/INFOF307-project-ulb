package be.ac.ulb.infof307.g03.viewsControllers;

import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.helper.Helper;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Objects;

import static javafx.application.Platform.exit;

/**
 * Singleton handling the display of all the views
 */
public class ViewLoaderSingleton {

    private Stage stage;
    private static ViewLoaderSingleton instance = null;

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    public static ViewLoaderSingleton getInstance() {
        if(instance == null){
            instance = new ViewLoaderSingleton();
        }
        return instance;
    }


    private ViewLoaderSingleton() {
    }

    public void initializeWindow(){
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("images/3xcel.png"))));
        stage.setTitle("Way T0 3xcel");
        EventHandler<WindowEvent> confirmCloseEventHandler = event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION); //credits: https://tagmycode.com/snippet/5207/yes-no-cancel-dialog-in-javafx
            alert.setTitle("Confirm");
            alert.setContentText("Are you sure you want to quit ? \n (Non Committed changes will be deleted)");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

            alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
            alert.showAndWait().ifPresent(type -> {
                if ((type == ButtonType.CANCEL)) {
                    event.consume();
                }else{
                    Helper.deleteTempGeetFile();
                    exit();
                }
            });
        };
        stage.setOnCloseRequest(confirmCloseEventHandler);
    }

    public BaseViewController loadView(String fxmlName) throws NavigationException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlName));
        try {
            loader.load();
        } catch (IOException e) {
            throw new NavigationException("Error loading " + fxmlName,e);
        }

        BaseViewController viewController = loader.getController();

        stage.setScene(new Scene(loader.getRoot()));
        stage.centerOnScreen();
        stage.show();

        return viewController;
    }

}
