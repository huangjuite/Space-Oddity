import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class Handler {
    Game game;
    public LinkedList<GameObject> objects = new LinkedList<GameObject>();
    private Rocket rocketObject;
    public enum Status{START,STOP,PAUSE,EDIT};
    private Status status = Status.START;
    public Boolean traceMode = false;

    public Handler(Game game){
        this.game = game;
    }

    public void tick(){
        for(GameObject tempObject : objects){
            tempObject.tick();
            /*
            if(status==Status.START && rocketObject!=null && tempObject!=rocketObject){
                if(detectCollision(rocketObject,tempObject)==true){
                    rocketObject.setPosition(0,0);
                }
            }
            */

            if(tempObject.getId() == ID.Planet)
            {
                double g = 1, M=60, t=1;
                double dx, dy, dis;
                double a, ax, ay;
                dx = tempObject.getX() - rocketObject.getX();
                dy = tempObject.getY() - rocketObject.getY();
                dis = Math.sqrt(dx*dx+dy*dy);
                if(dis>10) {
                    a = g * M / dis;
                    ax = a * (dx / dis);
                    ay = a * (dy / dis);
                    rocketObject.setVolx(rocketObject.getVolx() + ax*t);
                    rocketObject.setVoly(rocketObject.getVoly() + ay*t);
                }
            }
        }
    }

    public void render(Graphics g,AffineTransform at){
        if(traceMode){
            at.setToIdentity();
            at.scale(game.getScale(),game.getScale());
            at.translate(-rocketObject.getX(),-rocketObject.getY());
            at.translate(game.getWidth()/2/game.getScale(),game.getHeight()/2/game.getScale());
        }

        for(GameObject tempObject : objects){
            tempObject.render(g,at);
        }
    }


    public void addObject(GameObject object){
        if(object.getId()==ID.Rocket){
            rocketObject = (Rocket)object;
        }
        this.objects.add(object);
    }

    public void removeObject(GameObject object){
        this.objects.remove(object);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    protected Rectangle getCollisionRect(Rectangle rect1, Rectangle rect2) {
        Area a1 = new Area(rect1);
        Area a2 = new Area(rect2);
        a1.intersect(a2);
        return a1.getBounds();
    }


    protected boolean detectCollision(GameObject ob1,GameObject ob2) {
        if (ob1.getBounds().intersects(ob2.getBounds())) {
            Rectangle bounds = getCollisionRect(ob1.getBounds(), ob2.getBounds());
            if (!bounds.isEmpty()) {
                for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
                    for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
                        int pixel1 = ob1.getRotateImage().getRGB(x - ob1.getBounds().x, y - ob1.getBounds().y);
                        int pixel2 = ob2.getRotateImage().getRGB(x - ob2.getBounds().x, y - ob2.getBounds().y);
                        if (((pixel1 >> 24) & 0xFF) < 225 && ((pixel2 >> 24) & 0xFF) < 225) {
                            return true;
                        }
                    }
                }
                return false;
            }
            return false;
        }
        return false;
    }
}
