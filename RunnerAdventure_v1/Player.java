import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Player {
    public int x, y;
    public int width = Constants.PLAYER_WIDTH;
    public int height = Constants.PLAYER_HEIGHT;
    private int yVelocity = 0;
    private boolean jumping = false;
    private boolean sliding = false;

    public int hp = 100;        
    public int lives = 3;       

    // temporary power-ups
    private long bigEndTime = 0;
    private long speedEndTime = 0;
    public double sizeMultiplier = 1.0;

    private BufferedImage sprite;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        try {
            sprite = ImageIO.read(new File("assets/player.png"));
        } catch (Exception e) {
            sprite = null;
            System.out.println("Player sprite not found, using fallback rectangle.");
        }
    }

    public void update() {
        long now = System.currentTimeMillis();
        // apply gravity if jumping
        if (jumping) {
            yVelocity += Constants.GRAVITY;
            y += yVelocity;
            if (y >= Constants.GROUND_Y - height) {
                y = Constants.GROUND_Y - height;
                jumping = false;
                yVelocity = 0;
            }
        }

        if (now > bigEndTime) {
            sizeMultiplier = 1.0;
        } else {
            sizeMultiplier = 2.0; 
        }
        if (now > speedEndTime) {
            GameManager.gameSpeed = 1.0;
        }
    }

    public void draw(Graphics g) {
        int drawW = (int)(width * sizeMultiplier);
        int drawH = (int)(height * sizeMultiplier);
        if (sprite != null) {
            g.drawImage(sprite, x, y - (drawH - height), drawW, drawH, null); // adjust y when taller
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(x, y - (drawH - height), drawW, drawH);
        }
    }

    public void jump() {
        if (!jumping) {
            jumping = true;
            yVelocity = -Constants.JUMP_FORCE;
        }
    }

    public void slide() {
        sliding = true;
    }

    public void takeHit() {
        lives--;
        System.out.println("Player hit! Lives left: " + lives);
    }

    public void heal(int amount) {
        hp = Math.min(100, hp + amount);
    }

    public void makeBig(long durationMs) {
        bigEndTime = System.currentTimeMillis() + durationMs;
    }

    public void speedBoost(long durationMs, double multiplier) {
        speedEndTime = System.currentTimeMillis() + durationMs;
        GameManager.gameSpeed = multiplier;
    }

    public Rectangle getBounds() {
        int drawW = (int)(width * sizeMultiplier);
        int drawH = (int)(height * sizeMultiplier);
        return new Rectangle(x, y - (drawH - height), drawW, drawH);
    }

    public boolean isBig() {
        return sizeMultiplier > 1.0;
    }
}
