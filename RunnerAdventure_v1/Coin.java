import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Coin {
    public int x, y, size, speed;
    private BufferedImage sprite;
    public Coin(int x, int y, int size, int speed) {
        this.x = x; this.y = y; this.size = size; this.speed = speed;
        try { sprite = ImageIO.read(new File("assets/coin.png")); } catch (Exception e) { sprite = null; }
    }
    public void update() { x -= (int)(speed * GameManager.gameSpeed); }
    public void draw(Graphics g) {
        if (sprite != null) g.drawImage(sprite, x, y, size, size, null);
        else { g.setColor(Color.YELLOW); g.fillOval(x,y,size,size); }
    }
    public Rectangle getBounds() { return new Rectangle(x,y,size,size); }
    public boolean isOffScreen() { return x + size < 0; }
}
