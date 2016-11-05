package seedu.task.logic.commands;

import java.util.Arrays;
import java.util.List;

import seedu.task.commons.core.EventsCenter;
import seedu.task.commons.core.Messages;
import seedu.task.commons.events.ui.IncorrectCommandAttemptedEvent;
import seedu.task.model.Model;

/**
 * Represents a command with hidden internal logic and the ability to be
 * executed.
 */
public abstract class Command {
	protected Model model;

	/**
	 * Constructs a feedback message to summarise an operation that displayed a
	 * listing of tasks.
	 *
	 * @param displaySize
	 *            used to generate summary
	 * @return summary message for tasks displayed
	 */
	public static String getMessageForTaskListShownSummary(int displaySize) {
		return String.format(Messages.MESSAGE_TASKS_LISTED_OVERVIEW, displaySize);
	}

	/**
	 * Executes the command and returns the result message.
	 *
	 * @return feedback message of the operation result for display
	 */
	public abstract CommandResult execute();

	/**
	 * Raises an event to indicate an attempt to execute an incorrect command
	 */
	protected void indicateAttemptToExecuteIncorrectCommand() {
		EventsCenter.getInstance().post(new IncorrectCommandAttemptedEvent(this));
	}

	// @@author A0153411W
	/**
	 * Get all default formats of commands
	 */
	public static List<String> getAllCommands() {
		List<String> commands = Arrays.asList(AddCommand.COMMAND_WORD, SelectCommand.COMMAND_WORD,
				DeleteCommand.COMMAND_WORD, ClearCommand.COMMAND_WORD, EditCommand.COMMAND_WORD,
				FindCommand.COMMAND_WORD, ListCommand.COMMAND_WORD, ExitCommand.COMMAND_WORD, 
				HelpCommand.COMMAND_WORD, SaveCommand.COMMAND_WORD, DoneCommand.COMMAND_WORD, 
				CustomizeCommand.COMMAND_WORD, UndoCommand.COMMAND_WORD, RedoCommand.COMMAND_WORD, 
				HistoryCommand.COMMAND_WORD);
		return commands;
	}

	/**
	 * Executes the undo command and returns the result message.
	 *
	 * @return feedback message of the operation result for display
	 */
	public abstract CommandResult executeUndo();

	/**
	 * Indicates value whether command is reversible
	 *
	 * @return boolean value
	 */
	public abstract boolean isReversible();
	// @@author
	/**
	 * Provides any needed dependencies to the command. Commands making use of
	 * any of these should override this method to gain access to the
	 * dependencies.
	 */
	public void setData(Model model) {
		this.model = model;
	}
}
