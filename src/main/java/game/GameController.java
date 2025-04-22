package game;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Контроллер игры, обрабатывающий клики мыши по игровому полю,
 * по клику мыши устанавливает новую цель для робота
 */
public class GameController extends MouseAdapter {
    private final RobotModel model;

    public GameController(RobotModel model, GameVisualizer view) {
        this.model = model;
        view.addMouseListener(this);
    }

    /**
     * Обрабатывает щелчок мыши и устанавливает координаты новой
     * цели для робота
     * @param e the event to be processed
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        model.setTarget(e.getX(), e.getY());
    }
}
