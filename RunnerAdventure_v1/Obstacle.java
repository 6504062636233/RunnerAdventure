import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Obstacle {
    public int x, y, width, height;
    public int speed;
    private boolean movingVertically = false;
    private int baseY;
    private int amplitude = 20; 
    private double phase = 0;
    private double freq = 0.05; 

    private BufferedImage sprite;

    public Obstacle(int x, int y, int width, int height, int speed, boolean moving) {
        this.x = x;
        this.y = y;
        this.baseY = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.movingVertically = moving;
        try {
            sprite = ImageIO.read(new File("assets/obstacle.png"));
        } catch (Exception e) {
            sprite = null;
        }
        // randomize some properties
        if (movingVertically) {
            amplitude = 20 + (int)(Math.random() * 30);
            freq = 0.03 + Math.random() * 0.06;
            phase = Math.random() * Math.PI * 2;
        }
    }

    public void update() {
        x -= (int)(speed * GameManager.gameSpeed);
        if (movingVertically) {
            phase += freq;
            y = baseY + (int)(Math.sin(phase) * amplitude);
        }
    }

    public void draw(Graphics g) {
        if (sprite != null) {
            g.drawImage(sprite, x, y, width, height, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isOffScreen() {
        return x + width < 0;
    }
}
