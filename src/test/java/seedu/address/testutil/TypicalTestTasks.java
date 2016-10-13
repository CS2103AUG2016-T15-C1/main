package seedu.address.testutil;

import java.text.ParseException;

import seedu.task.commons.exceptions.IllegalValueException;
import seedu.task.model.TaskManager;
import seedu.task.model.tag.UniqueTagList;
import seedu.task.model.task.*;

/**
 *
 */
public class TypicalTestTasks {

    public static TestTask task1, task2, task3, task4, task5, task6, task7, task8, task9, task10;

    public TypicalTestTasks() {
        try {
        	task1 = new TaskBuilder().withTitle("Title1").withDescription("Description1").withStartDate("01-01-2012").withDueDate("02-01-2012").withInterval("1").withTimeInterval("1").build();
        	task2 = new TaskBuilder().withTitle("Title2").withDescription("Description2").withStartDate("03-02-2012").withDueDate("04-02-2012").withInterval("2").withTimeInterval("2").build();
        	task3 = new TaskBuilder().withTitle("Title3").withDescription("Description3").withStartDate("05-03-2012").withDueDate("06-03-2012").withInterval("3").withTimeInterval("3").build();
        	task4 = new TaskBuilder().withTitle("Title4").withDescription("Description4").withStartDate("07-04-2012").withDueDate("08-04-2012").withInterval("4").withTimeInterval("4").build();
        	task5 = new TaskBuilder().withTitle("Title5").withDescription("Description5").withStartDate("09-05-2012").withDueDate("10-05-2012").withInterval("5").withTimeInterval("5").build();
        	task6 = new TaskBuilder().withTitle("Title6").withDescription("Description6").withStartDate("11-06-2012").withDueDate("12-06-2012").withInterval("6").withTimeInterval("6").build();
        	task7 = new TaskBuilder().withTitle("Title7").withDescription("Description7").withStartDate("13-07-2012").withDueDate("14-07-2012").withInterval("7").withTimeInterval("7").build();
        	task8 = new TaskBuilder().withTitle("Title8").withDescription("Description8").withStartDate("15-08-2012").withDueDate("16-08-2012").withInterval("8").withTimeInterval("8").build();
        	task9 = new TaskBuilder().withTitle("Title9").withDescription("Description9").withStartDate("17-09-2012").withDueDate("18-09-2012").withInterval("9").withTimeInterval("9").build();
        	task10 = new TaskBuilder().withTitle("Title10").withDescription("Description10").withStartDate("19-10-2012").withDueDate("20-10-2012").withInterval("10").withTimeInterval("10").build();        	
        } catch (IllegalValueException e) {
            e.printStackTrace();
            assert false : "not possible";
        }
    }

    public static void loadTaskManagerWithSampleData(TaskManager ab) {

        try {
            ab.addTask(new Task(task1));
            ab.addTask(new Task(task2));
            ab.addTask(new Task(task3));
            ab.addTask(new Task(task4));
            ab.addTask(new Task(task5));
            ab.addTask(new Task(task6));
            ab.addTask(new Task(task7));
        } catch (UniqueTaskList.DuplicateTaskException e) {
            assert false : "not possible";
        }
    }

    public TestTask[] getTypicalTasks() {
        return new TestTask[]{task1, task2, task3, task4, task5, task6, task7};
    }

    public TaskManager getTypicalTaskManager(){
        TaskManager ab = new TaskManager();
        loadTaskManagerWithSampleData(ab);
        return ab;
    }
}
