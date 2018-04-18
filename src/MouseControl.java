import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class MouseControl implements MouseListener,MouseMotionListener,MouseWheelListener {
    Handler handler;
    Game game;

    public MouseControl(Handler handler,Game game) {
        this.handler = handler;
        this.game = game;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
            double scale = game.getScale();
            scale -= (0.1 * e.getWheelRotation());
            scale = Math.max(0.1, scale);
            game.setScale(scale);
        }
    }
}
