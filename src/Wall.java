import java.awt.*;

public class Wall {
    private int x, y;
    private int size = Maze.TILE_SIZE;
    private Color color = new Color(0, 0, 200); // Blue color for walls

    public Wall(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, size, size);
        
        // Add some detail to walls
        g.setColor(new Color(0, 0, 150));
        g.fillRect(x + 2, y + 2, size - 4, size - 4);
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
}
