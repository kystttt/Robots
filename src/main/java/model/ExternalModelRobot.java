package model;

import java.awt.*;

/**
 * Интерфейс для внешних роботов
 */
public interface ExternalModelRobot {
    double getMaxSpeed();
    void drawRobot(Graphics2D g, int x, int y, double direction);
}
