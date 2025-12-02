import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MenuPanel extends JPanel {
    private JFrame parent;

    public MenuPanel(JFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(Maze.TILE_SIZE * Maze.COLUMNS, Maze.TILE_SIZE * Maze.ROWS));
        // Dark background for the menu
        setBackground(new Color(18, 18, 18));
        setOpaque(true);
        initMainMenu();
    }

    private void initMainMenu() {
        removeAll();
        JPanel center = new JPanel();
        // let the center panel be transparent so the dark background shows
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));

        JLabel title = new JLabel("PACMAN");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 56));
        title.setForeground(new Color(255, 204, 0));

        JButton startBtn = new JButton("Iniciar");
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startBtn.setMaximumSize(new Dimension(200, 40));
        startBtn.setFont(new Font("Arial", Font.BOLD, 22));
        startBtn.setBackground(new Color(60, 60, 60));
        startBtn.setForeground(Color.WHITE);
        startBtn.setFocusPainted(false);
        startBtn.addActionListener(e -> startGame());

        JButton rankingBtn = new JButton("Ranking");
        rankingBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        rankingBtn.setMaximumSize(new Dimension(200, 40));
        rankingBtn.setFont(new Font("Arial", Font.BOLD, 22));
        rankingBtn.setBackground(new Color(60, 60, 60));
        rankingBtn.setForeground(Color.WHITE);
        rankingBtn.setFocusPainted(false);
        rankingBtn.addActionListener(e -> showRanking());

        JButton tutorialBtn = new JButton("Tutorial");
        tutorialBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        tutorialBtn.setMaximumSize(new Dimension(200, 40));
        tutorialBtn.setFont(new Font("Arial", Font.BOLD, 22));
        tutorialBtn.setBackground(new Color(60, 60, 60));
        tutorialBtn.setForeground(Color.WHITE);
        tutorialBtn.setFocusPainted(false);
        tutorialBtn.addActionListener(e -> showTutorial());

        center.add(title);
        center.add(Box.createRigidArea(new Dimension(0, 30)));
        center.add(startBtn);
        center.add(Box.createRigidArea(new Dimension(0, 10)));
        center.add(rankingBtn);
        center.add(Box.createRigidArea(new Dimension(0, 10)));
        center.add(tutorialBtn);

        add(center, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void startGame() {
        // Replace this panel with a new Maze instance
        SwingUtilities.invokeLater(() -> {
            Maze maze = new Maze();
            parent.getContentPane().removeAll();
            parent.getContentPane().add(maze);
            // Keep legacy key listener additions for reliability
            maze.addKeyListener(maze);
            parent.addKeyListener(maze);
            parent.pack();
            parent.setLocationRelativeTo(null);
            maze.requestFocusInWindow();
            maze.requestFocus();
            parent.requestFocus();
        });
    }

    private void showRanking() {
        removeAll();
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Ranking - Top 15");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // Load top 15 and trim the file to only these
        java.util.List<RankingManager.ScoreEntry> top = RankingManager.getTop(15);
        java.util.ArrayList<RankingManager.ScoreEntry> copy = new java.util.ArrayList<>(top);
        // Persist only top 15
        RankingManager.saveScores(copy);

        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        ta.setBackground(new Color(28, 28, 28));
        ta.setForeground(Color.WHITE);
        StringBuilder sb = new StringBuilder();
        int idx = 1;
        for (RankingManager.ScoreEntry e : copy) {
            sb.append(String.format("%2d. %s  -  %d pts  -  %s\n", idx++, e.name, e.score, formatTime(e.timeMs)));
        }
        if (copy.isEmpty()) sb.append("(Nenhum score salvo)\n");
        ta.setText(sb.toString());
        ta.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane sp = new JScrollPane(ta);
        add(sp, BorderLayout.CENTER);

        JButton back = new JButton("Voltar");
        back.setFont(new Font("Arial", Font.BOLD, 16));
        back.setBackground(new Color(60, 60, 60));
        back.setForeground(Color.WHITE);
        back.setFocusPainted(false);
        back.addActionListener(e -> initMainMenu());
        JPanel bottom = new JPanel();
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private void showTutorial() {
        removeAll();
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Tutorial");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        ta.setFont(new Font("Arial", Font.PLAIN, 16));
        ta.setBackground(new Color(28, 28, 28));
        ta.setForeground(Color.WHITE);
        ta.setText(getTutorialText());
        JScrollPane sp = new JScrollPane(ta);
        add(sp, BorderLayout.CENTER);

        JButton back = new JButton("Voltar");
        back.setFont(new Font("Arial", Font.BOLD, 16));
        back.setBackground(new Color(60, 60, 60));
        back.setForeground(Color.WHITE);
        back.setFocusPainted(false);
        back.addActionListener(e -> initMainMenu());
        JPanel bottom = new JPanel();
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private String getTutorialText() {
        StringBuilder sb = new StringBuilder();
        sb.append("Como Jogar:\n\n");
        sb.append("- Use as setas do teclado para mover o Pacman:\n");
        sb.append("  "+ (char)0x2190 + " esquerda, " + (char)0x2192 + " direita, "+ (char)0x2191 + " cima, "+ (char)0x2193 + " baixo\n\n");
        sb.append("- Pontuação:\n");
        sb.append("  * Pellets normais: 10 pontos cada.\n");
        sb.append("  * Power Pellets: 50 pontos e deixam os fantasmas vulneráveis.\n\n");
        sb.append("- Power Pellets:\n");
        sb.append("  Ao comer um Power Pellet, todos os fantasmas entram no modo FRIGHTENED.\n");
        sb.append("  Durante esse tempo, você pode comer os fantasmas para ganhar 100 pontos cada.\n\n");
        sb.append("- Comer Fantasmas:\n");
        sb.append("  Fantasmas no modo FRIGHTENED podem ser comidos.\n\n");
        sb.append("  Após serem comidos, voltam para casa e reaparecem.\n\n");
        sb.append("- Controles adicionais:\n");
        sb.append("  * 'P' - Pausar / Retomar o jogo.\n");
        sb.append("  * 'R' - Reiniciar o jogo.\n\n");
        sb.append("Boa sorte e tenha cuidado com os fantasmas!\n");
        return sb.toString();
    }

    private String formatTime(long ms) {
        long totalSec = ms / 1000;
        long min = totalSec / 60;
        long sec = totalSec % 60;
        return String.format("%02d:%02d", min, sec);
    }
}
