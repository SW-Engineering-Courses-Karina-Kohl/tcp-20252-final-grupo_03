import javax.swing.*;

public class Level{
    private static JFrame window;
    private static void initWindow() {
    
        window = new JFrame("Pacman");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Maze maze = new Maze();
        window.add(maze);

        // Add key listener both to the maze component and the window to improve
        // key handling reliability (request focus afterwards).
        maze.addKeyListener(maze);
        window.addKeyListener(maze);

        window.setResizable(false);
        window.pack();
        
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        // Request focus for key input on the maze component
        maze.requestFocusInWindow();
        maze.requestFocus();
        window.requestFocus();
        System.out.println("Window shown: " + window.isVisible());
    }

    public static void main(String[] args) {
        // Ensure Swing UI is created on the Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(Level::initWindow);
    }
}

