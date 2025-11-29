import java.util.List;

public class Inky extends Ghost {
    public Inky(int x, int y) {
        super(x, y, INKY_COLOR);
    }

    @Override
    protected int[] computeChaseTarget(Pacman pacman, List<Ghost> others, Maze maze) {
        // Inky: uses Blinky's position to form a pincer with Pac-Man.
        // Strategy: take the point two tiles ahead of Pac-Man, form vector
        // from Blinky to that point, and double it.

        // Find Blinky in the others list (by class name)
        Ghost blinky = null;
        for (Ghost g : others) {
            if (g != this) {
                if (g.getClass().getSimpleName().equals("Blinky")) {
                    blinky = g;
                    break;
                }
            }
        }

        int px = pacman.getX();
        int py = pacman.getY();
        int twoAhead = 2 * Maze.TILE_SIZE;
        int dir = pacman.getDirection();
        switch (dir) {
            case java.awt.event.KeyEvent.VK_UP: py -= twoAhead; break;
            case java.awt.event.KeyEvent.VK_DOWN: py += twoAhead; break;
            case java.awt.event.KeyEvent.VK_LEFT: px -= twoAhead; break;
            case java.awt.event.KeyEvent.VK_RIGHT: px += twoAhead; break;
        }

        if (blinky != null) {
            int bx = blinky.getX();
            int by = blinky.getY();
            int vx = px - bx;
            int vy = py - by;
            int tx = bx + 2 * vx;
            int ty = by + 2 * vy;
            return new int[] { tx, ty };
        }

        // Fallback: target pacman
        return new int[] { pacman.getX(), pacman.getY() };
    }
}
