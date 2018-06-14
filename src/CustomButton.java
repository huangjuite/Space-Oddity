import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class CustomButton extends GameObject {


    private double earthSize = 1268;
    private double planetScale = 0.05;
    private BufferedImage bufferedImage;
    private boolean hover;
    private String buttonName;

    public CustomButton(int x, int y, ID id, ObjectType type, Handler handler) {
        super(x, y, id,type,handler);
        String typeName="jupiter.png";
        switch (type){
            case JUPITER:
                typeName = "jupiter.png";
                buttonName = "Make Your Universe";
                break;
            case EARTH:
                typeName = "earth.png";
                buttonName = "Play";
                break;
            case SATURN:
                typeName = "saturn.png";
                buttonName = "How To Play";
                break;
            case NEPTUNE:
                typeName = "neptune.png";
                buttonName = "Author";
                break;
            case MARS:
                typeName = "mars.png";
                break;
            case MOON:
                typeName = "moon.png";
                break;
            case VENUS:
                typeName = "venus.png";
                break;
            case MERCURY:
                typeName = "mercury.png";
                break;
            case URANUS:
                typeName = "uranus.png";
                break;
            case SUN:
                typeName = "sun.png";
                break;
        }
        omega = 0.5;
        try {
            bufferedImage = ImageIO.read(getClass().getResource(typeName));
        }catch (IOException e){
            e.printStackTrace();
        }

        orbitTrack = new Rectangle(0,0,400,70);
        orbitTrackAngle = 45;
        orbitOmega = -0.40;

    }


    public void setHover(boolean hover) {
        this.hover = hover;
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
        if(handler.getStatus()== Handler.Status.STARTSCENE) {
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
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform newAt = AffineTransform.getTranslateInstance(
                x-bufferedImage.getWidth()*planetScale/2,
                y-bufferedImage.getHeight()*planetScale/2);
        if(hover){
            newAt.scale(planetScale*1.5, planetScale*1.5);
            g2d.setFont(new Font("Arial", Font.BOLD, 25));
            FontMetrics fm = g2d.getFontMetrics();
            g2d.setColor(Color.white);
            if(handler.getStatus()==Handler.Status.STARTSCENE) {
                g2d.drawString(buttonName
                        , x + handler.getGame().getWidth() / 2 - fm.stringWidth(buttonName) / 2 + 20
                        , y + handler.getGame().getHeight() / 2 - 70);
            }
            else if(handler.getStatus()==Handler.Status.EDIT) {
                g2d.drawString(getType().toString()
                        , x + handler.getGame().getWidth() / 2 - fm.stringWidth(getType().toString())-50
                        , y + handler.getGame().getHeight() / 2 + 20);
            }
        }
        else{
            newAt.scale(planetScale,planetScale);
        }

        newAt.rotate(Math.toRadians(degree),bufferedImage.getWidth()/2,bufferedImage.getHeight()/2);
        AffineTransform preAt = new AffineTransform();
        preAt.translate(handler.getGame().getWidth()/2,handler.getGame().getHeight()/2);
        newAt.preConcatenate(preAt);
        g2d.drawImage(bufferedImage,newAt,null);

        if(handler.getStatus()== Handler.Status.STARTSCENE){
            drawOrbitTrack(g2d,at,orbitTrack,orbitTrackAngle);
        }
    }


    @Override
    public String toString(){return null;}

}
