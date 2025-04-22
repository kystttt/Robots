package game;

import game.RobotModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Рисует робота и точку, после клика мышью
 */
public class GameVisualizer extends JPanel implements PropertyChangeListener {
    private final RobotModel model;

    public GameVisualizer(RobotModel model) {
        this.model = model;
        model.addPropertyChangeListener(this);
        setDoubleBuffered(true);

        Timer timer = new Timer(50, e -> {
            model.updateRobotPosition();
            repaint();
        });
        timer.start();
    }

    /**
     * Рисует текущие координаты робота и точку цели
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawRobot(g, model.getX(), model.getY(), model.getDirection());
        drawTarget(g, model.getTargetX(), model.getTargetY());
    }

    /**
     * Рисует модельку робота и указатель направления
     * @param g
     * @param x
     * @param y
     * @param direction
     */
    private void drawRobot(Graphics g, double x, double y, double direction) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.translate(x, y);
        g2d.rotate(direction);
        g2d.setColor(Color.BLACK);
        g2d.fillOval(-20, -5, 40, 10);
        g2d.setColor(Color.WHITE);
        g2d.fillOval(20 - 2, -2, 4, 4);
        g2d.dispose();
    }

    /**
     * Рисует точку(цель), оставленную игроком
     * @param g
     * @param targetX
     * @param targetY
     */
    private void drawTarget(Graphics g, double targetX, double targetY) {
        g.setColor(Color.GREEN);
        int targetCenterX = (int) targetX;
        int targetCenterY = (int) targetY;

        g.fillOval(targetCenterX - 5, targetCenterY - 5, 10, 10);
    }

    /**
     * Обрабатывает событие изменения свойств модели,
     * перерисовывает модель при изменении
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        repaint();
    }
}