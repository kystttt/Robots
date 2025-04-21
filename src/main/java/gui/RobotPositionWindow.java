package gui;

import game.RobotModel;
import state.WindowAction;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

public class RobotPositionWindow extends JInternalFrame implements WindowAction, PropertyChangeListener {
    private final JTextArea textArea;

    public RobotPositionWindow(RobotModel model) {
        super("Информация", true, true, true, true);
        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        setSize(300, 200);
        setLocation(100, 100);
        setVisible(true);
        pack();

        model.addPropertyChangeListener(this);
        updateText(model.getX(), model.getY());
    }

    private void updateText(double x, double y) {
        textArea.setText(String.format("Координаты робота:\nX: %.2f\nY: %.2f", x, y));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("position".equals(evt.getPropertyName())) {
            double[] newPos = (double[]) evt.getNewValue();
            updateText(newPos[0], newPos[1]);
        }
    }

    @Override
    public String getNameOfWindow() {
        return "RobotPositionWindow";
    }

    @Override
    public Map<String, Integer> saveWindowState() {
        Map<String, Integer> state = new HashMap<>();
        state.put("x", getX());
        state.put("y", getY());
        state.put("width", getWidth());
        state.put("height", getHeight());
        return state;
    }

    @Override
    public void loadWindowState(Map<String, Integer> params) {
        int x = params.getOrDefault("x", 100);
        int y = params.getOrDefault("y", 100);
        int width = params.getOrDefault("width", 300);
        int height = params.getOrDefault("height", 200);
        setBounds(x, y, width, height);
    }
}
