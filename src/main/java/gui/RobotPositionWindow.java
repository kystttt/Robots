package gui;

import model.RobotModel;
import localization.LocaleManager;
import state.WindowAction;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *Окно с текущей позицией робота(координаты)
 * Реагирует на изменения позиции робота через механизм PropertyChangeListener
 */
public class RobotPositionWindow extends BaseWindow implements WindowAction, PropertyChangeListener {
    private final JTextArea textArea;
    private double lastX;
    private double lastY;

    public RobotPositionWindow(RobotModel model) {
        super(LocaleManager.getInstance().getString("robot.position.format"), 300, 200, 100, 100);
        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        pack();
        model.addPropertyChangeListener(this);
        updateText(model.getX(), model.getY());
        updateText(model.getX(), model.getY());
        lastX = model.getX();
        lastY = model.getY();
    }


    @Override
    public void updateLocale() {
        setTitle(LocaleManager.getInstance().getString("robot.position.format"));
        updateText(lastX, lastY);
        invalidate();
        validate();
        repaint();
    }

    /**
     * Показывает координаты
     * @param x
     * @param y
     */
    private void updateText(double x, double y) {
        textArea.setText(String.format(LocaleManager.getInstance().getString("robot.position.format") + "\nX: %.2f\nY: %.2f", x, y));
    }

    /**
     *Обрабатывает событие изменения координат робота,
     *при получении события с именем "position" обновляет отображение координат
     * @param evt Объект PropertyChangeEvent, описывающий источник события
     *и свойство, которое изменилось.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("position".equals(evt.getPropertyName())) {
            double[] newPos = (double[]) evt.getNewValue();
            lastX = newPos[0];
            lastY = newPos[1];
            updateText(lastX, lastY);
        }
    }

    @Override
    public String getNameOfWindow() {
        return "RobotPositionWindow";
    }
}
