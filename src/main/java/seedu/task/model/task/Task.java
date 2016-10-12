package seedu.task.model.task;

import seedu.task.commons.util.CollectionUtil;
import seedu.task.model.tag.UniqueTagList;

import java.util.Objects;

/**
 * Represents a Task in the address book.
 * Guarantees: details are present and not null, field values are validated.
 */
public class Task implements ReadOnlyTask {

    private Title title;
    private Description description;
    private StartDate startDate; 
    private DueDate dueDate; 
    private Interval interval; 
    private TimeInterval timeInterval; 
    private UniqueTagList tags;

    /**
     * Every field must be present and not null.
     */
    public Task(Title title, Description description,StartDate startDate, DueDate dueDate,Interval interval,TimeInterval timeInterval, UniqueTagList tags) {
        assert !CollectionUtil.isAnyNull(title, description, startDate, dueDate,interval,timeInterval,tags);
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.interval = interval;
        this.timeInterval = timeInterval;
        this.tags = new UniqueTagList(tags); // protect internal tags from changes in the arg list
    }

    /**
     * Copy constructor.
     */
    public Task(ReadOnlyTask source) {
        this(source.getTitle(), source.getDescription(), source.getStartDate(), source.getDueDate(), source.getInterval(), source.getTimeInterval(), source.getTags());
    }

    @Override
    public Title getTitle() {
        return title;
    }

    @Override
    public Description getDescription() {
        return description;
    }

    @Override
    public StartDate getStartDate() {
        return startDate;
    }
    
    @Override
    public DueDate getDueDate() {
        return dueDate;
    }

    @Override
    public Interval getInterval() {
        return interval;
    }

    @Override
    public TimeInterval getTimeInterval() {
        return timeInterval;
    }

    @Override
    public UniqueTagList getTags() {
        return new UniqueTagList(tags);
    }

    /**
     * Replaces this task's tags with the tags in the argument tag list.
     */
    public void setTags(UniqueTagList replacement) {
        tags.setTags(replacement);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ReadOnlyTask // instanceof handles nulls
                && this.isSameStateAs((ReadOnlyTask) other));
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(title, description, startDate, dueDate,interval,timeInterval,tags);
    }

    @Override
    public String toString() {
        return getAsText();
    }

}