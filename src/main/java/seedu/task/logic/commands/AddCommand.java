package seedu.task.logic.commands;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.task.commons.core.EventsCenter;
import seedu.task.commons.events.ui.JumpToListRequestEvent;
import seedu.task.commons.exceptions.IllegalValueException;
import seedu.task.model.tag.Tag;
import seedu.task.model.tag.UniqueTagList;
import seedu.task.model.task.Description;
import seedu.task.model.task.DueDate;
import seedu.task.model.task.Interval;
import seedu.task.model.task.StartDate;
import seedu.task.model.task.Status;
import seedu.task.model.task.Task;
import seedu.task.model.task.TaskColor;
import seedu.task.model.task.TimeInterval;
import seedu.task.model.task.Title;
import seedu.task.model.task.UniqueTaskList;
import seedu.task.model.task.UniqueTaskList.TaskNotFoundException;


/**
 * Adds a task to the address book.
 */
public class AddCommand extends Command {

	public static final String COMMAND_WORD = "add";

	public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a task to the task manager. \n"
			+ "Parameters: TITLE [d/DESCRIPTION] [sd/START_DATE] [dd/DUE_DATE] [i/INTERVAL] [ti/TIMEINTERVAL] [ts/TAG]...\n"
			+ "Example: " + COMMAND_WORD + " HOMEWORK d/Math homework. dd/01-01-2012 23:59 i/2 ti/7 ts/Math";
	public static final String MESSAGE_TASK_USAGE = "To add a task, %1$s is required\n" + "Example: "
			+ COMMAND_WORD + " HOMEWORK d/Math homework. sd/01-01-2011 00:00 dd/01-01-2012 23:59 i/2 ti/7";
//@@author A0139932X
	public static final String MESSAGE_INVALID_DATE = "The DUE DATE is before the START DATE\n" + "Example: "
	        + COMMAND_WORD + " HOMEWORK d/Math homework. sd/01-01-2011 00:00 dd/01-01-2012 23:59 i/2 ti/7";
//@@author
	public static final String MESSAGE_SUCCESS = "New task added: %1$s";
	public static final String MESSAGE_SUCCESS_MANY_TASKS = "%1$s tasks added: %2$s";
	public static final String MESSAGE_DUPLICATE_TASK = "This task already exists in the task manager";	
	
	public final String MESSAGE_NOT_FOUND = "The task was not found.";

	private List<Task> tasksToAdd;


	/**
	 * Convenience constructor using raw values.
	 *
	 * @throws IllegalValueException
	 *             if any of the raw values are invalid
	 * @throws ParseException
	 */
	//@@author A0153751H
	public AddCommand(String title, String description, String startDate, String dueDate, String interval,
			String timeInterval, String taskColor, Set<String> tags) throws IllegalValueException, ParseException {
		final Set<Tag> tagSet = new HashSet<>();
		for (String tagName : tags) {
			tagSet.add(new Tag(tagName));
		}; 
		//@@author A0153411W
		//Create based task for duplication
		Task baseTask = new Task(new Title(title), new Description(description), new StartDate(startDate),
				new DueDate(dueDate), new Interval(interval), new TimeInterval(timeInterval),
				new Status("ONGOING"), new TaskColor(taskColor), new UniqueTagList(tagSet));
		addTasksToList(baseTask);
		//@@author
	}

	//@@author A0153411W
	/**
	 * Add tasks to list based on main task, time and time interval.
	 * @param mainTask
	 */
	private void addTasksToList(Task mainTask) {
		int timeInterval = mainTask.getTimeInterval().value;
		tasksToAdd = new ArrayList<Task>();
		tasksToAdd.add(mainTask);
		for (int i = 1; i < mainTask.getInterval().value; i++) {
			tasksToAdd.add(new Task(mainTask.getTitle(), mainTask.getDescription(),
					mainTask.getStartDateWithInterval(timeInterval * i),
					mainTask.getDueDateWithInterval(timeInterval * i), mainTask.getInterval(),
					mainTask.getTimeInterval(), new Status("ONGOING"), mainTask.getTaskColor(), mainTask.getTags()));
		}
	} 
	//@@author
	
	@Override
	public CommandResult execute() {
		assert model != null;

		try {
			//For every task from list, add task to manager
			for (Task task : tasksToAdd) {
				task.setStatus(new Status("ONGOING"));
				model.addTask(task);
			}
			//@@author A0148083A
			//to focus on the latest added task
			EventsCenter.getInstance().post(new JumpToListRequestEvent(model.getFilteredTaskList().size() - 1));
			//@@author
			
			if (tasksToAdd.size() == 1) {
				return new CommandResult(String.format(MESSAGE_SUCCESS, tasksToAdd.get(0)));
			}
			else
				return new CommandResult(String.format(MESSAGE_SUCCESS_MANY_TASKS, tasksToAdd.get(0).getInterval(),
						tasksToAdd.get(0).getTitle()));
		} catch (UniqueTaskList.DuplicateTaskException e) {
			//remove this command from list for undo
			model.removeCommandForUndo();
			return new CommandResult(MESSAGE_DUPLICATE_TASK);
		}
	}
	//@@author 

	//@@author A0153411W
	/**
	 * Execute undo method for add command - Delete added tasks to restore 
	 * task manager to state before method was executed
	 * @throws TaskNotFoundException
	 * 				if task to delete was not found
	 */
	@Override
	public CommandResult executeUndo() {
		try {
			for (Task task : tasksToAdd) {
				task.setStatus(new Status("ONGOING"));
				model.deleteTask(task);
			}
		} catch (TaskNotFoundException e) {
			return new CommandResult(MESSAGE_NOT_FOUND);
		}
		if (tasksToAdd.size() == 1) {
			return new CommandResult(String.format(MESSAGE_SUCCESS, tasksToAdd.get(0)));
		}
		else
			return new CommandResult(String.format(MESSAGE_SUCCESS_MANY_TASKS, tasksToAdd.get(0).getInterval(),
					tasksToAdd.get(0).getTitle()));
	}

	@Override
	public boolean isReversible() {
		return true;
	}
	//@@author
}
