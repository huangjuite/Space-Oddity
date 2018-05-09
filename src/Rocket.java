import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;

public class Rocket extends GameObject {
    BufferedImage rocketImage[];
    int boostStatus=0;
    double rocketImageScale = 0.8;

    public Rocket(int x, int y, ID id,Handler handler) {
        super(x, y, id,handler);
        rocketImage = new BufferedImage[5];
        try {
            for(int i=0;i<5;i++){
                BufferedImage image = ImageIO.read(getClass().getResource("rocket"+i+".png"));
                rocketImage[i] = resizeImage(image,rocketImageScale);
            }


        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public BufferedImage getRocketImage(){
        return rocketImage[0];
    }

    public void setBoostImage(int n,boolean b){
        if (b) {
            boostStatus |= 1<<n;
        }
        else {
            boostStatus &= ~(1<<n);
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
        AffineTransform newat = AffineTransform.getTranslateInstance(
                x-rocketImage[0].getWidth()/2,
                y-rocketImage[0].getHeight()/2);
        newat.rotate(Math.toRadians(degree),
                rocketImage[0].getWidth()/2,
                rocketImage[0].getHeight()/2);
        Graphics2D g2d = (Graphics2D) g;
        newat.preConcatenate(at);
        g2d.drawImage(rocketImage[0],newat,null);

        for(int i=1;i<5;i++){
            if((boostStatus&(1<<i)) != 0){
                g2d.drawImage(rocketImage[i],newat,null);
            }
        }
    }
}
