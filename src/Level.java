import javax.swing.*;

public class Level{
    private static JFrame window;
    private static void initWindow() {
    
        window = new JFrame("Pacman");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Show main menu first
        MenuPanel menu = new MenuPanel(window);
        window.add(menu);

        window.setResizable(false);
        window.pack();
        
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        // Request focus for key input on the maze component
        menu.requestFocusInWindow();
        menu.requestFocus();
        window.requestFocus();
        System.out.println("Window shown: " + window.isVisible());
    }

    public static void main(String[] args) {
        // Ensure Swing UI is created on the Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(Level::initWindow);
    }
}

