import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.KeyEvent;

public class Pacman {
    private int x, y;
    private final int size = Maze.TILE_SIZE;
    private final int speed = 2;
    public static final int TURN_UP_QUEUE = 8;
    public static final int TURN_LEFT_QUEUE = 4;
    public static final int TURN_RIGHT_QUEUE = 6;
    public static final int TURN_DOWN_QUEUE = 2;

    public int currentDirection = KeyEvent.VK_RIGHT;
    public int turnDirection = 6;
    public boolean willUpdate = true;

    // Mouth animation
    private int mouthAngle = 0; // current mouth half-angle
    private boolean mouthOpening = true;
    private final int mouthSpeed = 4; // degrees per frame
    private final int mouthMaxAngle = 45; // max half-angle

    public Pacman(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public void draw(Graphics g) {
        // animate mouth angle
        if (mouthOpening) {
            mouthAngle += mouthSpeed;
            if (mouthAngle >= mouthMaxAngle) {
                mouthAngle = mouthMaxAngle;
                mouthOpening = false;
            }
        } else {
            mouthAngle -= mouthSpeed;
            if (mouthAngle <= 0) {
                mouthAngle = 0;
                mouthOpening = true;
            }
        }

        g.setColor(Color.YELLOW);

        int startAngle = 0;
        switch (currentDirection) {
            case KeyEvent.VK_RIGHT:
                startAngle = mouthAngle;
                break;
            case KeyEvent.VK_DOWN:
                // corrected: DOWN should point to the bottom (270 degrees)
                startAngle = 270 + mouthAngle;
                break;
            case KeyEvent.VK_LEFT:
                startAngle = 180 + mouthAngle;
                break;
            case KeyEvent.VK_UP:
                // corrected: UP should point to the top (90 degrees)
                startAngle = 90 + mouthAngle;
                break;
        }

        int arcAngle = 360 - mouthAngle * 2;
        g.fillArc(x, y, size, size, startAngle, arcAngle);
    }

    public void move(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_UP -> this.turnDirection = 8;
            case KeyEvent.VK_RIGHT -> this.turnDirection = 6;
            case KeyEvent.VK_DOWN -> this.turnDirection = 2;
            case KeyEvent.VK_LEFT -> this.turnDirection = 4;
            default -> this.turnDirection = this.turnDirection;
        }
    }

    public void setDirection() {
        if (this.turnDirection == TURN_UP_QUEUE) {
            currentDirection = KeyEvent.VK_UP;
        }
        else if (this.turnDirection == TURN_LEFT_QUEUE) {
            currentDirection = KeyEvent.VK_LEFT;
        }
        else if (this.turnDirection == TURN_RIGHT_QUEUE) {
            currentDirection = KeyEvent.VK_RIGHT;
        }
        else if (this.turnDirection == TURN_DOWN_QUEUE) {
            currentDirection = KeyEvent.VK_DOWN;
        }
    }

    public void update() {
        // Determine allowed horizontal range for being 'inside' the maze
        int screenWidth = Maze.COLUMNS * Maze.TILE_SIZE;
        int allowedXMin = 0;
        int allowedXMax = screenWidth - this.size;

        // If Pacman is partially outside (in the tunnel), disallow vertical moves
        if ((currentDirection == KeyEvent.VK_UP || currentDirection == KeyEvent.VK_DOWN)
                && (this.x < allowedXMin || this.x > allowedXMax)) {
            // ignore vertical movement while in tunnel area
        } else {
            switch (currentDirection) {
                case KeyEvent.VK_UP:
                    y -= speed;
                    break;
                case KeyEvent.VK_DOWN:
                    y += speed;
                    break;
            }
        }

        // Horizontal movement always allowed (so tunnel works)
        if (currentDirection == KeyEvent.VK_RIGHT) {
            x += speed;
        } else if (currentDirection == KeyEvent.VK_LEFT) {
            x -= speed;
        }

        // Prevent leaving vertically: clamp Y inside maze bounds
        int maxY = Maze.ROWS * Maze.TILE_SIZE - this.size;
        if (this.y < 0) this.y = 0;
        if (this.y > maxY) this.y = maxY;

        // Teleport wraparound horizontally
        if (this.x > screenWidth) {
            this.x = -this.size;
        } else if (this.x < -this.size) {
            this.x = screenWidth;
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return size; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public int getDirection() { return currentDirection; }
}

