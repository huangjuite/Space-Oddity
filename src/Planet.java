import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Planet extends GameObject {
    public enum planetType{JUPITER,MARS,EARTH,MOON,VENUS,MERCURY,NEPTUNE,SATURN,URANUS}
    private planetType type;
    private double planetSize=1;

    public Planet(int x, int y, ID id,planetType type, Handler handler) {
        super(x, y, id,handler);
        this.type = type;
        String typeName="jupiter.png";
        switch (type){
            case JUPITER:
                typeName = "jupiter.png";
                planetSize *= 11.21;
                break;
            case MARS:
                typeName = "mars.png";
                planetSize *= 0.532;
                break;
            case MOON:
                typeName = "moon.png";
                planetSize *= 0.2724;
                break;
            case EARTH:
                typeName = "earth.png";
                planetSize *= 1;
                break;
            case VENUS:
                typeName = "venus.png";
                planetSize *= 0.949;
                break;
            case MERCURY:
                typeName = "mercury.png";
                planetSize *= 0.383;
                break;
            case SATURN:
                typeName = "saturn.png";
                planetSize *= 9.45;
                break;
            case NEPTUNE:
                typeName = "neptune.png";
                planetSize *= 3.88;
                break;
            case URANUS:
                typeName = "uranus.png";
                planetSize *= 4.01;
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
        AffineTransform newAt = AffineTransform.getTranslateInstance(x-bufferedImage.getWidth()/2,y-bufferedImage.getHeight()/2);
        newAt.rotate(Math.toRadians(degree),bufferedImage.getWidth()/2,bufferedImage.getHeight()/2);
        newAt.scale(planetSize,planetSize);
        Graphics2D g2d = (Graphics2D) g;
        newAt.preConcatenate(at);

        g2d.drawImage(bufferedImage,newAt,null);
    }
}
