package gui;

import game.GameController;
import game.GameVisualizer;
import localization.LocaleManager;
import model.RobotModel;
import state.WindowAction;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class GameWindow extends BaseWindow implements WindowAction, PropertyChangeListener {
    private final GameVisualizer m_visualizer;
    public final RobotModel model;

    public GameWindow() {
        super(LocaleManager.getInstance().getString("game.window"), 400, 400, 50, 50);
        addPropertyChangeListener(this);
        LocaleManager.getInstance().addPropertyChangeListener(this);
        model = new RobotModel();
        m_visualizer = new GameVisualizer(model);
        new GameController(model, m_visualizer);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        LocaleManager.getInstance().addPropertyChangeListener(this);
    }

    @Override
    public String getNameOfWindow() {
        return "GameWindow";
    }
    public void updateGame() {
        m_visualizer.repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("locale".equals(evt.getPropertyName())) {
            setTitle(LocaleManager.getInstance().getString("game.window"));
        }
    }
}
