import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.LinkedList;

public class Handler {
    Game game;
    public LinkedList<GameObject> objects = new LinkedList<GameObject>();
    public LinkedList<CustomButton> buttons = new LinkedList<CustomButton>();
    private Rocket rocketObject;
    public enum Status{PLAY, STARTSCENE,CHOOSING,HOWTO,CREDIT,PAUSE,EDIT};
    private Status status = Status.STARTSCENE;
    public Boolean traceMode = false;
    private Point2D drawRecPoint1,drawRecPoint2;
    private boolean deleteObject =false, drawingAsteroid =false;
    private GameObject selectedObject;
    private Checkbox showRadar;
    private Label selectLabel;
    private Choice seleItem;

    public Handler(Game game){
        this.game = game;
        showRadar = new Checkbox("show Radar",false);
        showRadar.setVisible(false);
        showRadar.setBounds(0,game.getHeight()-15,200,15);
        selectLabel = new Label("select:");
        selectLabel.setBounds(0,game.getHeight()-30,50,15);
        selectLabel.setVisible(false);
        seleItem = new Choice();
        seleItem.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int i = seleItem.getSelectedIndex();
                if(selectedObject!=null){
                    selectedObject.setSelected(false);
                    selectedObject.setComponentsVisible(false);
                    selectedObject=null;
                }
                if(i==0){
                    selectedObject = null;
                }
                else {
                    selectedObject = objects.get(i-1);
                    selectedObject.setSelected(true);
                    selectedObject.setComponentsVisible(true);
                }
            }
        });
        seleItem.setVisible(false);
        seleItem.addItem("0:None");
        seleItem.setBounds(50,game.getHeight()-30,150,15);
        showRadar.setBackground(Color.gray);
        selectLabel.setBackground(Color.gray);
        seleItem.setBackground(Color.gray);
        game.getFrame().add(showRadar,0);
        game.getFrame().add(seleItem,0);
        game.getFrame().add(selectLabel,0);
    }

    public void tick(){
        for(CustomButton button:buttons){
            button.tick();
        }

        for(GameObject tempObject : objects){
            //detect collision
            if(status==Status.PLAY && rocketObject!=null && tempObject!=rocketObject){
                Planet planet = (Planet)tempObject;
                if(detectCollision(rocketObject,planet)){
                    rocketObject.setPosition(0,0);
                }
            }

            //apply gravity
            if(tempObject.getId()==ID.Planet && status==Status.PLAY && rocketObject!=null)
            {
                Planet planet = (Planet)tempObject;
                double g = 800, M=planet.getPlanetMass(), t=1;
                double dx, dy, dis;
                double a, ax, ay;
                dx = tempObject.getX() - rocketObject.getX();
                dy = tempObject.getY() - rocketObject.getY();
                dis = Math.sqrt(dx*dx+dy*dy);
                if(dis>10) {
                    a = g * M / (dis*dis);
                    ax = a * (dx / dis);
                    ay = a * (dy / dis);
                    rocketObject.setVolx(rocketObject.getVolx() + ax*t);
                    rocketObject.setVoly(rocketObject.getVoly() + ay*t);
                }
            }

            //apply
            tempObject.tick();
        }
    }


    public void render(Graphics g, AffineTransform at){
        Graphics2D g2d = (Graphics2D) g;
        if(traceMode){
            at.setToIdentity();
            at.scale(game.getScale(),game.getScale());
            at.translate(-rocketObject.getX(),-rocketObject.getY());
            at.translate(game.getWidth()/2/game.getScale(),game.getHeight()/2/game.getScale());
        }

        if(deleteObject)
        {
            if(drawRecPoint1!=null) {
                AffineTransform newAt1 = AffineTransform.getTranslateInstance(drawRecPoint1.getX(), drawRecPoint1.getY());
                AffineTransform newAt2 = AffineTransform.getTranslateInstance(drawRecPoint2.getX(), drawRecPoint2.getY());
                newAt1.preConcatenate(at);
                newAt2.preConcatenate(at);
                g2d.setColor(Color.white);
                float dash1[]={8.0f};//這個跟下面那行都是畫虛線
                g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f));
                g2d.drawRect((int) newAt1.getTranslateX(), (int) newAt1.getTranslateY(),(int)(newAt2.getTranslateX()-newAt1.getTranslateX()), (int)(newAt2.getTranslateY()-newAt1.getTranslateY()));
            }
        }

        for(GameObject tempObject : objects){
            tempObject.render(g,at);
        }
        for(CustomButton button : buttons){
            button.render(g,at);
        }

    }

    public GameObject getSelectedObject() {
        return selectedObject;
    }

    public void setSelectItem(GameObject selectedObject) {
        this.selectedObject = selectedObject;
        if(selectedObject!=null){
            for(int i=0;i<objects.size();i++){
                if(selectedObject==objects.get(i)){
                    seleItem.select(i+1);
                }
            }
        }
        else{
            seleItem.select(0);
        }
    }

    public void setDeleteObject(boolean d) {
        deleteObject =d;
        if(d==false)
        {
            drawRecPoint1=null;
            drawRecPoint2=null;
        }
    }

    public boolean showingRadar(){
        return showRadar.getState();
    }

    public void setDrawingAsteroid(){ drawingAsteroid = !drawingAsteroid;}

    public boolean getDrawingAsteroid() {
        return drawingAsteroid;
    }

    public void drawRec(Point2D point1, Point2D point2) {
        if(point1!=null&&point2!=null) {
            drawRecPoint1 = new Point2D.Double(Math.min(point1.getX(), point2.getX()), Math.min(point1.getY(), point2.getY()));
            drawRecPoint2 = new Point2D.Double(Math.max(point1.getX(), point2.getX()), Math.max(point1.getY(), point2.getY()));
        }
    }

    public void addButton(CustomButton button){
        this.buttons.add(button);
    }

    public void removeButton(CustomButton button){
        this.buttons.remove(button);
    }

    public Rocket getRocketObject() {
        return rocketObject;
    }

    public void addObject(GameObject object){
        if(object.getId()==ID.Rocket){
            rocketObject = (Rocket)object;
        }
        this.objects.add(object);
        for(GameObject gameObject:objects){
            gameObject.update();
        }
        updateSelectItem();
    }

    private void updateSelectItem(){
        seleItem.removeAll();
        seleItem.add("0:None");
        int i=1;
        for(GameObject object:objects){
            seleItem.add(i+":"+object.getType().toString());
            i++;
        }
    }

    public void removeObject(GameObject object,Point2D LeftUpDown, Point2D RightDownPoint){
        if(object.getId() == ID.Asteroid) {
            if (((Asteroid) object).getCount() >0) {
                ((Asteroid) object).setNewAsteroid(LeftUpDown, RightDownPoint);
                if(((Asteroid) object).getCount() == 0){
                    this.objects.remove(object);
                }
            }
        }
        else {
            this.objects.remove(object);
            getGame().getFrame().remove(object.getTrackOmegaBar());
        }
        for(GameObject gameObject:objects){
            gameObject.update();
        }
        updateSelectItem();

        System.out.println("delete finish");
    }
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        if(status==Status.EDIT){
           showRadar.setVisible(true);
           seleItem.setVisible(true);
           selectLabel.setVisible(true);
        }
        else{
            showRadar.setVisible(false);
            seleItem.setVisible(false);
            selectLabel.setVisible(false);
        }
    }

    public Game getGame() {
        return game;
    }

    protected boolean detectCollision(Rocket rocket, Planet planet) {
        double dis = Math.sqrt(Math.pow(rocket.x-planet.x,2)+Math.pow(rocket.y-planet.y,2));

        if(dis<planet.getRadius()+rocket.getRocketImage().getHeight()-100)
            return true;

        return false;
    }
}
