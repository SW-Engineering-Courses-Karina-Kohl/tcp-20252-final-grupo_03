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
    private final int speed = 5;
    
    private final Map<String, BufferedImage> images;
    
    private String currentDirection = "right";

    public Pacman(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.images = new HashMap<>();
        
        loadImages(); 
    }

    private void loadImages() {
        try {
    
            images.put("up", ImageIO.read(new File("pacman_up.png")));
            images.put("down", ImageIO.read(new File("pacman_down.png")));
            images.put("left", ImageIO.read(new File("pacman_left.png")));
            images.put("right", ImageIO.read(new File("pacman_right.png")));
        } catch (IOException exc) {
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
            this.y += -speed;
            currentDirection = "up"; 
        }
        else if (key == KeyEvent.VK_RIGHT) {
            this.x += speed;
            currentDirection = "right"; 
        }
        else if (key == KeyEvent.VK_DOWN) {
            this.y += speed;
            currentDirection = "down"; 
        }
        else if (key == KeyEvent.VK_LEFT) {
            this.x += -speed;
            currentDirection = "left"; 
        }
    }
}