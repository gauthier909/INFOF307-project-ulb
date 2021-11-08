package be.ac.ulb.infof307.g03.viewsControllers;
import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.models.Statistic;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Class controlling the StatisticProjectView
 */
public class StatisticProjectViewController extends BaseViewController implements Initializable {
    private StatisticProjectViewController.ViewListener listener;

    private Stage stage;

    @FXML
    private Label labelPersonne;

    @FXML
    private Label labelTache;

    @FXML
    private Label labelEstime;

    @FXML
    private Label labelReel;

    @FXML
    private PieChart pieChartTaskCollab;

    @FXML
    private BarChart<String,Integer> barChartNumberOfCollabPerTask;

    @FXML
    private PieChart pieChartAsssignedTask;

    @FXML
    private Button goBackButton;

    @FXML
    private Button exportButtonJson;

    @FXML
    private Button exportButtonCsv;

    @FXML
    private ProgressBar dateProgressBar;




    public void setListener(StatisticProjectViewController.ViewListener listener) {
        this.listener = listener;
    }

    public void initialize(URL location, ResourceBundle resources) {
        FileChooser fileChooser = new FileChooser();

        String timeStamp =LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy-hh:mm:ss"));
        goBackButton.setOnAction(event -> {
            try {
                listener.goBack();
            } catch (NavigationException e) {
                showError("Navigation error",e.getMessage());
            }
        });
        exportButtonCsv.setOnAction(event -> {
            fileChooser.setTitle("Export to CSV");
            fileChooser.setInitialFileName("stats-"+timeStamp+".csv");
            try {
                listener.exportCsv( fileChooser.showSaveDialog(stage));
            } catch (IOException e) {
                showError("Error", "error while trying to write stats into csv file");
            }
        });
        exportButtonJson.setOnAction(event -> {
            fileChooser.setTitle("Export to JSON");
            fileChooser.setInitialFileName("stats-"+timeStamp+".json");
            try {
                listener.exportJson( fileChooser.showSaveDialog(stage));
            } catch (IOException e) {
                showError("Error", "error while trying to write stats into json file");
            }
        });

    }
    public  void setStage(Stage stage){
        this.stage = stage;
    }

    private void setPieChart(int numberOfAssignedTasks, int numberOfNonAssignedTask){
        // no data to show
        if (numberOfAssignedTasks == 0 && numberOfNonAssignedTask == 0) return;
        pieChartTaskCollab.setTitle("Number of task(s) \n assigned and non-assigned");
        pieChartTaskCollab.setData(FXCollections.observableList(Arrays.asList(
                new PieChart.Data("Assigned (" + numberOfAssignedTasks + ")", numberOfAssignedTasks),
                new PieChart.Data("Unassigned(" + numberOfNonAssignedTask + ")", numberOfNonAssignedTask))
        ));
    }
    private void setPieChart2(int numberOfCollaborators, int numberOfAssignedCollaborators){
        pieChartAsssignedTask.setTitle("Number of Collaborators assigned \n to at least one task");
        int unassigned = numberOfCollaborators - numberOfAssignedCollaborators;
        pieChartAsssignedTask.setData(FXCollections.observableList(Arrays.asList(
                new PieChart.Data("Assigned (" + numberOfAssignedCollaborators + ")" , numberOfAssignedCollaborators),
                new PieChart.Data("Unassigned (" + unassigned +")", unassigned))

        ));
    }

    private void setDateProgressBar(float estimatedTime,float realTime){
        float res = 1-(realTime/estimatedTime);
        dateProgressBar.setProgress(res);

    }



    private void setBarChart(Map<Integer, Integer> numberOfCollabPerTask){
        // no data to show
        if (numberOfCollabPerTask.isEmpty()) {
            barChartNumberOfCollabPerTask.setVisible(false);
            return;
        }
        barChartNumberOfCollabPerTask.setTitle("Number of collaborator(s) per task");
        numberOfCollabPerTask.forEach((t,c) -> {
            XYChart.Series<String, Integer> series1 = new XYChart.Series<>();
            series1.setName("Task id: "+ t);
            series1.getData().add(new XYChart.Data<>(String.valueOf(t), c));
            barChartNumberOfCollabPerTask.getData().add(series1);
        });
    }

    public void setData(){
        Statistic st = listener.getStatistics();
        labelPersonne.setText(String.valueOf(st.getNumberOfCollaborator()));
        labelTache.setText(String.valueOf(st.getNumberOfTaskLeft()));
        labelEstime.setText(st.getEstimatedTime());
        labelReel.setText(st.getRealTime());
        setPieChart(st.getNumberOfAssignedTasks(), st.getNumberOfNonAssignedTasks());
        setBarChart(st.getNumberOfCollabPerTask());
        setPieChart2(st.getNumberOfCollaborator(),st.getNumberOfAssignedCollaborators());
        setDateProgressBar(Float.parseFloat(st.getEstimatedTime().split(" ")[0]),Float.parseFloat(st.getRealTime().split(" ")[0]));
    }

    public interface ViewListener {

        /**
         * go back
         */
        void goBack() throws NavigationException;

        /**
         * Getter to get the statistics
         * @return Statistic containing all the statistics of the project
         */
        Statistic getStatistics();

        /**
         * Export raw stat data to .csv format
         * @param file destination to write data into.
         */
        void exportCsv(File file) throws IOException;
        /**
         * Export raw stat data to .json format
         * @param file destination to write data into.
         */
        void exportJson(File file) throws IOException;
    }
}
