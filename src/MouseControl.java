import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class MouseControl implements MouseListener,MouseMotionListener,MouseWheelListener {
    Handler handler;
    Game game;
    Point lastPoint;
    double asX,asY;
    Asteroid asteroid;
    boolean drawingAsteroid = false;
    boolean deleteObject = false;
    Point delPoint;
    Point2D anotherpoint=null;

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
        Point2D point = null;
        try {
            point = game.getAt().inverseTransform(e.getPoint(), point);
        } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
        }
        //創造小行星
        if(handler.getStatus()==Handler.Status.EDIT && e.getButton()== MouseEvent.BUTTON1) {

            if (handler.getDrawingAsteroid()) {
                //亂數產生座標
                asteroid = new Asteroid((int) (point.getX() + (Math.random() * 10) / game.getScale()), (int) (point.getY() + (Math.random() * 10) / game.getScale()), ID.Asteroid, handler);
                asX = point.getX();
                asY = point.getY();
                drawingAsteroid = true; //讓drag知道，可以產生更多小行星
                handler.addObject(asteroid);
            }
        }
        if(handler.getStatus()==Handler.Status.EDIT && e.getButton()== MouseEvent.BUTTON3) {
            deleteObject = true;
            delPoint = e.getPoint();
            handler.setDeleteObject(true);

        }


    }

    @Override
    public void mouseReleased(MouseEvent e) {
        drawingAsteroid = false;
        if(handler.getStatus()==Handler.Status.EDIT && e.getButton()== MouseEvent.BUTTON3) {

            Point2D LeftUpPoint=null, RightDownPoint=null;
            try {
                LeftUpPoint = game.getAt().inverseTransform(delPoint,LeftUpPoint);
                RightDownPoint = game.getAt().inverseTransform(e.getPoint(),RightDownPoint);
            } catch (NoninvertibleTransformException e1) {
                e1.printStackTrace();
            }
            Rectangle delRec = new Rectangle((int)Math.min(LeftUpPoint.getX(),RightDownPoint.getX()),(int)Math.min(LeftUpPoint.getY(),RightDownPoint.getY()),(int)Math.abs(RightDownPoint.getX()-LeftUpPoint.getX()),(int)Math.abs(RightDownPoint.getY()-LeftUpPoint.getY()));
            for(int i =0 ; i<handler.objects.size() ; i++)
            {
                if(handler.objects.get(i)!=null)
                    if (delRec.contains(handler.objects.get(i).getBounds())&& deleteObject) {
                        handler.removeObject(handler.objects.get(i));
                    }
            }
            deleteObject = false;
            handler.setDeleteObject(false);
        }
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
        Point2D point = null;
        try {
            point = game.getAt().inverseTransform(e.getPoint(),point);
            if(deleteObject ==true)
                anotherpoint = game.getAt().inverseTransform(delPoint,anotherpoint);
        } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
        }


        if(handler.getStatus()==Handler.Status.EDIT && e.getButton()==MouseEvent.BUTTON1){

            if(drawingAsteroid && deleteObject ==false)
            {
                double dis = Math.sqrt(Math.pow(asX-point.getX(),2)+Math.pow(asY-point.getY(),2));
                if(dis>128) {//小行星的width為128
                    asteroid.addAsteroid((int) (point.getX() + (Math.random() * 30) / game.getScale()), (int) (point.getY() + (Math.random() * 30) / game.getScale()));
                    asX = point.getX();
                    asY = point.getY();
                }
            }
            else if(deleteObject ==false){
                for (GameObject objects : handler.objects) {
                    if (objects.getBounds().contains(point) && !drawingAsteroid) {
                        int dx = (int) ((e.getX() - lastPoint.getX()) / game.getScale());
                        int dy = (int) ((e.getY() - lastPoint.getY()) / game.getScale());
                        lastPoint = e.getPoint();
                        objects.translate(dx, dy);
                        dragObject = true;
                        break;
                    }
                }
            }
        }


        if(!dragObject && e.getButton()==MouseEvent.BUTTON1&!drawingAsteroid &&!deleteObject){
            int dx = (int)((e.getX()-lastPoint.getX())/game.getScale());
            int dy = (int)((e.getY()-lastPoint.getY())/game.getScale());
            game.getAt().translate(dx,dy);
            lastPoint = e.getPoint();
        }

        //設定公轉軌道
        if(handler.getStatus()==Handler.Status.EDIT && e.getButton()==MouseEvent.BUTTON3){
            for(GameObject objects:handler.objects) {
                if (objects.getBounds().contains(point)){

                }
            }

            if(deleteObject ==true)
            {
                handler.drawRec(anotherpoint, point);
            }
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
