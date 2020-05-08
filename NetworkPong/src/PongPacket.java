import java.awt.*;
import java.io.Serializable;

public class PongPacket implements Serializable
{
    // TODO determine which ball/delta/paddle information to send
    Point delta, paddleLeft, paddleRight;

    PongPacket(Point delta, Point paddleLeft, Point paddleRight)
    {
        this.delta = new Point(delta);
        this.paddleLeft = new Point(paddleLeft);
        this.paddleRight = new Point(paddleRight);
    }

    @Override
    public String toString() {
        return "PongPacket{" +
                "delta=" + delta +
                ", paddleLeft=" + paddleLeft +
                ", paddleRight=" + paddleRight +
                '}';
    }
}