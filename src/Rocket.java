import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;

public class Rocket extends GameObject {

    Handler handler;
    BufferedImage bufferedImage;
    public Rocket(int x, int y, ID id,Handler handler) {
        super(x, y, id);
        this.handler = handler;
        try {
            bufferedImage = ImageIO.read(getClass().getResource("rocket.png"));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public Rectangle getBounds(){

        return new Rectangle(x,y,32,32);
    }

    public void collision(){
        for(GameObject tempObject:handler.objects){
            if(tempObject.getId()==ID.Planet){
                System.out.println("collision");
            }
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
