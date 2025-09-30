package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import game.RobotLoader;
import javax.swing.*;

import localization.LocaleManager;
import log.Logger;
import model.ExternalModelRobot;
import model.ExternalRobotGui;
import model.RobotModel;
import state.WindowAction;
import state.WindowSaver;

public class MainApplicationFrame extends JFrame implements WindowAction {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final WindowSaver windowSaver = new WindowSaver(new HashMap<>(), new HashSet<>());
    private LogWindow logWindow;
    private GameWindow gameWindow;
    private RobotPositionWindow robotPositionWindow;
    private final RobotLoader robotLoader = new RobotLoader();

    public MainApplicationFrame() {
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

    @Override
    public Map<String, Integer> saveWindowState() {
        Map<String, Integer> state = new HashMap<>();
        state.put("x", (getLocation().x));
        state.put("y", getLocation().y);
        state.put("width", getWidth());
        state.put("height", getHeight());
        state.put("state", getExtendedState());
        state.put("locale", LocaleManager.getInstance().getCurrentLocale().getLanguage().equals("ru") ? 0 : 1);
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
            int intLocale = params.getOrDefault("locale", 0);
            Locale newLoc = (intLocale == 0) ? Locale.of("ru") : Locale.of("en");
            LocaleManager.getInstance().setCurrentLocale(newLoc);
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
        Logger.debug(LocaleManager.getInstance().getString("log.window.first.text"));
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
        menuBar.add(switchLanguageMenu());
        menuBar.add(createRobotMenu());
        return menuBar;
    }

    private JMenu generateLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu(LocaleManager.getInstance().getString("button1"));
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(LocaleManager.getInstance().getString("title.look.feel.menu"));
        lookAndFeelMenu.add(createSystemLookAndFeelMenuButton());
        lookAndFeelMenu.add(createCrossPlatformLookAndFeelMenuButton());
        return lookAndFeelMenu;
    }

    /**
     * Создает меню загрузки роботов
     */
    private JMenu createRobotMenu(){
        JMenu robotMenu = new JMenu(LocaleManager.getInstance().getString("button5"));
        JMenuItem loadRobotItem = new JMenuItem(LocaleManager.getInstance().getString("button5.first"));
        loadRobotItem.addActionListener(e->loadExternalRobot());
        robotMenu.add(loadRobotItem);
        return robotMenu;
    }

