import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Pacman {
    private int x, y;
    private final int size = Maze.TILE_SIZE;
    private final int speed = 2;
    
    private final Map<Integer, BufferedImage> images;
    
    private int currentDirection = KeyEvent.VK_RIGHT;

    public Pacman(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.images = new HashMap<>();
        
        loadImages(); 
    }

    private void loadImages() {
        try {
            images.put(KeyEvent.VK_UP, ImageIO.read(getClass().getResource("/images/pacman_up.png")));
            images.put(KeyEvent.VK_DOWN, ImageIO.read(getClass().getResource("/images/pacman_down.png")));
            images.put(KeyEvent.VK_LEFT, ImageIO.read(getClass().getResource("/images/pacman_left.png")));
            images.put(KeyEvent.VK_RIGHT, ImageIO.read(getClass().getResource("/images/pacman_right.png")));
        } catch (IOException | IllegalArgumentException exc) {
            System.out.println("Erro ao carregar imagem do Pac-Man: " + exc.getMessage());
        }
    }

    public void draw(Graphics g) {
        BufferedImage imageToDraw = images.get(currentDirection);
        
        if (imageToDraw != null) {
            g.drawImage(imageToDraw, x, y, size, size, null);
        }
    }

    public void move(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_UP) {
            this.y -= speed;
            currentDirection = KeyEvent.VK_UP;
        }
        else if (key == KeyEvent.VK_RIGHT) {
            this.x += speed;
            currentDirection = KeyEvent.VK_RIGHT;
        }
        else if (key == KeyEvent.VK_DOWN) {
            this.y += speed;
            currentDirection = KeyEvent.VK_DOWN;
        }
        else if (key == KeyEvent.VK_LEFT) {
            this.x -= speed;
            currentDirection = KeyEvent.VK_LEFT;
        }
    }

    public void update() {
        switch (currentDirection) {
            case KeyEvent.VK_UP:
                y -= speed;
                break;
            case KeyEvent.VK_RIGHT:
                x += speed;
                break;
            case KeyEvent.VK_DOWN:
                y += speed;
                break;
            case KeyEvent.VK_LEFT:
                x -= speed;
                break;
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return size; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public int getDirection() { return currentDirection; }
}

