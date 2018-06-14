import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class UniverseButton extends Button implements ActionListener {
    String universe;
    Handler handler;
    Game game;
    String [][] data;
    LinkedList<GameObject> loadMap = new LinkedList<>();
    int i;

    public UniverseButton(String universe, Handler handler, Game game, LinkedList<GameObject>loadMap, String data[][]){
        super(universe);
        i=0;
        this.universe = universe;
        this.data = data;
        this.handler = handler;
        this.game = game;
        //我加的
        for(GameObject map :loadMap)
            this.loadMap.add(map);
        setBackground(Color.gray);
        addActionListener(this);
    }

    public void constructUniverse(){
        i=0;
        for(GameObject object : loadMap) {
            double degree, omega, orbitOmega, orbitTrackAngle, orbitAngle, volx, voly;
            if(object.getId() != ID.Asteroid)
            {
                Rectangle orbitTrack;
                degree = Double.parseDouble(data[i][4]);
                omega = Double.parseDouble(data[i][5]);
                orbitOmega = Double.parseDouble(data[i][6]);
                orbitTrack = new Rectangle((int)Double.parseDouble(data[i][7]),(int)Double.parseDouble(data[i][8])
                        ,(int)Double.parseDouble(data[i][9]), (int)Double.parseDouble(data[i][10]));
                orbitTrackAngle = Double.parseDouble(data[i][11]);
                orbitAngle = Double.parseDouble(data[i][12]);
                volx = Double.parseDouble(data[i][13]);
                voly = Double.parseDouble(data[i][14]);

                object.setDegree(degree);
                object.setOmega(omega);
                object.setOrbitOmega(orbitOmega);
                object.setOrbitTrack(orbitTrack);
                object.setOrbitTrackAngle(orbitTrackAngle);
                object.setOrbitAngle(orbitAngle);
                object.setVolx(volx);
                object.setVoly(voly);
                i++;
            }
            if(object.getId()== ID.Rocket){
                ((Rocket)object).setComponentsVisible(true);
            }
            handler.addObject(object);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        game.removeAll();
        constructUniverse();
        if(handler.getBackToStatus()==Handler.Status.PLAY) {
            handler.setStatus(Handler.Status.PLAY);
            game.buildPlayMode();
        }
        else if(handler.getBackToStatus()==Handler.Status.EDIT){
            handler.setStatus(Handler.Status.EDIT);
            game.buildEditMode();
        }
    }
}
