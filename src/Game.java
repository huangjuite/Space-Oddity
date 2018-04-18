import java.awt.*;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;

public class Game extends Canvas implements Runnable {

    public static final int WIDTH = 1080,HEIGHT = WIDTH/12*9;
    private Thread thread;
    private boolean running=false;
    private Handler handler;
    private Point translateCord;
    private double scale;


    public Game(){
        translateCord = new Point(WIDTH/2,HEIGHT/2);
        scale = 1;
        handler = new Handler();
        this.addKeyListener(new KeyInput(handler));
        MouseControl msc = new MouseControl(handler,this);
        this.addMouseListener(msc);
        this.addMouseMotionListener(msc);
        this.addMouseWheelListener(msc);
        handler.addObject(new Rocket(WIDTH/2-16,HEIGHT/2-16,ID.Rocket,handler));
        handler.addObject(new Planet(150,150,ID.Planet,handler));
        new Window(WIDTH,HEIGHT,"Space Oddity",this);
    }

    public Point getTranslateCord(){
        return translateCord;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public synchronized void start(){
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    public synchronized void stop(){
        try{
            thread.join();
            running = false;
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1e9/amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
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
                frames = 0;
            }
        }
        stop();
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
        Graphics2D g2d = (Graphics2D) g;
        g2d.scale(scale,scale);
        double width = getWidth();
        double height = getHeight();

        double zoomWidth = width * scale;
        double zoomHeight = height * scale;

        double anchorx = (width - zoomWidth) / 2;
        double anchory = (height - zoomHeight) / 2;

        AffineTransform at = new AffineTransform();
        at.translate(anchorx, anchory);
        at.scale(scale, scale);
        at.translate(-anchorx, -anchory);

        g2d.setTransform(at);
        g.setColor(Color.gray);
        g.fillRect(0,0,WIDTH,HEIGHT);
        handler.render(g);


        g.dispose();
        bs.show();
    }

    public static void main(String args[]){
        new Game();
    }
}
