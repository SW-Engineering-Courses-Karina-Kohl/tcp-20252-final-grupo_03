import java.awt.*;
import java.util.Random;

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
        
        // Pupils
        g.setColor(Color.BLUE);
        int pupilOffset = 0;
        switch (direction) {
            case 0: // Right
                pupilOffset = 2;
                break;
            case 90: // Down
                pupilOffset = 2;
                break;
            case 180: // Left
                pupilOffset = -2;
                break;
            case 270: // Up
                pupilOffset = -2;
                break;
        }
        g.fillOval(x + size/4 + pupilOffset, y + size/4 + pupilOffset, size/8, size/8);
        g.fillOval(x + size/2 + pupilOffset, y + size/4 + pupilOffset, size/8, size/8);
    }
    
    public void move() {
        // Random movement for now - can be improved with pathfinding
        if (random.nextInt(10) == 0) {
            direction = random.nextInt(4) * 90; // Change direction randomly
        }
        
        switch (direction) {
            case 0: // Right
                x += speed;
                break;
            case 90: // Down
                y += speed;
                break;
            case 180: // Left
                x -= speed;
                break;
            case 270: // Up
                y -= speed;
                break;
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
