import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;

public class Rocket extends GameObject {
    private BufferedImage rocketImage[];
    private BufferedImage pinImage;
    private int boostStatus=0;
    private double rocketImageScale = 0.8;
    private boolean locating = false;
    private int rocketImageSize;
    private int fule,tankSize;
    private Scrollbar tanksizeBar;
    private Choice factor;
    private Label tankSizeLabel;
    private TextField amountField;

    public Rocket(int x, int y,int tankSize, ID id,ObjectType type,Handler handler) {
        super(x, y, id,type,handler);
        this.tankSize = tankSize;
        rocketImage = new BufferedImage[5];
        try {
            for(int i=0;i<5;i++){
                BufferedImage image = ImageIO.read(getClass().getResource("rocket"+i+".png"));
                rocketImage[i] = resizeImage(image,rocketImageScale);
            }
            BufferedImage image = ImageIO.read(getClass().getResource("pin.png"));
            pinImage = resizeImage(image,1);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        rocketImageSize = rocketImage[0].getWidth();


        tanksizeBar = new Scrollbar(Scrollbar.HORIZONTAL,0,1,0,10000);
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
        tanksizeBar.setVisible(false);
        factor.setVisible(false);
        amountField.setVisible(false);
        tankSizeLabel.setVisible(false);

        tanksizeBar.setBackground(Color.gray);
        factor.setBackground(Color.gray);
        amountField.setBackground(Color.white);
        tankSizeLabel.setBackground(Color.gray);

        tanksizeBar.setBounds(350,30,300,20);
        factor.setBounds(650,30,100,20);
        amountField.setBounds(750,30,70,20);
        tankSizeLabel.setBounds(300,30,50,20);

        handler.getGame().getFrame().add(tanksizeBar,0);
        handler.getGame().getFrame().add(tankSizeLabel,0);
        handler.getGame().getFrame().add(factor,0);
        handler.getGame().getFrame().add(amountField,0);

        if(handler.getStatus()== Handler.Status.EDIT){
            tanksizeBar.setVisible(true);
            factor.setVisible(true);
            amountField.setVisible(true);
            tankSizeLabel.setVisible(true);
        }

        tanksizeBar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                int num = tanksizeBar.getValue()*(int)Math.pow(2,factor.getSelectedIndex());
                amountField.setText(Integer.toString(num));
                setTankSize(num);
            }
        });
        factor.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int num = tanksizeBar.getValue()*(int)Math.pow(2,factor.getSelectedIndex());
                amountField.setText(Integer.toString(num));
                setTankSize(num);
            }
        });
    }

    @Override
    public void removeComponent(){
        super.removeComponent();
        handler.getGame().getFrame().remove(tanksizeBar);
        handler.getGame().getFrame().remove(tankSizeLabel);
        handler.getGame().getFrame().remove(factor);
        handler.getGame().getFrame().remove(amountField);
    }

    @Override
    public void setComponentsVisible(boolean b){
        super.setComponentsVisible(b);
        tanksizeBar.setVisible(b);
        factor.setVisible(b);
        amountField.setVisible(b);
        tankSizeLabel.setVisible(b);
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

    @Override
    public Rectangle getBounds(){
        Rectangle rec  = new Rectangle(x-rocketImageSize/2,
                y-rocketImageSize/2,
                rocketImageSize,
                rocketImageSize);
        return rec;
    }

    @Override
    public void tick() {
        x+=volx;
        y+=voly;
        degree+=omega;
    }

    @Override
    public void render(Graphics g,AffineTransform at) {
        Graphics2D g2d = (Graphics2D) g;

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
            nat.translate(-pinImage.getWidth()/2,-pinImage.getHeight());
            nat.preConcatenate(at);
            g2d.drawImage(pinImage,nat,null);
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

    @Override
    public String toString()
    {
        return "Rocket"+","+x+","+y+","+tankSize+","+degree+","+omega+","+orbitOmega+","+orbitTrack.getLocation().getX()
                +","+orbitTrack.getLocation().getY()+","+orbitTrack.getWidth()+","+orbitTrack.getHeight()+","+
                orbitTrackAngle+","+orbitAngle+","+volx+","+voly;
    }
}
