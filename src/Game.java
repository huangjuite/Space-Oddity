import java.awt.*;
import java.awt.image.BufferStrategy;

public class Game extends Canvas implements Runnable {

    public static final int WIDTH = 1080,HEIGHT = WIDTH/12*9;
    private Thread thread;
    private boolean running=false;
    private Handler handler;

    public Game(){
        handler = new Handler();
        this.addKeyListener(new KeyInput(handler));
        new Window(WIDTH,HEIGHT,"Space Oddity",this);

        handler.addObject(new Rocket(WIDTH/2-16,HEIGHT/2-16,ID.Rocket,handler));
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
