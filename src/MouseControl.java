import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class MouseControl implements MouseListener,MouseMotionListener,MouseWheelListener {
    Handler handler;
    Game game;

    public MouseControl(Handler handler,Game game) {
        this.handler = handler;
        this.game = game;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
            double scale = game.getScale();

            if(e.getWheelRotation()>0){
                scale+=0.01;
            }
            else if(e.getWheelRotation()<0){
                scale-=0.01;
            }
            scale=(scale>2)?2:scale;
            scale=(scale<0.05)?0.05:scale;
            System.out.println(scale);
            game.setScale(scale);
        }
    }
}
