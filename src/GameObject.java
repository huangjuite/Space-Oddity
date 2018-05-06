import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public abstract class GameObject {
    protected int x;
    protected int y;
    protected double degree=0;
    protected double omega=0;
    protected ID id;
    protected double volx;
    protected double voly;
    Handler handler;
    BufferedImage bufferedImage;

    public GameObject(int x,int y,ID id,Handler handler){
        this.x = x;
        this.y = y;
        this.id = id;
        this.handler = handler;
    }

    public void setPosition(int x,int y){
        setX(x);setY(y);
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

    public ID getId() {
        return id;
    }

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

    public BufferedImage getRotateImage() {
        AffineTransform transform = new AffineTransform();
        transform.rotate(degree, bufferedImage.getWidth()/2, bufferedImage.getHeight()/2);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage image = op.filter(bufferedImage, null);
        return image;
    }

    public Rectangle getBounds(){
        BufferedImage image = getRotateImage();
        Rectangle rec = new Rectangle(x-image.getWidth()/2,y-image.getHeight()/2
                ,image.getWidth(),image.getHeight());
        return rec;
    }

    public abstract void tick();
    public abstract void render(Graphics g,AffineTransform at);

}
