package seedu.task.model.task;

import seedu.task.commons.exceptions.IllegalValueException;

/**
 * Represents a Task's time interval in the address book.
 */
public class TimeInterval {

    public static final String MESSAGE_TIME_INTERVAL_CONSTRAINTS =
            "Time interval should only contain numbers";
    public static final String TIME_INTERVAL_VALIDATION_REGEX = "[1-9][0-9]*|1";

    public final String value;

    /**
     * Validates given interval.
     *
     * @throws IllegalValueException if given interval string is invalid.
     */
    public TimeInterval(String timeInterval) throws IllegalValueException {
        assert timeInterval != null;
        timeInterval = timeInterval.trim();
        if (!isValidTimeInterval(timeInterval)) {
            throw new IllegalValueException(MESSAGE_TIME_INTERVAL_CONSTRAINTS);
        }
        this.value = timeInterval;
    }

    /**
     * Returns if a given string is a valid task time interval.
     */
    public static boolean isValidTimeInterval(String test) {
        return test.matches(TIME_INTERVAL_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof TimeInterval // instanceof handles nulls
                && this.value.equals(((TimeInterval) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
