import javax.swing.*;

public class Level{
    private static JFrame window;
    private static void initWindow() {
    
        window = new JFrame("Pacman");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Maze maze = new Maze();
        window.add(maze);

        maze.addKeyListener(maze);

        window.setResizable(false);
        window.pack();
        
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    public static void main(String[] args) {
        initWindow();
    }
}

