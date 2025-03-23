package gui;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import static java.awt.Frame.ICONIFIED;
import static java.awt.Frame.NORMAL;

public class GameWindow extends JInternalFrame implements WindowAction {
    private final GameVisualizer m_visualizer;
public GameWindow() {
    super("Игровое поле", true, true, true, true);
    m_visualizer = new GameVisualizer();

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(m_visualizer, BorderLayout.CENTER);
    getContentPane().add(panel);
    setSize(400, 400);
    setLocation(50, 50);
    setVisible(true);
    pack();
}

@Override
public Map<String, Integer> saveWindowState() {
    Map<String, Integer> state = new HashMap<>();
    state.put("x", getLocation().x);
    state.put("y", getLocation().y);
    state.put("width", getWidth());
    state.put("height", getHeight());
    if (isIcon()) {
        state.put("state", ICONIFIED);
    } else {
        state.put("state", NORMAL);
    }

    return state;
}

@Override
public void loadWindowState(Map<String, Integer> params) {
    if (params != null) {
        setLocation(params.getOrDefault("x", 50), params.getOrDefault("y", 50));
        setSize(params.getOrDefault("width", 400), params.getOrDefault("height", 400));
        if (params.getOrDefault("state", NORMAL) == ICONIFIED) {
            try {
                setIcon(true);
            } catch (java.beans.PropertyVetoException e) {
                e.printStackTrace();
            }
        }
    }
}

@Override
public String getNameOfWindow() {
    return "GameWindow";
}

public void updateGame() {
    m_visualizer.repaint();
}
 }