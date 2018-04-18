import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {
    Handler handler;

    public KeyInput(Handler handler) {
        this.handler = handler;

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        for(GameObject tempObject:handler.objects){
            if(tempObject.getId()==ID.Rocket){
                if(key== KeyEvent.VK_UP){
                    tempObject.setVoly(tempObject.getVoly()+1);
                }

                if(key== KeyEvent.VK_DOWN){
                    tempObject.setVoly(tempObject.getVoly()-1);
                }

            }
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
    }

}
