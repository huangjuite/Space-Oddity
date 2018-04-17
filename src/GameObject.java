import java.awt.Graphics;

public abstract class GameObject {
    protected int x;
    protected int y;
    protected ID id;
    protected int volx;
    protected int voly;

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

    public void setVolx(int volx) {
        this.volx = volx;
    }

    public void setVoly(int voly) {
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

    public int getVolx() {
        return volx;
    }

    public int getVoly() {
        return voly;
    }
    public abstract void tick();
    public abstract void render(Graphics g);
}
