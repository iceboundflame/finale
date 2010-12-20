package finale;

import java.awt.Container;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JApplet;

public class FinaleApplet extends JApplet {
	private static final long serialVersionUID = 5586376760233578236L;

	private static int DEFAULT_FPS = 30;

    private FinalePanel panel; // where the game is drawn
    
    private static FinaleApplet instance = null;

    public static FinaleApplet getInstance() {
    	return instance;
    }
    
    public FinaleApplet() {
    	instance = this;
    }
    
	public void init() {
        Container c = getContentPane(); // default BorderLayout used
        panel = new FinalePanel((long)(1e9/DEFAULT_FPS));
        c.add(panel, "Center");
        
//        this.requestFocus();
	}
}
