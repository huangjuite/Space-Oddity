import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.Key;

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
            //game.getAt().setToScale(1,1);
            game.getAt().setToTranslation(game.getTranslateCord().getX(),game.getTranslateCord().getY());
        }

        if(key== KeyEvent.VK_UP){
            for(GameObject tempObject:handler.objects) {
                if (tempObject.getId() == ID.Rocket) {
                    double vx = tempObject.getVolx();
                    double vy = tempObject.getVoly();
                    double value = Math.sqrt(vx * vx + vy * vy) + 1;
                    double ra = Math.toRadians(tempObject.getDegree() - 90);
                    tempObject.setVoly(value * Math.sin(ra));
                    tempObject.setVolx(value * Math.cos(ra));
                }
            }
        }

        if(key== KeyEvent.VK_DOWN){
            for(GameObject tempObject:handler.objects) {
                if (tempObject.getId() == ID.Rocket) {
                    double vx = tempObject.getVolx();
                    double vy = tempObject.getVoly();
                    double value = Math.sqrt(vx * vx + vy * vy) - 1;
                    if (value > 0) {
                        double ra = Math.toRadians(tempObject.getDegree() - 90);
                        tempObject.setVoly(value * Math.sin(ra));
                        tempObject.setVolx(value * Math.cos(ra));
                    } else {
                        tempObject.setVolx(0);
                        tempObject.setVoly(0);
                    }
                }
            }
        }

        if(key==KeyEvent.VK_LEFT){
            for(GameObject tempObject:handler.objects) {
                if (tempObject.getId() == ID.Rocket) {
                    tempObject.setOmega(tempObject.getOmega() - 0.5);
                    double vx = tempObject.getVolx();
                    double vy = tempObject.getVoly();
                    double value = Math.sqrt(vx * vx + vy * vy);
                    double ra = Math.toRadians(tempObject.getDegree() - 90);
                    tempObject.setVoly(value * Math.sin(ra));
                    tempObject.setVolx(value * Math.cos(ra));
                }
            }
        }

        if(key==KeyEvent.VK_RIGHT) {
            for (GameObject tempObject : handler.objects) {
                if (tempObject.getId() == ID.Rocket) {
                    tempObject.setOmega(tempObject.getOmega() + 0.5);
                    double vx = tempObject.getVolx();
                    double vy = tempObject.getVoly();
                    double value = Math.sqrt(vx * vx + vy * vy);
                    double ra = Math.toRadians(tempObject.getDegree() - 90);
                    tempObject.setVoly(value * Math.sin(ra));
                    tempObject.setVolx(value * Math.cos(ra));
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
    }

}
