package game;

import game.RobotModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawRobot(g, model.getX(), model.getY(), model.getDirection());
        drawTarget(g, model.getTargetX(), model.getTargetY());
    }

    private void drawRobot(Graphics g, double x, double y, double direction) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLUE);
        int centerX = (int) x;
        int centerY = (int) y;
        g2d.fillOval(centerX - 10, centerY - 10, 20, 20);
        g2d.setColor(Color.RED);
        g2d.drawLine(centerX, centerY,
                centerX + (int)(15 * Math.cos(direction)),
                centerY + (int)(15 * Math.sin(direction)));
    }

    private void drawTarget(Graphics g, double targetX, double targetY) {
        g.setColor(Color.GREEN);
        int targetCenterX = (int) targetX;
        int targetCenterY = (int) targetY;
        g.fillOval(targetCenterX - 5, targetCenterY - 5, 10, 10);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        repaint();
    }
}
