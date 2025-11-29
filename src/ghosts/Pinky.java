import java.awt.event.KeyEvent;
import java.util.List;

public class Pinky extends Ghost {
    public Pinky(int x, int y) {
        super(x, y, PINKY_COLOR);
    }

    @Override
    protected int[] computeChaseTarget(Pacman pacman, List<Ghost> others, Maze maze) {
        // Ambusher: target four tiles ahead of Pac-Man's current direction
        int dir = pacman.getDirection();
        int tilesAhead = 4;
        int offset = tilesAhead * Maze.TILE_SIZE;
        int tx = pacman.getX();
        int ty = pacman.getY();

        switch (dir) {
            case KeyEvent.VK_UP:
                ty -= offset;
                break;
            case KeyEvent.VK_DOWN:
                ty += offset;
                break;
            case KeyEvent.VK_LEFT:
                tx -= offset;
                break;
            case KeyEvent.VK_RIGHT:
                tx += offset;
                break;
        }

        return new int[] { tx, ty };
    }
}
