import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Eliezer Meth
 * @version 1
 * Start Date: 02.23.2020
 * Last Modified: 04.30.2020
 */

public class PongWindow extends JFrame
{
    // Window size
    private final int WINDOW_WIDTH = 500;
    private final int WINDOW_HEIGHT = 400;

    // Paddle and ball info
    private final int PADDLE_WIDTH = 10;
    private final int PADDLE_HEIGHT = 40;
    private final int PADDLE_SPEED = 5;
    private final int BALL_DIAMETER = 10;
    private int ball_dx = 2; // ball delta for change in direction and speed
    private int ball_dy = 2; // ball delta for change in direction and speed

    // Ball and paddle Point class initializer
    private Point ball = new Point((WINDOW_WIDTH / 2), (WINDOW_HEIGHT / 2));
    private Point paddleLeft = new Point(0, ((WINDOW_HEIGHT / 2) - (PADDLE_HEIGHT / 2)));
    private Point paddleRight = new Point((WINDOW_WIDTH - PADDLE_WIDTH), ((WINDOW_HEIGHT / 2) - (PADDLE_HEIGHT / 2)));

    private GamePanel gamePanel = new GamePanel();
    private int leftScore = 0;
    private int rightScore = 0;

    public PongWindow()
    {
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        Timer ballUpdater = new Timer(40, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ball.translate(ball_dx, ball_dy);
                if (ball.x <= PADDLE_WIDTH || ball.x >= (WINDOW_WIDTH - PADDLE_WIDTH - BALL_DIAMETER)) // going out of x bounds (W-E)
                    checkBounce(); // separate function to check if paddle there to bounce off of
                if (ball.y < 0 || ball.y > (WINDOW_HEIGHT - BALL_DIAMETER)) // going out of y bounds (N-S)
                    ball_dy = -ball_dy;
                try // localized repaint
                {
                    Point panelD = gamePanel.getLocationOnScreen();
                    Point ballAbs = ball.getLocation();
                    ballAbs.translate(panelD.x, panelD.y);
                    revalidate();
                    repaint(ballAbs.x - BALL_DIAMETER, ballAbs.y - BALL_DIAMETER,
                            BALL_DIAMETER * (Math.abs(ball_dx) + 1), BALL_DIAMETER * (Math.abs(2) + 1));
                }
                catch (IllegalComponentStateException b) // if component not showing on screen to determine location
                {
                    // will only occur once at beginning of game
                    revalidate();
                    repaint(); // full repaint
                }
            }
        });

        ballUpdater.start();

        setContentPane(gamePanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
        requestFocus();

        //TODO revalidate method?
        revalidate(); // updates location of components in GUI
        repaint();
    }

    private void checkBounce()
    {
        // Create paddle variable
        Point checkPaddle;
        if (ball.x <= PADDLE_WIDTH) // left side
            checkPaddle = paddleLeft;
        else // right side
            checkPaddle = paddleRight;

        // check paddle against top of ball, then against bottom of ball
        if ((checkPaddle.y <= ball.y && (checkPaddle.y + PADDLE_HEIGHT) >= ball.y) || // point between two coordinates
                (checkPaddle.y <= (ball.y + BALL_DIAMETER) && (checkPaddle.y + PADDLE_HEIGHT) >= (ball.y + BALL_DIAMETER)))
            ball_dx = -ball_dx; // bounce off paddle
        else // paddle missed ball
        {
            if (checkPaddle == paddleLeft)
                rightScore++; // increment right score
            else
                leftScore++;

            // Pause game for one second
            try {
                TimeUnit.SECONDS.sleep(1);
            }
            catch (InterruptedException e) {
                System.out.println(e + " at score " + leftScore + ":" + rightScore);
            }

            // reset ball
            ball.x = (WINDOW_WIDTH / 2) - (BALL_DIAMETER / 2);
            ball.y = (WINDOW_HEIGHT / 2) - (BALL_DIAMETER / 2);
            // reset deltas

            repaint(); // repaint full screen
        }
    }

    // -------------------------------------------
    // public methods for networked game
    public PongPacket getPacket() { return new PongPacket(new Point(ball_dx, ball_dy), paddleLeft, paddleRight); }

    public void setFromPacket(PongPacket packet)
    {
        ball_dx = packet.delta.x;
        ball_dy = packet.delta.y;
        paddleLeft = packet.paddleLeft;
        paddleRight = packet.paddleRight;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // classes within PongWindow to access variables
    class GamePanel extends JPanel
    {
        GamePanel()
        {
            setBackground(Color.DARK_GRAY);
            setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

            setFocusable(true); // to read keyboard

            //  --------------- for left paddle -----------------
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    // determine key, then move paddle up/down
                    int key = e.getKeyCode();
                    // up and down are not mutually exclusive
                    if (key == KeyEvent.VK_UP && paddleLeft.y > 0) // restrict to board
                        paddleLeft.translate(0, -PADDLE_SPEED);
                    if (key == KeyEvent.VK_DOWN && paddleLeft.y < (WINDOW_HEIGHT - PADDLE_HEIGHT)) // restrict to board
                        paddleLeft.translate(0, PADDLE_SPEED);

                    repaint();
                }
            });

            // --------------- for right paddle -----------------
            addMouseWheelListener(new MouseWheelListener() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    int dir = e.getWheelRotation(); // either 1 or -1
                    // restrict to board
                    if (dir == 1 && paddleRight.y < (WINDOW_HEIGHT - PADDLE_HEIGHT)) // up
                        paddleRight.translate(0, PADDLE_SPEED);
                    else if (paddleRight.y > 0) // -1 for down; need condition
                        paddleRight.translate(0, -PADDLE_SPEED);
                    repaint();
                }
            });
        }

        // paint components onto screen
        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g; //  cast for dotted middle line

            // Ball
            g.setColor(Color.WHITE);
            g.fillOval(ball.x, ball.y, BALL_DIAMETER, BALL_DIAMETER);

            // Paddles
            g.setColor(Color.RED);
            g.fillRect(paddleLeft.x, paddleLeft.y, PADDLE_WIDTH, PADDLE_HEIGHT); // left paddle
            g.fillRect(paddleRight.x, paddleRight.y, PADDLE_WIDTH, PADDLE_HEIGHT); // left paddle

            // Outside boundary
            g.setColor(Color.YELLOW);
            g.drawRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

            // Dotted middle line
            float[] dashingPattern = {2f, 2f};
            Stroke stroke = new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    1.0f, dashingPattern, 2.0f);
            g2.setStroke(stroke);
            g2.drawLine((WINDOW_WIDTH / 2), 0, (WINDOW_WIDTH / 2), WINDOW_HEIGHT);

            // Scores
            Font f = g.getFont();
            g.setFont(new Font(f.getFontName(), Font.PLAIN, (f.getSize() * 4)));
            g.drawString(Integer.toString(leftScore), ((WINDOW_WIDTH / 2) - 50), 50);
            g.drawString(Integer.toString(rightScore), ((WINDOW_WIDTH / 2) + 50), 50);
        }
    }
}
// fix ball that stays in middle
// localized repaint for paddles
// is revalidate necessary?
