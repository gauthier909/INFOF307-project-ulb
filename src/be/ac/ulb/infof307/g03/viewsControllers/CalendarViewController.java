package be.ac.ulb.infof307.g03.viewsControllers;

import be.ac.ulb.infof307.g03.exceptions.NavigationException;
import be.ac.ulb.infof307.g03.models.Project;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Class controlling the CalendarView
 */
public class CalendarViewController extends BaseViewController implements Initializable {
    ViewListener listener;
    List<Calendar> projectCalendars;
    CalendarSource myCalendarSource;

    @FXML
    private AnchorPane calendarPane;

    public void setListener(ViewListener listener) {
        this.listener=listener;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CalendarView calendarView = new CalendarView();
        calendarView.setShowAddCalendarButton(false);
        myCalendarSource = new CalendarSource("My Projects");
        projectCalendars = new ArrayList<>();

        calendarView.getCalendarSources().addAll(myCalendarSource);
        calendarView.setMinSize(1200,800);

        calendarView.setRequestedTime(LocalTime.now());

        calendarPane.getChildren().add(calendarView);
        calendarPane.setMinSize(calendarView.getMinWidth(),calendarView.getMinHeight());
        Button returnButton = new Button();
        returnButton.setLayoutX(800);
        returnButton.setLayoutY(10);
        returnButton.setMinWidth(60);
        returnButton.setText("Return");
        returnButton.setOnAction((ActionEvent event) -> {
            try {
                listener.goBack();
            } catch (NavigationException e) {
                showError("An error occurred", e.getMessage(), false);
            }
        });
        calendarPane.getChildren().add(returnButton);

    }

    /**
     * Add the list of project in the calendar
     * @param projects to add in the calendar
     */
    public void setProjects(List<Project> projects) {
        for(int i=0;i<projects.size();i++){
            Calendar c = new Calendar(projects.get(i).getName());
            c.setStyle(Calendar.Style.getStyle(i));
            projectCalendars.add(c);

            if(projects.get(i).getTasks() != null) {
                for (int j = 0; j < projects.get(i).getTasks().size(); j++) {
                    Entry<?> entry = new Entry<>();
                    entry.setCalendar(c);
                    entry.setTitle(projects.get(i).getTasks().get(j).getDescription());
                    entry.setInterval(LocalDate.now(), projects.get(i).getTasks().get(j).getEndDate());
                    c.addEntry(entry);
                }
            }
        }
        myCalendarSource.getCalendars().addAll(projectCalendars);
    }
    public interface ViewListener{
        void goBack() throws NavigationException;
    }
}
