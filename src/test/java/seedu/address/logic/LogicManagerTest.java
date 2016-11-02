package seedu.address.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static seedu.task.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.task.commons.core.Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX;
import static seedu.task.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.eventbus.Subscribe;

import seedu.task.commons.core.Config;
import seedu.task.commons.core.EventsCenter;
import seedu.task.commons.events.model.TaskManagerChangedEvent;
import seedu.task.commons.events.ui.JumpToListRequestEvent;
import seedu.task.commons.events.ui.ShowHelpRequestEvent;
import seedu.task.commons.util.ConfigUtil;
import seedu.task.logic.Logic;
import seedu.task.logic.LogicManager;
import seedu.task.logic.commands.AddCommand;
import seedu.task.logic.commands.ClearCommand;
import seedu.task.logic.commands.Command;
import seedu.task.logic.commands.CommandResult;
import seedu.task.logic.commands.DeleteCommand;
import seedu.task.logic.commands.DoneCommand;
import seedu.task.logic.commands.EditCommand;
import seedu.task.logic.commands.ExitCommand;
import seedu.task.logic.commands.FindCommand;
import seedu.task.logic.commands.HelpCommand;
import seedu.task.logic.commands.SaveCommand;
import seedu.task.logic.commands.SelectCommand;
import seedu.task.model.Model;
import seedu.task.model.ModelManager;
import seedu.task.model.ReadOnlyTaskManager;
import seedu.task.model.TaskManager;
import seedu.task.model.tag.Tag;
import seedu.task.model.tag.UniqueTagList;
import seedu.task.model.task.Description;
import seedu.task.model.task.DueDate;
import seedu.task.model.task.Interval;
import seedu.task.model.task.ReadOnlyTask;
import seedu.task.model.task.StartDate;
import seedu.task.model.task.Status;
import seedu.task.model.task.Task;
import seedu.task.model.task.TaskColor;
import seedu.task.model.task.TimeInterval;
import seedu.task.model.task.Title;
import seedu.task.storage.StorageManager;


public class LogicManagerTest {

    /**
     * See https://github.com/junit-team/junit4/wiki/rules#temporaryfolder-rule
     */
    @Rule
    public TemporaryFolder saveFolder = new TemporaryFolder();

    private Model model;
    private Logic logic;

    //These are for checking the correctness of the events raised
    private ReadOnlyTaskManager latestSavedTaskManager;
    private boolean helpShown;
    private int targetedJumpIndex;

    @Subscribe
    private void handleLocalModelChangedEvent(TaskManagerChangedEvent abce) throws ParseException {
        latestSavedTaskManager = new TaskManager(abce.data);
    }

    @Subscribe
    private void handleShowHelpRequestEvent(ShowHelpRequestEvent she) {
        helpShown = true;
    }

    @Subscribe
    private void handleJumpToListRequestEvent(JumpToListRequestEvent je) {
        targetedJumpIndex = je.targetIndex;
    }

    @Before
    public void setup() throws ParseException {
        model = new ModelManager();
        String tempTaskManagerFile = saveFolder.getRoot().getPath() + "TempTaskManager.xml";
        String tempPreferencesFile = saveFolder.getRoot().getPath() + "TempPreferences.json";
        logic = new LogicManager(model, new StorageManager(tempTaskManagerFile, tempPreferencesFile));
        EventsCenter.getInstance().registerHandler(this);

        latestSavedTaskManager = new TaskManager(model.getTaskManager()); // last saved assumed to be up to date before.
        helpShown = false;
        targetedJumpIndex = -1; // non yet
    }

    @After
    public void teardown() {
        EventsCenter.clearSubscribers();
    }

