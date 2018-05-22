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
    int rocketImageSize;

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
        rocketImageSize = rocketImage[0].getWidth();
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
    public Rectangle getBounds(){
        Rectangle rec = new Rectangle(x-rocketImageSize/2,
                y-rocketImageSize/2,
                rocketImageSize,
                rocketImageSize);
        return rec;
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
                x-rocketImageSize/2,
                y-rocketImageSize/2);
        newat.rotate(Math.toRadians(degree),
                rocketImageSize/2,
                rocketImageSize/2);
        Graphics2D g2d = (Graphics2D) g;
        newat.preConcatenate(at);
        g2d.drawImage(rocketImage[0],newat,null);

        for(int i=1;i<5;i++){
            if((boostStatus&(1<<i)) != 0){
                g2d.drawImage(rocketImage[i],newat,null);
            }
        }

        drawHUD(g2d);

    }

    public void drawHUD(Graphics2D g2d){
        AffineTransform newat = new AffineTransform();
        newat.setToIdentity();
        newat.rotate(Math.toRadians(degree),
                rocketImageSize/2,
                rocketImageSize/2);
        AffineTransform preAt = new AffineTransform();
        int radius = rocketImageSize/2+40;
        int centerX = handler.getGame().getWidth()-rocketImageSize/2-40;
        int centerY = handler.getGame().getHeight()-rocketImageSize/2-40;
        preAt.translate(centerX-rocketImageSize/2,
                centerY-rocketImageSize/2);

        newat.preConcatenate(preAt);
        g2d.setColor(Color.white);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(centerX-rocketImageSize/2-40,
                centerY-rocketImageSize/2-40,
                2*radius, 2*radius);
        g2d.setStroke(new BasicStroke(4));
        double dis = Math.sqrt(volx*volx+voly*voly);
        float hue = (float)(0.5 - ((dis>120)?0.5:dis*0.5/120));
        g2d.setColor(new Color(Color.HSBtoRGB(hue,(float)0.77,(float)1)));

        int dirx = (int)(volx/dis*radius);
        int diry = (int)(voly/dis*radius);
        g2d.drawLine(centerX, centerY,centerX+dirx, centerY+diry);
        g2d.drawImage(rocketImage[0],newat,null);

        for(int i=1;i<5;i++){
            if((boostStatus&(1<<i)) != 0){
                g2d.drawImage(rocketImage[i],newat,null);
            }
        }
    }
}
