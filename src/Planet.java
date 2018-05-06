import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Planet extends GameObject {
    public enum planetType{JUPITER,MARS,EARTH,MOON,VENUS,MERCURY,NEPTUNE,SATURN}
    private planetType type;

    public Planet(int x, int y, ID id,planetType type, Handler handler) {
        super(x, y, id,handler);
        this.type = type;
        String typeName="jupiter.png";
        switch (type){
            case JUPITER:
                typeName = "jupiter.png";
                break;
            case MARS:
                typeName = "mars.png";
                break;
            case MOON:
                typeName = "moon.png";
                break;
            case EARTH:
                typeName = "earth.png";
                break;
            case VENUS:
                typeName = "venus.png";
                break;
            case MERCURY:
                typeName = "mercury.png";
                break;
            case SATURN:
                typeName = "saturn.png";
                break;
            case NEPTUNE:
                typeName = "neptune.png";
                break;
        }
        omega=0.5;
        try {
            bufferedImage = ImageIO.read(getClass().getResource(typeName));
        }catch (IOException e){
            e.printStackTrace();
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
