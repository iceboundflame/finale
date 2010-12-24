package finale;

import java.awt.Container;

import javax.swing.JApplet;

import finale.remote.ScoreReporter;

@SuppressWarnings("serial")
public class FinaleApplet extends JApplet {
    private FinalePanel panel; // where the game is drawn
    
    private static FinaleApplet instance = null;
    private String id;

    public static FinaleApplet getInstance() {
    	return instance;
    }
    
    public String getID() {
    	return id; // for analytics
    }
    
    public FinaleApplet() {
    	instance = this;

		// id is used to track sessions on server
		id = Integer.toHexString((int)(Math.random() * Integer.MAX_VALUE));
    }
    
    @Override
	public void init() {
        Container c = getContentPane(); // default BorderLayout used
        panel = new FinalePanel();
        c.add(panel, "Center");

		ScoreReporter.logInBackground("applet_opened");
	}
	
	@Override
	public void stop() {
		if (panel != null)
			panel.stopGame();
		
		// last ditch effort to call home
		String event = "applet_closed ";
		if (panel != null) {
			// this horrible encapsulation violation is a hack for analytics
			event += panel.getController().browserQuitting();
		} else {
			event += "no_panel?!";
		}
		// Log this in foreground, hopefully it stalls the applet destruction
		// long enough to finish the POST.
		new ScoreReporter().log(event);
		
		System.out.println("Quitting");
	}
	
	public void stopTheWorld() { // or just the game
		if (panel != null)
			panel.pauseGame();
	}
	public void goOn() {
		if (panel != null) {
			panel.resumeGame();
			panel.requestFocus();
		}
	}
}
