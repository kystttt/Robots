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

import log.Logger;
import state.WindowAction;
import state.WindowSaver;

public class MainApplicationFrame extends JFrame implements WindowAction {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final WindowSaver windowSaver = new WindowSaver(new HashMap<>(), new HashSet<>());
    private LogWindow logWindow;
    private GameWindow gameWindow;

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

        logWindow = createLogWindow();
        addWindow(logWindow);
        windowSaver.registerWindow(logWindow.getNameOfWindow());

        gameWindow = new GameWindow();
        addWindow(gameWindow);
        windowSaver.registerWindow(gameWindow.getNameOfWindow());

        windowSaver.registerWindow(this.getNameOfWindow());
        loadWindowState(windowSaver.getWindowParams());

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
        windowSaver.setWindowParams(logWindow);
        windowSaver.setWindowParams(gameWindow);
    }

    @Override
    public String getNameOfWindow() {
        return "MainApplicationFrame";
    }

    private void saveWindowStateBeforeExit() {
        windowSaver.saveWindowParams(this);
        windowSaver.saveWindowParams(logWindow);
        windowSaver.saveWindowParams(gameWindow);
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
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

        return menuBar;
    }

    private JMenu generateLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription("Управление режимом отображения приложения");
        lookAndFeelMenu.add(createSystemLookAndFeelMenuButton());
        lookAndFeelMenu.add(createCrossPlatformLookAndFeelMenuButton());
        return lookAndFeelMenu;
    }

    private JMenuItem createSystemLookAndFeelMenuButton() {
        JMenuItem systemLookAndFeelMenu = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeelMenu.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        return systemLookAndFeelMenu;
    }

    private JMenuItem createCrossPlatformLookAndFeelMenuButton() {
        JMenuItem crossplatformLookAndMenuButton = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        crossplatformLookAndMenuButton.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        return crossplatformLookAndMenuButton;
    }

    private JMenu generateTestMenu() {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription("Тестовые команды");
        testMenu.add(createAddLogMessageButton());
        return testMenu;
    }

    private JMenuItem createAddLogMessageButton() {
        JMenuItem addLogMessageButton = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageButton.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        return addLogMessageButton;
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // just ignore
        }
    }

    private JMenu generateDocumentMenu() {
        JMenu menu = new JMenu("Приложение");
        menu.setMnemonic(KeyEvent.VK_D);
        menu.add(createQuitButton());
        return menu;
    }

    private JMenuItem createQuitButton() {
        JMenuItem menuItem = new JMenuItem("Выход");
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("quit");

        menuItem.addActionListener((event) -> quit());

        return menuItem;
    }

    private void quit() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit?",
                "Confirm Exit",
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