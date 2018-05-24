import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

public class Asteroid extends GameObject{
    private BufferedImage bufferedImage;
    private double scale=0.25;
    private ArrayList<Integer> arrayX = new ArrayList<>();
    private ArrayList<Integer> arrayY = new ArrayList<>();
    private int count;
    public Asteroid(int x, int y, ID id, Handler handler)
    {
        super(x, y, id,handler);
        count = 0;
        try {
            bufferedImage = ImageIO.read(getClass().getResource("Asteroid.png"));
            bufferedImage = resizeImage(bufferedImage, scale);
        }catch (IOException e){
            e.printStackTrace();
        }
        arrayX.add(x);
        arrayY.add(y);
    }
    public void addAsteroid(int x, int y)
    {
        count++;
        arrayX.add(x);
        arrayY.add(y);
    }
    public int getX(){return arrayX.get(count-1);}
    public int getY(){return arrayY.get(count-1);}
    @Override
    public void tick() {
        x+=volx;
        y+=voly;
        degree+=omega;
    }
    @Override
    public void render(Graphics g, AffineTransform at) {
        Graphics2D g2d = (Graphics2D) g;
        for(int i=0 ; i<=count ; i++){
            AffineTransform newAt = AffineTransform.getTranslateInstance(
                    arrayX.get(i) - bufferedImage.getWidth() * scale / 2,
                    arrayY.get(i) - bufferedImage.getHeight() * scale / 2);
            newAt.preConcatenate(at);
            g2d.drawImage(bufferedImage,newAt, null);
        }
    }
    @Override
    public  Rectangle getBounds(){
        Rectangle rec = new Rectangle(x-(int)bufferedImage.getWidth()/2,y-(int)bufferedImage.getHeight()/2,
                Math.abs(arrayX.get(count)-x),Math.abs(arrayY.get(count)-y));
        return rec;
    }
}
