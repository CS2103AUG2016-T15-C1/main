package seedu.task.model;

import java.util.Set;

import seedu.task.commons.core.UnmodifiableObservableList;
import seedu.task.logic.commands.Command;
import seedu.task.model.task.ReadOnlyTask;
import seedu.task.model.task.Task;
import seedu.task.model.task.UniqueTaskList;

/**
 * The API of the Model component.
 */
public interface Model {

	/**
	 * Clears existing backing model and replaces with the provided new data.
	 */
	void resetData(ReadOnlyTaskManager newData);

	/** Returns the TaskManager */
	ReadOnlyTaskManager getTaskManager();

	/** Deletes the given task. */
	void deleteTask(ReadOnlyTask target) throws UniqueTaskList.TaskNotFoundException;

	// @@author A0148083A
	/** Complete the given task. */
	void completeTask(ReadOnlyTask target, ReadOnlyTask toBeComplete) throws UniqueTaskList.TaskNotFoundException;
	// @@author

	// @@author A0139932X
	void changeFilePath();
	// @@author

	/** Adds the given task */
	void addTask(Task task) throws UniqueTaskList.DuplicateTaskException;

	/**
	 * Adds the given task at specified index of the list in the data, not the
	 * index shown to the user
	 */
	void addTaskWithSpecifiedIndex(Task task, int index) throws UniqueTaskList.DuplicateTaskException;

	/**
	 * Returns the filtered task list as an
	 * {@code UnmodifiableObservableList<ReadOnlyTask>}
	 */
	UnmodifiableObservableList<ReadOnlyTask> getFilteredTaskList();

	/** Updates the filter of the filtered task list to show all tasks */
	void updateFilteredListToShowAll();

	/**
	 * Updates the filter of the filtered task list to filter by the given
	 * keywords
	 */
	void updateFilteredTaskList(Set<String> keywords);

	// @@author A0153411W
	/** Add executed reversible command for undo operation */
	void addCommandForUndo(Command command);
	
	/**
	 * Add user inputed command to command manager for history
	 */
	void addCommandForHistory(String commandText);
	
	
	/**
	 * Get history of executed user's commands
	 */
	String getCommandHistory();

	/**
	 * Get last executed reversible command 
	 * return Command
	 */
	Command getCommandForUndo();
	
	/**
	 * Remove command from undo stack
	 * return Command
	 */
	void removeCommandForUndo();

	/**
	 * Get last undo command for redo operation
	 */
	Command getCommandForRedo();
	// @@author
}