    /**
     * Загружает внешнюю реализацию робота
     */
    private void loadExternalRobot(){
        JFileChooser chooser = robotLoader.createJarFileChooser(
                LocaleManager.getInstance().getString("button5.second")
        );
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try{
                File file = chooser.getSelectedFile();
                ExternalModelRobot robotModel = robotLoader.loadRobotFromJarModel(file, "custom.CustomRobotModel");
                ExternalRobotGui robotGui = robotLoader.loadRobotFromJarGui(file, "custom.CustomRobotView");
                gameWindow.model.setExternalModelRobot(robotModel);
                gameWindow.getVisualizer().setExternalRobot(robotGui);
                System.out.println("Model class    : " + robotModel.getClass().getName());
                var mcs = robotModel.getClass().getProtectionDomain().getCodeSource();
                System.out.println("Model location : " + (mcs != null ? mcs.getLocation() : "unknown"));
                System.out.println("Model CL       : " + robotModel.getClass().getClassLoader());

                System.out.println("GUI class      : " + robotGui.getClass().getName());
                var gcs = robotGui.getClass().getProtectionDomain().getCodeSource();
                System.out.println("GUI location   : " + (gcs != null ? gcs.getLocation() : "unknown"));
                System.out.println("GUI CL         : " + robotGui.getClass().getClassLoader());
                showSucсessMessage();
            } catch (Exception ex){
                Logger.error("Failed to load: " + ex.getMessage());
                showErrorMessage();
            }
        }
        repaint();
    }

    /**
     * Сообщение об успешной загрузке
     */
    private void showSucсessMessage(){
        JOptionPane.showMessageDialog(this,
                LocaleManager.getInstance().getString("load.success"),
                LocaleManager.getInstance().getString("load.loading"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Сообщение об ошибке при загрузке
     */
    private void showErrorMessage(){
        JOptionPane.showMessageDialog(this,
                LocaleManager.getInstance().getString("load.error"),
                LocaleManager.getInstance().getString("load.loading"),
                JOptionPane.ERROR_MESSAGE);
    }

    private JMenuItem createSystemLookAndFeelMenuButton() {
        JMenuItem systemLookAndFeelMenu = new JMenuItem(LocaleManager.getInstance().getString("button1.first"), KeyEvent.VK_S);
        systemLookAndFeelMenu.addActionListener(event -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        return systemLookAndFeelMenu;
    }

    private JMenuItem createCrossPlatformLookAndFeelMenuButton() {
        JMenuItem crossplatformLookAndMenuButton = new JMenuItem(LocaleManager.getInstance().getString("button1.second"), KeyEvent.VK_U);
        crossplatformLookAndMenuButton.addActionListener(event -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        return crossplatformLookAndMenuButton;
    }

    private JMenu generateTestMenu() {
        JMenu testMenu = new JMenu(LocaleManager.getInstance().getString("button2"));
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(LocaleManager.getInstance().getString("title.log"));
        testMenu.add(createAddLogMessageButton());
        return testMenu;
    }

    private JMenuItem createAddLogMessageButton() {
        JMenuItem addLogMessageButton = new JMenuItem(LocaleManager.getInstance().getString("button2.first"), KeyEvent.VK_L);
        addLogMessageButton.addActionListener(event -> Logger.debug(LocaleManager.getInstance().getString("log.window.print")));
        return addLogMessageButton;
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
        JMenu menu = new JMenu(LocaleManager.getInstance().getString("button3"));
        menu.setMnemonic(KeyEvent.VK_D);
        menu.add(createQuitButton());
        return menu;
    }

    private JMenu switchLanguageMenu(){
        JMenu switchMenu = new JMenu(LocaleManager.getInstance().getString("button4"));
        switchMenu.setMnemonic(KeyEvent.VK_L);
        switchMenu.add(switchLanguageButtonRu());
        switchMenu.add(switchLanguageButtonEn());
        return switchMenu;
    }
    /**
     * Кнопка переключения локали на Ru
     */
    private JMenuItem switchLanguageButtonRu(){
        JMenuItem languageRu = new JMenuItem("Русский");
        languageRu.addActionListener(e -> switchLanguage(Locale.of("ru")));
        return languageRu;
    }

    /**
     * Кнопка переключения локали на En
     */
    private JMenuItem switchLanguageButtonEn(){
        JMenuItem languageEn = new JMenuItem("English");
        languageEn.addActionListener(e -> switchLanguage(Locale.of("en")));
        return languageEn;
    }

    /**
     * Перерисовывает наше окошко после смены лангуаге))) и меняет локаль
     * @param locale новая локаль
     */
    private void switchLanguage(Locale locale) {
        LocaleManager.getInstance().setCurrentLocale(locale);
        UIManager.put("OptionPane.yesButtonText", LocaleManager.getInstance().getString("yes"));
        UIManager.put("OptionPane.noButtonText", LocaleManager.getInstance().getString("no"));
        setJMenuBar(createMenuBar());
        revalidate();
        repaint();
    }

    private JMenuItem createQuitButton() {
        JMenuItem menuItem = new JMenuItem(LocaleManager.getInstance().getString("button3.first"));
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("quit");
        menuItem.addActionListener(event -> quit());
        return menuItem;
    }

    private void quit() {
        int response = JOptionPane.showConfirmDialog(
                this,
                LocaleManager.getInstance().getString("exit.window.title"),
                LocaleManager.getInstance().getString("exit.confirm"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            saveWindowStateBeforeExit();
            try {
                windowSaver.saveToFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
    }
}
