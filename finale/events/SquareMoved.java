package finale.events;

import finale.controllers.GameController;
import finale.views.GameView;
import finale.views.ResourceManager;

public class SquareMoved implements GameEvent {
    public static final int DIR_LEFT = 1;
    public static final int DIR_RIGHT = 2;
    public static final int DIR_DOWN = 3;
    public static final int TYPE_STARTREPEAT = 4;
    public static final int TYPE_REPEAT = 8;
    public static final int TYPE_FIRST = 16;
    public static final int TYPE_AUTODROP = 32;
    
    private int type;
    
    public SquareMoved(int dir, int type) {
    	this.type = type;
    }
    
    public void action(GameController ctl, GameView view) {
    	if (type == TYPE_FIRST) {
    		ResourceManager.getInstance().playSound("move");
    	} else if (type == TYPE_STARTREPEAT) {
    		ResourceManager.getInstance().playSound("moverepeat");
    	} 
    }
}
