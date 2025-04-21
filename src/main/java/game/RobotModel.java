package game;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class RobotModel {
    private double x = 100;
    private double y = 100;
    private double direction = 0;

    private double targetX = 100;
    private double targetY = 100;

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void updateRobotPosition() {
        double distance = distanceToTarget();
        if (distance < 0.5) {
            return;
        }

        double oldX = x;
        double oldY = y;

        double angleToTarget = Math.atan2(targetY - y, targetX - x);
        double angleDiff = normalizeAngle(angleToTarget - direction);

        if (Math.abs(angleDiff) > 0.1) {
            direction += Math.signum(angleDiff) * 0.05;
        } else {
            double speed = Math.min(2.0, distance);
            x += speed * Math.cos(direction);
            y += speed * Math.sin(direction);
        }

        pcs.firePropertyChange("position", new double[]{oldX, oldY}, new double[]{x, y});
    }

    public void setTarget(int x, int y) {
        double oldTargetX = this.targetX;
        double oldTargetY = this.targetY;
        this.targetX = x;
        this.targetY = y;

        pcs.firePropertyChange("target", new double[]{oldTargetX, oldTargetY}, new double[]{targetX, targetY});
    }

    private double distanceToTarget() {
        double dx = targetX - x;
        double dy = targetY - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private double normalizeAngle(double angle) {
        while (angle < -Math.PI) angle += 2 * Math.PI;
        while (angle > Math.PI) angle -= 2 * Math.PI;
        return angle;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getDirection() {
        return direction;
    }

    public double getTargetX() {
        return targetX;
    }

    public double getTargetY() {
        return targetY;
    }
}
