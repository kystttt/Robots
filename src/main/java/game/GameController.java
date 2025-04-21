package game;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameController extends MouseAdapter {
    private final RobotModel model;

    public GameController(RobotModel model, GameVisualizer view) {
        this.model = model;
        view.addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        model.setTarget(e.getX(), e.getY());
    }
}
