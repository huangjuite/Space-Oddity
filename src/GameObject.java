import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public abstract class GameObject{
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
    Handler handler;

    public GameObject(int x,int y,ID id,Handler handler){
        this.x = x;
        this.y = y;
        this.id = id;
        this.handler = handler;
    }

    public void setPosition(int x,int y){
        setX(x);setY(y);
    }

    public void translate(int dx,int dy){
        setX(x+dx);
        setY(y+dy);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setId(ID id) {
        this.id = id;
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


    public abstract Rectangle getBounds();
    public abstract void tick();
    public abstract void render(Graphics g,AffineTransform at);

}
