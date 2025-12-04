import java.awt.Color;
import java.util.List;

public class Blinky extends Ghost {
    public Blinky(int x, int y) {
        super(x, y, BLINKY_COLOR);
    }

    @Override
    protected int[] computeChaseTarget(Pacman pacman, List<Ghost> others, Maze maze) {
        // Aggressive: target Pac-Man's current position
        return new int[] { pacman.getX(), pacman.getY() };
    }
}
