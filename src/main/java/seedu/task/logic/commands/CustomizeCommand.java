package seedu.task.logic.commands;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import seedu.task.commons.core.Config;
import seedu.task.commons.core.Config.DublicatedValueCustomCommandsException;
import seedu.task.commons.util.ConfigUtil;
import seedu.task.storage.StorageManager;

public class CustomizeCommand extends Command {

	public static final String COMMAND_WORD = "customize";

	public static final String MESSAGE_USAGE = COMMAND_WORD + ": Add customized format for specified command"
			+ " Parameters: command_format" + " Example: " + COMMAND_WORD + " LIST f/ls";

	private String commandWord;
	private String userCommand;

	public CustomizeCommand(String commandWord, String userCommand) {
		this.commandWord = commandWord.toLowerCase();
		this.userCommand = userCommand.toLowerCase();
	}

	@Override
	public CommandResult execute() {
		Config config = Config.getInstance();
		String configFilePathUsed = Config.DEFAULT_CONFIG_FILE;
		if (!isCommandWordPresent(commandWord))
			return new CommandResult("There is no such command.");
		try {
			config.setCustomCommandFormat(commandWord, userCommand);
			ConfigUtil.saveConfig(config, configFilePathUsed);
			new StorageManager(config.getTaskManagerFilePath(), config.getUserPrefsFilePath());
			return new CommandResult("Add customized format: " + userCommand + " for command: " + commandWord);
		} catch (IOException e) {
			// remove this command from list for undo
			model.getCommandForUndo();
			return new CommandResult(
					"Failed to add customized format: " + userCommand + " for command: " + commandWord);
		} catch (DublicatedValueCustomCommandsException e) {
			return new CommandResult(e.getMessage());
		}

	}

	private boolean isCommandWordPresent(String commandWord) {
		List<String> commands = Arrays.asList(AddCommand.COMMAND_WORD, SelectCommand.COMMAND_WORD,
				DeleteCommand.COMMAND_WORD, ClearCommand.COMMAND_WORD, EditCommand.COMMAND_WORD,
				FindCommand.COMMAND_WORD, ListCommand.COMMAND_WORD, ExitCommand.COMMAND_WORD, HelpCommand.COMMAND_WORD,
				SaveCommand.COMMAND_WORD, DoneCommand.COMMAND_WORD, CustomizeCommand.COMMAND_WORD,
				UndoCommand.COMMAND_WORD);
		return commands.contains(commandWord);
	}

	/**
	 * Save Command is not reversible.
	 */
	@Override
	public CommandResult executeUndo() {
		return this.execute();
	}

	@Override
	public boolean isReversible() {
		return true;
	}

}
