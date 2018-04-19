import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class MouseControl implements MouseListener,MouseMotionListener,MouseWheelListener {
    Handler handler;
    Game game;
    Point lastPoint;

    public MouseControl(Handler handler,Game game) {
        this.handler = handler;
        this.game = game;
        lastPoint = new Point();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        lastPoint = e.getPoint();
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
        if(e.getButton()==MouseEvent.BUTTON1){
            int dx = (int)((e.getX()-lastPoint.getX())/game.getScale());
            int dy = (int)((e.getY()-lastPoint.getY())/game.getScale());
            game.getAt().translate(dx,dy);
            lastPoint = e.getPoint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
            double scale = game.getScale();
            double magnitude = 1.05;
            if(e.getWheelRotation()>0){
                scale*=magnitude;
                zoom(e.getPoint(),magnitude);
            }
            else if(e.getWheelRotation()<0){
                scale*=1/magnitude;
                zoom(e.getPoint(),1/magnitude);
            }
            if(scale>2){
                scale=2;
                zoom(e.getPoint(),1/magnitude);
            }
            else if(scale<0.1){
                scale=0.1;
                zoom(e.getPoint(),magnitude);
            }
            game.setScale(scale);
        }
    }

    public void zoom(Point e,double magnitude){
        game.getAt().translate(e.getX(),e.getY());
        game.getAt().scale(magnitude,magnitude);
        game.getAt().translate(-e.getX(),-e.getY());
    }
}
