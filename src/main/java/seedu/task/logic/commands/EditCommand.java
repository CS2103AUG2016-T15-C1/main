package seedu.task.logic.commands;

import java.util.List;

import seedu.task.commons.core.Messages;
import seedu.task.commons.core.UnmodifiableObservableList;
import seedu.task.commons.exceptions.IllegalValueException;
import seedu.task.model.ReadOnlyTaskManager;
import seedu.task.model.task.Title;
import seedu.task.model.task.ReadOnlyTask;
import seedu.task.model.task.Task;
import seedu.task.model.task.UniqueTaskList;
import seedu.task.model.task.UniqueTaskList.DuplicateTaskException;
import seedu.task.model.task.UniqueTaskList.TaskNotFoundException;

/**
 * Edits a task in the address book.
 * Specifically, it finds the details that requires editing and creates a new task, deleting the old one.
 * Currently, it can only edit the name of a task.
 */

public class EditCommand extends Command {
	public static final String COMMAND_WORD = "edit";
	public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits a task in the address book. "
            + "Parameters: Index n/newName p/phoneNumber"
            + "Example: " + COMMAND_WORD
            + "1 t/newTaskName";
	
	public final String MESSAGE_SUCCESS = "The data has been successfully edited.";
	public final String MESSAGE_NOT_FOUND = "The task was not found.";
	public final String MESSAGE_DUPLICATE = "The edited task is a duplicate of an existing task.";
	public final String MESSAGE_PARAM = "Incorrect parameters.";
	
	private ReadOnlyTask selectedTask;
	private Task copy, editedTask;
	private int paramLength;
	private String[] params;
	private String newTitle;
	private UnmodifiableObservableList<ReadOnlyTask> taskList;
	private int taskIndex;

	/**
	 * Constructor
	 * @param name the name/identifier of the task
	 * @param strings the parameters
	 */
	public EditCommand(int index, String title, String... strings) {
		taskIndex = index;
		paramLength = strings.length;
		params = strings;
		newTitle = title;
	}
	
	
	/**
	 * Searches through the task list to find the specified task
	 * @param name the name/identifier of the task
	 * @return the specified task
	 * @throws TaskNotFoundException if the task was not found
	 */
	public ReadOnlyTask searchTask(int index) throws TaskNotFoundException {
		assert !taskList.isEmpty();
		return taskList.get(taskIndex - 1);
	}

	@Override
	public CommandResult execute() {
		taskList = model.getFilteredTaskList();
		assert model != null;
        if (taskList.size() < taskIndex) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
        }

		try {
			selectedTask = searchTask(taskIndex);
			edit(selectedTask);
			model.deleteTask(selectedTask);
			model.addTask(editedTask);
		} catch (TaskNotFoundException e) {
			return new CommandResult(MESSAGE_NOT_FOUND);
		} catch (DuplicateTaskException e) {
			return new CommandResult(MESSAGE_DUPLICATE);
		} catch (IllegalValueException e) {
			return new CommandResult(MESSAGE_PARAM);
		}
		return new CommandResult(MESSAGE_SUCCESS);
	}
	
	/**
	 * Begins the editing process
	 * @param task the specified task to edit
	 * @throws IllegalValueException if the parameters provided were incorrect
	 */
	public void edit(ReadOnlyTask task) throws IllegalValueException{
		copy = (Task) selectedTask;
		iterateParams(newTitle, params);
		editedTask = copy;
	}
	
	/**
	 * Iterates through the parameters
	 * @param params array of parameters
	 * @throws IllegalValueException if the parameters provided were incorrect
	 */
	public void iterateParams(String name, String[] params) throws IllegalValueException{
		if (name != null) {
		    changeTitle(newTitle);
		}
	}
	
	/**
	 * Changes the name in a task if specified in the parameters
	 * @param name the new name value
	 * @throws IllegalValueException if the name value is invalid
	 */
	public void changeTitle(String title) throws IllegalValueException {
		Title newTitle = new Title(title);
		copy = new Task(newTitle, copy.getDescription(), copy.getStartDate(), copy.getDueDate(), copy.getInterval(), copy.getTimeInterval(), copy.getStatus(), copy.getTags());
	}

}
