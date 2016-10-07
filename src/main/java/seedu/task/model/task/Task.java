package seedu.task.model.task;

import seedu.task.commons.util.CollectionUtil;
import seedu.task.model.tag.UniqueTagList;

import java.util.Objects;

/**
 * Represents a Task in the address book.
 * Guarantees: details are present and not null, field values are validated.
 */
public class Task implements ReadOnlyTask {

    private Name name;
    private Description description;
    private Status status;

    //private UniqueTagList tags;

    /**
     * Every field must be present and not null.
     */
    public Task(Name name, Description description, Status status) {
        assert !CollectionUtil.isAnyNull(name, description, status);
        this.name = name;
        this.description = description;
        this.status = status;
    }

    /**
     * Copy constructor.
     */
    public Task(ReadOnlyTask source) {
        this(source.getName(), source.getDescription(), source.getStatus());
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public Description getDescription() {
        return description;
    }

    @Override
    public Status getStatus() {
        return status;
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
        return Objects.hash(name, description, status);
    }

    @Override
    public String toString() {
        return getAsText();
    }

}
