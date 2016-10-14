package seedu.task.ui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import seedu.task.model.task.ReadOnlyTask;
import java.text.*;

public class TaskCard extends UiPart{

    private static final String FXML = "TaskListCard.fxml";

    @FXML
    private HBox cardPane;
    @FXML
    private Label id;
    @FXML
    private Label title;
    @FXML
    private Label description;
    @FXML
    private Label startDate;
    @FXML
    private Label dueDate;
    @FXML
    private Label interval;
    @FXML
    private Label timeInterval;
    @FXML
    private Label status;
    @FXML
    private Label tags;

    private ReadOnlyTask task;
    private int displayedIndex;

    public TaskCard(){

    }

    public static TaskCard load(ReadOnlyTask task, int displayedIndex){
        TaskCard card = new TaskCard();
        card.task = task;
        card.displayedIndex = displayedIndex;
        return UiPartLoader.loadUiPart(card);
    }

    @FXML
    public void initialize() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("E dd-MM-yyyy");
    	title.setText(task.getTitle().fullTitle);
        id.setText(displayedIndex + ". ");
        description.setText(task.getDescription().fullDescription);
        startDate.setText("Start Date: " + dateFormat.format(task.getStartDate().startDate));
        dueDate.setText("Due Date: " + dateFormat.format(task.getDueDate().dueDate));
        interval.setText(task.getInterval().value);
        timeInterval.setText(task.getTimeInterval().value);
        status.setText("Status: " + task.getStatus().status.toString());
        tags.setText(task.tagsString());
    }

    public HBox getLayout() {
        return cardPane;
    }

    @Override
    public void setNode(Node node) {
        cardPane = (HBox)node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }
}
