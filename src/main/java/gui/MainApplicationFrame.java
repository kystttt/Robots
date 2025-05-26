package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.*;

import localization.LocaleChangeListener;
import localization.LocaleManager;
import log.Logger;
import state.WindowAction;
import state.WindowSaver;


public class MainApplicationFrame extends JFrame implements WindowAction, LocaleChangeListener {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final WindowSaver windowSaver = new WindowSaver(new HashMap<>(), new HashSet<>());
    private LogWindow logWindow;
    private GameWindow gameWindow;
    private RobotPositionWindow robotPositionWindow;

    public MainApplicationFrame() {
        LocaleManager.getInstance().addListener(this);
        localizeOptionPaneButtons();
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);
        setContentPane(desktopPane);

        try {
            windowSaver.loadFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        gameWindow = new GameWindow();
        addWindow(gameWindow);
        windowSaver.registerWindow(gameWindow.getNameOfWindow());

        robotPositionWindow = new RobotPositionWindow(gameWindow.model);
        addWindow(robotPositionWindow);
        windowSaver.registerWindow(robotPositionWindow.getNameOfWindow());

        logWindow = createLogWindow();
        addWindow(logWindow);
        windowSaver.registerWindow(logWindow.getNameOfWindow());

        windowSaver.registerWindow(this.getNameOfWindow());
        windowSaver.setWindowParams(this);
        windowSaver.setWindowParams(logWindow);
        windowSaver.setWindowParams(gameWindow);
        windowSaver.setWindowParams(robotPositionWindow);

        setJMenuBar(createMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                quit();
            }
        });
    }


    private void localizeOptionPaneButtons() {
        LocaleManager lm = LocaleManager.getInstance();
        UIManager.put("OptionPane.yesButtonText", lm.getString("dialog.yes"));
        UIManager.put("OptionPane.noButtonText", lm.getString("dialog.no"));
        UIManager.put("OptionPane.cancelButtonText", lm.getString("dialog.cancel"));
    }


    /**
     * Перерисовывает панель соответственно текущей локали
     */
    private void updateMenuBar() {
        setJMenuBar(createMenuBar());
        revalidate();
        repaint();
    }

    @Override
    public void onLocaleChanged() {
        localizeOptionPaneButtons();
        updateMenuBar();
        logWindow.updateLocale();
        gameWindow.updateLocale();
        robotPositionWindow.updateLocale();
        SwingUtilities.updateComponentTreeUI(this);
    }

    @Override
    public Map<String, Integer> saveWindowState() {
        Map<String, Integer> state = new HashMap<>();
        state.put("x", getLocation().x);
        state.put("y", getLocation().y);
        state.put("width", getWidth());
        state.put("height", getHeight());
        state.put("state", getExtendedState());
        return state;
    }

    @Override
    public void loadWindowState(Map<String, Integer> params) {
        if (params != null) {
            Integer x = params.get("x");
            Integer y = params.get("y");
            Integer width = params.get("width");
            Integer height = params.get("height");
            Integer state = params.get("state");

            if (x != null && y != null) {
                setLocation(x, y);
            }
            if (width != null && height != null) {
                setSize(width, height);
            }
            if (state != null) {
                setExtendedState(state);
            }
        }
    }

    @Override
    public String getNameOfWindow() {
        return "MainApplicationFrame";
    }

    private void saveWindowStateBeforeExit() {
        windowSaver.saveWindowParams(this);
        windowSaver.saveWindowParams(logWindow);
        windowSaver.saveWindowParams(gameWindow);
        windowSaver.saveWindowParams(robotPositionWindow);
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug(LocaleManager.getInstance().getString("logger.protocol"));
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(generateLookAndFeelMenu());
        menuBar.add(generateTestMenu());
        menuBar.add(generateDocumentMenu());
        menuBar.add(generateLocalizationMenu());
        return menuBar;
    }

    private JMenu generateLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu(LocaleManager.getInstance().getString("menu.view"));
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext()
                .setAccessibleDescription(LocaleManager.getInstance().getString("menu.view.desc"));
        lookAndFeelMenu.add(createSystemLookAndFeelMenuButton());
        lookAndFeelMenu.add(createCrossPlatformLookAndFeelMenuButton());
        return lookAndFeelMenu;
    }

    private JMenuItem createSystemLookAndFeelMenuButton() {
        JMenuItem systemLookAndFeelMenu = new JMenuItem(LocaleManager.getInstance().getString("menu.view.system"), KeyEvent.VK_S);
        systemLookAndFeelMenu.addActionListener(event -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        return systemLookAndFeelMenu;
    }

    private JMenuItem createCrossPlatformLookAndFeelMenuButton() {
        JMenuItem crossplatformLookAndMenuButton = new JMenuItem(LocaleManager.getInstance().getString("menu.view.universal"), KeyEvent.VK_U);
        crossplatformLookAndMenuButton.addActionListener(event -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        return crossplatformLookAndMenuButton;
    }

    private JMenu generateTestMenu() {
        JMenu testMenu = new JMenu(LocaleManager.getInstance().getString("menu.tests"));
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(LocaleManager.getInstance().getString("menu.tests.desc"));
        testMenu.add(createAddLogMessageButton());
        return testMenu;
    }

    private JMenuItem createAddLogMessageButton() {
        JMenuItem addLogMessageButton = new JMenuItem(LocaleManager.getInstance().getString("menu.tests.addlog"), KeyEvent.VK_L);
        addLogMessageButton.addActionListener(event -> Logger.debug(LocaleManager.getInstance().getString("logger.string")));
        return addLogMessageButton;
    }

    private JMenu generateLocalizationMenu() {
        JMenu langMenu = new JMenu(LocaleManager.getInstance().getString("menu.localization"));

        JMenuItem ru = new JMenuItem("Русский");
        ru.addActionListener(e -> LocaleManager.getInstance().setLocale("ru"));

        JMenuItem en = new JMenuItem("English");
        en.addActionListener(e -> LocaleManager.getInstance().setLocale("en"));

        langMenu.add(ru);
        langMenu.add(en);
        return langMenu;
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            // просто игнор
        }
    }

    private JMenu generateDocumentMenu() {
        JMenu menu = new JMenu(LocaleManager.getInstance().getString("menu.app"));
        menu.setMnemonic(KeyEvent.VK_D);
        menu.add(createQuitButton());
        return menu;
    }

    private JMenuItem createQuitButton() {
        JMenuItem menuItem = new JMenuItem(LocaleManager.getInstance().getString("menu.exit"));
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("quit");
        menuItem.addActionListener(event -> quit());
        return menuItem;
    }

    private void quit() {
        int response = JOptionPane.showConfirmDialog(
                this,
                LocaleManager.getInstance().getString("dialog.exit.confirm"),
                LocaleManager.getInstance().getString("dialog.exit.title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            saveWindowStateBeforeExit();
            LocaleManager.getInstance().saveCurrentLocale();
            try {
                windowSaver.saveToFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
    }
}
