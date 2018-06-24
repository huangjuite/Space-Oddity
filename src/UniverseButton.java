import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class UniverseButton extends Button implements ActionListener {
    String universe;
    Handler handler;
    Game game;
    LinkedList<LinkedList<String>> data = new LinkedList<>();
    LinkedList<GameObject> loadMap = new LinkedList<>();
    int i;

    public UniverseButton(String universe, Handler handler, Game game, LinkedList<LinkedList<String>> data){
        super(universe);
        i=0;
        this.universe = universe;
        for(int i=0 ; i<data.size() ; i++) {
            LinkedList<String> d = new LinkedList<>();
            if(data.get(i)!=null) {
                for (int j = 0; j < data.get(i).size(); j++) {
                    d.add(data.get(i).get(j));
                }
            }
            this.data.add(d);
        }
        this.handler = handler;
        this.game = game;
        for(GameObject obj : loadMap){
            this.loadMap.add(obj);
        }
        setBackground(Color.gray);
        addActionListener(this);
    }

    public void constructUniverse() {
        while(loadMap.size()!=0)
            loadMap.removeLast();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) != null) {
                int x, y, tankSize, count;
                GameObject.ObjectType type = GameObject.ObjectType.EARTH;
                switch (data.get(i).get(0)) {
                    case "Planet":
                        x = Integer.parseInt(data.get(i).get(1));
                        y = Integer.parseInt(data.get(i).get(2));
                        Planet planet;
                        switch (data.get(i).get(3)) {
                            case "JUPITER":
                                type = GameObject.ObjectType.JUPITER;
                                break;
                            case "MARS":
                                type = GameObject.ObjectType.MARS;
                                break;
                            case "MOON":
                                type = GameObject.ObjectType.MOON;
                                break;
                            case "EARTH":
                                type = GameObject.ObjectType.EARTH;
                                break;
                            case "VENUS":
                                type = GameObject.ObjectType.VENUS;
                                break;
                            case "MERCURY":
                                type = GameObject.ObjectType.MERCURY;
                                break;
                            case "SATURN":
                                type = GameObject.ObjectType.SATURN;
                                break;
                            case "NEPTUNE":
                                type = GameObject.ObjectType.NEPTUNE;
                                break;
                            case "URANUS":
                                type = GameObject.ObjectType.URANUS;
                                break;
                            case "SUN":
                                type = GameObject.ObjectType.SUN;
                                break;
                        }
                        planet = new Planet(x, y, ID.Planet, type, handler);
                        loadMap.add(planet);
                        break;
                    case "Rocket":
                        Rocket rockets;
                        x = Integer.parseInt(data.get(i).get(1));
                        y = Integer.parseInt(data.get(i).get(2));
                        tankSize = Integer.parseInt(data.get(i).get(3));
                        rockets = new Rocket(x, y, tankSize, ID.Rocket, GameObject.ObjectType.ROCKET, handler);
                        loadMap.add(rockets);
                        break;
                    case "Asteroid":
                        count = Integer.parseInt(data.get(i).get(1));
                        x = Integer.parseInt(data.get(i).get(2)) + 64;
                        y = Integer.parseInt(data.get(i).get(3)) + 64;
                        Asteroid asteroid = new Asteroid(x, y, ID.Asteroid, handler);
                        for (int j = 1; j < count; j++) {
                            x = Integer.parseInt(data.get(i).get(2 * j + 2));
                            y = Integer.parseInt(data.get(i).get(2 * j + 3));
                            asteroid.addAsteroid(x, y);
                        }
                        loadMap.add(asteroid);
                        break;
                }
            }
        }
        for (int i=0 ; i<loadMap.size() ; i++) {
            double degree, omega, orbitOmega, orbitTrackAngle, orbitAngle, volx, voly;
            if (loadMap.get(i).getId() != ID.Asteroid) {
                Rectangle orbitTrack;
                degree = Double.parseDouble(data.get(i).get(4));
                omega = Double.parseDouble(data.get(i).get(5));
                orbitOmega = Double.parseDouble(data.get(i).get(6));
                orbitTrack = new Rectangle((int) Double.parseDouble(data.get(i).get(7)), (int) Double.parseDouble(data.get(i).get(8))
                        , (int) Double.parseDouble(data.get(i).get(9)), (int) Double.parseDouble(data.get(i).get(10)));
                orbitTrackAngle = Double.parseDouble(data.get(i).get(11));
                orbitAngle = Double.parseDouble(data.get(i).get(12));
                volx = Double.parseDouble(data.get(i).get(13));
                voly = Double.parseDouble(data.get(i).get(14));

                loadMap.get(i).setDegree(degree);
                loadMap.get(i).setOmega(omega);
                loadMap.get(i).setOrbitOmega(orbitOmega);
                loadMap.get(i).setOrbitTrack(orbitTrack);
                loadMap.get(i).setOrbitTrackAngle(orbitTrackAngle);
                loadMap.get(i).setOrbitAngle(orbitAngle);
                loadMap.get(i).setVolx(volx);
                loadMap.get(i).setVoly(voly);
            }
            if (loadMap.get(i).getId() == ID.Rocket) {
                if (handler.getStatus() == Handler.Status.PLAY) {
                    ((Rocket) loadMap.get(i)).setComponentsVisible(false);
                } else if (handler.getStatus() == Handler.Status.EDIT) {
                    ((Rocket) loadMap.get(i)).setComponentsVisible(true);
                }
            } else {
                loadMap.get(i).setComponentsVisible(false);
            }
            handler.addObject(loadMap.get(i));
        }
        for(int i=0 ; i<loadMap.size() ; i++)
        {
            if(loadMap.get(i).getId() == ID.Planet)
            {
                loadMap.get(i).setOrbitCenterChoice(Integer.parseInt(data.get(i).get(15)));
            }
            if(loadMap.get(i).getId() == ID.Rocket)
            {
                Rocket rt = (Rocket) loadMap.get(i);
                rt.setDestChoice(Integer.parseInt(data.get(i).get(15)));
            }
        }
        for(GameObject object : loadMap)
        {
            if(object.getId() == ID.Rocket)
                ((Rocket)object).update();
        }
    }

    public LinkedList<LinkedList<String>> getData() { return data; }

    public String toString()
    {
        String string = "";
        for(int i = 0 ; i<data.size() ; i++)
        {
            string += data.get(i).get(0);
            for(int j=1 ; j<data.get(i).size() ; j++)
            {
                string+=","+data.get(i).get(j);
            }
            string+="\r\n";
        }
        return string;
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
        constructUniverse();
    }
}
