package seedu.address.ui;

import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.address.commons.core.Config;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.ui.ExitAppRequestEvent;
import seedu.address.commons.events.ui.ShowHelpRequestEvent;
import seedu.address.commons.events.ui.SwitchToContactsListEvent;
import seedu.address.commons.events.ui.SwitchToEventsListEvent;
import seedu.address.commons.util.FxViewUtil;
import seedu.address.logic.Logic;
import seedu.address.model.UserPrefs;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Region> {

    private static final String ICON = "/images/address_book_32.png";
    private static final String FXML = "MainWindow.fxml";
    private static final int MIN_HEIGHT = 600;
    private static final int MIN_WIDTH = 600;

    private final Logger logger = LogsCenter.getLogger(this.getClass());

    private Stage primaryStage;
    private Logic logic;
    private Config config;
    private UserPrefs prefs;

    // Independent Ui parts residing in this Ui container
    private PersonListPanel personListPanel;
    private EventListPanel eventListPanel;
    private BrowserPanel dataDetailsPanel;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private StackPane commandBoxPlaceholder;
    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane dataListPanelPlaceholder;
    @FXML
    private StackPane dataDetailsPanelPlaceholder;

    @FXML
    private StackPane statusBarPlaceholder;

    public MainWindow(Stage primaryStage, Config config, UserPrefs prefs, Logic logic) {
        super(FXML);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;
        this.config = config;
        this.prefs = prefs;

        // Configure the UI
        setTitle(config.getAppTitle());
        setIcon(ICON);
        setWindowMinSize();
        setWindowDefaultSize(prefs);
        Scene scene = new Scene(getRoot());
        primaryStage.setScene(scene);

        setAccelerators();
        registerAsAnEventHandler(this);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in later version of SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, and
         * ResultDisplay contains a TextArea, thus some accelerators (e.g F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add the following event filter to capture such key events and open
         * help window purposely so to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        CommandBox commandBox = new CommandBox(logic);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());

        ResultDisplay resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        personListPanel = new PersonListPanel(logic.getFilteredPersonList());
        eventListPanel = new EventListPanel(logic.getFilteredEventList());
        dataListPanelPlaceholder.getChildren().add(personListPanel.getRoot());

        dataDetailsPanel = new BrowserPanel();
        dataDetailsPanelPlaceholder.getChildren().add(dataDetailsPanel.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(prefs.getAddressBookFilePath(),
                                                              logic.getFilteredPersonList().size(),
                                                              logic.getFilteredEventList().size());
        statusBarPlaceholder.getChildren().add(statusBarFooter.getRoot());
    }

    void hide() {
        primaryStage.hide();
    }

    private void setTitle(String appTitle) {
        primaryStage.setTitle(appTitle);
    }

    /**
     * Sets the given image as the icon of the main window.
     * @param iconSource e.g. {@code "/images/help_icon.png"}
     */
    private void setIcon(String iconSource) {
        FxViewUtil.setStageIcon(primaryStage, iconSource);
    }

    /**
     * Sets the default size based on user preferences.
     */
    private void setWindowDefaultSize(UserPrefs prefs) {
        primaryStage.setHeight(prefs.getGuiSettings().getWindowHeight());
        primaryStage.setWidth(prefs.getGuiSettings().getWindowWidth());
        if (prefs.getGuiSettings().getWindowCoordinates() != null) {
            primaryStage.setX(prefs.getGuiSettings().getWindowCoordinates().getX());
            primaryStage.setY(prefs.getGuiSettings().getWindowCoordinates().getY());
        }
    }

    private void setWindowMinSize() {
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setMinWidth(MIN_WIDTH);
    }

    /**
     * Returns the current size and the position of the main Window.
     */
    GuiSettings getCurrentGuiSetting() {
        return new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
    }

    void show() {
        primaryStage.show();
    }

    public PersonListPanel getPersonListPanel() {
        return this.personListPanel;
    }

    public EventListPanel getEventListPanel() {
        return this.eventListPanel;
    }

    void releaseResources() {
        dataDetailsPanel.freeResources();
    }

    /**
     * Take note of the following two methods, which overload each other. The one without parameter is used as the
     * callback when the user clicks on the sidebar button; the other one is used as the subscriber when the user
     * enters some command(s) that raise(s) the corresponding event(s).
     */
    @FXML
    private void handleSwitchToContacts() {
        dataListPanelPlaceholder.getChildren().clear();
        dataListPanelPlaceholder.getChildren().add(personListPanel.getRoot());
    }

    @Subscribe
    public void handleSwitchToContacts(SwitchToContactsListEvent event) {
        dataListPanelPlaceholder.getChildren().clear();
        dataListPanelPlaceholder.getChildren().add(personListPanel.getRoot());
    }

    @FXML
    private void handleSwitchToEvents() {
        dataListPanelPlaceholder.getChildren().clear();
        dataListPanelPlaceholder.getChildren().add(eventListPanel.getRoot());
    }

    @Subscribe
    public void handleSwitchToEvents(SwitchToEventsListEvent event) {
        dataListPanelPlaceholder.getChildren().clear();
        dataListPanelPlaceholder.getChildren().add(eventListPanel.getRoot());
    }

    /**
     * Opens the help window.
     */
    @FXML
    public void handleHelp() {
        HelpWindow helpWindow = new HelpWindow();
        helpWindow.show();
    }

    @Subscribe
    private void handleShowHelpEvent(ShowHelpRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        handleHelp();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        raise(new ExitAppRequestEvent());
    }
}
