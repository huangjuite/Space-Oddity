import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.LinkedList;

public class Handler {
    private Game game;
    public LinkedList<GameObject> objects;
    public LinkedList<CustomButton> buttons;
    private Rocket rocketObject;
    public enum Status{PLAY, STARTSCENE,CHOOSING,HOWTO,CREDIT,PAUSE,EDIT};
    private Status status = Status.STARTSCENE;
    private Status backToStatus;
    public Boolean traceMode = false;
    private Point2D drawRecPoint1,drawRecPoint2;
    private boolean deleteObject =false, drawingAsteroid =false;
    private GameObject selectedObject;
    private Button homeButton,selectUniverseButton,addNewButton,saveButton,loadButton;
    private LinkedList<UniverseButton> universeButton;
    private Checkbox showRadar,planetCenter;
    private Label selectLabel;
    private Choice seleItem;
    //public LinkedList<GameObject> loadObjects;
    boolean readMode = false;
    private File logFile;
    private static final double gravityConstant = 850;//6.674×10−11

    public Handler(Game game){
        this.game = game;
        objects = new LinkedList<>();
        buttons = new LinkedList<>();
        universeButton = new LinkedList<>();
        //loadObjects = new LinkedList<>();

        saveButton = new Button("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(status==Status.CHOOSING)
                        saveGame();
                   else
                        creatingButton();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        loadButton = new Button("Load");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    openGame();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        showRadar = new Checkbox("show Radar",false);
        showRadar.setVisible(false);
        planetCenter = new Checkbox("set center",false);
        planetCenter.setVisible(false);

        homeButton = new Button("Home");
        homeButton.setVisible(false);
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.removeAll();
                setStatus(Status.STARTSCENE);
                game.buildStartAnimation();
            }
        });

        selectLabel = new Label("select:");
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

        selectUniverseButton = new Button("Universe");
        selectUniverseButton.setVisible(false);
        selectUniverseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.removeAll();
                setStatus(Status.CHOOSING);
                game.buildChooseMode();
            }
        });

        addNewButton = new Button("New");
        addNewButton.setVisible(false);
        addNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.removeAll();
                setStatus(Status.EDIT);
                game.buildEditMode();
                addObject(new Rocket(0,0,100,ID.Rocket, GameObject.ObjectType.ROCKET, Handler.this));
            }
        });

        seleItem.setBounds(50,game.getHeight()-45,150,15);
        homeButton.setBounds(0,0,70,70);
        showRadar.setBounds(0,game.getHeight()-15,200,15);
        selectLabel.setBounds(0,game.getHeight()-45,50,15);
        selectUniverseButton.setBounds(80,0,70,70);
        addNewButton.setBounds(80,0,70,70);
        planetCenter.setBounds(0,game.getHeight()-30,200,15);
        saveButton.setBounds(game.getWidth()-200,0,70,70);
        loadButton.setBounds(game.getWidth()-100,0,70,70);

        planetCenter.setBackground(Color.gray);
        selectUniverseButton.setBackground(Color.gray);
        homeButton.setBackground(Color.gray);
        showRadar.setBackground(Color.gray);
        selectLabel.setBackground(Color.gray);
        seleItem.setBackground(Color.gray);
        addNewButton.setBackground(Color.gray);
        saveButton.setBackground(Color.gray);
        loadButton.setBackground(Color.gray);

        game.getFrame().add(saveButton,0);
        game.getFrame().add(loadButton,0);
        game.getFrame().add(planetCenter,0);
        game.getFrame().add(addNewButton,0);
        game.getFrame().add(homeButton,0);
        game.getFrame().add(selectUniverseButton,0);
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
                double g = gravityConstant, M=planet.getPlanetMass();
                double dx, dy, dis;
                double a, ax, ay;
                dx = tempObject.getX() - rocketObject.getX();
                dy = tempObject.getY() - rocketObject.getY();
                dis = Math.sqrt(dx*dx+dy*dy);
                if(dis>10) {
                    a = g * M / (dis*dis);
                    ax = a * (dx / dis);
                    ay = a * (dy / dis);
                    rocketObject.setVolx(rocketObject.getVolx() + ax);
                    rocketObject.setVoly(rocketObject.getVoly() + ay);
                }
            }

            //apply
            tempObject.tick();
        }
    }


    public void render(Graphics g, AffineTransform at) {
        Graphics2D g2d = (Graphics2D) g;
        if (traceMode && rocketObject != null) {
            at.setToIdentity();
            at.scale(game.getScale(), game.getScale());
            at.translate(-rocketObject.getX(), -rocketObject.getY());
            at.translate(game.getWidth() / 2 / game.getScale(), game.getHeight() / 2 / game.getScale());
        } else if (selectedObject != null && planetCenter.getState()) {
            at.setToIdentity();
            at.scale(game.getScale(), game.getScale());
            at.translate(-selectedObject.getX(), -selectedObject.getY());
            at.translate(game.getWidth() / 2 / game.getScale(), game.getHeight() / 2 / game.getScale());
        }

        if (deleteObject) {
            if (drawRecPoint1 != null) {
                AffineTransform newAt1 = AffineTransform.getTranslateInstance(drawRecPoint1.getX(), drawRecPoint1.getY());
                AffineTransform newAt2 = AffineTransform.getTranslateInstance(drawRecPoint2.getX(), drawRecPoint2.getY());
                newAt1.preConcatenate(at);
                newAt2.preConcatenate(at);
                g2d.setColor(Color.white);
                float dash1[] = {8.0f};//這個跟下面那行都是畫虛線
                g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f));
                g2d.drawRect((int) newAt1.getTranslateX(), (int) newAt1.getTranslateY(), (int) (newAt2.getTranslateX() - newAt1.getTranslateX()), (int) (newAt2.getTranslateY() - newAt1.getTranslateY()));
            }
        }

        synchronized (this) {
            for (GameObject tempObject : objects) {
                if (tempObject.getId() != ID.Rocket)
                    tempObject.render(g, at);
            }
            for (CustomButton button : buttons) {
                button.render(g, at);
            }

            if (rocketObject != null) {
                Point[] p = new Point[rocketObject.getEstimateLine().length];
                for (int i = 0; i < p.length; i++) {
                    p[i] = new Point();
                }
                p[0].setLocation(rocketObject.getPosition());
                Point.Double v = new Point.Double();
                v.setLocation(rocketObject.getVelocity());
                for (int i = 1; i < p.length; i++) {
                    p[i].setLocation(p[i - 1].getLocation());
                    for (GameObject object : objects) {
                        if (object.getId() == ID.Planet && status == Status.PLAY && rocketObject != null) {
                            Planet planet = (Planet) object;
                            double M = planet.getPlanetMass();
                            double dx, dy, dis;
                            double a, ax, ay;
                            dx = object.getX() - p[i].getX();
                            dy = object.getY() - p[i].getY();
                            dis = Math.sqrt(dx * dx + dy * dy);
                            if (dis > 10) {
                                a = gravityConstant * M / (dis * dis);
                                ax = a * (dx / dis);
                                ay = a * (dy / dis);
                                v.setLocation(v.getX() + ax, v.getY() + ay);
                            }
                        }
                    }
                    p[i].setLocation(p[i].getX() + v.getX(), p[i].getY() + v.getY());
                }
                rocketObject.setEstimateLine(p);
                rocketObject.render(g, at);
            }

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

    public Status getBackToStatus() {
        return backToStatus;
    }

    public void setBackToStatus(Status backToStatus) {
        this.backToStatus = backToStatus;
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

    public synchronized void addButton(CustomButton button){
        this.buttons.add(button);
    }

    public void removeButton(CustomButton button){
        this.buttons.remove(button);
    }

    public Rocket getRocketObject() {
        return rocketObject;
    }

    public synchronized void addObject(GameObject object){
        if(object.getId()==ID.Rocket){
            rocketObject = (Rocket)object;
        }
        this.objects.add(object);
        for(GameObject gameObject:objects){
            gameObject.update();
        }
        updateSelectItem();
        if(rocketObject!=null){
            rocketObject.updateDestChoice();
        }
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

    public void removeAllObjects(){
        while(objects.size()!=0){
            objects.getLast().removeComponent();
            objects.removeLast();
        }
        while(buttons.size()!=0){
            buttons.getLast().removeComponent();
            buttons.removeLast();
        }
        if(rocketObject!=null){
            rocketObject.removeComponent();
            rocketObject=null;
        }
        setSelectItem(null);
    }

    public void removeObject(GameObject object,Point2D LeftUpDown, Point2D RightDownPoint){
        if(object.getId() == ID.Asteroid) {
            if (((Asteroid) object).getCount() >0) {
                ((Asteroid) object).setNewAsteroid(LeftUpDown, RightDownPoint);
                if(((Asteroid) object).getCount() == 0){
                    object.removeComponent();
                    this.objects.remove(object);
                }
            }
        }
        else {
            if(object.getId()!=ID.Rocket){
                object.removeComponent();
                this.objects.remove(object);
            }
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
        boolean i[] = new boolean[10];
        switch (status){
            case PLAY:
                i = new boolean[]{true,true,true,true,true,false,true,false,false,false};
                break;
            case STARTSCENE:
                i = new boolean[]{false,false,false,false,false,false,false,false,false,false};
                break;
            case EDIT:
                i = new boolean[]{true,true,true,true,true,false,true,false,true,false};
                break;
            case CHOOSING:
                i = new boolean[]{true,false,false,false,false,true,false,true,true,true};
                break;
            case HOWTO:
                break;
            case CREDIT:
                break;
            case PAUSE:
                break;
        }
        homeButton.setVisible(i[0]);
        showRadar.setVisible(i[1]);
        seleItem.setVisible(i[2]);
        selectUniverseButton.setVisible(i[3]);
        selectLabel.setVisible(i[4]);
        addNewButton.setVisible(i[5]);
        planetCenter.setVisible(i[6]);
        for(Button b:universeButton){
            b.setVisible(i[7]);
        }
        saveButton.setVisible(i[8]);
        loadButton.setVisible(i[9]);

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

   public void creatingButton()
    {
        LinkedList<LinkedList<String>> data = new LinkedList<>();
        String currentString;
        for(int i=0; i<objects.size() ; i++)
        {
            currentString = objects.get(i).toString();
            String[] set = currentString.split(",");
            LinkedList<String> d = new LinkedList<>();
            for(String s :set)
                d.add(s);
            data.add(d);
        }
        UniverseButton b = new UniverseButton("Map"+String.valueOf(universeButton.size()+1),this,game, data);
        b.setBounds((universeButton.size()*100)%800+100,(universeButton.size()/10*100)+200,50,50);
        b.setVisible(false);
        universeButton.add(b);
        game.getFrame().add(b,0);
    }

    public void saveGame() throws IOException
    {
        int useSelection = JFileChooser.APPROVE_OPTION;
        if(logFile == null)
        {
            JFileChooser fc = new JFileChooser();
            useSelection = fc.showSaveDialog(game.getFrame());
            if(useSelection == JFileChooser.APPROVE_OPTION)
                logFile = fc.getSelectedFile();
        }
        if(useSelection == JFileChooser.APPROVE_OPTION) {
            if (!logFile.getAbsolutePath().endsWith(".txt")) {
                File t = new File(logFile.getAbsolutePath() + ".txt");
                logFile = t;
            }
            FileWriter fileWriter = new FileWriter(logFile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (int i = 0; i < universeButton.size(); i++) {
                bufferedWriter.write("$" + Integer.toString(i+ 1));
                bufferedWriter.newLine();
                LinkedList<LinkedList<String>> data = universeButton.get(i).getData();
                for(int j = 0 ; j<data.size() ; j++)
                {
                    String string = "";
                    string += data.get(j).get(0);
                    for(int k=1 ; k<data.get(j).size() ; k++)
                    {
                        string+=","+data.get(j).get(k);
                    }
                    bufferedWriter.write(string);
                    bufferedWriter.newLine();
                }
                bufferedWriter.write("#" + Integer.toString(i + 1));
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }
    }

    public void openGame() throws  IOException
    {
        JFileChooser fc = new JFileChooser();
        int user = fc.showOpenDialog(game.getFrame());
        LinkedList<LinkedList<String>> data = new LinkedList<>();
        if(user == JFileChooser.APPROVE_OPTION){
            logFile = fc.getSelectedFile();
            String thisLine;
            try {
                FileReader fileReader = new FileReader(logFile.getAbsolutePath());
                BufferedReader br = new BufferedReader(fileReader);
                while ((thisLine = br.readLine()) != null)
                {
                    if (thisLine.contains("$")) {
                        readMode = true;
                        while(data.size()!=0)
                            data.removeLast();
                    }
                    if(thisLine.contains("#"))
                    {
                        readMode = false;
                        UniverseButton b = new UniverseButton("Map"+String.valueOf(universeButton.size()+1),this,game, data);
                        b.setBounds((universeButton.size()*100)%800+100,(universeButton.size()/10*100)+200,50,50);
                        universeButton.add(b);
                        game.getFrame().add(b,0);
                    }
                    if (readMode) {
                        String[] set = thisLine.split(",");
                        LinkedList<String> d = new LinkedList<>();
                        switch (set[0])
                        {
                            case "Planet":
                                for(String s : set)
                                    d.add(s);
                                data.add(d);
                                break;
                            case "Rocket":
                                for(String s : set)
                                    d.add(s);
                                data.add(d);
                                break;
                            case "Asteroid":
                                for(String s : set)
                                    d.add(s);
                                data.add(d);
                                break;
                        }
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
