package gui;

import game.GameController;
import game.GameVisualizer;
import game.RobotModel;
import localization.LocaleManager;
import state.WindowAction;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends BaseWindow implements WindowAction {
    private final GameVisualizer m_visualizer;
    public final RobotModel model;

    public GameWindow() {
        super(LocaleManager.getInstance().getString("game.title"), 400, 400, 50, 50);
        model = new RobotModel();
        m_visualizer = new GameVisualizer(model);
        new GameController(model, m_visualizer);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    @Override
    public void updateLocale() {
        setTitle(LocaleManager.getInstance().getString("game.title"));
    }

    @Override
    public String getNameOfWindow() {
        return "GameWindow";
    }

    public void updateGame() {
        m_visualizer.repaint();
    }
}
