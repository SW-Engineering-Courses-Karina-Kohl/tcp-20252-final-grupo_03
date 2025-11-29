import java.awt.*;
import java.util.Random;
import java.util.List;
import java.awt.Rectangle;
import java.awt.Color;

public class Ghost {
    private int x, y;
    private int size = Maze.TILE_SIZE;
    private int speed = 1;
    private Color color;
    private int direction;
    private Random random = new Random();
    
    public enum Mode { CHASE, SCATTER, FRIGHTENED }
    private Mode mode = Mode.CHASE;
    private Mode previousMode = Mode.CHASE;
    private int frightenedTimer = 0; // milliseconds remaining
    private Color baseColor;
    private int scatterTargetX, scatterTargetY;
    private int directionLockMs = 0; // prevent immediate redecisions to avoid oscillation
    
    // Ghost colors
    public static final Color BLINKY_COLOR = Color.RED;
    public static final Color PINKY_COLOR = Color.PINK;
    public static final Color INKY_COLOR = Color.CYAN;
    public static final Color CLYDE_COLOR = Color.ORANGE;

    public Ghost(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.baseColor = color;
        // Start with a random direction
        direction = random.nextInt(4) * 90; // 0, 90, 180, 270
        // Default scatter corners based on color (approx):
        // Red (Blinky) -> top-right, Pink -> top-left, Cyan -> bottom-right, Orange -> bottom-left
        if (color.equals(BLINKY_COLOR)) {
            scatterTargetX = (Maze.COLUMNS - 1) * Maze.TILE_SIZE;
            scatterTargetY = 0;
        } else if (color.equals(PINKY_COLOR)) {
            scatterTargetX = 0;
            scatterTargetY = 0;
        } else if (color.equals(INKY_COLOR)) {
            scatterTargetX = (Maze.COLUMNS - 1) * Maze.TILE_SIZE;
            scatterTargetY = (Maze.ROWS - 1) * Maze.TILE_SIZE;
        } else if (color.equals(CLYDE_COLOR)) {
            scatterTargetX = 0;
            scatterTargetY = (Maze.ROWS - 1) * Maze.TILE_SIZE;
        } else {
            scatterTargetX = 0;
            scatterTargetY = 0;
        }
    }

    public void setMode(Mode m, int durationMs) {
        if (m == Mode.FRIGHTENED) {
            previousMode = mode;
            mode = Mode.FRIGHTENED;
            frightenedTimer = durationMs;
            color = Color.BLUE;
            // reverse direction when frightened
            direction = (direction + 180) % 360;
        } else {
            mode = m;
            if (m != Mode.FRIGHTENED) {
                color = baseColor;
                frightenedTimer = 0;
            }
        }
    }

    public Mode getMode() {
        return mode;
    }

    public void setDirection(int dir) {
        this.direction = dir;
    }

