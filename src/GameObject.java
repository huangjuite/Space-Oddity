import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public abstract class GameObject {
    protected int x;
    protected int y;
    protected double degree=0;
    protected double omega=0;
    protected double orbitOmega;
    protected Rectangle orbitTrack;
    protected double orbitTrackAngle;
    protected double orbitAngle;
    protected ID id;
    protected double volx;
    protected double voly;
    private int radarRadius;
    protected boolean isSelected;
    Handler handler;
    public enum ObjectType {JUPITER,MARS,EARTH,MOON,VENUS,MERCURY,NEPTUNE,SATURN,URANUS,SUN,ASTEROID,ROCKET}
    protected ObjectType type;
    protected Scrollbar trackOmegaBar,trackAbar,trackBbar,trackAngleBar;
    protected Label label[];
    protected Choice orbitCenterChoice;
    protected GameObject orbitCenterObject;
    protected LinkedList<GameObject> centerChoice;

    public GameObject(int x,int y,ID id,ObjectType type,Handler handler){
        this.x = x;
        this.y = y;
        this.id = id;
        this.type = type;
        this.handler = handler;
        orbitTrack = new Rectangle(x,y,0,0);

        label = new Label[5];
        label[0] = new Label("omega:");
        label[1] = new Label("width:");
        label[2] = new Label("height");
        label[3] = new Label("tilt:");
        label[4] = new Label("orbit center:");
        for(Label l:label){
            l.setVisible(false);
            l.setBackground(Color.gray);
        }
        trackOmegaBar = new Scrollbar(Scrollbar.HORIZONTAL,0,1,-200,200);
        trackAbar = new Scrollbar(Scrollbar.HORIZONTAL,0,1,0,200000);
        trackBbar = new Scrollbar(Scrollbar.HORIZONTAL,0,1,0,200000);
        trackAngleBar = new Scrollbar(Scrollbar.HORIZONTAL,0,1,0,360);
        trackAngleBar.setBackground(Color.gray);
        trackOmegaBar.setBackground(Color.gray);
        trackAbar.setBackground(Color.gray);
        trackBbar.setBackground(Color.gray);
        trackOmegaBar.setVisible(false);
        trackAbar.setVisible(false);
        trackBbar.setVisible(false);
        trackAngleBar.setVisible(false);
        AdListener lis = new AdListener();
        trackAngleBar.addAdjustmentListener(lis);
        trackBbar.addAdjustmentListener(lis);
        trackAbar.addAdjustmentListener(lis);
        trackOmegaBar.addAdjustmentListener(lis);

        orbitCenterChoice = new Choice();
        orbitCenterChoice.setVisible(false);
        orbitCenterChoice.setBackground(Color.gray);
        orbitCenterChoice.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int i = orbitCenterChoice.getSelectedIndex();
                if(i==0){
                    orbitCenterObject = null;
                }
                else {
                    orbitCenterObject = centerChoice.get(i-1);
                }
            }
        });
        orbitCenterChoice.add("None");

        label[0].setBounds(200,handler.getGame().getHeight()-30,50,15);
        trackOmegaBar.setBounds(250,handler.getGame().getHeight()-30,250,15);
        label[1].setBounds(500,handler.getGame().getHeight()-30,50,15);
        trackAbar.setBounds(550,handler.getGame().getHeight()-30,400,15);

        label[3].setBounds(200,handler.getGame().getHeight()-15,50,15);
        trackAngleBar.setBounds(250,handler.getGame().getHeight()-15,250,15);
        label[2].setBounds(500,handler.getGame().getHeight()-15,50,15);
        trackBbar.setBounds(550,handler.getGame().getHeight()-15,400,15);

        label[4].setBounds(200,handler.getGame().getHeight()-45,100,15);
        orbitCenterChoice.setBounds(300,handler.getGame().getHeight()-45,200,15);

        handler.getGame().getFrame().add(trackOmegaBar,0);
        handler.getGame().getFrame().add(trackAbar,0);
        handler.getGame().getFrame().add(trackBbar,0);
        handler.getGame().getFrame().add(trackAngleBar,0);
        handler.getGame().getFrame().add(orbitCenterChoice,0);
        for(Label l:label){
            handler.getGame().getFrame().add(l,0);
        }

    }

    protected class AdListener implements AdjustmentListener{

        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            if(e.getSource()==trackAbar){
                if(trackBbar.getValue()>trackAbar.getValue()){
                    trackBbar.setValue(trackAbar.getValue());
                    setOrbitTrackB(trackBbar.getValue());
                }
                setOrbitTrackA(trackAbar.getValue());
            }
            else if(e.getSource()==trackBbar){
                setOrbitTrackB(trackBbar.getValue());
                if(trackBbar.getValue()>trackAbar.getValue()){
                    trackAbar.setValue(trackBbar.getValue());
                    setOrbitTrackA(trackAbar.getValue());
                }
            }
            else if(e.getSource()==trackOmegaBar){
                setOrbitOmega(trackOmegaBar.getValue()/100.);
            }
            else if(e.getSource()==trackAngleBar){
                setOrbitTrackAngle(trackAngleBar.getValue());
            }
        }
    }

    void update(){
        centerChoice = new LinkedList<GameObject>();
        orbitCenterChoice.removeAll();
        orbitCenterChoice.add("0:None");
        int i=1;
        for(GameObject object:handler.objects){
            if(object!=this){
                centerChoice.add(object);
                orbitCenterChoice.add(i+":"+object.getType().toString());
                i++;
            }
        }
    }

    public void setComponentsVisible(boolean b){
        trackOmegaBar.setVisible(b);
        trackAbar.setVisible(b);
        trackBbar.setVisible(b);
        trackAngleBar.setVisible(b);
        orbitCenterChoice.setVisible(b);
        for(Label l:label){
            l.setVisible(b);
        }
    }

    public void setPosition(int x,int y){
        setX(x);setY(y);
    }

    public void translate(int dx,int dy){
        setX(x+dx);
        setY(y+dy);

        orbitTrack.setRect(orbitTrack.getX()+dx,orbitTrack.getY()+dy,
                orbitTrack.getWidth(),orbitTrack.getHeight());
    }

    public void setPosition(Point p){
        setX((int)p.getX());
        setY((int)p.getY());
    }

    public void removeComponent(){
        for(Label l:label){
            handler.getGame().getFrame().remove(l);
        }
        handler.getGame().getFrame().remove(trackOmegaBar);
        handler.getGame().getFrame().remove(trackAbar);
        handler.getGame().getFrame().remove(trackBbar);
        handler.getGame().getFrame().remove(trackAngleBar);
        handler.getGame().getFrame().remove(orbitCenterChoice);
    }

    public Point getPosition(){
        return new Point(x,y);
    }

    public Scrollbar getTrackOmegaBar() {
        return trackOmegaBar;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public ObjectType getType() { return type; }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setVolx(double volx) {
        this.volx = volx;
    }

    public void setVoly(double voly) {
        this.voly = voly;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ID getId() { return id; }

    public double getVolx() {
        return volx;
    }

    public double getVoly() {
        return voly;
    }

    public Point.Double getVelocity(){
        return new Point.Double(getVolx(),getVoly());
    }

    public double getDegree() {
        return degree;
    }

    public void setDegree(double degree) {
        this.degree = degree;
    }

    public double getOmega() {
        return omega;
    }

    public void setOmega(double omega) {
        this.omega = omega;
    }

    public Rectangle getOrbitTrack() {
        return orbitTrack;
    }

    public void setOrbitTrack(Rectangle orbitTrack) {
        this.orbitTrack = orbitTrack;
    }

    public void setOrbitTrackA(int a) {
        orbitTrack.setRect(orbitTrack.getX(),orbitTrack.getY(),
                a,orbitTrack.getHeight());

    }
    public void setOrbitTrackB(int b) {
        orbitTrack.setRect(orbitTrack.getX(),orbitTrack.getY(),
                orbitTrack.getWidth(),b);
    }

    public void setOrbitTrackPosition(int x,int y){
        orbitTrack.setRect(x,y,
                orbitTrack.getWidth(),orbitTrack.getHeight());
    }

    public double getOrbitTrackAngle() {
        return orbitTrackAngle;
    }

    public void setOrbitTrackAngle(double orbitTrackAngle) {
        this.orbitTrackAngle = orbitTrackAngle;
    }

    public double getOrbitOmega() {
        return orbitOmega;
    }

    public void setOrbitOmega(double orbitOmega) {
        this.orbitOmega = orbitOmega;
    }

    public double getOrbitAngle() {
        return orbitAngle;
    }

    public void setOrbitAngle(double orbitAngle) {
        this.orbitAngle = orbitAngle;
    }

    public BufferedImage resizeImage(BufferedImage before, double scale){
        int w = (int)(before.getWidth()*scale);
        int h = (int)(before.getHeight()*scale);
        BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(scale, scale);
        AffineTransformOp scaleOp =
                new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        after = scaleOp.filter(before, after);

        return after;
    }

    public void drawOrbitTrack(Graphics2D g2d,AffineTransform at,Rectangle trackRec,double trackAngle){
        double translatex=at.getTranslateX()+trackRec.getX()*at.getScaleX(),
                translatey=at.getTranslateY()+trackRec.getY()*at.getScaleX();
        g2d.rotate(Math.toRadians(trackAngle),translatex,translatey);
        g2d.setColor(Color.white);
        Stroke dashed = new BasicStroke((float)1.5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{(float)1.5,15}, 0);
        g2d.setStroke(dashed);
        g2d.drawArc((int)((-trackRec.getWidth())*at.getScaleX()+translatex),
                (int)((-trackRec.getHeight())*at.getScaleX()+translatey),
                (int)(trackRec.getWidth()*2*at.getScaleX()),(int)(trackRec.getHeight()*2*at.getScaleX()),
                0,360);
        g2d.rotate(Math.toRadians(-trackAngle),translatex,translatey);
    }

    public void drawRadar(Graphics2D g2d,AffineTransform at){
        int fullSize = 120000;
        radarRadius += 1000;
        radarRadius %= fullSize;
        g2d.setStroke(new BasicStroke(1));
        if(isSelected) {
            g2d.setColor(new Color(1, 1, 0, 1 - radarRadius / (float) fullSize));
        }
        else{
            g2d.setColor(new Color(1, 1, 1, 1 - radarRadius / (float) fullSize));
        }
        int px = (int)(at.getTranslateX()+(x-radarRadius)*at.getScaleX());
        int py = (int)(at.getTranslateY()+(y-radarRadius)*at.getScaleY());
        int size = (int)(2*radarRadius*at.getScaleX());
        g2d.drawOval(px,py,size,size);
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

    public abstract Rectangle getBounds();
    public abstract void tick();
    public abstract void render(Graphics g,AffineTransform at);
    public abstract  String toString();

}
