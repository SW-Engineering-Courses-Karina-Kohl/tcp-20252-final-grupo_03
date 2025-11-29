import java.util.List;

public class Clyde extends Ghost {
    public Clyde(int x, int y) {
        super(x, y, CLYDE_COLOR);
    }

    @Override
    protected int[] computeChaseTarget(Pacman pacman, List<Ghost> others, Maze maze) {
        // If closer than ~8 tiles, go to scatter corner; otherwise chase like Blinky.
        int gx = this.getX();
        int gy = this.getY();
        int px = pacman.getX();
        int py = pacman.getY();
        double dist = Math.hypot(px - gx, py - gy);
        int threshold = 8 * Maze.TILE_SIZE;
        if (dist <= threshold) {
            return new int[] { getScatterTargetX(), getScatterTargetY() };
        }
        return new int[] { px, py };
    }
}
