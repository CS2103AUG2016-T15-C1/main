package seedu.task.logic.parser;

import static seedu.task.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.task.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.task.commons.core.Config;
import seedu.task.commons.exceptions.IllegalValueException;
import seedu.task.commons.util.StringUtil;
import seedu.task.logic.commands.AddCommand;
import seedu.task.logic.commands.ClearCommand;
import seedu.task.logic.commands.Command;
import seedu.task.logic.commands.CustomizeCommand;
import seedu.task.logic.commands.DeleteCommand;
import seedu.task.logic.commands.DoneCommand;
import seedu.task.logic.commands.EditCommand;
import seedu.task.logic.commands.ExitCommand;
import seedu.task.logic.commands.FindCommand;
import seedu.task.logic.commands.HelpCommand;
import seedu.task.logic.commands.IncorrectCommand;
import seedu.task.logic.commands.ListCommand;
import seedu.task.logic.commands.SaveCommand;
import seedu.task.logic.commands.SelectCommand;
import seedu.task.logic.commands.UndoCommand;
import seedu.task.logic.parser.ArgumentTokenizer.NoValueForRequiredTagException;
import seedu.task.logic.parser.ArgumentTokenizer.Prefix;
import seedu.task.model.task.DueDate;
import seedu.task.model.task.StartDate;

/**
 * Parses user input.
 */
