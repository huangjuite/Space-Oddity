import java.awt.*;

public class Planet extends GameObject {
    Handler handler;

    public Planet(int x, int y, ID id, Handler handler) {
        super(x, y, id);
        this.handler = handler;
    }

    @Override
    public Rectangle getBounds() {
        return null;
    }

    @Override
    public void tick() {

    }

    @Override
    public void render(Graphics g) {

    }
}
