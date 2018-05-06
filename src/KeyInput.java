import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {
    Handler handler;
    Game game;

    public KeyInput(Handler handler,Game game) {
        this.handler = handler;
        this.game = game;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if(key==KeyEvent.VK_R){
            game.getAt().setToTranslation(game.getWidth()/2,game.getHeight()/2);
            game.getAt().scale(game.getScale(),game.getScale());
        }

        for(GameObject tempObject:handler.objects) {
            if (tempObject.getId() == ID.Rocket){
                Rocket rocket = (Rocket)tempObject;

                if(key== KeyEvent.VK_UP){
                    rocket.setBoostImage(true);
                    double value = 1;
                    double ra = Math.toRadians(rocket.getDegree() - 90);
                    rocket.setVoly(value * Math.sin(ra)+rocket.getVoly());
                    rocket.setVolx(value * Math.cos(ra)+rocket.getVolx());
                }

                if(key== KeyEvent.VK_DOWN){
                    //rocket.setBoostImage(false);
                    double value = -1;
                    double ra = Math.toRadians(rocket.getDegree() - 90);
                    rocket.setVoly(value * Math.sin(ra)+rocket.getVoly());
                    rocket.setVolx(value * Math.cos(ra)+rocket.getVolx());
                }

                if(key==KeyEvent.VK_LEFT){
                    tempObject.setOmega(tempObject.getOmega() - 0.2);

                }

                if(key==KeyEvent.VK_RIGHT) {
                    tempObject.setOmega(tempObject.getOmega() + 0.2);

                }

                if(key==KeyEvent.VK_T){
                    if(handler.traceMode==true){
                        handler.traceMode=false;
                    }
                    else {
                        handler.traceMode = true;
                    }
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        for(GameObject tempObject:handler.objects) {
            if(tempObject.getId()==ID.Rocket){
                ((Rocket)tempObject).setBoostImage(false);
            }
        }
    }

}
