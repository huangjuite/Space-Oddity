import java.awt.*;
import java.util.LinkedList;

public class Handler {
    public LinkedList<GameObject> objects = new LinkedList<GameObject>();

    public void tick(){
        for(GameObject tempObject : objects){
            tempObject.tick();
        }
    }

    public void render(Graphics g){
        for(GameObject tempObject : objects){
            tempObject.render(g);
        }
    }

    public void addObject(GameObject object){
        this.objects.add(object);
    }

    public void removeObject(GameObject object){
        this.objects.remove(object);
    }


}
