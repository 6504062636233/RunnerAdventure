import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class GameManager {
    private Random random = new Random();
    public static int score = 0;
    public static int scoreMultiplier = 1;
    public static double gameSpeed = 1.0;
    private int obstacleTimer = 0;
    private int itemTimer = 0;
    private int coinTimer = 0;

    private int distance = 0;
    private int themeIndex = 0;

    // spawn probability for moving obstacles (e.g., 35%)
    private double movingChance = 0.35;

    public void updateScore() {
        score += 1 * scoreMultiplier;
        distance += 1;
        if (distance % 2000 == 0) {
            themeIndex = (themeIndex + 1) % 3;
        }
    }

    public int getThemeIndex() { return themeIndex; }

    public void drawUI(Graphics g, Player player) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("HP: " + player.hp, 20, 30);
        g.drawString("Lives: " + player.lives, 20, 60);
        g.drawString("Score: " + score, 20, 90);
        g.drawString("Distance: " + distance, 20, 120);
    }

    public void decreaseHP(Player player) {
        player.hp -= 0.02;
        if (player.hp < 0) player.hp = 0;
    }

    public void spawnObstacles(ArrayList<Obstacle> list) {
        obstacleTimer++;
        if (obstacleTimer > 90) {
            int height = 30 + random.nextInt(40);
            boolean moving = random.nextDouble() < movingChance;
            list.add(new Obstacle(Constants.SCREEN_WIDTH, Constants.GROUND_Y - height, 48, height, 6 + random.nextInt(3), moving));
            obstacleTimer = 0;
        }
        list.removeIf(Obstacle::isOffScreen);
    }

    public void spawnItems(ArrayList<Item> items, ArrayList<Obstacle> obstacles) {
        itemTimer++;
        if (itemTimer > 220) {
            int tries = 0;
            while (tries < 8) {
                int y = Constants.GROUND_Y - 120 - random.nextInt(120);
                int x = Constants.SCREEN_WIDTH;
                boolean ok = true;
                for (Obstacle o : obstacles) {
                    if (Math.abs(o.x - x) < 150) { ok = false; break; }
                }
                if (ok) {
                    Item.Type type = Item.Type.values()[random.nextInt(Item.Type.values().length)];
                    items.add(new Item(x, y, 28, 5, type));
                    break;
                }
                tries++;
            }
            itemTimer = 0;
        }
        items.removeIf(Item::isOffScreen);
    }

    public void spawnCoins(ArrayList<Coin> coins, ArrayList<Obstacle> obstacles) {
        coinTimer++;
        if (coinTimer > 40) {
            // spawn a small line of coins sometimes
            int lane = 1 + random.nextInt(3);
            int baseY = Constants.GROUND_Y - 80 - lane * 20;
            int count = 1 + random.nextInt(3);
            for (int i = 0; i < count; i++) {
                int x = Constants.SCREEN_WIDTH + i * 30;
                // ensure coin not too close to obstacles
                boolean ok = true;
                for (Obstacle o : obstacles) {
                    if (Math.abs(o.x - x) < 120) { ok = false; break; }
                }
                if (ok) coins.add(new Coin(x, baseY, 20, 5));
            }
            coinTimer = 0;
        }
        coins.removeIf(Coin::isOffScreen);
    }
}
