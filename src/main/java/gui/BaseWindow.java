package gui;

import state.WindowAction;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

import static java.awt.Frame.ICONIFIED;
import static java.awt.Frame.NORMAL;

/**
 * Абстрактный класс, который реализует методы: saveWindowState, loadWindowSave интерфейса WindowAction, избавляясь от дублирования кода в
 * классах LogWindow, GameWindow, RobotPositionWindow
 */
public abstract class BaseWindow extends JInternalFrame implements WindowAction {
    public BaseWindow(String title, int width, int height, int defaultX, int defaultY) {
        super(title, true, true, true, true);
        setSize(width, height);
        setLocation(defaultX, defaultY);
        setVisible(true);
    }

    /**
     * Сохраняет текущее состояние окна
     * @return мапу с параметрами окна
     */
    @Override
    public Map<String, Integer> saveWindowState() {
        Map<String, Integer> state = new HashMap<>();
        state.put("x", getX());
        state.put("y", getY());
        state.put("width", getWidth());
        state.put("height", getHeight());
        state.put("state", isIcon() ? ICONIFIED : NORMAL);
        return state;
    }

    /**
     * Загружает состояние окна из переданной мапы
     * @param params
     */
    @Override
    public void loadWindowState(Map<String, Integer> params) {
        if (params != null) {
            int x = params.getOrDefault("x", 50);
            int y = params.getOrDefault("y", 50);
            int width = params.getOrDefault("width", 200);
            int height = params.getOrDefault("height", 300);
            setBounds(x, y, width, height);
            if (params.getOrDefault("state", NORMAL) == ICONIFIED) {
                try {
                    setIcon(true);
                } catch (java.beans.PropertyVetoException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}