import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class Planet extends GameObject {
    double earthSize = 1268;
    private double planetScale=1;
    private double planetMass = 60/5.97;
    private BufferedImage bufferedImage;
    private Polygon boundPolygon;
    private double boundsMargin = 1.5;
    private double sizeError = 1;

    public Planet(int x, int y, ID id,ObjectType type, Handler handler){
        super(x, y, id,type,handler);
        String typeName="jupiter.png";
        switch (type){
            case JUPITER:
                typeName = "jupiter.png";
                planetScale *= 11.21;
                planetMass *= 1898;
                boundsMargin = 1.5;
                sizeError = 1;
                break;
            case MARS:
                typeName = "mars.png";
                planetScale *= 0.532;
                planetMass *= 0.642;
                boundsMargin = 1.5;
                sizeError = 1;
                break;
            case MOON:
                typeName = "moon.png";
                planetScale *= 0.2724;
                planetMass *= 0.073;
                boundsMargin = 1.5;
                sizeError = 1;
                break;
            case EARTH:
                typeName = "earth.png";
                planetScale *= 1;
                planetMass *= 5.97;
                boundsMargin = 1.9;
                sizeError = 1.05;
                break;
            case VENUS:
                typeName = "venus.png";
                planetScale *= 0.949;
                planetMass *= 4.87;
                boundsMargin = 1.5;
                sizeError = 1;
                break;
            case MERCURY:
                typeName = "mercury.png";
                planetScale *= 0.383;
                planetMass *= 0.330;
                boundsMargin = 1.5;
                sizeError = 1;
                break;
            case SATURN:
                typeName = "saturn.png";
                planetScale *= 9.45;
                planetMass *= 568;
                boundsMargin = 1.5;
                sizeError = 1.19;
                break;
            case NEPTUNE:
                typeName = "neptune.png";
                planetScale *= 3.88;
                planetMass *= 102;
                boundsMargin = 1.5;
                sizeError = 1;
                break;
            case URANUS:
                typeName = "uranus.png";
                planetScale *= 4.01;
                planetMass *= 86.8;
                boundsMargin = 1.5;
                sizeError = 1.05;
                break;
            case SUN:
                typeName = "sun.png";
                planetScale *= 20;
                planetMass *= 4000;
                boundsMargin = 1.5;
                sizeError = 0.95;
        }
        omega=0.5;
        try {
            bufferedImage = ImageIO.read(getClass().getResource(typeName));
        }catch (IOException e){
            e.printStackTrace();
        }
        boundPolygon = new Polygon();
    }

    public double getBoundsMargin() {
        return boundsMargin;
    }

    public double getRadius(){
        return  earthSize*planetScale/2;
    }

    public Polygon getBoundPolygon(AffineTransform at,boolean acurate) {
        boundPolygon.reset();
        for(int i=0;i<360;i++){
            double radian = Math.toRadians(i);
            double rad = getRadius()*((acurate)?1:boundsMargin);
            int dx = (int)((x+(Math.cos(radian)*rad))*at.getScaleX()+at.getTranslateX());
            int dy = (int)((y+(Math.sin(radian)*rad))*at.getScaleY()+at.getTranslateY());
            boundPolygon.addPoint(dx,dy);
        }
        return boundPolygon;
    }

    @Override
    public Rectangle getBounds(){
        Rectangle rec = new Rectangle(x-(int)getRadius(),y-(int)getRadius(),
                (int)(2*getRadius()),(int)(2*getRadius()));
        return rec;
    }

    public double getPlanetMass() {
        return planetMass;
    }

    @Override
    public void tick() {
        double omega = orbitOmega;
        if(orbitCenterObject!=null){
            Rectangle rec = getOrbitTrack();
            double dis = Math.sqrt(Math.pow(orbitCenterObject.getX()-x,2)+Math.pow(orbitCenterObject.getY()-y,2));
            double c = Math.sqrt(Math.pow(rec.getWidth(),2)-Math.pow(rec.getHeight(),2));
            int px = (int)(orbitCenterObject.getX()-c*(-Math.cos(Math.toRadians(getOrbitTrackAngle()))));
            int py = (int)(orbitCenterObject.getY()-c*(-Math.sin(Math.toRadians(getOrbitTrackAngle()))));
            setOrbitTrackPosition(px,py);
            dis = (dis>1)?dis:1;
            omega = orbitOmega*((rec.getWidth()+c)/dis);
        }
        orbitAngle = (orbitAngle+omega)%360;
        double angle = orbitAngle;
        int tx,ty;
        tx = (int) (orbitTrack.getWidth() * Math.cos(angle * Math.PI / 180.));
        ty = (int) (orbitTrack.getHeight() * Math.sin(angle * Math.PI / 180.));

        angle = orbitTrackAngle * Math.PI / 180.;
        x = (int)(orbitTrack.getX()+Math.cos(angle) * tx - Math.sin(angle) * ty);
        y = (int)(orbitTrack.getY()+Math.sin(angle) * tx + Math.cos(angle) * ty);

        degree+=getOmega();
    }

    @Override
    public void render(Graphics g,AffineTransform at) {
        double scaleFactor = planetScale*sizeError;
        AffineTransform newAt = AffineTransform.getTranslateInstance(
                x-bufferedImage.getWidth()*scaleFactor/2,
                y-bufferedImage.getHeight()*scaleFactor/2);
        newAt.scale(scaleFactor,scaleFactor);
        newAt.rotate(Math.toRadians(degree),bufferedImage.getWidth()/2,bufferedImage.getHeight()/2);
        Graphics2D g2d = (Graphics2D) g;
        newAt.preConcatenate(at);
        if(handler.showingRadar())
            drawRadar(g2d,at);
        g2d.drawImage(bufferedImage,newAt,null);

        drawOrbitTrack(g2d,at,orbitTrack,orbitTrackAngle);
        if(isSelected){
            drawCircularBounds(g2d,at);
        }
    }

    public void drawCircularBounds(Graphics2D g2d,AffineTransform at){
        Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{10}, 0);
        g2d.setStroke(dashed);
        g2d.setColor(Color.white);
        g2d.drawPolygon(getBoundPolygon(at,false));
    }

    public void drawBounds(Graphics2D g2d,AffineTransform at){
        Rectangle rec = getBounds();
        int px = (int)(at.getTranslateX()+(x-rec.getWidth()/2-200)*at.getScaleX());
        int py = (int)(at.getTranslateY()+(y-rec.getHeight()/2-200)*at.getScaleY());
        Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{10}, 0);
        g2d.setStroke(dashed);
        g2d.setColor(Color.white);
        g2d.drawRoundRect(px,py,(int)((rec.getWidth()+400)*at.getScaleX()),
                (int)((rec.getHeight()+400)*at.getScaleY()),
                (int)(100*at.getScaleY()),(int)(100*at.getScaleY()));
    }

    @Override
    public String toString()
    {
        return "Planet"+","+x+","+y+","+type+","+degree+","+omega+","+orbitOmega+","+orbitTrack.getLocation().getX()
                +","+orbitTrack.getLocation().getY()+","+orbitTrack.getWidth()+","+orbitTrack.getHeight()+","+
                orbitTrackAngle+","+orbitAngle+","+volx+","+voly+","+orbitCenterChoice.getSelectedIndex();
    }
}
