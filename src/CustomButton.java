import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class CustomButton extends GameObject {

    public enum buttonType{JUPITER,MARS,EARTH,MOON,VENUS,MERCURY,NEPTUNE,SATURN,URANUS}
    private CustomButton.buttonType type;
    double earthSize = 1268;
    double planetScale = 0.05;
    private BufferedImage bufferedImage;

    public CustomButton(int x, int y, ID id,CustomButton.buttonType type, Handler handler) {
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
            case URANUS:
                typeName = "uranus.png";
                break;
        }
        omega = 0.5;
        try {
            bufferedImage = ImageIO.read(getClass().getResource(typeName));
        }catch (IOException e){
            e.printStackTrace();
        }

        orbitTrack = new Rectangle(0,0,400,70);
        orbitTrackAngle = 0;
        orbitOmega = -0.55;

    }


    public double getRadius(){
        return  earthSize*planetScale/2;
    }

    @Override
    public Rectangle getBounds(){
        Rectangle rec = new Rectangle(x-(int)getRadius(),y-(int)getRadius(),
                (int)(2*getRadius()),(int)(2*getRadius()));
        return rec;
    }

    @Override
    public void tick() {
        planetScale = 0.05;
        if(handler.getStatus()== Handler.Status.STOP) {
            orbitAngle = (orbitAngle+orbitOmega)%360;
            double angle = orbitAngle;
            x = (int) (orbitTrack.getWidth() * Math.cos(angle * Math.PI / 180.));
            y = (int) (orbitTrack.getHeight() * Math.sin(angle * Math.PI / 180.));

            double factor;
            angle = (angle<0)?360+angle:angle;
            if(angle<=180){
                factor = 1-Math.abs((angle-90)/90);
            }else{
                angle = angle - 180;
                factor = (1-Math.abs((angle-90)/90))*(-1);
            }
            planetScale = 0.05 + factor*0.03;

            int tx = x, ty = y;
            angle = orbitTrackAngle * Math.PI / 180.;
            x = (int) (Math.cos(angle) * tx - Math.sin(angle) * ty);
            y = (int) (Math.sin(angle) * tx + Math.cos(angle) * ty);
        }
        degree = (degree+omega)%360;
    }

    @Override
    public void render(Graphics g, AffineTransform at) {
        AffineTransform newAt = AffineTransform.getTranslateInstance(
                x-bufferedImage.getWidth()*planetScale/2,
                y-bufferedImage.getHeight()*planetScale/2);
        newAt.scale(planetScale,planetScale);
        newAt.rotate(Math.toRadians(degree),bufferedImage.getWidth()/2,bufferedImage.getHeight()/2);
        Graphics2D g2d = (Graphics2D) g;

        AffineTransform preAt = new AffineTransform();
        preAt.translate(handler.getGame().getWidth()/2,handler.getGame().getHeight()/2);

        newAt.preConcatenate(preAt);
        g2d.drawImage(bufferedImage,newAt,null);
    }
}
