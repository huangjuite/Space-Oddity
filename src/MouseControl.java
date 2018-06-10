import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class MouseControl implements MouseListener,MouseMotionListener,MouseWheelListener {
    private Handler handler;
    private Game game;
    private Point lastPoint;
    private double asX,asY;
    private Asteroid asteroid;
    private boolean drawingAsteroid = false;
    private boolean deleteObject = false;
    private Point delPoint;
    private Point2D anotherpoint=null;
    private double tempOrbitOmega;
    private CustomButton tempButton;
    private Point originalButtonPoint;

    public MouseControl(Handler handler,Game game) {
        this.handler = handler;
        this.game = game;
        lastPoint = new Point();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point mp = e.getPoint();
        mp.translate(-game.getWidth()/2,-game.getHeight()/2);

        if(handler.getStatus()== Handler.Status.STARTSCENE){
            for(CustomButton button : handler.buttons){
                boolean flag = false;
                if(button.getBounds().contains(mp)){
                    flag = true;
                    game.removeAll();
                    switch (button.getType()){
                        case EARTH:
                            handler.setStatus(Handler.Status.CHOOSING);
                            break;
                        case SATURN:
                            handler.setStatus(Handler.Status.HOWTO);
                            break;
                        case NEPTUNE:
                            handler.setStatus(Handler.Status.CREDIT);
                            break;
                        case JUPITER:
                            handler.setStatus(Handler.Status.EDIT);
                            game.buildEditMode();
                            break;
                    }
                }
                if(flag) break;
            }
        }
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


        if(handler.getStatus()== Handler.Status.STARTSCENE){
            for(CustomButton button : handler.buttons){
                tempOrbitOmega = button.getOrbitOmega();
                button.setOrbitOmega(0);
            }
        }else if(handler.getStatus()== Handler.Status.EDIT){
            if(e.getButton()== MouseEvent.BUTTON1) {
                //創造小行星
                if(handler.getDrawingAsteroid()) {
                    //亂數產生座標
                    asteroid = new Asteroid((int) (point.getX() + (Math.random() * 10) / game.getScale()),
                            (int) (point.getY() + (Math.random() * 10) / game.getScale()), ID.Asteroid, handler);
                    asX = point.getX();
                    asY = point.getY();
                    drawingAsteroid = true; //讓drag知道，可以產生更多小行星
                    handler.addObject(asteroid);
                }
                else{
                    Point mp = e.getPoint();
                    mp.translate(-game.getWidth()/2,-game.getHeight()/2);
                    for(CustomButton button : handler.buttons){
                        if(button.getBounds().contains(mp)){
                            tempButton = button;
                            originalButtonPoint = button.getPosition();
                            break;
                        }
                    }

                    GameObject selectedObject = handler.getSelectedObject();
                    if(selectedObject!=null){
                        selectedObject.setSelected(false);
                        selectedObject.setComponentsVisible(false);
                        handler.setSelectItem(null);
                    }
                    for(GameObject object : handler.objects){
                        if(object.getBounds().contains(point)){
                            handler.setSelectItem(object);
                            object.setSelected(true);
                            object.setComponentsVisible(true);
                            break;
                        }
                    }
                }
            }
            if(e.getButton()== MouseEvent.BUTTON3){
                deleteObject = true;
                delPoint = e.getPoint();
                handler.setDeleteObject(true);
            }
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        drawingAsteroid = false;

        if(handler.getStatus()== Handler.Status.STARTSCENE){
            for(CustomButton button:handler.buttons){
                button.setOrbitOmega(tempOrbitOmega);
            }
        }
        else if(handler.getStatus()==Handler.Status.EDIT){

            if(e.getButton()== MouseEvent.BUTTON3) {
                Point2D point1=null, point2=null;
                try {
                    point1 = game.getAt().inverseTransform(delPoint,point1);
                    point2 = game.getAt().inverseTransform(e.getPoint(),point2);
                } catch (NoninvertibleTransformException e1) {
                    e1.printStackTrace();
                }
                Point2D LeftUpPoint = new Point2D.Double((int)Math.min(point1.getX(),point2.getX()), (int)Math.min(point1.getY(),point2.getY()));
                Point2D RightDownPoint = new Point2D.Double(LeftUpPoint.getX()+Math.abs(point2.getX()-point1.getX()), LeftUpPoint.getY()+Math.abs(point2.getY()-point1.getY()));
                Rectangle delRec = new Rectangle((int)LeftUpPoint.getX(),(int)LeftUpPoint.getY(),(int)(RightDownPoint.getX()-LeftUpPoint.getX()),(int)(RightDownPoint.getY()-LeftUpPoint.getY()));
                for(int i=0;i<handler.objects.size();i++)
                {
                    if(handler.objects.get(i)!=null) {
                        if (delRec.intersects(handler.objects.get(i).getBounds()) && deleteObject) {
                            GameObject tob = handler.objects.get(i);
                            handler.removeObject(tob, LeftUpPoint, RightDownPoint);
                            if(!handler.objects.contains(tob)){
                                i--;
                            }
                        }
                    }
                }
                deleteObject = false;
                handler.setDeleteObject(false);
            }
            else if(e.getButton()== MouseEvent.BUTTON1){
                Point2D point = null;
                try {
                    point = game.getAt().inverseTransform(e.getPoint(), point);
                } catch (NoninvertibleTransformException e1) {
                    e1.printStackTrace();
                }
                if(tempButton!=null){
                    tempButton.setPosition(originalButtonPoint);
                    handler.addObject(new Planet((int)point.getX(),(int)point.getY(),
                            ID.Planet, tempButton.getType(),handler));
                    tempButton = null;
                }
            }
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
        Point2D point = null;
        try {
            point = game.getAt().inverseTransform(e.getPoint(),point);
            if(deleteObject)
                anotherpoint = game.getAt().inverseTransform(delPoint,anotherpoint);
        } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
        }


        if(handler.getStatus()==Handler.Status.STARTSCENE){
            for(CustomButton button : handler.buttons){
                double delta = (lastPoint.x-e.getPoint().x);
                button.setOrbitOmega(0);
                button.setOrbitAngle(button.getOrbitAngle()+delta);
            }

            lastPoint = e.getPoint();
        }
        else{
            boolean dragObject = false;

            if(handler.getStatus()==Handler.Status.EDIT && e.getButton()==MouseEvent.BUTTON1){

                if(tempButton!=null){
                    Point mp = e.getPoint();
                    mp.translate(-game.getWidth()/2,-game.getHeight()/2);
                    tempButton.setPosition(mp);
                }
                else if(drawingAsteroid && !deleteObject)
                {
                    double dis = Math.sqrt(Math.pow(asX-point.getX(),2)+Math.pow(asY-point.getY(),2));
                    if(dis>128) {//小行星的width為128
                        asteroid.addAsteroid((int) (point.getX() + (Math.random() * 30) / game.getScale()),
                                (int) (point.getY() + (Math.random() * 30) / game.getScale()));
                        asX = point.getX();
                        asY = point.getY();
                    }
                }
                else if(!deleteObject){
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

            if(!dragObject && e.getButton()==MouseEvent.BUTTON1 && !drawingAsteroid &&
                    !deleteObject && tempButton==null){
                int dx = (int)((e.getX()-lastPoint.getX())/game.getScale());
                int dy = (int)((e.getY()-lastPoint.getY())/game.getScale());
                game.getAt().translate(dx,dy);
                lastPoint = e.getPoint();
            }


            if(handler.getStatus()==Handler.Status.EDIT && e.getButton()==MouseEvent.BUTTON3){
                //設定公轉軌道
                for(GameObject objects:handler.objects) {
                    if(objects.getId()!=ID.Asteroid) {
                        if(objects.getBounds().contains(point)) {

                        }
                    }
                }

                if(deleteObject)
                {
                    handler.drawRec(anotherpoint, point);
                }
            }
        }

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(handler.getStatus()== Handler.Status.STARTSCENE){
            Point mp = e.getPoint();
            mp.translate(-game.getWidth()/2,-game.getHeight()/2);
            for(CustomButton button : handler.buttons){
                if(button.getBounds().contains(mp)){
                    button.setHover(true);
                }
                else{
                    button.setHover(false);
                }
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if(handler.getStatus()== Handler.Status.STARTSCENE){

        }
        else{
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
