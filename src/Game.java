import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import javax.swing.*;

public class Game extends Canvas implements Runnable {

    public static final int WIDTH = 1080,HEIGHT = WIDTH/12*9;
    private Thread thread;
    private boolean running=false;
    private Handler handler;
    private double scale;
    private AffineTransform at;
    private Window mainWindow;

    public Game(){
        at = new AffineTransform();
        at.setToTranslation(WIDTH/2,HEIGHT/2);
        scale = 1;
        mainWindow = new Window(WIDTH,HEIGHT,"Space Oddity",this);
        handler = new Handler(this);
        this.addKeyListener(new KeyInput(handler,this));
        MouseControl msc = new MouseControl(handler,this);
        this.addMouseListener(msc);
        this.addMouseMotionListener(msc);
        this.addMouseWheelListener(msc);

        handler.setStatus(Handler.Status.STARTSCENE);

        buildStartAnimation();
    }

    public void removeAll(){
        running = false;
        try {
            Thread.currentThread().sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("render stop");
        handler.removeAllObjects();

        at = new AffineTransform();
        at.setToTranslation(WIDTH/2,HEIGHT/2);
        scale = 1;

        System.out.println("delete finish");
    }

    public void buildPlayMode(){
        handler.setStatus(Handler.Status.PLAY);

        System.out.println("build play mode finish");
        start();
    }

    public void buildEditMode(){
        int i=0;
        int d = getHeight()/(2+9);
        for(CustomButton.ObjectType type: CustomButton.ObjectType.values()){
            if(type!=GameObject.ObjectType.ASTEROID && type!= GameObject.ObjectType.ROCKET){
                CustomButton button = new CustomButton(getWidth()/2-70,d*i-getHeight()/2+d,ID.CustomButton,type,handler);
                System.out.println(type);
                i++;
                handler.addButton(button);
            }
        }
        scale = 0.1;
        at.scale(scale,scale);
        System.out.println("build edit mode finish");
        start();
    }

    public void buildStartAnimation(){
        CustomButton button0 = new CustomButton(0,0,ID.CustomButton, CustomButton.ObjectType.SATURN,handler);
        CustomButton button1 = new CustomButton(0,0,ID.CustomButton, CustomButton.ObjectType.EARTH,handler);
        CustomButton button2 = new CustomButton(0,0,ID.CustomButton, CustomButton.ObjectType.JUPITER,handler);
        CustomButton button3 = new CustomButton(0,0,ID.CustomButton, CustomButton.ObjectType.NEPTUNE,handler);
        button0.setOrbitAngle(0);
        button1.setOrbitAngle(90);
        button2.setOrbitAngle(180);
        button3.setOrbitAngle(270);
        handler.addButton(button0);
        handler.addButton(button1);
        handler.addButton(button2);
        handler.addButton(button3);

        System.out.println("build start animation finish");
        start();
    }

    public void buildChooseMode(){


        System.out.println("build choose mode finish");
        start();
    }

    public JFrame getFrame(){
        return mainWindow.getFrame();
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public AffineTransform getAt() {
        return at;
    }

    public synchronized void start(){
        thread = new Thread(this);
        running = true;
        thread.start();
        System.out.println("start render");
    }

    @Override
    public void run(){
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1e9/amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        int sleepTime = 16;
        while(running){
            long now = System.nanoTime();
            delta += (now-lastTime) / ns;
            lastTime = now;
            while(delta >= 1){
                tick();
                delta--;
            }
            if(running){
                render();
            }
            frames++;

            if(System.currentTimeMillis()-timer > 1000){
                timer += 1000;
                System.out.println("FPS:"+frames);
                if(frames>=60){
                    sleepTime++;
                }
                else{
                    sleepTime--;
                }
                frames = 0;
            }
            try{
                thread.sleep(sleepTime);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void tick(){
        handler.tick();
    }

    public void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs==null){
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0,0,WIDTH,HEIGHT);
        handler.render(g,at);

        g.dispose();
        bs.show();
    }

    public static void main(String args[]){
        new Game();
    }
}
