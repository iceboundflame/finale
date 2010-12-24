package finale;

import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

/**
   The JFrame for the Finale application.
  
   @author  David Liu, Brandon Liu, Yuzhi Zheng
   @version Jun 4, 2008
   @author FINALE
 */
@SuppressWarnings("serial")
public class FinaleApp extends JFrame implements WindowListener {
    private FinalePanel panel; // where the game is drawn

    /**
     * Creates a new FinaleApp with the specified period
       @param period the period between drawing, in nanoseconds
     */
    public FinaleApp() {
        super("[ FINALE ] [by Team FINALE - David Liu, Brandon Liu, Yuzhi Zheng]");

        Container c = getContentPane(); // default BorderLayout used
        panel = new FinalePanel();
        c.add(panel, "Center");

        addWindowListener(this);
        pack();
//        setResizable(false);
        setVisible(true);
    }

    // ----------------- window listener methods -------------

    public void windowActivated(WindowEvent e) {
        panel.resumeGame();
    }

    public void windowDeactivated(WindowEvent e) {
        panel.pauseGame();
    }

    public void windowDeiconified(WindowEvent e) {
        panel.resumeGame();
    }

    public void windowIconified(WindowEvent e) {
        panel.pauseGame();
    }

    public void windowClosing(WindowEvent e) {
        panel.stopGame();
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    // ----------------------------------------------------

    public static void main(String args[]) {
        new FinaleApp();
    }
}
