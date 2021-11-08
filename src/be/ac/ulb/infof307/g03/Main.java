package be.ac.ulb.infof307.g03;


import be.ac.ulb.infof307.g03.controllers.WelcomeController;
import be.ac.ulb.infof307.g03.database.Database;
import be.ac.ulb.infof307.g03.database.dao.user.UserSetterDao;
import be.ac.ulb.infof307.g03.exceptions.DaoException;
import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.helper.Helper;
import be.ac.ulb.infof307.g03.viewsControllers.ViewLoaderSingleton;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private WelcomeController controller;

    @Override
    public void start(Stage primaryStage) throws NavigationException {
        ViewLoaderSingleton.getInstance().setStage(primaryStage);
        ViewLoaderSingleton.getInstance().initializeWindow();
        controller = new WelcomeController();
        try {
            Database.init("MyDataBase.db");
        } catch (DaoException e) {
            controller.displayError("Error", "Unable to connect to the database");
        }
        controller.show();

    }

    @Override
    public void stop(){
        // Force disconnection of user when closing the program
        try {
            if (Helper.getCurrentUser() != null) UserSetterDao.setUserConnection(Helper.getCurrentUser().getId(), false);
            Database.closeConnection();
        }catch (DaoException e){
            controller.displayError("disconnection error", "Unable to disconnect the user when closing the application");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}