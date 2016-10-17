package seedu.task.logic.parser;

import seedu.task.commons.exceptions.IllegalValueException;
import seedu.task.commons.util.StringUtil;
import seedu.task.logic.commands.*;
import seedu.task.logic.parser.ArgumentTokenizer.Prefix;

import static seedu.task.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.task.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static final Prefix descriptionPrefix = new Prefix(" d/");
    public static final Prefix startDatePrefix = new Prefix(" sd/");
    public static final Prefix dueDatePrefix = new Prefix(" dd/");
    public static final Prefix intervalPrefix = new Prefix(" i/");
    public static final Prefix timeIntervalPrefix = new Prefix(" ti/");
    public static final Prefix tagArgumentsPrefix = new Prefix(" t/");   

    private static final Pattern TASK_DATA_ARGS_FORMAT_EDIT = // '/' forward slashes are reserved for delimiter prefixes
            Pattern.compile("(?<index>[^/]+)"
                    + " t/(?<newTitle>[^/]+)");


    public Parser() {}

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException 
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
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

        default:
            return new IncorrectCommand(MESSAGE_UNKNOWN_COMMAND);
        }
    }
    
    /**
     * Parses arguments in the context of the add task command.
     *
     * @param args full command args string
     * @return the prepared command
     * @throws ParseException 
     */

	private Command prepareAdd(String args) throws ParseException {

		ArgumentTokenizer argsTokenizer = new ArgumentTokenizer(descriptionPrefix, startDatePrefix, dueDatePrefix,
				intervalPrefix, timeIntervalPrefix, tagArgumentsPrefix);
		argsTokenizer.tokenize(args);
		try {
			return new AddCommand(argsTokenizer.getPreamble().get(), 
					argsTokenizer.getValue(descriptionPrefix).get(),
					argsTokenizer.getValue(startDatePrefix).get(), 
					argsTokenizer.getValue(dueDatePrefix).get(),
					argsTokenizer.getValue(intervalPrefix).get(), 
					argsTokenizer.getValue(timeIntervalPrefix).get(),
					toSet(argsTokenizer.getAllValues(tagArgumentsPrefix)));
		} catch (NoSuchElementException nsee) {
			return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
		} catch (IllegalValueException ive) {
			return new IncorrectCommand(ive.getMessage());
		}
	}
    
    /**
     * Parses arguments in the context of the edit task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareEdit(String args){
        final Matcher matcher = TASK_DATA_ARGS_FORMAT_EDIT.matcher(args.trim());
        // Validate arg string format
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }
        try {
			return new EditCommand(
			        Integer.parseInt(matcher.group("index")),
			        matcher.group("newTitle")
			);
		} catch (NumberFormatException e) {
			return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
		}
    }

    private Set<String> toSet(Optional<List<String>> tagsOptional) {
    	List<String> tags = tagsOptional.orElse(Collections.emptyList());
    	return new HashSet<>(tags);
    }
    
    /**
     * Parses arguments in the context of the delete task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareDelete(String args) {

        Optional<Integer> index = parseIndex(args);
        if(!index.isPresent()){
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }

        return new DeleteCommand(index.get());
    }

    /**
     * Parses arguments in the context of the select task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareSelect(String args) {
        Optional<Integer> index = parseIndex(args);
        if(!index.isPresent()){
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SelectCommand.MESSAGE_USAGE));
        }

        return new SelectCommand(index.get());
    }

    /**
     * Returns the specified index in the {@code command} IF a positive unsigned integer is given as the index.
     *   Returns an {@code Optional.empty()} otherwise.
     */
    private Optional<Integer> parseIndex(String command) {
        final Matcher matcher = TASK_INDEX_ARGS_FORMAT.matcher(command.trim());
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String index = matcher.group("targetIndex");
        if(!StringUtil.isUnsignedInteger(index)){
            return Optional.empty();
        }
        return Optional.of(Integer.parseInt(index));

    }

    /**
     * Parses arguments in the context of the find task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareFind(String args) {
        final Matcher matcher = KEYWORDS_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    FindCommand.MESSAGE_USAGE));
        }

        // keywords delimited by whitespace
        final String[] keywords = matcher.group("keywords").split("\\s+");
        final Set<String> keywordSet = new HashSet<>(Arrays.asList(keywords));
        return new FindCommand(keywordSet);
    }

}