public class Parser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    private static final Pattern TASK_INDEX_ARGS_FORMAT = Pattern.compile("(?<targetIndex>.+)");

    private static final Pattern KEYWORDS_ARGS_FORMAT =
            Pattern.compile("(?<keywords>\\S+(?:\\s+\\S+)*)"); // one or more keywords separated by whitespace
    
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    
    public static final SimpleDateFormat DATE_FORMAT_WITHOUT_TIME = new SimpleDateFormat("dd-MM-yyyy");
    //@@author A0153411W
    //@@author A0153751H
    public static final Prefix descriptionPrefix = new Prefix(" d/", "description");
    public static final Prefix startDatePrefix = new Prefix(" sd/","startDate", true);
    public static final Prefix dueDatePrefix = new Prefix(" dd/","due date", true);
    public static final Prefix intervalPrefix = new Prefix(" i/","interval", true);
    public static final Prefix timeIntervalPrefix = new Prefix(" ti/", "time interval",true);
    public static final Prefix tagArgumentsPrefix = new Prefix(" t/", "tag arguments");
    public static final Prefix taskColorPrefix = new Prefix(" c/","task color", true);
	public static final Prefix formatCustomCommandPrefix = new Prefix(" f/", "format custom command");
	// @@author

	// @@author A0153751H
	private static final Pattern TASK_DATA_ARGS_FORMAT_EDIT = // '/' forward
																// slashes are
																// reserved for
																// delimiter
																// prefixes
			Pattern.compile("(?<index>[^/]+)" + "(( t/(?<newTitle>[^/]+))|" + "( d/(?<description>[^/]+))|"
					+ "( sd/(?<startDate>[^/]+))|" + "( dd/(?<dueDate>[^/]+))|" + "( i/(?<interval>[^/]+))|"
					+ "( ti/(?<timeInterval>[^/]+))|" + "( c/(?<taskColor>[^/]+)))+?"
					+ "(?<tagArguments>(?: ts/[^/]+)*)");
	// @@author

	// @@author A0139932X
	private static final Pattern SAVE_COMMAND_FORMAT = Pattern.compile("(?<path>[^/]+)");
	// @@author

	public Parser() {
	}

	/**
	 * Parses user input into command for execution.
	 *
	 * @param userInput
	 *            full user input string
	 * @return the command based on the user input
	 * @throws ParseException
	 */
	public Command parseCommand(String userInput) throws ParseException {
		final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
		if (!matcher.matches()) {
			return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
		}

		final String commandWord = getCommandWord(matcher);
		final String arguments = matcher.group("arguments");
		switch (commandWord) {

		case AddCommand.COMMAND_WORD:
			return prepareAdd(arguments);

		case SelectCommand.COMMAND_WORD:
			return prepareSelect(arguments);

		case DeleteCommand.COMMAND_WORD:
			return prepareDelete(arguments);

		case ClearCommand.COMMAND_WORD:
			return new ClearCommand();

		case EditCommand.COMMAND_WORD:
			return prepareEdit(arguments);

		case FindCommand.COMMAND_WORD:
			return prepareFind(arguments);

		case ListCommand.COMMAND_WORD:
			return new ListCommand();

		case ExitCommand.COMMAND_WORD:
			return new ExitCommand();

		case HelpCommand.COMMAND_WORD:
			return new HelpCommand();

		case SaveCommand.COMMAND_WORD:
			return prepareSave(arguments);

		case DoneCommand.COMMAND_WORD:
			return prepareDone(arguments);

		case UndoCommand.COMMAND_WORD:
			return new UndoCommand();

		case CustomizeCommand.COMMAND_WORD:
			return prepareCustomize(arguments);

		default:
			return new IncorrectCommand(MESSAGE_UNKNOWN_COMMAND);
		}
	}

	// @@author A0153411W
	private String getCommandWord(Matcher matcher) {
		String command = matcher.group("commandWord");
		if (Command.getAllCommands().contains(command))
			return command;
		else {
			command = Config.getInstance().getCommandbyCustomValue(command);
			return command;
		}
	}
    
    /**
     * Parses arguments in the context of the add task command.
     *
     * @param args full command args string
     * @return the prepared command
     * @throws ParseException 
     */

    private Command prepareAdd(String args) throws ParseException{
        //@@author A0153411W
    	//@@author A0153751H
    	//Reset dueDatePrefix for every add command as optional
    	dueDatePrefix.SetIsOptional(true);
		ArgumentTokenizer argsTokenizer = new ArgumentTokenizer(descriptionPrefix, startDatePrefix, dueDatePrefix,
				intervalPrefix, timeIntervalPrefix, taskColorPrefix, tagArgumentsPrefix);
		argsTokenizer.tokenize(args);
		// @@author
		try {
			// @@author A0148083A
			// For deadlines - if there is startDate, set dueDatePrefix as
			// required
			if (argsTokenizer.getValue(startDatePrefix) != null) {
				dueDatePrefix.SetIsOptional(false);
			}
			// @@author

			// @@author A0139932X
			// to validate start date is before due date

			Date dueDate, startDate;
			if (!isInputPresent(argsTokenizer.getValue(startDatePrefix)).equals("Not Set")
					&& (!isInputPresent(argsTokenizer.getValue(dueDatePrefix)).equals("Not Set"))) {

				// validate start date format
				if (!checkDateFormate(argsTokenizer.getValue(startDatePrefix))) {
					throw new IllegalValueException(StartDate.MESSAGE_DATE_CONSTRAINTS);
				} else {
					startDate = parseStartDate(argsTokenizer.getValue(startDatePrefix));
				}
				// validate due date format
				if (!checkDateFormate(argsTokenizer.getValue(dueDatePrefix))) {
					throw new IllegalValueException(DueDate.MESSAGE_DATE_CONSTRAINTS);
				} else {
					dueDate = parseDueDate(argsTokenizer.getValue(dueDatePrefix));
				}

				if (startDate.compareTo(dueDate) > 0) {
					return new IncorrectCommand(
							String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_INVALID_DATE));
				}

			}
			//@@author
			
			//@@author A0153751H
		    //@@author A0153411W
			return new AddCommand(argsTokenizer.getPreamble(), 
					isInputPresent(argsTokenizer.getValue(descriptionPrefix)),
					isInputPresent(argsTokenizer.getValue(startDatePrefix)),
					isInputPresent(argsTokenizer.getValue(dueDatePrefix)),
					isInputPresent(argsTokenizer.getValue(intervalPrefix)),
					isInputPresent(argsTokenizer.getValue(timeIntervalPrefix)),
					argsTokenizer.getValue(taskColorPrefix),
					toSet(argsTokenizer.getAllValues(tagArgumentsPrefix)));
			// @@author
		} catch (NoSuchElementException nsee) {
			return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
		} catch (IllegalValueException ive) {
			return new IncorrectCommand(ive.getMessage());
		} catch (NoValueForRequiredTagException nvfrt) {
			return new IncorrectCommand(nvfrt.getMessage());
		}
	}

	// @@author A0153411W
	private Command prepareCustomize(String args) throws ParseException {
		ArgumentTokenizer argsTokenizer = new ArgumentTokenizer(formatCustomCommandPrefix);
		argsTokenizer.tokenize(args);
		try {
			return new CustomizeCommand(argsTokenizer.getPreamble(), argsTokenizer.getValue(formatCustomCommandPrefix));
		} catch (NoSuchElementException nsee) {
			return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, CustomizeCommand.MESSAGE_USAGE));
		} catch (NoValueForRequiredTagException nvfrt) {
			return new IncorrectCommand(nvfrt.getMessage());
		}
	}
	// @@author

	// @@author A0139932X
	public boolean checkDateFormate(String dateToValidate) {
		if (!DueDate.isValidDateTime(dateToValidate) && !DueDate.isValidDate(dateToValidate)) {
			return false;
		}
		return true;
	}

	public Date parseDueDate(String dateToValidate) throws ParseException {
		Date date;

		if (DueDate.isValidDate(dateToValidate)) {
			date = DATE_FORMAT.parse(dateToValidate + " 23:59");
		} else {
			date = DATE_FORMAT.parse(dateToValidate);
		}

		return date;
	}

	public Date parseStartDate(String dateToValidate) throws ParseException {
		Date date;

		if (StartDate.isValidDate(dateToValidate)) {
			date = DATE_FORMAT.parse(dateToValidate + " 08:00");
		} else {
			date = DATE_FORMAT.parse(dateToValidate);
		}

		return date;
	}
	// @@author

	/**
	 * Check if the input is present, hence having the attribute of task
	 * optional
	 * 
	 * @param input
	 *            of task's attribute
	 * @return specified null format or actual input
	 **/
	// @@author A0148083A
	private String isInputPresent(String input) {
		return input == null ? "Not Set" : input;
	}
	// @@author

	/**
	 * Parses arguments in the context of the edit task command.
	 *
	 * @param args
	 *            full command args string
	 * @return the prepared command
	 */
	// @@author A0153751H
	private Command prepareEdit(String args) {
		final Matcher matcher = TASK_DATA_ARGS_FORMAT_EDIT.matcher(args.trim());
		// Validate arg string format
		if (!matcher.matches()) {
			return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
		} else if (matcher.group("newTitle") == null && matcher.group("description") == null
				&& matcher.group("startDate") == null && matcher.group("dueDate") == null
				&& matcher.group("interval") == null && matcher.group("timeInterval") == null
				&& matcher.group("tagArguments") == null && matcher.group("taskColor") == null) {
			return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
		}
		try {
			return new EditCommand(Integer.parseInt(matcher.group("index")), matcher.group("newTitle"),
					matcher.group("description"), matcher.group("startDate"), matcher.group("dueDate"),
					matcher.group("interval"), matcher.group("timeInterval"), matcher.group("taskColor"),
					getTagsFromArgs(matcher.group("tagArguments")));
		} catch (NumberFormatException e) {
			return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
		} catch (NullPointerException e) {
			return new EditCommand(Integer.parseInt(matcher.group("index")), matcher.group("newTitle"),
					matcher.group("description"), matcher.group("startDate"), matcher.group("dueDate"),
					matcher.group("interval"), matcher.group("timeInterval"), matcher.group("taskColor"), null);
		} catch (IllegalValueException e) {
			return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
		}
	}
	// @@author

	// @@author A0153751H_reused
	private static Set<String> getTagsFromArgs(String tagArguments) throws IllegalValueException {
		// no tags
		if (tagArguments.isEmpty()) {
			return Collections.emptySet();
		}
		// replace first delimiter prefix, then split
		final Collection<String> tagStrings = Arrays.asList(tagArguments.replaceFirst(" ts/", "").split(" ts/"));
		return new HashSet<>(tagStrings);
	}
	// @@author

	private Set<String> toSet(Optional<List<String>> tagsOptional) {
		List<String> tags = tagsOptional.orElse(Collections.emptyList());
		return new HashSet<>(tags);
	}

	/**
	 * Parses arguments in the context of the delete task command.
	 *
	 * @param args
	 *            full command args string
	 * @return the prepared command
	 */
	private Command prepareDelete(String args) {

		Optional<Integer> index = parseIndex(args);
		if (!index.isPresent()) {
			return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
		}

		return new DeleteCommand(index.get());
	}

	/**
	 * Parses arguments in the context of the done task command.
	 *
	 * @param args
	 *            full command args string
	 * @return the prepared command
	 */
	// @@author A0148083A
	private Command prepareDone(String args) {

		Optional<Integer> index = parseIndex(args);
		if (!index.isPresent()) {
			return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DoneCommand.MESSAGE_USAGE));
		}

		return new DoneCommand(index.get());
	}
	// @@author

	/**
	 * Parses arguments in the context of the select task command.
	 *
	 * @param args
	 *            full command args string
	 * @return the prepared command
	 */
	private Command prepareSelect(String args) {
		Optional<Integer> index = parseIndex(args);
		if (!index.isPresent()) {
			return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SelectCommand.MESSAGE_USAGE));
		}

		return new SelectCommand(index.get());
	}

	/**
	 * Returns the specified index in the {@code command} IF a positive unsigned
	 * integer is given as the index. Returns an {@code Optional.empty()}
	 * otherwise.
	 */
	private Optional<Integer> parseIndex(String command) {
		final Matcher matcher = TASK_INDEX_ARGS_FORMAT.matcher(command.trim());
		if (!matcher.matches()) {
			return Optional.empty();
		}

		String index = matcher.group("targetIndex");
		if (!StringUtil.isUnsignedInteger(index)) {
			return Optional.empty();
		}
		return Optional.of(Integer.parseInt(index));

	}

	/**
	 * Parses arguments in the context of the find task command.
	 *
	 * @param args
	 *            full command args string
	 * @return the prepared command
	 */
	private Command prepareFind(String args) {
		final Matcher matcher = KEYWORDS_ARGS_FORMAT.matcher(args.trim());
		if (!matcher.matches()) {
			return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
		}

		// keywords delimited by whitespace
		final String[] keywords = matcher.group("keywords").split("\\s+");
		final Set<String> keywordSet = new HashSet<>(Arrays.asList(keywords));
		return new FindCommand(keywordSet);
	}

	// @@author A0139932X
	/**
	 * Parses arguments in the context of the save task command.
	 *
	 * @param args
	 *            full command args string
	 * @return the prepared command
	 */
	private Command prepareSave(String args) {
		// Validate arg string format
		final Matcher matcher = SAVE_COMMAND_FORMAT.matcher(args.trim());
		if (!matcher.matches()) {
			return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SaveCommand.MESSAGE_USAGE));
		}
		// String storagePath = args.trim().concat(".xml");
		return new SaveCommand(args.trim());

	}

}