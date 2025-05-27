package Controller;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import log.Logger;
import localization.LocaleManager;
import model.RobotModel;
import view.GameVisualizer;

/**
 * Контроллер игры, обрабатывающий клики мыши по игровому полю,
 * по клику мыши устанавливает новую цель для робота
 */
public class GameController extends MouseAdapter {
    private final RobotModel model;

    public GameController(RobotModel model, GameVisualizer view) {
        this.model = model;
        view.addMouseListener(this);
        Timer timer = new Timer(50, e -> {
            model.updateRobotPosition();
        });
        timer.start();
    }


    /**
     * Обрабатывает щелчок мыши и устанавливает координаты новой
     * цели для робота
     * @param e the event to be processed
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        model.setTarget(e.getX(), e.getY());
        Logger.debug(String.format(
                LocaleManager.getInstance().getString("coordinates.changed") + " X:%d, Y:%d" , e.getX(), e.getY()));
    }
}
