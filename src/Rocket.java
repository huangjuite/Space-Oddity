import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.LinkedList;

public class Rocket extends GameObject {
    private BufferedImage rocketImage[];
    private BufferedImage startPinImage,destPinImage;
    private int boostStatus=0;
    private double rocketImageScale = 0.8;
    private boolean locating = false;
    private int rocketImageSize;
    private int fule,tankSize;
    private Scrollbar tankSizeBar;
    private Choice factor;
    private Label tankSizeLabel,destLabel;
    private TextField amountField;
    private Point[] estimateLine;
    private Choice destChoice;
    private GameObject destinationPlanet;
    private LinkedList<GameObject> destChoiceList;
    private boolean gameFinish;
    private Polygon path;
    private int completeCount;

    public Rocket(int x, int y,int tankSize, ID id,ObjectType type,Handler handler) {
        super(x, y, id,type,handler);
        this.tankSize = tankSize;
        destChoiceList = new LinkedList<>();
        rocketImage = new BufferedImage[5];
        try {
            for(int i=0;i<5;i++){
                BufferedImage image = ImageIO.read(getClass().getResource("rocket"+i+".png"));
                rocketImage[i] = resizeImage(image,rocketImageScale);
            }
            BufferedImage image = ImageIO.read(getClass().getResource("pin.png"));
            startPinImage = resizeImage(image,0.1);
            BufferedImage image1 = ImageIO.read(getClass().getResource("pin_destination.png"));
            destPinImage = resizeImage(image1,0.1);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        rocketImageSize = rocketImage[0].getWidth();
        estimateLine = new Point[200];
        for(int i=0;i<estimateLine.length;i++){
            estimateLine[i] = new Point();
        }
        path = new Polygon();

        destChoice = new Choice();
        destChoice.add("0:None");
        destChoice.select(0);
        destLabel = new Label("Destination:");
        tankSizeBar = new Scrollbar(Scrollbar.HORIZONTAL,0,1,0,10000);
        factor = new Choice();
        amountField = new TextField();
        tankSizeLabel = new Label("Energy");
        amountField.setEditable(false);
        factor.add("x1");
        factor.add("x2");
        factor.add("x4");
        factor.add("x8");
        factor.add("x16");
        factor.add("x32");
        destLabel.setVisible(false);
        destChoice.setVisible(false);
        tankSizeBar.setVisible(false);
        factor.setVisible(false);
        amountField.setVisible(false);
        tankSizeLabel.setVisible(false);

        destChoice.setBackground(Color.gray);
        destLabel.setBackground(Color.gray);
        tankSizeBar.setBackground(Color.gray);
        factor.setBackground(Color.gray);
        amountField.setBackground(Color.white);
        tankSizeLabel.setBackground(Color.gray);

        destChoice.setBounds(380,50,100,20);
        destLabel.setBounds(300,50,80,20);
        tankSizeBar.setBounds(350,30,300,20);
        factor.setBounds(650,30,100,20);
        amountField.setBounds(750,30,70,20);
        tankSizeLabel.setBounds(300,30,50,20);

        addComponent();

        if(handler.getStatus()== Handler.Status.EDIT){
            setComponentsVisible(true);
        }

        tankSizeBar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                int num = tankSizeBar.getValue()*(int)Math.pow(2,factor.getSelectedIndex());
                amountField.setText(Integer.toString(num));
                setTankSize(num);
            }
        });
        factor.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int num = tankSizeBar.getValue()*(int)Math.pow(2,factor.getSelectedIndex());
                amountField.setText(Integer.toString(num));
                setTankSize(num);
            }
        });
        destChoice.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int index = destChoice.getSelectedIndex();
                if(index!=0){
                    destinationPlanet = destChoiceList.get(index-1);
                }
                else{
                    destinationPlanet = null;
                }
            }
        });
    }


    @Override
    public void removeComponent(){
        super.removeComponent();
        handler.getGame().getFrame().remove(tankSizeBar);
        handler.getGame().getFrame().remove(tankSizeLabel);
        handler.getGame().getFrame().remove(factor);
        handler.getGame().getFrame().remove(amountField);
        handler.getGame().getFrame().remove(destLabel);
        handler.getGame().getFrame().remove(destChoice);
    }

    @Override
    public void addComponent(){
        handler.getGame().getFrame().add(destChoice,0);
        handler.getGame().getFrame().add(destLabel,0);
        handler.getGame().getFrame().add(tankSizeBar,0);
        handler.getGame().getFrame().add(tankSizeLabel,0);
        handler.getGame().getFrame().add(factor,0);
        handler.getGame().getFrame().add(amountField,0);
    }

    @Override
    public void setComponentsVisible(boolean b){
        super.setComponentsVisible(false);
        destLabel.setVisible(b);
        destChoice.setVisible(b);
        tankSizeBar.setVisible(b);
        factor.setVisible(b);
        amountField.setVisible(b);
        tankSizeLabel.setVisible(b);
    }

    public boolean isGameFinish() {
        return gameFinish;
    }

    public void setGameFinish(boolean gameFinish) {
        this.gameFinish = gameFinish;
    }

    public GameObject getDestinationPlanet() {
        return destinationPlanet;
    }

    public void setDestinationPlanet(GameObject destinationPlanet) {
        this.destinationPlanet = destinationPlanet;
    }

    public void updateDestChoice(){
        destChoiceList.removeAll(destChoiceList);
        for(GameObject obj:handler.objects){
            if(obj!=this && obj.getId()!=ID.Asteroid){
                destChoiceList.add(obj);
            }
        }

        int index = destChoice.getSelectedIndex();
        destChoice.removeAll();
        destChoice.add("0:None");
        int i=1;
        for(GameObject object:destChoiceList){
            destChoice.add(i+":"+object.getType().toString());
            i++;
        }
        destChoice.select(index);
    }

    public BufferedImage getRocketImage(){
        return rocketImage[0];
    }

    public void setBoostImage(int n,boolean b){
        if (b) {
            boostStatus |= 1<<n;
        }
        else {
            boostStatus &= ~(1<<n);
        }
    }

    public Point2D[] getEstimateLine() {
        return estimateLine;
    }

    public void setEstimateLine(Point[] estimateLine) {
        for(int i=0;i<estimateLine.length;i++){
            this.estimateLine[i] = estimateLine[i] ;
        }
    }

    @Override
    public Rectangle getBounds(){
        Rectangle rec  = new Rectangle(x-rocketImageSize/2,
                y-rocketImageSize/2,
                rocketImageSize,
                rocketImageSize);
        return rec;
    }

    public Polygon getTransformedPoly(){
        int[] ax = path.xpoints;
        int[] ay = path.ypoints;
        Polygon polygon = new Polygon();
        AffineTransform at = handler.getGame().getAt();
        for(int i=0;i<path.npoints;i++){
            polygon.addPoint((int)(ax[i]*at.getScaleX()+at.getTranslateX()),
                    (int)(ay[i]*at.getScaleY()+at.getTranslateY()));
        }
        return  polygon;
    }

    public boolean checkProgress(){
        Polygon polygon = ((Planet)destinationPlanet).getBoundPolygon(handler.getGame().getAt(),true);
        Polygon p = getTransformedPoly();
        for(int i=0;i<polygon.npoints;i++){
            if(i%(polygon.npoints/20)==0) {
                if (!p.contains(polygon.xpoints[i], polygon.ypoints[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void tick() {
        //temp
        if(handler.objects.size()==2) {
            destinationPlanet = handler.objects.getLast();
        }
        //temp

        if(destinationPlanet!=null && handler.getStatus()== Handler.Status.PLAY){
            double dis = Math.sqrt(Math.pow(x-destinationPlanet.x,2)+Math.pow(y-destinationPlanet.y,2));
            Planet planet = (Planet)destinationPlanet;
            if(dis<planet.getRadius()*planet.getBoundsMargin()){
                AffineTransform at = handler.getGame().getAt();

                int dx = x;
                int dy = y;
                path.addPoint(dx,dy);
                if(checkProgress()){
                    if(completeCount<5){
                        completeCount++;
                        path.reset();
                    }
                    else {
                        setGameFinish(true);
                        handler.setGamePass(true);
                        path.reset();
                        System.out.println("finish");
                    }
                }
            }
            else{
                completeCount = 0;
                path.reset();
            }
        }

        x+=volx;
        y+=voly;
        degree+=omega;
    }

    @Override
    public void render(Graphics g,AffineTransform at) {
        Graphics2D g2d = (Graphics2D) g;

        if(destinationPlanet!=null){
            AffineTransform nat = new AffineTransform();
            Planet planet = (Planet)destinationPlanet;
            double dx = planet.getX()*at.getScaleX()-startPinImage.getWidth()/2+at.getTranslateX();
            double dy = planet.getY()*at.getScaleY()-startPinImage.getHeight()
                    -(planet.getRadius()+100)*at.getScaleY()+at.getTranslateY();
            double maxX=handler.getGame().getWidth()-startPinImage.getWidth();
            double maxY=handler.getGame().getHeight()-startPinImage.getHeight();
            dx=(dx<0)? 0:dx; dx=(dx>maxX)?maxX:dx;
            dy=(dy<0)? 0:dy; dy=(dy>maxY)?maxY:dy;
            nat.translate(dx,dy);
            double dis = Math.sqrt(Math.pow(x-destinationPlanet.x,2)+Math.pow(y-destinationPlanet.y,2));
            if(handler.getStatus()== Handler.Status.EDIT ||
                    (handler.getStatus()== Handler.Status.PLAY &&
                            dis>(planet.getRadius()*planet.getBoundsMargin()))){
                g2d.drawImage(destPinImage, nat, null);
            }


            g2d.drawPolygon(getTransformedPoly());

            Polygon polygon = ((Planet)destinationPlanet).getBoundPolygon(handler.getGame().getAt(),true);
            g2d.drawPolygon(polygon);

        }

        if(handler.getStatus()==Handler.Status.EDIT){
            omega = 0.4;
            AffineTransform nat = new AffineTransform();
            nat.translate(200,10);
            nat.scale(0.7,0.7);
            nat.rotate(Math.toRadians(degree),
                    rocketImageSize/2,
                    rocketImageSize/2);
            g2d.drawImage(rocketImage[0],nat,null);
            nat.setToIdentity();
            nat.translate(-startPinImage.getWidth()/2,-startPinImage.getHeight());
            nat.translate(at.getTranslateX(),at.getTranslateY());
            g2d.drawImage(startPinImage,nat,null);
        }
        else {
            AffineTransform newat = AffineTransform.getTranslateInstance(
                    x-rocketImageSize/2,
                    y-rocketImageSize/2);
            newat.rotate(Math.toRadians(degree),
                    rocketImageSize/2,
                    rocketImageSize/2);
            newat.preConcatenate(at);
            g2d.drawImage(rocketImage[0],newat,null);

            for(int i=1;i<5;i++){
                if((boostStatus&(1<<i)) != 0){
                    g2d.drawImage(rocketImage[i],newat,null);
                }
            }

            if(locating) drawRadar(g2d,at);
            drawHUD(g2d);

            g2d.setColor(Color.white);
            g2d.setStroke(new BasicStroke((float)1));
            for(int i=0;i<estimateLine.length;i++){
                if(i%5==0){
                    g2d.drawRect((int)(estimateLine[i].getX()*at.getScaleX()+at.getTranslateX()),
                            (int)(estimateLine[i].getY()*at.getScaleY()+at.getTranslateY()),
                            1,1);
                }
            }
        }

    }


    public void drawHUD(Graphics2D g2d){
        AffineTransform newat = new AffineTransform();
        newat.setToIdentity();
        newat.rotate(Math.toRadians(degree),
                rocketImageSize/2,
                rocketImageSize/2);
        AffineTransform preAt = new AffineTransform();
        int radius = rocketImageSize/2+40;
        int centerX = handler.getGame().getWidth()-rocketImageSize/2-40;
        int centerY = handler.getGame().getHeight()-rocketImageSize/2-40;
        preAt.translate(centerX-rocketImageSize/2,
                centerY-rocketImageSize/2);
        newat.preConcatenate(preAt);

        float hue = (float)(0.5*fule/(float)tankSize);
        g2d.setColor(new Color(Color.HSBtoRGB(hue,(float)0.77,1)));
        g2d.fillArc(centerX-rocketImageSize/2-80,
                centerY-rocketImageSize/2-80,
                2*radius+80, 2*radius+80,(int)(135+90*(1-fule/(float)tankSize)),
                (int)(90*(fule/(float)tankSize)));
        g2d.setColor(Color.gray);
        g2d.setStroke(new BasicStroke(3));
        g2d.fillOval(centerX-rocketImageSize/2-40,
                centerY-rocketImageSize/2-40,
                2*radius, 2*radius);
        g2d.setColor(Color.white);
        g2d.drawOval(centerX-rocketImageSize/2-40,
                centerY-rocketImageSize/2-40,
                2*radius, 2*radius);
        g2d.setStroke(new BasicStroke(4));
        double dis = Math.sqrt(volx*volx+voly*voly);
        hue = (float)(0.5 - ((dis>120)?0.5:dis*0.5/120));
        g2d.setColor(new Color(Color.HSBtoRGB(hue,(float)0.77,(float)1)));

        int dirx = (int)(volx/dis*radius);
        int diry = (int)(voly/dis*radius);
        g2d.drawLine(centerX, centerY,centerX+dirx, centerY+diry);
        g2d.drawImage(rocketImage[0],newat,null);

        for(int i=1;i<5;i++){
            if((boostStatus&(1<<i)) != 0){
                g2d.drawImage(rocketImage[i],newat,null);
            }
        }
    }

    public int getFule() {
        return fule;
    }

    public void setFule(int fule) {
        if(fule>0) this.fule = fule;
    }

    public int getTankSize() {
        return tankSize;
    }

    public void setTankSize(int tankSize) {
        this.tankSize = tankSize;
    }

    public boolean isLocating() {
        return locating;
    }

    public void setLocating(boolean locating) {
        this.locating = locating;
    }

    public void setDestChoice(int x)
    {
        destChoice.select(x);
        if(x==0)
            destinationPlanet = null;
        else
            destinationPlanet = destChoiceList.get(x-1);
    }

    @Override
    public String toString()
    {
        return "Rocket"+","+x+","+y+","+tankSize+","+degree+","+omega+","+orbitOmega+","+orbitTrack.getLocation().getX()
                +","+orbitTrack.getLocation().getY()+","+orbitTrack.getWidth()+","+orbitTrack.getHeight()+","+
                orbitTrackAngle+","+orbitAngle+","+volx+","+voly+","+destChoice.getSelectedIndex();
    }
}
