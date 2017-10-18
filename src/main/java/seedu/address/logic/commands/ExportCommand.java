package seedu.address.logic.commands;

import java.io.IOException;

import seedu.address.logic.commands.exceptions.CommandException;

/**
 * Exports the address book to a user defined location {@code filePath}
 */
public class ExportCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "export";
    public static final String COMMAND_ALIAS = "p";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Exports the address book data to the location defined by the file path. Existing file data will be "
            + "overwritten by the current data in the address book.\n"
            + "Parameters: FILEPATH (must end with an extension of .xml)\n"
            + "Example:\n"
            + COMMAND_WORD + " C:\\Users\\John Doe\\Documents\\addressbook.xml (Windows)\n"
            + COMMAND_WORD + " /Users/John Doe/Documents/addressbook.xml (Unix)\n";

    public static final String MESSAGE_EXPORT_SUCCESS = "Exported address book data to: %1$s";
    public static final String MESSAGE_NOT_XML_FILE = "The file path does not point to an XML file.";
    public static final String MESSAGE_PROBLEM_WRITING_FILE = "There is a problem exporting to the specified file path.\n"
            + "Make sure the XML file name does not contain any punctuations or special characters.";

    private final String filePath;

    public ExportCommand(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {

        try {
            storage.saveAddressBook(model.getAddressBook(), filePath);
            return new CommandResult(String.format(MESSAGE_EXPORT_SUCCESS, filePath));
        } catch (IOException e) {
            throw new CommandException(MESSAGE_PROBLEM_WRITING_FILE);
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ExportCommand // instanceof handles nulls
                && this.filePath.equals(((ExportCommand) other).filePath)); // state check
    }
}