    // Set the global mode (used by Maze to switch between CHASE/SCATTER).
    // If the ghost is currently frightened, update its previousMode so it will
    // resume the correct global mode when frightened ends.
    public void setGlobalMode(Mode m) {
        if (mode == Mode.FRIGHTENED) {
            previousMode = m;
        } else {
            mode = m;
            color = baseColor;
            frightenedTimer = 0;
        }
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
    
    // deltaMs: milliseconds elapsed since last tick (pass ~16)
    public void move(List<Wall> walls, List<Ghost> others, Pacman pacman, int deltaMs, Maze maze) {
        // Random movement for now - can be improved with pathfinding
        // Update frightened timer
        if (mode == Mode.FRIGHTENED) {
            frightenedTimer -= deltaMs;
            if (frightenedTimer <= 0) {
                mode = previousMode;
                color = baseColor;
                frightenedTimer = 0;
            }
        } else {
            // Occasionally change direction randomly when not in a targeted mode
            if (random.nextInt(50) == 0) {
                direction = random.nextInt(4) * 90; // small random jitter
                directionLockMs = 200; // lock briefly to avoid immediate re-evaluation
            }
        }

        // Decrement direction lock timer and if locked, skip re-deciding direction
        boolean locked = false;
        if (directionLockMs > 0) {
            directionLockMs -= deltaMs;
            if (directionLockMs > 0) locked = true;
            else directionLockMs = 0;
        }

        // Only re-decide direction when the ghost is aligned to the tile grid
        boolean aligned = (x % Maze.TILE_SIZE == 0) && (y % Maze.TILE_SIZE == 0);

        // Decide desired direction based on mode (only when aligned and not locked)
        if (aligned && !locked) {
            if (mode == Mode.FRIGHTENED) {
            // Move away from Pacman
            int px = pacman.getX();
            int py = pacman.getY();
            int gx = x;
            int gy = y;
            int dxp = gx - px;
            int dyp = gy - py;
            if (Math.abs(dxp) > Math.abs(dyp)) {
                direction = dxp > 0 ? 0 : 180; // if ghost is to right of pacman, move right (0), else left
            } else {
                direction = dyp > 0 ? 90 : 270; // if below pacman, move down (90), else up
            }
            } else if (mode == Mode.SCATTER) {
            // Move towards scatter target corner
            int tx = scatterTargetX;
            int ty = scatterTargetY;
            int gx = x;
            int gy = y;
            int dxp = tx - gx;
            int dyp = ty - gy;
            if (Math.abs(dxp) > Math.abs(dyp)) {
                direction = dxp > 0 ? 0 : 180;
            } else {
                direction = dyp > 0 ? 90 : 270;
            }
        } else {
            // CHASE: simple greedy chase towards pacman
            int px = pacman.getX();
            int py = pacman.getY();
            int gx = x;
            int gy = y;
            int dxp = px - gx;
            int dyp = py - gy;
            if (Math.abs(dxp) > Math.abs(dyp)) {
                direction = dxp > 0 ? 0 : 180;
            } else {
                direction = dyp > 0 ? 90 : 270;
            }
            }
            // small random jitter when aligned to avoid perfect deadlocks
            if (random.nextInt(50) == 0) {
                direction = random.nextInt(4) * 90;
                directionLockMs = 100; // brief lock
            }
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
                // check collision with other ghosts
                for (Ghost o : others) {
                    if (o != this) {
                        Rectangle otherRect = new Rectangle(o.getX(), o.getY(), o.getSize(), o.getSize());
                        if (ghostRectX.intersects(otherRect)) {
                            collideX = true;
                            break;
                        }
                    }
                }
            }
            if (!collideX) {
                x = newX;
            } else {
                // If blocked, pick an alternative direction (try orthogonal moves)
                int oldDir = direction;
                boolean moved = tryAlternateDirections(walls, others, true);
                if (!moved) {
                    direction = random.nextInt(4) * 90;
                }
                else {
                    // If we successfully changed direction, lock briefly to avoid flip-flopping
                    directionLockMs = 200;
                }
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
                // check collision with other ghosts
                for (Ghost o : others) {
                    if (o != this) {
                        Rectangle otherRect = new Rectangle(o.getX(), o.getY(), o.getSize(), o.getSize());
                        if (ghostRectY.intersects(otherRect)) {
                            collideY = true;
                            break;
                        }
                    }
                }
            }
            if (!collideY) {
                y = newY;
            } else {
                boolean moved = tryAlternateDirections(walls, others, false);
                if (!moved) {
                    direction = random.nextInt(4) * 90;
                }
                else {
                    directionLockMs = 200;
                }
            }
        }
    }

    // Try orthogonal directions when blocked. If horiz=true, try up/down, else try left/right.
    private boolean tryAlternateDirections(List<Wall> walls, List<Ghost> others, boolean horiz) {
        int[] dirs = horiz ? new int[] {90, 270} : new int[] {0, 180};
        for (int d : dirs) {
            int dx = 0, dy = 0;
            switch (d) {
                case 0: dx = speed; break;
                case 90: dy = speed; break;
                case 180: dx = -speed; break;
                case 270: dy = -speed; break;
            }
            int newX = x + dx;
            int newY = y + dy;
            Rectangle ghostRect = new Rectangle(newX, newY, size, size);
            boolean collide = false;
            for (Wall w : walls) {
                Rectangle wallRect = new Rectangle(w.getX(), w.getY(), w.getSize(), w.getSize());
                if (ghostRect.intersects(wallRect)) {
                    collide = true;
                    break;
                }
            }
            if (!collide) {
                for (Ghost o : others) {
                    if (o != this) {
                        Rectangle otherRect = new Rectangle(o.getX(), o.getY(), o.getSize(), o.getSize());
                        if (ghostRect.intersects(otherRect)) {
                            collide = true;
                            break;
                        }
                    }
                }
            }
            if (!collide) {
                direction = d;
                if (dx != 0) x = newX; else y = newY;
                return true;
            }
        }
        return false;
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
