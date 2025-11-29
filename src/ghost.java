import java.awt.*;
import java.util.Random;
import java.util.List;
import java.awt.Rectangle;

public class Ghost {
    private int x, y;
    private int size = Maze.TILE_SIZE;
    private int speed = 1;
    private Color color;
    private int direction;
    private Random random = new Random();
    
    // Ghost colors
    public static final Color BLINKY_COLOR = Color.RED;
    public static final Color PINKY_COLOR = Color.PINK;
    public static final Color INKY_COLOR = Color.CYAN;
    public static final Color CLYDE_COLOR = Color.ORANGE;

    public Ghost(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
        // Start with a random direction
        direction = random.nextInt(4) * 90; // 0, 90, 180, 270
    }

    public void draw(Graphics g) {
        // Ghost body
        g.setColor(color);
        g.fillRoundRect(x, y, size, size, size/2, size/2);
        
        // Ghost bottom wavy part
        g.fillRect(x, y + size/2, size, size/2);
        
        // Eyes
        g.setColor(Color.WHITE);
        g.fillOval(x + size/4, y + size/4, size/4, size/4);
        g.fillOval(x + size/2, y + size/4, size/4, size/4);
        
        // Pupils: compute centers and constrain inside the white eye areas
        g.setColor(Color.BLUE);
        int eyeW = size / 4;
        int eyeH = size / 4;
        int leftEyeX = x + size / 4;
        int rightEyeX = x + size / 2;
        int eyeY = y + size / 4;

        int leftCenterX = leftEyeX + eyeW / 2;
        int rightCenterX = rightEyeX + eyeW / 2;
        int centerY = eyeY + eyeH / 2;

        int pupilSize = size / 8;
        int pr = pupilSize / 2; // pupil radius
        int offset = Math.max(1, size / 16); // how far the pupil moves

        int pupilLeftX = leftCenterX;
        int pupilLeftY = centerY;
        int pupilRightX = rightCenterX;
        int pupilRightY = centerY;

        switch (direction) {
            case 0: // Right
                pupilLeftX += offset;
                pupilRightX += offset;
                break;
            case 90: // Down
                pupilLeftY += offset;
                pupilRightY += offset;
                break;
            case 180: // Left
                pupilLeftX -= offset;
                pupilRightX -= offset;
                break;
            case 270: // Up
                pupilLeftY -= offset;
                pupilRightY -= offset;
                break;
        }

        // Clamp pupils so they never leave the white eye area
        int leftMinX = leftEyeX + pr;
        int leftMaxX = leftEyeX + eyeW - pr;
        int rightMinX = rightEyeX + pr;
        int rightMaxX = rightEyeX + eyeW - pr;
        int minY = eyeY + pr;
        int maxY = eyeY + eyeH - pr;

        pupilLeftX = Math.max(leftMinX, Math.min(pupilLeftX, leftMaxX));
        pupilLeftY = Math.max(minY, Math.min(pupilLeftY, maxY));
        pupilRightX = Math.max(rightMinX, Math.min(pupilRightX, rightMaxX));
        pupilRightY = Math.max(minY, Math.min(pupilRightY, maxY));

        g.fillOval(pupilLeftX - pr, pupilLeftY - pr, pupilSize, pupilSize);
        g.fillOval(pupilRightX - pr, pupilRightY - pr, pupilSize, pupilSize);
    }
    
    public void move(List<Wall> walls) {
        // Random movement for now - can be improved with pathfinding
        if (random.nextInt(10) == 0) {
            direction = random.nextInt(4) * 90; // Change direction randomly
        }

        // Attempt movement separately on X and Y so ghosts don't clip corners
        int dx = 0, dy = 0;
        switch (direction) {
            case 0: // Right
                dx = speed;
                break;
            case 90: // Down
                dy = speed;
                break;
            case 180: // Left
                dx = -speed;
                break;
            case 270: // Up
                dy = -speed;
                break;
        }

        // Try horizontal move
        if (dx != 0) {
            int newX = x + dx;
            Rectangle ghostRectX = new Rectangle(newX, y, size, size);
            boolean collideX = false;
            for (Wall w : walls) {
                Rectangle wallRect = new Rectangle(w.getX(), w.getY(), w.getSize(), w.getSize());
                if (ghostRectX.intersects(wallRect)) {
                    collideX = true;
                    break;
                }
            }
            if (!collideX) {
                x = newX;
            } else {
                // If blocked, pick a new random direction to avoid sticking
                direction = random.nextInt(4) * 90;
            }
        }

        // Try vertical move
        if (dy != 0) {
            int newY = y + dy;
            Rectangle ghostRectY = new Rectangle(x, newY, size, size);
            boolean collideY = false;
            for (Wall w : walls) {
                Rectangle wallRect = new Rectangle(w.getX(), w.getY(), w.getSize(), w.getSize());
                if (ghostRectY.intersects(wallRect)) {
                    collideY = true;
                    break;
                }
            }
            if (!collideY) {
                y = newY;
            } else {
                direction = random.nextInt(4) * 90;
            }
        }
    }
    
    // Getters for collision detection
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getSize() {
        return size;
    }
    
    // Setters for position adjustment
    public void setX(int x) {
        this.x = x;
    }
    
    public void setY(int y) {
        this.y = y;
    }
}
