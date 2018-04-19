import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;

public class Handler {
    public LinkedList<GameObject> objects = new LinkedList<GameObject>();

    public void tick(){
        for(GameObject tempObject : objects){
            tempObject.tick();
        }
    }

    public void render(Graphics g,AffineTransform at){
        for(GameObject tempObject : objects){
            tempObject.render(g,at);
        }
    }

    public void addObject(GameObject object){
        this.objects.add(object);
    }

    public void removeObject(GameObject object){
        this.objects.remove(object);
    }


}
