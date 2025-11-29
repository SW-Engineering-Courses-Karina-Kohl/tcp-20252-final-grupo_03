import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class Maze extends JPanel implements KeyListener, ActionListener {
    public static final int TILE_SIZE = 32;
    public static final int ROWS = 21;
    public static final int COLUMNS = 19;

    //X = wall, O = skip, P = pac man, ' ' = food, . = power pellet
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] grid = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "O       bpo       O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX" 
    };

    private ArrayList<Pellet> pellets;
    private ArrayList<Wall> walls;
    private ArrayList<Ghost> ghosts;
    private Pacman pacman;
    private Timer timer;
    private int score = 0;
    private boolean gameOver = false;

    public Maze(){
        setPreferredSize(new Dimension(TILE_SIZE * COLUMNS, TILE_SIZE * ROWS));
        setBackground(Color.BLACK);

        pellets = new ArrayList<>();
        walls = new ArrayList<>();
        ghosts = new ArrayList<>();

        loadMaze();
        System.out.println(pellets.size() + " pellets");
        timer = new Timer(16, this);
        timer.start();

        setFocusable(true);
    }

    private void loadMaze(){
        for(int i = 0; i < ROWS; i++){
            for(int j = 0; j < COLUMNS; j++){
                char tile = grid[i].charAt(j);
                int x = j * TILE_SIZE;
                int y = i * TILE_SIZE;
                
                if(tile == ' '){
                    pellets.add(new Pellet(x, y));
                }
                else if(tile == '.'){
                    pellets.add(new Pellet(x, y, true)); // Power pellet
                }
                else if(tile == 'P'){
                    pacman = new Pacman(x, y);
                }
                else if(tile == 'X'){
                    walls.add(new Wall(x, y));
                }
                else if(tile == 'r'){
                    ghosts.add(new Ghost(x, y, Ghost.BLINKY_COLOR));
                }
                else if(tile == 'p'){
                    ghosts.add(new Ghost(x, y, Ghost.PINKY_COLOR));
                }
                else if(tile == 'b'){
                    ghosts.add(new Ghost(x, y, Ghost.INKY_COLOR));
                }
                else if(tile == 'o'){
                    ghosts.add(new Ghost(x, y, Ghost.CLYDE_COLOR));
                }
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            // Update game state
            pacman.update();
            
            // Move ghosts
            for (Ghost ghost : ghosts) {
                ghost.move();
            }
            
            // Check collisions
            checkCollisions();
            
            // Check win condition
            if (pellets.isEmpty()) {
                gameOver = true;
            }
        }
        
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
        
        // Draw walls
        for(Wall wall : walls){
            wall.draw(g);
        }
        
        // Draw pellets
        for(Pellet pellet : pellets){
            pellet.draw(g);
        }
        
        // Draw ghosts
        for(Ghost ghost : ghosts){
            ghost.draw(g);
        }
        
        // Draw pacman
        pacman.draw(g);
        
        // Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + score, 10, 20);
        
        // Draw game over message
        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 40));
            if (pellets.isEmpty()) {
                g.drawString("YOU WIN!", getWidth()/2 - 100, getHeight()/2);
            } else {
                g.drawString("GAME OVER", getWidth()/2 - 120, getHeight()/2);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameOver) {
            pacman.move(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
    }
    
    private void checkCollisions() {
        // Check wall collisions
        for (Wall wall : walls) {
            if (isColliding(pacman, wall)) {
                // Reset pacman position based on direction
                switch (pacman.getDirection()) {
                    case KeyEvent.VK_UP:
                        pacman.setY(wall.getY() + wall.getSize());
                        break;
                    case KeyEvent.VK_RIGHT:
                        pacman.setX(wall.getX() - pacman.getSize());
                        break;
                    case KeyEvent.VK_DOWN:
                        pacman.setY(wall.getY() - pacman.getSize());
                        break;
                    case KeyEvent.VK_LEFT:
                        pacman.setX(wall.getX() + wall.getSize());
                        break;
                }
            }
        }
        
        // Check pellet collisions
        for (int i = pellets.size() - 1; i >= 0; i--) {
            Pellet pellet = pellets.get(i);
            if (isColliding(pacman, pellet)) {
                pellets.remove(i);
                score += pellet.isPowerPellet() ? 50 : 10;
            }
        }
        
        // Check ghost collisions
        for (Ghost ghost : ghosts) {
            if (isColliding(pacman, ghost)) {
                gameOver = true;
            }
        }
    }
    
    // Simple collision detection
    private boolean isColliding(Pacman pacman, Wall wall) {
        return pacman.getX() < wall.getX() + wall.getSize() &&
               pacman.getX() + pacman.getSize() > wall.getX() &&
               pacman.getY() < wall.getY() + wall.getSize() &&
               pacman.getY() + pacman.getSize() > wall.getY();
    }
    
    private boolean isColliding(Pacman pacman, Pellet pellet) {
        int pacmanCenterX = pacman.getX() + pacman.getSize() / 2;
        int pacmanCenterY = pacman.getY() + pacman.getSize() / 2;
        int pelletCenterX = pellet.getX() + pellet.getSize() / 2;
        int pelletCenterY = pellet.getY() + pellet.getSize() / 2;
        
        // Calculate distance between centers
        double distance = Math.sqrt(Math.pow(pacmanCenterX - pelletCenterX, 2) + 
                                   Math.pow(pacmanCenterY - pelletCenterY, 2));
        
        return distance < (pacman.getSize() / 2 + pellet.getSize() / 2);
    }
    
    private boolean isColliding(Pacman pacman, Ghost ghost) {
        return pacman.getX() < ghost.getX() + ghost.getSize() &&
               pacman.getX() + pacman.getSize() > ghost.getX() &&
               pacman.getY() < ghost.getY() + ghost.getSize() &&
               pacman.getY() + pacman.getSize() > ghost.getY();
    }
}