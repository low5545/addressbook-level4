package seedu.address.model.reminder;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;

import org.fxmisc.easybind.EasyBind;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.model.reminder.exceptions.DuplicateReminderException;
import seedu.address.model.reminder.exceptions.ReminderNotFoundException;



/**
 * A list of reminders that enforces uniqueness between its elements and does not allow nulls.
 *
 * Supports a minimal set of list operations.
 *
 * @see Reminder#equals(Object)
 * @see CollectionUtil#elementsAreUnique(Collection)
 */
public class UniqueReminderList implements Iterable<Reminder> {


    private final ObservableList<Reminder> internalList = FXCollections.observableArrayList();
    // used by asObservableList()
    private final ObservableList<ReadOnlyReminder> mappedList = EasyBind.map(internalList, (reminder) -> reminder);

    /**
     * Constructs empty UniqueReminderList
     */
    public UniqueReminderList(ArrayList<Reminder> reminders) {
        requireNonNull(reminders);
        internalList.addAll(reminders);
    }
    public UniqueReminderList() {
    }

    /**
     * Returns true if the list contains an equivalent reminder as the given argument.
     */
    public boolean contains(ReadOnlyReminder toCheck) {
        requireNonNull(toCheck);
        return internalList.contains(toCheck);
    }

    /**
     * Adds an reminder to the list.
     *
     * @throws DuplicateReminderException if the reminder to add is a duplicate of an existing reminder in the list.
     */
    public void add(ReadOnlyReminder toAdd) throws DuplicateReminderException {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateReminderException();
        }
        internalList.add(new Reminder(toAdd));
    }

    /**
     * Removes the equivalent reminder from the list.
     *
     * @throws ReminderNotFoundException if no such reminder could be found in the list.
     */
    public boolean remove(ReadOnlyReminder toRemove) throws ReminderNotFoundException {
        requireNonNull(toRemove);
        final boolean reminderFoundAndDeleted = internalList.remove(toRemove);
        if (!reminderFoundAndDeleted) {
            throw new ReminderNotFoundException();
        }
        return reminderFoundAndDeleted;
    }

    /**
     * Returns all properties (collection of values in all entries) in this map as a Set. This set is mutable
     * and change-insulated against the internal list.
     */
    public ArrayList<Reminder> toList() {
        assert CollectionUtil.elementsAreUnique(internalList);
        return new ArrayList<>(internalList);
    }


    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<ReadOnlyReminder> asObservableList() {
        return FXCollections.unmodifiableObservableList(mappedList);
    }

    @Override
    public Iterator<Reminder> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueReminderList // instanceof handles nulls
                && this.internalList.equals(((UniqueReminderList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }
}


