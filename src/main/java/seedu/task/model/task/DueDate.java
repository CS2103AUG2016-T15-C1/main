package seedu.task.model.task;

import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import seedu.task.commons.exceptions.IllegalValueException;

//Represents a Task's(event) start date in the task manager.
public class DueDate {
  
  public static final String MESSAGE_DATE_CONSTRAINTS = "Task's start date should be entered as DD-MM-YYYY hh:mm";
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");
  
  public final Date dueDate;

  public DueDate(String dateToValidate) throws IllegalValueException, ParseException {
      if (dateToValidate.equals("Not Set")) {
          this.dueDate = null;
      }
      else if (!isValidDate(dateToValidate)) {
          throw new IllegalValueException(MESSAGE_DATE_CONSTRAINTS);
      }
      else {
          this.dueDate = DATE_FORMAT.parse(dateToValidate);
      }
  }
  
	public DueDate(Date date) {
		dueDate = date;
	}
  
  public static boolean isValidDate(String inDate) {
      DATE_FORMAT.setLenient(false);
      try {
          DATE_FORMAT.parse(inDate.trim());
      } catch (ParseException pe) {
        return false;
      }
      return true;
    }
  
  @Override
  public String toString() {
      return dueDate == null ? "Not Set" : DATE_FORMAT.format(dueDate);
  }

  @Override
  public boolean equals(Object other) {
      return other == this; // short circuit if same object 
  }

  @Override
  public int hashCode() {
      return dueDate.hashCode();
  }

}
