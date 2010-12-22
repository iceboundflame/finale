package finale;

import java.awt.Container;

import javax.swing.JApplet;

public class FinaleApplet extends JApplet {
	private static final long serialVersionUID = 5586376760233578236L;

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
        panel = new FinalePanel();
        c.add(panel, "Center");
	}
}
