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
        boolean dragObject = false;

        if(handler.getStatus()==Handler.Status.EDIT && e.getButton()==MouseEvent.BUTTON1){
            Point2D point = null;
            try {
                point = game.getAt().inverseTransform(e.getPoint(),point);
            } catch (NoninvertibleTransformException e1) {
                e1.printStackTrace();
            }

            for(GameObject objects:handler.objects){
                if(objects.getBounds().contains(point)){
                    int dx = (int)((e.getX()-lastPoint.getX())/game.getScale());
                    int dy = (int)((e.getY()-lastPoint.getY())/game.getScale());
                    lastPoint = e.getPoint();
                    objects.translate(dx,dy);
                    dragObject = true;
                    break;
                }
            }
        }


        if(!dragObject && e.getButton()==MouseEvent.BUTTON1){
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
            double magnitude = 1.1;
            if(e.getWheelRotation()>0){
                scale*=magnitude;
                zoom(e.getPoint());
            }
            else if(e.getWheelRotation()<0){
                scale*=1/magnitude;
                zoom(e.getPoint());
            }
            if(scale>2){
                scale=2;
                zoom(e.getPoint());
            }
            else if(scale<0.005){
                scale=0.005;
                zoom(e.getPoint());
            }
            game.setScale(scale);
        }
    }

    public void zoom(Point e){
        AffineTransform at = game.getAt();
        Point2D p1 = e;
        Point2D p2 = null;
        try {
            p2 = at.inverseTransform(p1,null);
        } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
        }
        at.setToIdentity();
        at.translate(p1.getX(),p1.getY());
        at.scale(game.getScale(),game.getScale());
        at.translate(-p2.getX(),-p2.getY());
    }
}
