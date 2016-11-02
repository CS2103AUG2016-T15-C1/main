package seedu.task.commons.core;

import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Config values used by the app
 */
public class Config {

	private static Config instance = null;
	public static final String DEFAULT_CONFIG_FILE = "config.json";

	// Config values customizable through config file
	private String appTitle = "Task Manager";
	private Level logLevel = Level.INFO;
	private String userPrefsFilePath = "preferences.json";
	private String taskManagerFilePath = "data/taskmanager.xml";
	private String taskManagerName = "MyTaskManager";
	private static HashMap<String, String> customCommands = new HashMap<String, String>();

	public Config() {
	}

	public static Config getInstance() {
		if (instance == null) {
			instance = new Config();
		}
		return instance;
	}

	public String getAppTitle() {
		return appTitle;
	}

	public void setAppTitle(String appTitle) {
		this.appTitle = appTitle;
	}

	public Level getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(Level logLevel) {
		this.logLevel = logLevel;
	}

	public String getUserPrefsFilePath() {
		return userPrefsFilePath;
	}

	public void setUserPrefsFilePath(String userPrefsFilePath) {
		this.userPrefsFilePath = userPrefsFilePath;
	}

	public String getTaskManagerFilePath() {
		return taskManagerFilePath;
	}

	public void setTaskManagerFilePath(String taskManagerFilePath) {
		this.taskManagerFilePath = taskManagerFilePath;
	}

	public String getTaskManagerName() {
		return taskManagerName;
	}

	public void setTaskManagerName(String taskManagerName) {
		this.taskManagerName = taskManagerName;
	}

	public void setCustomCommandFormat(String commandWord, String userCommand)
			throws DublicatedValueCustomCommandsException {
		for (String key : customCommands.keySet()) {
			if (customCommands.get(key).equals(userCommand))
				throw new DublicatedValueCustomCommandsException("This command already exists!");
		}
		customCommands.put(commandWord, userCommand);
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (!(other instanceof Config)) { // this handles null as well.
			return false;
		}

		Config o = (Config) other;

		return Objects.equals(appTitle, o.appTitle) && Objects.equals(logLevel, o.logLevel)
				&& Objects.equals(userPrefsFilePath, o.userPrefsFilePath)
				&& Objects.equals(taskManagerFilePath, o.taskManagerFilePath)
				&& Objects.equals(taskManagerName, o.taskManagerName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(appTitle, logLevel, userPrefsFilePath, taskManagerFilePath, taskManagerName);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("App title : " + appTitle);
		sb.append("\nCurrent log level : " + logLevel);
		sb.append("\nPreference file Location : " + userPrefsFilePath);
		sb.append("\nLocal data file location : " + taskManagerFilePath);
		sb.append("\nTaskManager name : " + taskManagerName);
		return sb.toString();
	}

	public class DublicatedValueCustomCommandsException extends Exception {
		public DublicatedValueCustomCommandsException(String message) {
			super(message);
		}
	}

}
