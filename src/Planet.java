import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Planet extends GameObject {
    Handler handler;
    BufferedImage bufferedImage;

    public Planet(int x, int y, ID id, Handler handler) {
        super(x, y, id);
        this.handler = handler;
        omega=0.5;
        try {
            bufferedImage = ImageIO.read(getClass().getResource("jupiter-planet.png"));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public Rectangle getBounds() {
        return null;
    }

    @Override
    public void tick() {
        degree+=omega;
    }

    @Override
    public void render(Graphics g,AffineTransform at) {
        AffineTransform newat = AffineTransform.getTranslateInstance(x-bufferedImage.getWidth()/2,y-bufferedImage.getHeight()/2);
        newat.rotate(Math.toRadians(degree),bufferedImage.getWidth()/2,bufferedImage.getHeight()/2);
        Graphics2D g2d = (Graphics2D) g;
        newat.preConcatenate(at);
        g2d.drawImage(bufferedImage,newat,null);
    }
}
