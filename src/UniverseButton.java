import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UniverseButton extends Button implements ActionListener {
    String Universe;
    Handler handler;
    Game game;

    public UniverseButton(String universe, Handler handler, Game game){
        super("b");
        Universe = universe;
        this.handler = handler;
        this.game = game;
        setBackground(Color.gray);
        addActionListener(this);
    }

    public void constructUniverse(){
        handler.addObject(new Rocket(0,0,ID.Rocket, GameObject.ObjectType.ROCKET,handler));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        game.removeAll();

        if(handler.getBackToStatus()==Handler.Status.PLAY) {
            handler.setStatus(Handler.Status.PLAY);
            game.buildPlayMode();
        }
        else if(handler.getBackToStatus()==Handler.Status.EDIT){
            handler.setStatus(Handler.Status.EDIT);
            game.buildEditMode();
        }
        //constructUniverse();
    }
}
