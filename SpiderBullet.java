
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

 public class SpiderBullet extends Attack {
        public static final int HEIGHT = 16;
        public static final int WIDTH = 16;
        double normalizedX, normalizedY;
        private static BufferedImage sprite;

        static {
            try {
                BufferedImage img = ImageIO.read(SpiderBullet.class.getResourceAsStream("resources/Sprites/Spider/spiderbullet.png"));
                sprite = img;
            } catch (IOException e) {
                System.out.println("Exception in SpiderBullet setSprites()" + e);
            }
        }

        public SpiderBullet(Entity owner, int x, int y, double nX, double nY){
            attackNum++;
            id = attackNum;
            identifier = NetworkProtocol.SPIDERBULLET;
            this.owner = owner;
            isFriendly = false;
            damage = 1;
            //Temporary hitPoints allocation
            width = 16;
            height = 16;
            worldX = x;
            worldY = y;
            speed = 2; // Note: Do not make equal to 1. When multiplied with floats, becomes 0.
            normalizedX = nX;
            normalizedY = nY;

            //For checking attack duration
            duration = 2000;
            setExpirationTime(duration);

            matchHitBoxBounds();
        }

        @Override
        public void draw(Graphics2D g2d, int xOffset, int yOffset){
            g2d.drawImage(sprite, xOffset, yOffset, width, height, null);
        }

        @Override
        public void updateEntity(ServerMaster gsm) {
            moveBullet();
        }

        @Override
        public void matchHitBoxBounds() {
            hitBoxBounds = new int[4];
            hitBoxBounds[0]= worldY;
            hitBoxBounds[1] = worldY + height;
            hitBoxBounds[2]= worldX;
            hitBoxBounds[3] = worldX + width;
        }

        private void moveBullet(){
            worldX += speed * normalizedX;
            worldY += speed * normalizedY;
            matchHitBoxBounds();
        }
        
    }