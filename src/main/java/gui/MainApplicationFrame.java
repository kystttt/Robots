package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;

import log.Logger;


public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();

    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);

        setContentPane(desktopPane);


        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);

        setJMenuBar(createMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                quit();
            }
        });
    }

    /**
     * Создает и настраивает окно логов
     *
     * @return возвращает настроенный экземпляр
     */
    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);//300
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    /**
     * Добавляет окно
     *
     *
     * @param frame
     */
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

    /**
     * Выпадающая кнопка "Режим отображения" на основной полоске приложения
     *
     * @return возвращает готовый экземпляр
     */
    private JMenu generateLookAndFeelMenu() {
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext()
                .setAccessibleDescription(
                        "Управление режимом отображения приложения");
        lookAndFeelMenu.add(createSystemLookAndFeelMenuButton());
        lookAndFeelMenu.add(createCrossPlatformLookAndFeelMenuButton());
        return lookAndFeelMenu;
    }


    /**
     * Создает кнопку "Системная схема" в менюшке
     * "управление режима отображения", которая меняет оформление
     * приложения на системное
     * @return возвращает готовый экземпляр
     */
    private JMenuItem createSystemLookAndFeelMenuButton() {
        JMenuItem systemLookAndFeelMenu = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeelMenu.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        return systemLookAndFeelMenu;
    }


    /**
     *Создает кнопку "Универсальная схема" в менюшке
     * "управление режима отображения", которая меняет оформление
     * приложения на белое
     * @return возвращает готовый экземпляр
     */
    private JMenuItem createCrossPlatformLookAndFeelMenuButton() {
        JMenuItem crossplatformLookAndMenuButton = new JMenuItem(
                "Универсальная схема", KeyEvent.VK_S);
        crossplatformLookAndMenuButton.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        return crossplatformLookAndMenuButton;
    }


    /**
     * Создает выпадающую кнопку "Тесты" в основном баре
     * приложения
     * @return готовый экземпляр
     */
    private JMenu generateTestMenu(){
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");
        testMenu.add(createAddLogMessageButton());
        return testMenu;
    }


    /**
     * Создает кнопку "Сообщение в лог" в менюшке "Тесты"
     * @return готовый экземпляр
     */
    private JMenuItem createAddLogMessageButton(){
        JMenuItem addLogMessageButton = new JMenuItem(
                "Сообщение в лог", KeyEvent.VK_S);
        addLogMessageButton.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        return addLogMessageButton;
    }


    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            // just ignore
        }
    }

    private JMenu generateDocumentMenu(){
        JMenu menu = new JMenu("Приложение");
        menu.setMnemonic(KeyEvent.VK_D);
        menu.add(createQuitButton());
        return menu;
    }


    private JMenuItem createQuitButton(){
        JMenuItem menuItem = new JMenuItem("Выход");
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("quit");

        menuItem.addActionListener((event) -> quit());

        return menuItem;
    }

    /**
     * Окошко подтверждения выхода и закрытия приложения
     */
    private void quit() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
