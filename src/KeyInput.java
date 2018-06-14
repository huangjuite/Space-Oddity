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

        if(key==KeyEvent.VK_A){handler.setDrawingAsteroid();}

        if(key==KeyEvent.VK_R){
            game.getAt().setToTranslation(game.getWidth()/2,game.getHeight()/2);
            game.getAt().scale(game.getScale(),game.getScale());
        }

        if(key==KeyEvent.VK_Z){
            game.getAt().scale(1/game.getScale(),1/game.getScale());
            game.setScale(1/game.getScale());
        }

        if(key==KeyEvent.VK_E){
            handler.setStatus(Handler.Status.EDIT);
        }

        if(key==KeyEvent.VK_S){
            handler.setStatus(Handler.Status.PLAY);
        }

        if(key==KeyEvent.VK_L){
            Rocket rocket = handler.getRocketObject();
            if(rocket!=null){
                if(rocket.isLocating()) rocket.setLocating(false);
                else rocket.setLocating(true);
            }
        }

        if(handler.getStatus()== Handler.Status.PLAY) {
            for (GameObject tempObject : handler.objects) {
                if (tempObject.getId() == ID.Rocket) {
                    Rocket rocket = (Rocket) tempObject;

                    if (key == KeyEvent.VK_UP) {
                        rocket.setBoostImage(1, true);
                        double value = 1;
                        double ra = Math.toRadians(rocket.getDegree() - 90);
                        rocket.setVoly(value * Math.sin(ra) + rocket.getVoly());
                        rocket.setVolx(value * Math.cos(ra) + rocket.getVolx());
                        rocket.setFule(rocket.getFule() - 10);
                    }

                    if (key == KeyEvent.VK_DOWN) {
                        rocket.setBoostImage(2, true);
                        double value = -1;
                        double ra = Math.toRadians(rocket.getDegree() - 90);
                        rocket.setVoly(value * Math.sin(ra) + rocket.getVoly());
                        rocket.setVolx(value * Math.cos(ra) + rocket.getVolx());
                        rocket.setFule(rocket.getFule() - 10);

                    }

                    if (key == KeyEvent.VK_LEFT) {
                        rocket.setBoostImage(3, true);
                        tempObject.setOmega(tempObject.getOmega() - 0.2);
                        rocket.setFule(rocket.getFule() - 10);

                    }

                    if (key == KeyEvent.VK_RIGHT) {
                        rocket.setBoostImage(4, true);
                        tempObject.setOmega(tempObject.getOmega() + 0.2);
                        rocket.setFule(rocket.getFule() - 10);

                    }

                    if (key == KeyEvent.VK_T) {
                        if (handler.traceMode == true) {
                            handler.traceMode = false;
                        } else {
                            handler.traceMode = true;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        for(GameObject tempObject:handler.objects){
            if(tempObject.getId()==ID.Rocket){
                Rocket rocket = (Rocket)tempObject;

                if(key== KeyEvent.VK_UP){
                    rocket.setBoostImage(1,false);
                }
                if(key== KeyEvent.VK_DOWN){
                    rocket.setBoostImage(2,false);
                }
                if(key== KeyEvent.VK_LEFT){
                    rocket.setBoostImage(3,false);
                }
                if(key== KeyEvent.VK_RIGHT){
                    rocket.setBoostImage(4,false);
                }
            }
        }
    }

}
