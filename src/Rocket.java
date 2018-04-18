import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Rocket extends GameObject {

    Handler handler;
    Image image;
    BufferedImage bufferedImage;
    public Rocket(int x, int y, ID id,Handler handler) {
        super(x, y, id);
        this.handler = handler;
        try {
            image = ImageIO.read(new File("rocket.png"));
            bufferedImage = ImageIO.read(new File("rocket.png"));
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
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(image,x-bufferedImage.getWidth()/2,y-bufferedImage.getHeight()/2,null);
    }
}
