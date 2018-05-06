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
            //game.getAt().setToScale(1,1);
            game.getAt().setToTranslation(game.getTranslateCord().getX(),game.getTranslateCord().getY());
        }

        for(GameObject tempObject:handler.objects) {
            if (tempObject.getId() == ID.Rocket){
                Rocket rocket = (Rocket)tempObject;

                if(key== KeyEvent.VK_UP){
                    rocket.setBoostImage(true);
                    double vx = rocket.getVolx();
                    double vy = rocket.getVoly();
                    double value = Math.sqrt(vx * vx + vy * vy) + 0.1;
                    double ra = Math.toRadians(rocket.getDegree() - 90);
                    rocket.setVoly(value * Math.sin(ra));
                    rocket.setVolx(value * Math.cos(ra));
                }

                if(key== KeyEvent.VK_DOWN){
                    double vx = tempObject.getVolx();
                    double vy = tempObject.getVoly();
                    double value = Math.sqrt(vx * vx + vy * vy) - 0.1;
                    if (value > 0) {
                        double ra = Math.toRadians(tempObject.getDegree() - 90);
                        tempObject.setVoly(value * Math.sin(ra));
                        tempObject.setVolx(value * Math.cos(ra));
                    } else {
                        tempObject.setVolx(0);
                        tempObject.setVoly(0);
                    }
                }

                if(key==KeyEvent.VK_LEFT){
                    tempObject.setOmega(tempObject.getOmega() - 0.5);

                }

                if(key==KeyEvent.VK_RIGHT) {
                    tempObject.setOmega(tempObject.getOmega() + 0.5);

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
