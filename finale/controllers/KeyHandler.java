package finale.controllers;

import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.TreeMap;

/**
 * A class that handles all the key inputs.
 * 
 * @author David Liu, Brandon Liu, Yuzhi Zheng
 * @author FINALE
 * @version June 2nd, 2008
 */
public class KeyHandler
{
    private Map<Integer, Integer> pressed = new TreeMap<Integer, Integer>();

    private final static int REPEAT_DELAY = 5;

    /**
     * Processes the key inputs. 
     * 
     * @param e: the KeyEvent
     */
    public void processKey( KeyEvent e )
    {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
			if (!pressed.containsKey(e.getKeyCode()))
				pressed.put(e.getKeyCode(), 0);
		} else if (e.getID() == KeyEvent.KEY_RELEASED) {
			pressed.remove(e.getKeyCode());
		}
    }

    public KeyState getState(int keyCode)
    {
		if (!pressed.containsKey(keyCode))
			return new KeyState(false, false, false, false);
		
		int t = pressed.get(keyCode);
		if (t == 0)
			return new KeyState(true, true, false, false);
		else if (t == REPEAT_DELAY)
			return new KeyState(true, false, true, false);
		else if (t > REPEAT_DELAY)
			return new KeyState(true, false, false, true);
		else
			return new KeyState(true, false, false, false);
    }
    
    public void resetKey(int keyCode) {
    	pressed.remove(keyCode);
    }

    /**
     * Call this method every frame.  Is used for repeat delays.
     */
    public void advance()
    {
    	for (Integer key : pressed.keySet()) {
    		pressed.put(key, pressed.get(key)+1);
    	}
    }
    
    public class KeyState {
    	private boolean pressed, firstPress, startRepeat, repeat;

		public KeyState(boolean pressed, boolean firstPress, boolean startRepeat, boolean repeat) {
			this.pressed = pressed;
			this.firstPress = firstPress;
			this.startRepeat = startRepeat;
			this.repeat = repeat;
		}

		public boolean isActive() {
			return firstPress || startRepeat || repeat;
		}
		
		public boolean isPressed() {
			return pressed;
		}

		public boolean isFirstPress() {
			return firstPress;
		}

		public boolean isStartRepeat() {
			return startRepeat;
		}

		public boolean isRepeat() {
			return repeat;
		}
    }
}
