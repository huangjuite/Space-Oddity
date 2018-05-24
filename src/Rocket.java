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
    boolean locating = false;
    int rocketImageSize;
    int radarRadius;
    int fule,tankSize;

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

        setFule(1000);
        setTankSize(1000);
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
        Rectangle rec  = new Rectangle(x-rocketImageSize/2,
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

        if(locating) drawRadar(g2d,at);

        drawHUD(g2d);

    }

    public void drawRadar(Graphics2D g2d,AffineTransform at){
        int fullSize = 10000;
        radarRadius += 100;
        if(radarRadius == fullSize) radarRadius = 0;
        g2d.setColor(new Color(1,1,1,1-radarRadius/(float)fullSize));
        int px = (int)(at.getTranslateX()+(x-radarRadius)*at.getScaleX());
        int py = (int)(at.getTranslateY()+(y-radarRadius)*at.getScaleY());
        int size = (int)(2*radarRadius*handler.getGame().getScale());
        g2d.drawOval(px,py,size,size);
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

        float hue = (float)(0.5*fule/(float)tankSize);
        g2d.setColor(new Color(Color.HSBtoRGB(hue,(float)0.77,1)));
        g2d.fillArc(centerX-rocketImageSize/2-80,
                centerY-rocketImageSize/2-80,
                2*radius+80, 2*radius+80,(int)(135+90*(1-fule/(float)tankSize)),
                (int)(90*(fule/(float)tankSize)));
        g2d.setColor(Color.gray);
        g2d.setStroke(new BasicStroke(3));
        g2d.fillOval(centerX-rocketImageSize/2-40,
                centerY-rocketImageSize/2-40,
                2*radius, 2*radius);
        g2d.setColor(Color.white);
        g2d.drawOval(centerX-rocketImageSize/2-40,
                centerY-rocketImageSize/2-40,
                2*radius, 2*radius);
        g2d.setStroke(new BasicStroke(4));
        double dis = Math.sqrt(volx*volx+voly*voly);
        hue = (float)(0.5 - ((dis>120)?0.5:dis*0.5/120));
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

    public int getFule() {
        return fule;
    }

    public void setFule(int fule) {
        if(fule>0) this.fule = fule;
    }

    public int getTankSize() {
        return tankSize;
    }

    public void setTankSize(int tankSize) {
        this.tankSize = tankSize;
    }

    public boolean isLocating() {
        return locating;
    }

    public void setLocating(boolean locating) {
        this.locating = locating;
    }
}
