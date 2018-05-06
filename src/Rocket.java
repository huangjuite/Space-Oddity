import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;

public class Rocket extends GameObject {
    BufferedImage boostImage;
    BufferedImage noboostImage;
    public enum BoostSide{FRONT,BACK,RIGHT,LEFT,OFF}
    public BoostSide boostSide = BoostSide.OFF;

    public Rocket(int x, int y, ID id,Handler handler) {
        super(x, y, id,handler);
        try {
            noboostImage = ImageIO.read(getClass().getResource("rocket.png"));
            boostImage = ImageIO.read(getClass().getResource("rocketBoost.png"));
            bufferedImage = noboostImage;
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setBoostImage(boolean b){
        if(b){
            bufferedImage=boostImage;
        }
        else{
            bufferedImage=noboostImage;
        }
    }

    @Override
    public void tick() {
        x+=volx;
        y+=voly;
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
