import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;

public class Snakey extends JFrame {

    public Snakey() {
        setTitle("Snakey");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        System.out.println("Snakey game Initialized. Use arrow keys to control the snake. Press 'R' to restart after game over.");
        System.out.println("Enjoy the game!");
        SwingUtilities.invokeLater(Snakey::new);
    }
}

class GamePanel extends JPanel implements ActionListener, KeyListener {

    private static final int TILE_SIZE = 25;
    private int gridWidth = 20;
    private int gridHeight = 20;

    private final LinkedList<Point> snake = new LinkedList<>();
    private Point food;
    private char direction = 'R';
    private boolean running = true;
    private Timer timer;
    private Random rand = new Random();

    public GamePanel() {
        setPreferredSize(new Dimension(TILE_SIZE * gridWidth, TILE_SIZE * gridHeight));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                gridWidth = Math.max(5, getWidth() / TILE_SIZE);
                gridHeight = Math.max(5, getHeight() / TILE_SIZE);
                ensureSnakeInBounds();
                spawnFood();
                repaint();
            }
        });
        startGame();
    }

    private void startGame() {
        if (timer != null) timer.stop();
        snake.clear();
        snake.add(new Point(5, 5));
        direction = 'R';
        running = true;
        spawnFood();
        timer = new Timer(100, this);
        timer.start();
        requestFocusInWindow();
    }

    private void spawnFood() {
        int tries = 0;
        do {
            food = new Point(rand.nextInt(gridWidth), rand.nextInt(gridHeight));
            tries++;
            if (tries > 100) break;
        } while (snake.contains(food));
    }

    private void ensureSnakeInBounds() {
        for (Point p : snake) {
            p.x = Math.max(0, Math.min(p.x, gridWidth - 1));
            p.y = Math.max(0, Math.min(p.y, gridHeight - 1));
        }
        
        Point head = snake.getFirst();
        if (head.x >= gridWidth || head.y >= gridHeight) {
            snake.clear();
            snake.add(new Point(gridWidth / 2, gridHeight / 2));
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int currentGridWidth = panelWidth / TILE_SIZE;
        int currentGridHeight = panelHeight / TILE_SIZE;

        if (running) {
            g.setColor(Color.DARK_GRAY);
            for (int i = 0; i <= currentGridHeight; i++) {
                g.drawLine(0, i * TILE_SIZE, panelWidth, i * TILE_SIZE);
            }
            for (int i = 0; i <= currentGridWidth; i++) {
                g.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, panelHeight);
            }

            g.setColor(Color.RED);
            g.fillOval(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

            for (int i = 0; i < snake.size(); i++) {
                g.setColor(i == 0 ? Color.GREEN : Color.LIGHT_GRAY);
                Point p = snake.get(i);
                g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("Score: " + (snake.size() - 1), 10, 20);
        } else {
            gameOver(g, panelWidth, panelHeight);
        }
    }

    private void move() {
        Point head = new Point(snake.getFirst());
        switch (direction) {
            case 'U' -> head.y--;
            case 'D' -> head.y++;
            case 'L' -> head.x--;
            case 'R' -> head.x++;
        }

        if (head.x < 0 || head.x >= gridWidth || head.y < 0 || head.y >= gridHeight || snake.contains(head)) {
            running = false;
            timer.stop();
            return;
        }

        snake.addFirst(head);
        if (head.equals(food)) {
            spawnFood();
        } else {
            snake.removeLast();
        }
    }

    private void gameOver(Graphics g, int panelWidth, int panelHeight) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics fm = g.getFontMetrics();
        String msg = "Game Over!";
        g.drawString(msg, (panelWidth - fm.stringWidth(msg)) / 2, panelHeight / 2 - 30);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String scoreMsg = "Final Score: " + (snake.size() - 1);
        g.drawString(scoreMsg, (panelWidth - fm.stringWidth(scoreMsg)) / 2, panelHeight / 2);

        String restartMsg = "Press R to Restart";
        g.drawString(restartMsg, (panelWidth - fm.stringWidth(restartMsg)) / 2, panelHeight / 2 + 30);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> {
                if (direction != 'D') direction = 'U';
            }
            case KeyEvent.VK_DOWN -> {
                if (direction != 'U') direction = 'D';
            }
            case KeyEvent.VK_LEFT -> {
                if (direction != 'R') direction = 'L';
            }
            case KeyEvent.VK_RIGHT -> {
                if (direction != 'L') direction = 'R';
            }
            case KeyEvent.VK_R -> {
                if (!running) {
                    startGame();
                    repaint();
                }
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}