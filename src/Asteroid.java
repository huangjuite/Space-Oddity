import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

public class Asteroid extends GameObject{
    private BufferedImage bufferedImage;
    private double scale=0.25;
    private ArrayList<Integer> arrayX = new ArrayList<>();
    private ArrayList<Integer> arrayY = new ArrayList<>();
    public Asteroid(int x, int y, ID id, Handler handler)
    {
        super(x, y, id,ObjectType.ASTEROID,handler);
        System.out.println("new");
        try {
            bufferedImage = ImageIO.read(getClass().getResource("Asteroid.png"));
            bufferedImage = resizeImage(bufferedImage, scale);
        }catch (IOException e){
            e.printStackTrace();
        }
        arrayX.add((int)(x- bufferedImage.getWidth() / 2));
        arrayY.add((int)(y- bufferedImage.getHeight() / 2));
    }
    public void addAsteroid(int x, int y)
    {
        arrayX.add((int)(x- bufferedImage.getWidth() / 2));
        arrayY.add((int)(y- bufferedImage.getHeight()  / 2));
    }

    public void show()
    {
        for(int i = 0 ; i<arrayX.size() ; i++)
            System.out.println(i+"*"+arrayX.get(i)+"*"+arrayY.get(i));
    }
    public int getCount(){return arrayX.size();}
    public int getX(){return arrayX.get(arrayX.size()-1);}
    public int getY(){return arrayY.get(arrayY.size()-1);}
    public void setNewAsteroid(Point2D LeftUpPoint, Point2D RightDownPoint)
    {
        for(int i =0 ; i<arrayX.size() ; i++)
            if(arrayX.get(i)+64>=LeftUpPoint.getX()&&arrayX.get(i)+64<=RightDownPoint.getX()
                    &&arrayY.get(i)+64>=LeftUpPoint.getY()&&arrayY.get(i)+64<=RightDownPoint.getY())
            {
                arrayX.remove(i);
                arrayY.remove(i);
                i--;
            }
    }
    @Override
    public void translate(int dx,int dy){

    }

    @Override
    public void tick() {
        x+=volx;
        y+=voly;
        degree+=omega;
    }
    @Override
    public void render(Graphics g, AffineTransform at) {
        Graphics2D g2d = (Graphics2D) g;
        for(int i=0 ; i<arrayX.size() ; i++){
            AffineTransform newAt = AffineTransform.getTranslateInstance(arrayX.get(i) , arrayY.get(i) );
            newAt.preConcatenate(at);
            g2d.drawImage(bufferedImage,newAt, null);
        }
    }
    @Override
    public  Rectangle getBounds() {
        if (arrayX.size() > 1){
            return new Rectangle(Collections.min(arrayX), Collections.min(arrayY), Collections.max(arrayX) - Collections.min(arrayX), Collections.max(arrayY) - Collections.min(arrayY));
        }
        else {
            Rectangle r = new Rectangle(Collections.min(arrayX), Collections.min(arrayY), 128, 128);
            return r;
        }
    }
}