    @Test
    public void execute_invalid() throws Exception {
        String invalidCommand = "       ";
        assertCommandBehavior(invalidCommand,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
    }

    /**
     * Executes the command and confirms that the result message is correct.
     * Both the 'address book' and the 'last shown list' are expected to be empty.
     * @see #assertCommandBehavior(String, String, ReadOnlyTaskManager, List)
     */
    private void assertCommandBehavior(String inputCommand, String expectedMessage) throws Exception {
        assertCommandBehavior(inputCommand, expectedMessage, new TaskManager(), Collections.emptyList());
    }

    /**
     * Executes the command and confirms that the result message is correct and
     * also confirms that the following three parts of the LogicManager object's state are as expected:<br>
     *      - the internal address book data are same as those in the {@code expectedTaskManager} <br>
     *      - the backing list shown by UI matches the {@code shownList} <br>
     *      - {@code expectedTaskManager} was saved to the storage file. <br>
     */
    private void assertCommandBehavior(String inputCommand, String expectedMessage,
                                       ReadOnlyTaskManager expectedTaskManager,
                                       List<? extends ReadOnlyTask> expectedShownList) throws Exception {
     //Execute the command
        CommandResult result = logic.execute(inputCommand);

        //Confirm the ui display elements should contain the right data
        assertEquals(expectedMessage, result.feedbackToUser);
        assertEquals(expectedShownList, model.getFilteredTaskList());

        //Confirm the state of data (saved and in-memory) is as expected
        assertEquals(expectedTaskManager, model.getTaskManager());
        assertEquals(expectedTaskManager, latestSavedTaskManager);
    }


    @Test
    public void execute_unknownCommandWord() throws Exception {
        String unknownCommand = "uicfhmowqewca";
        assertCommandBehavior(unknownCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_help() throws Exception {
        assertCommandBehavior("help", HelpCommand.SHOWING_HELP_MESSAGE);
        assertTrue(helpShown);
    }

    @Test
    public void execute_exit() throws Exception {
        assertCommandBehavior("exit", ExitCommand.MESSAGE_EXIT_ACKNOWLEDGEMENT);
    }

    @Test
    public void execute_clear() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        model.addTask(helper.generateTask(1));
        model.addTask(helper.generateTask(2));
        model.addTask(helper.generateTask(3));

        assertCommandBehavior("clear", ClearCommand.MESSAGE_SUCCESS, new TaskManager(), Collections.emptyList());
    }


    @Test
    public void execute_add_noTitle() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);
        assertCommandBehavior(
                "add d/description", expectedMessage);
    }
    //@@author A0148083A
    @Test
    public void execute_add_event_noDueDate() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_EVENT_USAGE);
        assertCommandBehavior("add event d/without due date sd/12-12-2016", expectedMessage);
    }
    //@@author
    
    @Test
    public void execute_add_invalidTaskData() throws Exception {
        //TODO
    }

    //@@author A0153411W
	@Test
	public void execute_add_successful() throws Exception {
		// setup expectations
		TestDataHelper helper = new TestDataHelper();
		Task toBeAdded = helper.homework();
		TaskManager expectedAB = new TaskManager();
		String expectedMessage = String.format(AddCommand.MESSAGE_SUCCESS,
				toBeAdded.getTitle() + " Description: " + toBeAdded.getDescription() + " Start Date: "
						+ toBeAdded.getStartDate() + " Due Date: " + toBeAdded.getDueDate() + " Status: "
						+ toBeAdded.getStatus());
		expectedAB.addTask(toBeAdded);

		// execute command and verify result
		assertCommandBehavior(helper.generateAddCommand(toBeAdded), expectedMessage, expectedAB,
				expectedAB.getTaskList());
	}


	@Test
	public void execute_undo_add_successful() throws Exception {
		// setup expectations
		TestDataHelper helper = new TestDataHelper();
		TaskManager expectedManager = new TaskManager();
		
		//Add task to manager
		Task toBeAdded = helper.homework();
		String commadForAdd = helper.generateAddCommand(toBeAdded);
        logic.execute(commadForAdd);

		// execute undo command and verify result
		assertCommandBehavior(helper.generateUndoCommand(), AddCommand.MESSAGE_SUCCESS_UNDO, expectedManager,
				expectedManager.getTaskList());
	}
	
	@Test
	public void execute_undo_manyAdds_successful() throws Exception {
		// setup expectations
		TestDataHelper helper = new TestDataHelper();
		TaskManager expectedManager = new TaskManager();
		
		//Add task to manager
		List<Task> toBeAdded = helper.generateTaskList(3);
		for(Task toAdd : toBeAdded){
			String commadForAdd = helper.generateAddCommand(toAdd);
	        logic.execute(commadForAdd);
	        expectedManager.addTask(toAdd);
		}

		for(int i =1; i<=toBeAdded.size(); i++){
			//remove last added Task for comparison
			Task taskToRemove= toBeAdded.get(toBeAdded.size()-i);
			expectedManager.removeTask(taskToRemove);
			
			// execute undo command and verify result
			assertCommandBehavior(helper.generateUndoCommand(), AddCommand.MESSAGE_SUCCESS_UNDO, expectedManager,
					expectedManager.getTaskList());
		}
	}
	
	@Test
	public void execute_undo_edit_successful() throws Exception {
		// setup expectations
		TestDataHelper helper = new TestDataHelper();
		TaskManager expectedManager = new TaskManager();
		
		//Add task to manager
		Task toBeEdited = helper.homework();
		String commadForAdd = helper.generateAddCommand(toBeEdited);
		logic.execute(commadForAdd);
		expectedManager.addTask(toBeEdited);
		
		//Change task
		String commadForEdit = "edit 1 t/ChangedTitle";
        logic.execute(commadForEdit);
        
		// execute undo command and verify result
		assertCommandBehavior(helper.generateUndoCommand(), EditCommand.MESSAGE_SUCCESS_UNDO, expectedManager,
				expectedManager.getTaskList());
	}
	
	
	@Test
	public void execute_undo_done_successful() throws Exception {
		// setup expectations
		TestDataHelper helper = new TestDataHelper();
		TaskManager expectedManager = new TaskManager();
		
		//Add task to manager
		Task taskWithNotCompletedStatus = helper.homework();
		String commadForAdd = helper.generateAddCommand(taskWithNotCompletedStatus);
		logic.execute(commadForAdd);
		expectedManager.addTask(taskWithNotCompletedStatus);
		
		//Change task
		String commadForDone = "done 1";
        logic.execute(commadForDone);
        
		// execute undo command and verify result
		assertCommandBehavior(helper.generateUndoCommand(), DoneCommand.MESSAGE_SUCCESS_UNDO, expectedManager,
				expectedManager.getTaskList());
	}
	
	@Test
	public void execute_undo_clear_successful() throws Exception {
		// setup expectations
		TestDataHelper helper = new TestDataHelper();
		TaskManager expectedManager = new TaskManager();
		
		//Add task to manager
		Task task = helper.homework();
		String commadForAdd = helper.generateAddCommand(task);
		logic.execute(commadForAdd);
		expectedManager.addTask(task);
		
		//Change task
		String commadForClear = "clear";
        logic.execute(commadForClear);
        
		// execute undo command and verify result
		assertCommandBehavior(helper.generateUndoCommand(), ClearCommand.MESSAGE_SUCCESS_UNDO, expectedManager,
				expectedManager.getTaskList());
	}
	
	@Test
	public void execute_undo_delete_successful() throws Exception {
		// setup expectations
		TestDataHelper helper = new TestDataHelper();
		TaskManager expectedManager = new TaskManager();
		
		//Add task to manager
		Task toBeDeleted = helper.homework();
		String commadForAdd = helper.generateAddCommand(toBeDeleted);
		logic.execute(commadForAdd);
		expectedManager.addTask(toBeDeleted);
		
		//Change task
		String commadForDelete = "delete 1";
        logic.execute(commadForDelete);
        
		// execute undo command and verify result
		assertCommandBehavior(helper.generateUndoCommand(), DeleteCommand.MESSAGE_SUCCESS_UNDO, expectedManager,
				expectedManager.getTaskList());
	}
	
	@Test
	public void execute_undo_find_successful() throws Exception {
		// setup expectations
		TestDataHelper helper = new TestDataHelper();
		TaskManager expectedManager = new TaskManager();
		
		//Add task to manager
		Task task = helper.homework();
		String commadForAdd = helper.generateAddCommand(task);
		logic.execute(commadForAdd);
		expectedManager.addTask(task);
		
		//Change task
		String commadForFind = "find Homework";
        logic.execute(commadForFind);
        
		// execute undo command and verify result
		assertCommandBehavior(helper.generateUndoCommand(), FindCommand.MESSAGE_SUCCESS_UNDO, expectedManager,
				expectedManager.getTaskList());
	}
    //@@author
	
    /**
     * Confirms the 'invalid argument index number behaviour' for the given command
     * targeting a single task in the shown list, using visible index.
     * @param commandWord to test assuming it targets a single task in the last shown list based on visible index.
     */
    private void assertIncorrectIndexFormatBehaviorForCommand(String commandWord, String expectedMessage) throws Exception {
        assertCommandBehavior(commandWord , expectedMessage); //index missing
        assertCommandBehavior(commandWord + " +1", expectedMessage); //index should be unsigned
        assertCommandBehavior(commandWord + " -1", expectedMessage); //index should be unsigned
        assertCommandBehavior(commandWord + " 0", expectedMessage); //index cannot be 0
        assertCommandBehavior(commandWord + " not_a_number", expectedMessage);
    }

    /**
     * Confirms the 'invalid argument index number behaviour' for the given command
     * targeting a single task in the shown list, using visible index.
     * @param commandWord to test assuming it targets a single task in the last shown list based on visible index.
     */
    private void assertIndexNotFoundBehaviorForCommand(String commandWord) throws Exception {
        String expectedMessage = MESSAGE_INVALID_TASK_DISPLAYED_INDEX;
        TestDataHelper helper = new TestDataHelper();
        List<Task> taskList = helper.generateTaskList(2);

        // set AB state to 2 tasks
        model.resetData(new TaskManager());
        for (Task p : taskList) {
            model.addTask(p);
        }

        assertCommandBehavior(commandWord + " 3", expectedMessage, model.getTaskManager(), taskList);
    }

    @Test
    public void execute_selectInvalidArgsFormat_errorMessageShown() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SelectCommand.MESSAGE_USAGE);
        assertIncorrectIndexFormatBehaviorForCommand("select", expectedMessage);
    }

    @Test
    public void execute_selectIndexNotFound_errorMessageShown() throws Exception {
        assertIndexNotFoundBehaviorForCommand("select");
    }

    @Test
    public void execute_select_jumpsToCorrectTask() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        List<Task> threeTasks = helper.generateTaskList(3);

        TaskManager expectedAB = helper.generateTaskManager(threeTasks);
        helper.addToModel(model, threeTasks);

        assertCommandBehavior("select 2",
                String.format(SelectCommand.MESSAGE_SELECT_TASK_SUCCESS, 2),
                expectedAB,
                expectedAB.getTaskList());
        assertEquals(1, targetedJumpIndex);
        assertEquals(model.getFilteredTaskList().get(1), threeTasks.get(1));
    }


    @Test
    public void execute_deleteInvalidArgsFormat_errorMessageShown() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);
        assertIncorrectIndexFormatBehaviorForCommand("delete", expectedMessage);
    }

    @Test
    public void execute_deleteIndexNotFound_errorMessageShown() throws Exception {
        assertIndexNotFoundBehaviorForCommand("delete");
    }

    @Test
    public void execute_delete_removesCorrectTask() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        List<Task> threeTasks = helper.generateTaskList(3);

        TaskManager expectedAB = helper.generateTaskManager(threeTasks);
        expectedAB.removeTask(threeTasks.get(1));
        helper.addToModel(model, threeTasks);

        assertCommandBehavior("delete 2",
                String.format(DeleteCommand.MESSAGE_DELETE_TASK_SUCCESS, threeTasks.get(1)),
                expectedAB,
                expectedAB.getTaskList());
    }


    @Test
    public void execute_find_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        assertCommandBehavior("find ", expectedMessage);
    }
    //@@author A0139932X
    @Test
    public void execute_find_onlyMatchesFullWordsInNames() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task pTarget1 = helper.generateTaskWithName("bla bla KEY bla");
        Task pTarget2 = helper.generateTaskWithName("bla KEY bla bceofeia");
        Task p1 = helper.generateTaskWithName("KE Y");
        Task pTarget3 = helper.generateTaskWithName("KEYKEYKEY sduauo");

        List<Task> fourTasks = helper.generateTaskList(p1, pTarget1, pTarget2, pTarget3);
        TaskManager expectedAB = helper.generateTaskManager(fourTasks);
        List<Task> expectedList = helper.generateTaskList(pTarget1, pTarget2, pTarget3);
        helper.addToModel(model, fourTasks);

        assertCommandBehavior("find KEY",
                Command.getMessageForTaskListShownSummary(expectedList.size()),
                expectedAB,
                expectedList);
    }
    //author

    @Test
    public void execute_find_isNotCaseSensitive() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task p1 = helper.generateTaskWithName("bla bla KEY bla");
        Task p2 = helper.generateTaskWithName("bla KEY bla bceofeia");
        Task p3 = helper.generateTaskWithName("key key");
        Task p4 = helper.generateTaskWithName("KEy sduauo");

        List<Task> fourTasks = helper.generateTaskList(p3, p1, p4, p2);
        TaskManager expectedAB = helper.generateTaskManager(fourTasks);
        List<Task> expectedList = fourTasks;
        helper.addToModel(model, fourTasks);

        assertCommandBehavior("find KEY",
                Command.getMessageForTaskListShownSummary(expectedList.size()),
                expectedAB,
                expectedList);
    }

    @Test
    public void execute_find_matchesIfAnyKeywordPresent() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task pTarget1 = helper.generateTaskWithName("bla bla KEY bla");
        Task pTarget2 = helper.generateTaskWithName("bla rAnDoM bla bceofeia");
        Task pTarget3 = helper.generateTaskWithName("key key");
        Task p1 = helper.generateTaskWithName("sduauo");

        List<Task> fourTasks = helper.generateTaskList(pTarget1, p1, pTarget2, pTarget3);
        TaskManager expectedAB = helper.generateTaskManager(fourTasks);
        List<Task> expectedList = helper.generateTaskList(pTarget1, pTarget2, pTarget3);
        helper.addToModel(model, fourTasks);

        assertCommandBehavior("find key rAnDoM",
                Command.getMessageForTaskListShownSummary(expectedList.size()),
                expectedAB,
                expectedList);
    }
    //author A0139932X
    @Test
    public void execute_save_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SaveCommand.MESSAGE_USAGE);
        assertCommandBehavior("save", expectedMessage);
    }
    
    @Test
    public void execute_save_successful() throws Exception {

     
        Config config = new Config();
        
        String expectedMessage = "Change save path:.\\test Updated";
        assertCommandBehavior("save .\\test", expectedMessage);
        config.setTaskManagerFilePath("data/taskmanager.xml");
        ConfigUtil.saveConfig(config, "config.json");
        
    }
    //@@author
    
    //@@author A0148083A
    @Test
    public void execute_doneInvalidArgsFormat_errorMessageShown() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DoneCommand.MESSAGE_USAGE);
        assertIncorrectIndexFormatBehaviorForCommand("done", expectedMessage);
    }

    @Test
    public void execute_doneIndexNotFound_errorMessageShown() throws Exception {
        assertIndexNotFoundBehaviorForCommand("done");
    }
    
    @Test
    public void execute_done_success() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        List<Task> oneTasks = helper.generateTaskList(1);
        helper.addToModel(model, oneTasks);
        String expectedTask = "Task 1 "
                + "Description: Description 1 "
                + "Start Date: 01-01-2016 00:00 "
                + "Due Date: 01-01-2016 23:59 "
                + "Status: COMPLETED "
                + "Tags: [tag1][tag2]";
        String expectedMessage = String.format(DoneCommand.MESSAGE_COMPLETED_TASK_SUCCESS, expectedTask);
        //assertCommandBehavior("done 1", expectedMessage);
        CommandResult result = logic.execute("done 1");
        assertEquals(expectedMessage, result.feedbackToUser);
    }
    //@@author

    /**
     * A utility class to generate test data.
     */
    class TestDataHelper{

        Task homework() throws Exception {
        	Title title = new Title("Homework");
        	Description description= new Description("Database Tutorials.");
        	StartDate startDate= new StartDate("11-01-2012 00:00");
        	DueDate dueDate= new DueDate("11-01-2012 23:59");
        	Interval interval= new Interval("1");
        	TimeInterval timeInterval = new TimeInterval("7");
        	Status status = new Status("ONGOING");
            //@@author A0153751H
        	TaskColor taskColor = new TaskColor("none");
        	//@@author
            Tag tag1 = new Tag("tag1");
            Tag tag2 = new Tag("tag2");
            UniqueTagList tags = new UniqueTagList(tag1, tag2);
            return new Task(title,description,startDate,dueDate,interval, timeInterval, status, taskColor, tags);
        }

        /**
         * Generates a valid task using the given seed.
         * Running this function with the same parameter values guarantees the returned task will have the same state.
         * Each unique seed will generate a unique Task object.
         *
         * @param seed used to generate the task data field values
         */
        //@@author A0153751H
        Task generateTask(int seed) throws Exception {
            return new Task(
                    new Title("Task " + seed),
                    new Description("Description " + seed),
                    new StartDate("01-01-2016 00:00"),
                    new DueDate("01-01-2016 23:59"),
                    new Interval("1"),
                    new TimeInterval(""+seed),
                    new Status("ONGOING"),
                    new TaskColor("none"),
                    new UniqueTagList(new Tag("tag" + Math.abs(seed)), new Tag("tag" + Math.abs(seed + 1)))
            );
        }
        //@@author

        /** Generates the correct add command based on the task given */
        String generateAddCommand(Task t) {
            StringBuffer cmd = new StringBuffer();

            cmd.append("add ");

            cmd.append(t.getTitle().toString());
            cmd.append(" d/").append(t.getDescription());
            cmd.append(" sd/").append(t.getStartDate());
            cmd.append(" dd/").append(t.getDueDate());
            cmd.append(" i/").append(t.getInterval());
            cmd.append(" ti/").append(t.getTimeInterval());
            UniqueTagList tags = t.getTags();
            for(Tag tag: tags){
                cmd.append(" t/").append(tag.tagName);
            }

            return cmd.toString();
        }
        
        /** Generates the correct undo command*/
        String generateUndoCommand() {
            StringBuffer cmd = new StringBuffer();
            cmd.append("undo ");
            return cmd.toString();
        }
        

        /**
         * Generates an TaskManager with auto-generated tasks.
         */
        TaskManager generateTaskManager(int numGenerated) throws Exception{
            TaskManager addressBook = new TaskManager();
            addToTaskManager(addressBook, numGenerated);
            return addressBook;
        }

        /**
         * Generates an TaskManager based on the list of Tasks given.
         */
        TaskManager generateTaskManager(List<Task> tasks) throws Exception{
            TaskManager addressBook = new TaskManager();
            addToTaskManager(addressBook, tasks);
            return addressBook;
        }

        /**
         * Adds auto-generated Task objects to the given TaskManager
         * @param addressBook The TaskManager to which the Tasks will be added
         */
        void addToTaskManager(TaskManager addressBook, int numGenerated) throws Exception{
            addToTaskManager(addressBook, generateTaskList(numGenerated));
        }

        /**
         * Adds the given list of Tasks to the given TaskManager
         */
        void addToTaskManager(TaskManager addressBook, List<Task> tasksToAdd) throws Exception{
            for(Task p: tasksToAdd){
                addressBook.addTask(p);
            }
        }

        /**
         * Adds auto-generated Task objects to the given model
         * @param model The model to which the Tasks will be added
         */
        void addToModel(Model model, int numGenerated) throws Exception{
            addToModel(model, generateTaskList(numGenerated));
        }

        /**
         * Adds the given list of Tasks to the given model
         */
        void addToModel(Model model, List<Task> tasksToAdd) throws Exception{
            for(Task p: tasksToAdd){
                model.addTask(p);
            }
        }

        /**
         * Generates a list of Tasks based on the flags.
         */
        List<Task> generateTaskList(int numGenerated) throws Exception{
            List<Task> tasks = new ArrayList<>();
            for(int i = 1; i <= numGenerated; i++){
                tasks.add(generateTask(i));
            }
            return tasks;
        }

        List<Task> generateTaskList(Task... tasks) {
            return Arrays.asList(tasks);
        }

        /**
         * Generates a Task object with given name. Other fields will have some dummy values.
         */
        Task generateTaskWithName(String name) throws Exception {
            return new Task(
            		new Title(name),
                    new Description("Description"),
                    new StartDate("11-01-2012 00:00"),
                    new DueDate("11-01-2012 23:59"),
                    new Interval("7"),
                    new TimeInterval("1"),
                    new Status("ONGOING"),
                    new TaskColor("none"),
                    new UniqueTagList(new Tag("tag"))
            );
        }
    }
}
