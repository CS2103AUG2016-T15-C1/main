package seedu.task.model;

import java.text.ParseException;
import java.util.Set;
import java.util.logging.Logger;

import javafx.collections.transformation.FilteredList;
import seedu.task.commons.core.ComponentManager;
import seedu.task.commons.core.LogsCenter;
import seedu.task.commons.core.UnmodifiableObservableList;
import seedu.task.commons.events.model.TaskManagerChangedEvent;
import seedu.task.commons.util.StringUtil;
import seedu.task.logic.commands.Command;
import seedu.task.model.task.ReadOnlyTask;
import seedu.task.model.task.Task;
import seedu.task.model.task.UniqueTaskList;
import seedu.task.model.task.UniqueTaskList.TaskNotFoundException;

/**
 * Represents the in-memory model of the address book data. All changes to any
 * model should be synchronized.
 */
public class ModelManager extends ComponentManager implements Model {
	private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

	private TaskManager taskManager;
	// @@author A0153411W
	private final CommandManager commandManager;
	// @@author
	private final FilteredList<Task> filteredTasks;

	/**
	 * Initializes a ModelManager with the given TaskManager TaskManager and its
	 * variables should not be null
	 * 
	 * @throws ParseException
	 */
	public ModelManager(TaskManager src, UserPrefs userPrefs) throws ParseException {
		super();
		assert src != null;
		assert userPrefs != null;

		logger.fine("Initializing with address book: " + src + " and user prefs " + userPrefs);

		taskManager = new TaskManager(src);
		// @@author A0153411W
		commandManager = new CommandManager();
		// @@author
		filteredTasks = new FilteredList<>(taskManager.getTasks());
	}

	public ModelManager() throws ParseException {
		this(new TaskManager(), new UserPrefs());
	}

	public ModelManager(ReadOnlyTaskManager initialData, UserPrefs userPrefs) throws ParseException {
		taskManager = new TaskManager(initialData);
		// @@author A0153411W
		commandManager = new CommandManager();
		// @@author
		filteredTasks = new FilteredList<>(taskManager.getTasks());
	}

	@Override
	public void resetData(ReadOnlyTaskManager newData) {
		taskManager.resetData(newData);
		indicateTaskManagerChanged();
	}

	@Override
	public ReadOnlyTaskManager getTaskManager() {
		return taskManager;
	}

	/** Raises an event to indicate the model has changed */
	private void indicateTaskManagerChanged() {
		raise(new TaskManagerChangedEvent(taskManager));
	}

	@Override
	public synchronized void deleteTask(ReadOnlyTask target) throws TaskNotFoundException {
		taskManager.removeTask(target);
		indicateTaskManagerChanged();
	}

	// @@author A0148083A
	@Override
	public synchronized void completeTask(ReadOnlyTask target, ReadOnlyTask toBeComplete) throws TaskNotFoundException {
		taskManager.completeTask(target, toBeComplete);
		indicateTaskManagerChanged();
	}
	// @@author

	// @@author A0139932X
	public synchronized void changeFilePath() {
		indicateTaskManagerChanged();
	}
	// @@author

	@Override
	public synchronized void addTask(Task task) throws UniqueTaskList.DuplicateTaskException {
		taskManager.addTask(task);
		updateFilteredListToShowAll();
		indicateTaskManagerChanged();
	}

	// @@author A0153411W
	/**
	 * Adds a task to the task manager at specific place
     *
     * @throws UniqueTaskList.DuplicateTaskException if an equivalent task already exists.
	 */
	@Override
	public synchronized void addTaskWithSpecifiedIndex(Task task, int index)
			throws UniqueTaskList.DuplicateTaskException {
		taskManager.addAtSpecificPlace(task, index);
		updateFilteredListToShowAll();
		indicateTaskManagerChanged();
	}
	// @@author

	// =========== Filtered Task List Accessors
	// ===============================================================

	@Override
	public UnmodifiableObservableList<ReadOnlyTask> getFilteredTaskList() {
		return new UnmodifiableObservableList<>(filteredTasks);
	}

	@Override
	public void updateFilteredListToShowAll() {
		filteredTasks.setPredicate(null);
	}

	@Override
	public void updateFilteredTaskList(Set<String> keywords) {
		updateFilteredTaskList(new PredicateExpression(new NameQualifier(keywords)));
	}

	private void updateFilteredTaskList(Expression expression) {
		filteredTasks.setPredicate(expression::satisfies);
	}

	// ========== Inner classes/interfaces used for filtering
	// ==================================================

	interface Expression {
		boolean satisfies(ReadOnlyTask task);

		String toString();
	}

	private class PredicateExpression implements Expression {

		private final Qualifier qualifier;

		PredicateExpression(Qualifier qualifier) {
			this.qualifier = qualifier;
		}

		@Override
		public boolean satisfies(ReadOnlyTask task) {
			return qualifier.run(task);
		}

		@Override
		public String toString() {
			return qualifier.toString();
		}
	}

	interface Qualifier {
		boolean run(ReadOnlyTask task);

		String toString();
	}

	private class NameQualifier implements Qualifier {
		private Set<String> nameKeyWords;

		NameQualifier(Set<String> nameKeyWords) {
			this.nameKeyWords = nameKeyWords;
		}

		@Override
		public boolean run(ReadOnlyTask task) {
			return nameKeyWords.stream()
					.filter(keyword -> StringUtil.containsIgnoreCase(task.getTitle().fullTitle, keyword)).findAny()
					.isPresent();
		}

		@Override
		public String toString() {
			return "name=" + String.join(", ", nameKeyWords);
		}
	}

	// @@author A0153411W
	/** Add executed command for undo */
	@Override
	public void addCommandForUndo(Command command) {
		updateFilteredListToShowAll();
		commandManager.addCommandForUndo(command);
	}
	
	/**
	 * Add user inputed command to command manager for history
	 */
	@Override
	public void addCommandForHistory(String commandText) {
		commandManager.addCommandForHistory(commandText);
	}
	
	/**
	 * Get history of executed user's commands
	 */
	@Override
	public String getCommandHistory(){
		return commandManager.getCommandHistory();
	};
	
	/**
	 * Get last executed reversible command for undo operation
	 */
	@Override
	public Command getCommandForUndo() {
		updateFilteredListToShowAll();
		Command undoCommand= commandManager.getCommandForUndo();
		//Add undoCommand to manager for redo 
		commandManager.addCommandForRedo(undoCommand);
		return undoCommand;
	}
	
	/**
	 * Remove command from undo manager
	 */
	@Override
	public void removeCommandForUndo() {
		updateFilteredListToShowAll();
		commandManager.getCommandForUndo();
	}


	/**
	 * Get last undo command for redo operation
	 */
	@Override
	public Command getCommandForRedo() {
		updateFilteredListToShowAll();
		Command redoCommand= commandManager.getCommandForRedo();
		//Add redoCommand to manager for undo 
		commandManager.addCommandForUndo(redoCommand);
		return redoCommand;
	}

	// @@author

}
