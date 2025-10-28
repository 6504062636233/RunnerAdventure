import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    private Thread gameThread;
    private boolean running = false;
    private boolean started = false;

    private Player player;
    private ArrayList<Obstacle> obstacles;
    private ArrayList<Item> items;
    private ArrayList<Coin> coins;
    private GameManager manager;
    private BufferedImage[] backgrounds;

    public GamePanel() {
        this.setPreferredSize(new Dimension(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        this.setFocusable(true);
        this.addKeyListener(this);

        loadAssets();
    }

    private void loadAssets() {
        backgrounds = new BufferedImage[3];
        try {
            backgrounds[0] = ImageIO.read(new File("assets/background_forest.png"));
            backgrounds[1] = ImageIO.read(new File("assets/background_desert.png"));
            backgrounds[2] = ImageIO.read(new File("assets/background_volcano.png"));
        } catch (Exception e) {
            System.out.println("Background assets missing, continuing with fallback color.");
        }
    }

    public void startGame() {
        player = new Player(100, Constants.GROUND_Y - Constants.PLAYER_HEIGHT);
        obstacles = new ArrayList<>();
        items = new ArrayList<>();
        coins = new ArrayList<>();
        manager = new GameManager();

        running = true;
        started = true;

        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerFrame = 1000000000.0 / 60.0; // 60 FPS
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerFrame;
            lastTime = now;

            while (delta >= 1) {
                update();
                repaint();
                delta--;
            }
            try { Thread.sleep(2); } catch (InterruptedException e) { }
            // check game over by lives
            if (player.lives <= 0) running = false;
        }
        repaint(); // show game over screen
    }

    private void update() {
        player.update();
        manager.decreaseHP(player);

        manager.spawnObstacles(obstacles);
        manager.spawnItems(items, obstacles);
        manager.spawnCoins(coins, obstacles);

        // update obstacles movement and collisions
        Iterator<Obstacle> obsIt = obstacles.iterator();
        while (obsIt.hasNext()) {
            Obstacle o = obsIt.next();
            o.update();
            Rectangle obb = o.getBounds();
            if (player.getBounds().intersects(obb)) {
                if (player.isBig()) {
                    // destroy obstacle when big
                    obsIt.remove();
                } else {
                    player.takeHit();
                    obsIt.remove();
                    if (player.lives <= 0) {
                        running = false;
                    }
                }
            }
        }

        // items collection
        Iterator<Item> itemIt = items.iterator();
        while (itemIt.hasNext()) {
            Item it = itemIt.next();
            it.update();
            if (player.getBounds().intersects(it.getBounds())) {
                it.applyTo(player);
                itemIt.remove();
            }
        }

        // coins collection
        Iterator<Coin> coinIt = coins.iterator();
        while (coinIt.hasNext()) {
            Coin c = coinIt.next();
            c.update();
            if (player.getBounds().intersects(c.getBounds())) {
                GameManager.score += 10; // +10 per coin
                coinIt.remove();
            }
        }

        manager.updateScore();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // background selection: start uses forest, running uses theme
        BufferedImage bg = backgrounds[0];
        if (started && running) {
            int theme = manager.getThemeIndex();
            if (backgrounds != null && backgrounds[theme] != null) bg = backgrounds[theme];
        }

        if (bg != null) {
            g.drawImage(bg, 0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, null);
        } else {
            g.setColor(Color.CYAN);
            g.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        }

        if (!started) {
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.setColor(Color.WHITE);
            g.drawString("RUNNER ADVENTURE", Constants.SCREEN_WIDTH / 2 - 230, Constants.SCREEN_HEIGHT / 2 - 60);
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            g.drawString("Press ENTER to Start", Constants.SCREEN_WIDTH / 2 - 120, Constants.SCREEN_HEIGHT / 2);
            return;
        }

        // ground
        g.setColor(new Color(80, 160, 40));
        g.fillRect(0, Constants.GROUND_Y, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT - Constants.GROUND_Y);

        // draw entities
        player.draw(g);
        for (Obstacle o : obstacles) o.draw(g);
        for (Item it : items) it.draw(g);
        for (Coin c : coins) c.draw(g);

        // UI
        manager.drawUI(g, player);

        if (!running) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("GAME OVER", Constants.SCREEN_WIDTH / 2 - 160, Constants.SCREEN_HEIGHT / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Press ENTER to Restart", Constants.SCREEN_WIDTH / 2 - 130, Constants.SCREEN_HEIGHT / 2 + 40);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (!started && key == KeyEvent.VK_ENTER) {
            startGame();
        } else if (started && running) {
            if (key == KeyEvent.VK_SPACE) player.jump();
            if (key == KeyEvent.VK_S) player.slide();
        } else if (started && !running && key == KeyEvent.VK_ENTER) {
            startGame();
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
