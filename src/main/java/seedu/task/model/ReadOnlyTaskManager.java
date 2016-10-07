package seedu.task.model;


import seedu.task.model.task.ReadOnlyTask;
import seedu.task.model.task.UniqueTaskList;

import java.util.List;

/**
 * Unmodifiable view of an task manager
 */
public interface ReadOnlyTaskManager {

    UniqueTaskList getUniqueTaskList();

    /**
     * Returns an unmodifiable view of tasks list
     */
    List<ReadOnlyTask> getTaskList();

}
