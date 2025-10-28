import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Item {
    public enum Type { HEART, BIG, SPEED }

    public int x, y, size, speed;
    public Type type;
    private BufferedImage sprite;

    public Item(int x, int y, int size, int speed, Type type) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.speed = speed;
        this.type = type;
        try {
            sprite = ImageIO.read(new File("assets/item_"+type.toString().toLowerCase()+".png"));
        } catch (Exception e) {
            sprite = null;
        }
    }

    public void update() {
        x -= (int)(speed * GameManager.gameSpeed);
    }

    public void draw(Graphics g) {
        if (sprite != null) {
            g.drawImage(sprite, x, y, size, size, null);
        } else {
            switch (type) {
                case HEART: g.setColor(Color.PINK); break;
                case BIG: g.setColor(Color.CYAN); break;
                case SPEED: g.setColor(Color.ORANGE); break;
            }
            g.fillOval(x, y, size, size);
        }
    }

    public void applyTo(Player player) {
        switch (type) {
            case HEART -> player.heal(30);
            case BIG -> player.makeBig(5000); // 5 seconds
            case SPEED -> player.speedBoost(4000, 1.8); // 4s, 1.8x
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    public boolean isOffScreen() {
        return x + size < 0;
    }
}
