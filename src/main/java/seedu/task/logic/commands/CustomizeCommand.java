package seedu.task.logic.commands;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import seedu.task.commons.core.Config;
import seedu.task.commons.util.ConfigUtil;
import seedu.task.storage.StorageManager;

public class CustomizeCommand extends Command {

	public static final String COMMAND_WORD = "customize";

	public static final String MESSAGE_USAGE = COMMAND_WORD + ": Add customized format for specified command"
			+ " Parameters: command_format" + " Example: " + COMMAND_WORD + " LIST f/ls";

	private String commandWord;
	private String userCommand;

	public CustomizeCommand(String commandWord, String userCommand) {
		this.commandWord = commandWord;
		this.userCommand = userCommand;
	}

	@Override
	public CommandResult execute() {
		Config config = new Config();
		String configFilePathUsed = Config.DEFAULT_CONFIG_FILE;

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
		}

	}

	
	public static List<String> getAllFields(List<String> fields, Class<?> type) {
	    fields.addAll(Arrays.asList(Command.class.co));

	    if (type.getSuperclass() != null) {
	        fields = getAllFields(fields, type.getSuperclass());
	    }

	    return fields;
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
