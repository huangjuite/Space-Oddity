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
    Game game;
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

    public LinkedList<GameObject> loadObjects;
    boolean readMode = false;
    File logFile;
    String data[][] = new String [100][11];

    public Handler(Game game){
        this.game = game;
        objects = new LinkedList<>();
        buttons = new LinkedList<>();
        universeButton = new LinkedList<>();
        loadObjects = new LinkedList<>();

        saveButton = new Button("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    saveGame();
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
        if(traceMode && rocketObject!=null){
            at.setToIdentity();
            at.scale(game.getScale(),game.getScale());
            at.translate(-rocketObject.getX(),-rocketObject.getY());
            at.translate(game.getWidth()/2/game.getScale(),game.getHeight()/2/game.getScale());
        }else if(selectedObject!=null && planetCenter.getState()){
            at.setToIdentity();
            at.scale(game.getScale(),game.getScale());
            at.translate(-selectedObject.getX(),-selectedObject.getY());
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

    public void removeAllObjects(){
        while(objects.size()!=0){
            if(objects.getLast().getId()== ID.Rocket){
                Rocket rocket = (Rocket)objects.getLast();
                rocket.removeComponent();
            }else {
                objects.getLast().removeComponent();
            }
            objects.removeLast();
        }
        while(buttons.size()!=0){
            buttons.getLast().removeComponent();
            buttons.removeLast();
        }
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
        if(useSelection == JFileChooser.APPROVE_OPTION)
        {
            if(!logFile.getAbsolutePath().endsWith(".txt"))
            {
                File t = new File(logFile.getAbsolutePath()+".txt");
                logFile = t;
            }
            FileWriter fileWriter = new FileWriter(logFile,true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("$"+Integer.toString(universeButton.size()+1));
            bufferedWriter.newLine();
            for(GameObject gameObject:objects)
            {
                bufferedWriter.write(gameObject.toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.write("#"+Integer.toString(universeButton.size()+1));
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
    }

    public void openGame() throws  IOException
    {
        JFileChooser fc = new JFileChooser();
        int user = fc.showOpenDialog(game.getFrame());
        int i=0;
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
                        i=0;
                        while(loadObjects.size()!=0)
                            loadObjects.removeLast();
                    }
                    if(thisLine.contains("#"))
                    {
                        readMode = false;
                        UniverseButton b = new UniverseButton("Map"+String.valueOf(universeButton.size()+1),this,game, loadObjects, data);
                        b.setBounds((universeButton.size()*100)%800+100,(universeButton.size()/10*100)+200,50,50);
                        universeButton.add(b);
                        game.getFrame().add(b,0);
                    }
                    if (readMode) {
                        String[] set = thisLine.split(",");
                        int x, y, tankSize,count;
                        double degree, omega, orbitOmega, orbitTrackAngle, orbitAngle, volx, voly;
                        Rectangle orbitTrack;
                        GameObject.ObjectType type = GameObject.ObjectType.EARTH;
                        switch (set[0])
                        {
                            case "Planet":
                                x = Integer.parseInt(set[1]);
                                y = Integer.parseInt(set[2]);
                                Planet planet;
                                switch (set[3])
                                {
                                    case "JUPITER":
                                        type = GameObject.ObjectType.JUPITER;
                                        break;
                                    case "MARS":
                                        type = GameObject.ObjectType.MARS;
                                        break;
                                    case "MOON":
                                        type = GameObject.ObjectType.MOON;
                                        break;
                                    case "EARTH":
                                        type = GameObject.ObjectType.EARTH;
                                        break;
                                    case "VENUS":
                                        type = GameObject.ObjectType.VENUS;
                                        break;
                                    case "MERCURY":
                                        type = GameObject.ObjectType.MERCURY;
                                        break;
                                    case "SATURN":
                                        type = GameObject.ObjectType.SATURN;
                                        break;
                                    case "NEPTUNE":
                                        type = GameObject.ObjectType.NEPTUNE;
                                        break;
                                    case "URANUS":
                                        type = GameObject.ObjectType.URANUS;
                                        break;
                                }
                                planet = new Planet(x, y, ID.Planet, type, this);
                                data[i] =set;
                                i++;
                                loadObjects.add(planet);
                                break;
                            case "Rocket":
                                Rocket rocket;
                                x = Integer.parseInt(set[1]);
                                y = Integer.parseInt(set[2]);
                                tankSize = Integer.parseInt(set[3]);
                                rocket = new Rocket(x, y,tankSize,ID.Rocket, GameObject.ObjectType.ROCKET,this);
                                data[i] = set;
                                i++;
                                loadObjects.add(rocket);
                                break;
                            case "Asteroid":
                                count = Integer.parseInt(set[1]);
                                x = Integer.parseInt(set[2])+64;
                                y = Integer.parseInt(set[3])+64;
                                Asteroid asteroid = new Asteroid(x, y, ID.Asteroid, this);
                                for(int j = 1 ; j<count ; j++)
                                {
                                    x = Integer.parseInt(set[2*j+2]);
                                    y = Integer.parseInt(set[2*j+3]);
                                    asteroid.addAsteroid(x,y);
                                }
                                loadObjects.add(asteroid);
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
