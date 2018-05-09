import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.security.PrivateKey;

public class Planet extends GameObject {
    public enum planetType{JUPITER,MARS,EARTH,MOON,VENUS,MERCURY,NEPTUNE,SATURN,URANUS}
    private planetType type;
    double earthSize = 1268;
    private double planetScale=1;
    private BufferedImage bufferedImage;

    public Planet(int x, int y, ID id,planetType type, Handler handler) {
        super(x, y, id,handler);
        this.type = type;
        String typeName="jupiter.png";
        switch (type){
            case JUPITER:
                typeName = "jupiter.png";
                planetScale *= 11.21;
                break;
            case MARS:
                typeName = "mars.png";
                planetScale *= 0.532;
                break;
            case MOON:
                typeName = "moon.png";
                planetScale *= 0.2724;
                break;
            case EARTH:
                typeName = "earth.png";
                planetScale *= 1;
                break;
            case VENUS:
                typeName = "venus.png";
                planetScale *= 0.949;
                break;
            case MERCURY:
                typeName = "mercury.png";
                planetScale *= 0.383;
                break;
            case SATURN:
                typeName = "saturn.png";
                planetScale *= 9.45;
                break;
            case NEPTUNE:
                typeName = "neptune.png";
                planetScale *= 3.88;
                break;
            case URANUS:
                typeName = "uranus.png";
                planetScale *= 4.01;
                break;
        }
        omega=0.5;
        try {
            bufferedImage = ImageIO.read(getClass().getResource(typeName));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public double getRadius(){
        return  earthSize*planetScale/2;
    }

    @Override
    public void tick() {
        x+=volx;
        y+=voly;
        degree+=omega;
    }

    @Override
    public void render(Graphics g,AffineTransform at) {
        AffineTransform newAt = AffineTransform.getTranslateInstance(
                x-bufferedImage.getWidth()*planetScale/2,
                y-bufferedImage.getHeight()*planetScale/2);
        newAt.scale(planetScale,planetScale);
        newAt.rotate(Math.toRadians(degree),bufferedImage.getWidth()/2,bufferedImage.getHeight()/2);
        Graphics2D g2d = (Graphics2D) g;
        newAt.preConcatenate(at);
        g2d.drawImage(bufferedImage,newAt,null);
    }
}
