package finale.animation;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import finale.gameModel.Block;

/**
   A simple Animation
  
   @author  David Liu, Brandon Liu, Yuzhi Zheng
   @version Jun 4, 2008
   @author FINALE
*/
public interface Animation {
    /**
       Steps the animation once.  Returns false if the animation has completed.
       @return false if the animation has completed.
     */
    boolean step();
    /**
       Returns the blocks to be hidden by the animation
       @return the blocks to be hidden by the animation
     */
    Block[] getBlocksToHide();
    /**
       Draws whatever the animations is
       @param g : the graphics2D object
       @param b : the rectangle of the grid section
       @param field : rectangle of the whole window 
     */
    void draw(Graphics2D g, Rectangle b, Rectangle field);
}
