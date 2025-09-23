package model;

import java.awt.*;
import java.awt.geom.AffineTransform;


/**
 * Класс с роботом, который мы будем загружать из jar -файла
 */
public class CustomRobot implements ExternalModelRobot, ExternalRobotGui {
    @Override
    public double getMaxSpeed() { return 0.15; }

    @Override
    public void drawRobot(Graphics2D g, int x, int y, double direction) {
        AffineTransform oldTransform = g.getTransform();
        g.rotate(direction, x, y);
        g.setColor(new Color(240, 165, 0)); // Оранжевый
        g.fillRect(x - 20, y - 10, 40, 20);
        g.setColor(Color.BLUE);
        g.fillOval(x + 15, y - 3, 6, 6);
        g.setColor(Color.BLACK);
        g.fillOval(x - 22, y - 8, 12, 12);
        g.fillOval(x - 22, y + 2, 12, 12);
        g.fillOval(x + 15, y - 8, 12, 12);
        g.fillOval(x + 15, y + 2, 12, 12);
        g.setTransform(oldTransform);
    }
}