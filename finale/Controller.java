package finale;

import java.awt.event.KeyEvent;

/**
   The interface for a controller.
  
   @author  David Liu, Brandon Liu, Yuzhi Zheng
   @version May 30, 2008
   
   @author FINALE
 */
public interface Controller {
	/**
	   Processes KeyEvents passed on from FinalePanel.
	   @param e
	 */
	void processKey(KeyEvent e);
	/**
	   Steps the controller, updating the game state.
	 */
	void step();
	/**
	   Returns the View for this controller.
	   @return the View for this Controller
	 */
	View getView();
	/**
	   Sets the ControllerChangeListener to a new listener, usually FinalePanel.
	   @param c the new ControllerChangeListener
	 */
	void setControllerChangeListener(ControllerChangeListener c);
	
	/**
	 * Browser is quitting.
	 * @return any text to return to the server on last phone-home
	 */
	String browserQuitting();
}
