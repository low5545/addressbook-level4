package seedu.address.logic.commands;

import static seedu.address.logic.parser.CliSyntax.PREFIX_CONFIG_TYPE;

import seedu.address.logic.commands.exceptions.CommandException;

/**
 * Customizes the configuration of the application.
 */
public class ConfigCommand extends Command {

    public static final String COMMAND_WORD = "config";
    public static final String COMMAND_ALIAS = "cfg";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Changes the configuration of the application. "
            + "Parameters: "
            + PREFIX_CONFIG_TYPE + "CONFIG_TYPE "
            + "NEW_CONFIG_VALUE\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_CONFIG_TYPE + "set-tag-color "
            + "friends #9381a0";

    public static final String MESSAGE_SUCCESS = "Configuration changed: %1$s";

    private String configType;
    private String configValue;

    public ConfigCommand(String configType, String configValue) {
        this.configType = configType;
        this.configValue = configValue;
    }

    @Override
    public CommandResult execute() throws CommandException {
        return null;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ConfigCommand // instanceof handles nulls
                && configType.equals(((ConfigCommand) other).configType)
                && configValue.equals(((ConfigCommand) other).configValue));
    }
}
