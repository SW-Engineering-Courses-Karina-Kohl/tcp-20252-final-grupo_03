import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class Maze extends JPanel implements KeyListener, ActionListener {
    public static final int TILE_SIZE = 32;
    public static final int ROWS = 21;
    public static final int COLUMNS = 19;
    public static final int TURN_UP_QUEUE = 8;
    public static final int TURN_LEFT_QUEUE = 4;
    public static final int TURN_RIGHT_QUEUE = 6;
    public static final int TURN_DOWN_QUEUE = 2;

    //X = wall, O = skip, P = pac man, ' ' = food, . = power pellet
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] grid = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X.               .X",
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
        "X. X     P     X .X",
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
    private boolean paused = false;
    private int poweredUpTimer = 0;
    private GameTimer gameTimer;
    private final int POWERUP_DURATION = 400;
    private final int GHOST_DEAD_DURATION = 20; // ticks the ghost stays dead before reviving

    public Maze(){
        setPreferredSize(new Dimension(TILE_SIZE * COLUMNS, TILE_SIZE * ROWS));
        setBackground(Color.BLACK);

        pellets = new ArrayList<>();
        walls = new ArrayList<>();
        ghosts = new ArrayList<>();

	gameTimer = new GameTimer();
        loadMaze();
        System.out.println(pellets.size() + " pellets");
        timer = new Timer(16, this);
        timer.start();
	gameTimer.start();

        setFocusable(true);
        // Setup key bindings so pause/reset/enter work even if focus is elsewhere
        setupKeyBindings();
    }

    // Configure key bindings using InputMap/ActionMap so keys work when window focused
    private void setupKeyBindings() {
        InputMap im = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.getActionMap();

        im.put(KeyStroke.getKeyStroke('P'), "pauseToggle");
        im.put(KeyStroke.getKeyStroke('p'), "pauseToggle");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), "pauseToggle");
        am.put("pauseToggle", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePause();
            }
        });

        im.put(KeyStroke.getKeyStroke('R'), "reset");
        im.put(KeyStroke.getKeyStroke('r'), "reset");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "reset");
        am.put("reset", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
        am.put("enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameOver) resetGame();
            }
        });
    }

    private void togglePause() {
        paused = !paused;
        if (paused) {
            timer.stop();
            gameTimer.stop();
            System.out.println("Game paused");
        } else {
            timer.start();
            if (!gameOver) gameTimer.start();
            System.out.println("Game resumed");
        }
        repaint();
    }

    // Reset the game state to initial layout
    private void resetGame() {
        // Stop timers while resetting
        timer.stop();
        gameTimer.reset();

        // Clear current state
        pellets.clear();
        walls.clear();
        ghosts.clear();
        pacman = null;

        // Reset flags and counters
        score = 0;
        poweredUpTimer = 0;
        gameOver = false;
        paused = false;

        // Reload maze contents
        loadMaze();

        // Restart timers
        timer.start();
        gameTimer.start();
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
        // If paused, don't update game logic but still repaint overlay
        if (paused) {
            repaint();
            return;
        }

        if (!gameOver) {
            // Update game state
            if (pacman.willUpdate) pacman.update();
            if (poweredUpTimer > 0) poweredUpTimer -= 1;

            int deltaMs = timer.getDelay(); // tick duration (ms)
            // Move ghosts
            for (Ghost ghost : ghosts) {
                ghost.move(walls, ghosts, pacman, deltaMs, this);
            }

            // Check collisions
            checkCollisions();

            // Check win condition
            if (pellets.isEmpty()) {
                gameOver = true;
                gameTimer.stop();
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
        g.drawString((poweredUpTimer > 0) ? String.valueOf(poweredUpTimer) : " ", 250, 20);
	gameTimer.draw(g, getWidth() - 100, 20);
        
        // Draw game over message with restart hint
        if (gameOver) {
            gameTimer.stop();
            String msg = pellets.isEmpty() ? "YOU WIN!" : "GAME OVER";
            g.setFont(new Font("Arial", Font.BOLD, 40));
            FontMetrics fm = g.getFontMetrics();
            int mw = fm.stringWidth(msg);
            g.setColor(Color.WHITE);
            g.drawString(msg, getWidth()/2 - mw/2, getHeight()/2);

            String hint = "Aperte ENTER para reiniciar";
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            fm = g.getFontMetrics();
            int hw = fm.stringWidth(hint);
            g.drawString(hint, getWidth()/2 - hw/2, getHeight()/2 + 40);
        }

        // Draw paused overlay
        if (paused && !gameOver) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            String title = "PAUSE";
            g.setFont(new Font("Arial", Font.BOLD, 48));
            FontMetrics fm = g.getFontMetrics();
            int tw = fm.stringWidth(title);
            g.drawString(title, getWidth()/2 - tw/2, getHeight()/2 - 10);

            String sub = "aperte p para continuar";
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            fm = g.getFontMetrics();
            int sw = fm.stringWidth(sub);
            g.drawString(sub, getWidth()/2 - sw/2, getHeight()/2 + 30);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // Pause toggle: P
        if (key == KeyEvent.VK_P) {
            paused = !paused;
            if (paused) {
                timer.stop();
                gameTimer.stop();
            } else {
                timer.start();
                if (!gameOver) gameTimer.start();
            }
            return;
        }

        // Reset: R
        if (key == KeyEvent.VK_R) {
            resetGame();
            return;
        }

        // If game over, Enter restarts
        if (key == KeyEvent.VK_ENTER && gameOver) {
            resetGame();
            return;
        }

        if (!gameOver && !paused) {
            pacman.move(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
    }
    
    private void checkCollisions() {
        // Check wall collisions
        boolean changeDirection = true;
        pacman.willUpdate = true;
        for (Wall wall : walls) {
            //if (isColliding(pacman, wall)) {
                // Reset pacman position based on direction
            //    switch (pacman.getDirection()) {
            //        case KeyEvent.VK_UP:
            //            pacman.setY(wall.getY() + wall.getSize());
            //            break;
            //        case KeyEvent.VK_RIGHT:
            //            pacman.setX(wall.getX() - pacman.getSize());
            //            break;
            //        case KeyEvent.VK_DOWN:
            //            pacman.setY(wall.getY() - pacman.getSize());
            //            break;
            //        case KeyEvent.VK_LEFT:
            //            pacman.setX(wall.getX() + wall.getSize());
            //            break;
            //    }
            //}
            if (willCollide(pacman, wall) == true) {
                changeDirection = false;
            }
            if (isColliding(pacman, wall) == true) {
                pacman.willUpdate = false;
            }
        }
        if (changeDirection) {
            pacman.setDirection();
        }
        
        // Check pellet collisions
        for (int i = pellets.size() - 1; i >= 0; i--) {
            Pellet pellet = pellets.get(i);
                if (isColliding(pacman, pellet)) {
                // If pellet is a power pellet and Pacman already powered up, ignore it
                if (pellet.isPowerPellet() && poweredUpTimer > 0) {
                    // do nothing (cannot pick up another power pellet while frightened)
                    continue;
                }
                pellets.remove(i);
                score += pellet.isPowerPellet() ? 50 : 10;
                if (pellet.isPowerPellet() == true) {
                    poweredUpTimer = POWERUP_DURATION;
                    // put all ghosts into frightened mode for the duration (ticks)
                    for (Ghost g : ghosts) {
                        g.setMode(Ghost.Mode.FRIGHTENED, POWERUP_DURATION);
                    }
                }
            }
        }
        
        // Check ghost collisions
        for (Ghost ghost : ghosts) {
            if (isColliding(pacman, ghost)) {
                if (ghost.getMode() == Ghost.Mode.FRIGHTENED) {
                    // Pacman eats frightened ghost: set ghost to DEAD; it will walk back to its home
                    ghost.setMode(Ghost.Mode.DEAD, GHOST_DEAD_DURATION);
                    // Award points for eating a ghost
                    score += 100;
                } else if (ghost.getMode() == Ghost.Mode.DEAD) {
                    // already dead, ignore
                } else {
                    // normal ghost kills Pacman
                    gameOver = true;
                }
            }
        }
    }

    // Return the maze center coordinates (tile-aligned)
    public int getCenterX() {
        return (COLUMNS / 2) * TILE_SIZE;
    }

    public int getCenterY() {
        return (ROWS / 2) * TILE_SIZE;
    }
    
    // Simple collision detection
    //private boolean isColliding(Pacman pacman, Wall wall) {
    //    return pacman.getX() < wall.getX() + wall.getSize() &&
    //           pacman.getX() + pacman.getSize() > wall.getX() &&
    //           pacman.getY() < wall.getY() + wall.getSize() &&
    //           pacman.getY() + pacman.getSize() > wall.getY();
    //}

    private boolean isColliding(Pacman pacman, Wall wall) {
        switch(pacman.currentDirection) {
            case KeyEvent.VK_UP: return pacman.getX() < wall.getX() + wall.getSize() &&
               pacman.getX() + pacman.getSize() > wall.getX() &&
               pacman.getY() - 2 < wall.getY() + wall.getSize() &&
               pacman.getY() + pacman.getSize() - 2 > wall.getY();
            case KeyEvent.VK_LEFT: return pacman.getX() - 2 < wall.getX() + wall.getSize() &&
               pacman.getX() + pacman.getSize() - 2 > wall.getX() &&
               pacman.getY() < wall.getY() + wall.getSize() &&
               pacman.getY() + pacman.getSize() > wall.getY();
            case KeyEvent.VK_RIGHT: return pacman.getX() + 2 < wall.getX() + wall.getSize() &&
               pacman.getX() + pacman.getSize() + 2 > wall.getX() &&
               pacman.getY() < wall.getY() + wall.getSize() &&
               pacman.getY() + pacman.getSize() > wall.getY();
            case KeyEvent.VK_DOWN: return pacman.getX() < wall.getX() + wall.getSize() &&
               pacman.getX() + pacman.getSize() > wall.getX() &&
               pacman.getY() + 2 < wall.getY() + wall.getSize() &&
               pacman.getY() + pacman.getSize() + 2 > wall.getY();
            default: return false;
        }
    }

    private boolean willCollide(Pacman pacman, Wall wall) {
        switch(pacman.turnDirection) {
            case TURN_UP_QUEUE: return pacman.getX() < wall.getX() + wall.getSize() &&
               pacman.getX() + pacman.getSize() > wall.getX() &&
               pacman.getY() - 2 < wall.getY() + wall.getSize() &&
               pacman.getY() + pacman.getSize() - 2 > wall.getY();
            case TURN_LEFT_QUEUE: return pacman.getX() - 2 < wall.getX() + wall.getSize() &&
               pacman.getX() + pacman.getSize() - 2 > wall.getX() &&
               pacman.getY() < wall.getY() + wall.getSize() &&
               pacman.getY() + pacman.getSize() > wall.getY();
            case TURN_RIGHT_QUEUE: return pacman.getX() + 2 < wall.getX() + wall.getSize() &&
               pacman.getX() + pacman.getSize() + 2 > wall.getX() &&
               pacman.getY() < wall.getY() + wall.getSize() &&
               pacman.getY() + pacman.getSize() > wall.getY();
            case TURN_DOWN_QUEUE: return pacman.getX() < wall.getX() + wall.getSize() &&
               pacman.getX() + pacman.getSize() > wall.getX() &&
               pacman.getY() + 2 < wall.getY() + wall.getSize() &&
               pacman.getY() + pacman.getSize() + 2 > wall.getY();
            default: return false;
        }
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

    // Compute a tile-aligned direction (0=right,90=down,180=left,270=up)
    // from (fromX,fromY) to (toX,toY) using BFS on the maze grid.
    // Returns -1 if no path found.
    public int getNextDirectionTowards(int fromX, int fromY, int toX, int toY) {
        int fromTileX = fromX / TILE_SIZE;
        int fromTileY = fromY / TILE_SIZE;
        int toTileX = toX / TILE_SIZE;
        int toTileY = toY / TILE_SIZE;

        // If either position is outside the maze grid (e.g. during tunnel wrap),
        // bail out so callers can fallback to a greedy move. This avoids
        // ArrayIndexOutOfBoundsExceptions when ghosts/pacman are temporarily
        // positioned at negative coordinates (teleport tunnels).
        if (fromTileX < 0 || fromTileX >= COLUMNS || fromTileY < 0 || fromTileY >= ROWS) return -1;
        if (toTileX < 0 || toTileX >= COLUMNS || toTileY < 0 || toTileY >= ROWS) return -1;

        if (fromTileX == toTileX && fromTileY == toTileY) return -1;

        boolean[][] visited = new boolean[ROWS][COLUMNS];
        int[][] px = new int[ROWS][COLUMNS];
        int[][] py = new int[ROWS][COLUMNS];
        java.util.ArrayDeque<int[]> q = new java.util.ArrayDeque<>();
        q.add(new int[] {fromTileX, fromTileY});
        visited[fromTileY][fromTileX] = true;
        boolean found = false;

        int[][] dirs = new int[][] {{1,0},{-1,0},{0,1},{0,-1}};

        while (!q.isEmpty()) {
            int[] cur = q.removeFirst();
            int cx = cur[0], cy = cur[1];
            if (cx == toTileX && cy == toTileY) { found = true; break; }
            for (int[] d : dirs) {
                int nx = cx + d[0];
                int ny = cy + d[1];
                if (nx < 0 || nx >= COLUMNS || ny < 0 || ny >= ROWS) continue;
                if (visited[ny][nx]) continue;
                char tile = grid[ny].charAt(nx);
                if (tile == 'X') continue; // wall
                visited[ny][nx] = true;
                px[ny][nx] = cx;
                py[ny][nx] = cy;
                q.add(new int[] {nx, ny});
            }
        }

        if (!found) return -1;

        // backtrack from target to find the first step
        int bx = toTileX, by = toTileY;
        while (!(px[by][bx] == fromTileX && py[by][bx] == fromTileY)) {
            int nx = px[by][bx];
            int ny = py[by][bx];
            // if we reached the start but parent isn't set correctly break
            if (nx == bx && ny == by) break;
            bx = nx; by = ny;
            if (bx == fromTileX && by == fromTileY) break;
        }

        // bx,by is the tile to move into from the origin
        if (bx > fromTileX) return 0;
        if (bx < fromTileX) return 180;
        if (by > fromTileY) return 90;
        if (by < fromTileY) return 270;
        return -1;
    }
    
    private boolean isColliding(Pacman pacman, Ghost ghost) {
        return pacman.getX() < ghost.getX() + ghost.getSize() &&
               pacman.getX() + pacman.getSize() > ghost.getX() &&
               pacman.getY() < ghost.getY() + ghost.getSize() &&
               pacman.getY() + pacman.getSize() > ghost.getY();
    }
}
