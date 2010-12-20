package finale;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
   View draws onto the screen.
  
   @author  David Liu, Brandon Liu, Yuzhi Zheng
   @version May 22nd, 2008
   @author FINALE
 */
public interface View {
	/**
	   Draws onto the screen.
	   @param g the graphics content
	   @param b the bounds of the area where View should draw
	 */
	void draw(Graphics2D g, Rectangle b);
}
