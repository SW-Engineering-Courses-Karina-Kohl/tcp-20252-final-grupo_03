import java.awt.*;

public class Pellet {
    private int x, y;
    private int size;
    private Color color = Color.WHITE;
    private boolean isPowerPellet = false;

    public Pellet(int x, int y) {
        this.x = x + Maze.TILE_SIZE/2 - Maze.TILE_SIZE/8; // Center in tile
        this.y = y + Maze.TILE_SIZE/2 - Maze.TILE_SIZE/8;
        this.size = Maze.TILE_SIZE / 4;
    }
    
    public Pellet(int x, int y, boolean isPowerPellet) {
        this.x = x + Maze.TILE_SIZE/2 - Maze.TILE_SIZE/4; // Center in tile
        this.y = y + Maze.TILE_SIZE/2 - Maze.TILE_SIZE/4;
        this.size = isPowerPellet ? Maze.TILE_SIZE / 2 : Maze.TILE_SIZE / 4;
        this.isPowerPellet = isPowerPellet;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        if (isPowerPellet) {
            // Make power pellets flash
            if (System.currentTimeMillis() % 1000 < 500) {
                g.fillOval(x, y, size, size);
            }
        } else {
            g.fillOval(x, y, size, size);
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
    
    public boolean isPowerPellet() {
        return isPowerPellet;
    }
}
