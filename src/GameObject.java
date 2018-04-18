import java.awt.*;

public abstract class GameObject {
    protected int x;
    protected int y;
    protected ID id;
    protected double volx;
    protected double voly;

    public GameObject(int x,int y,ID id){
        this.x = x;
        this.y = y;
        this.id = id;
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
    public abstract void tick();
    public abstract void render(Graphics g);
    public abstract Rectangle getBounds();
}
