package finale.animation;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import finale.gameModel.Block;
import finale.views.GameView;

/**
    Fades out the block.
  
   @author  David Liu, Brandon Liu, Yuzhi Zheng
   @version Jun 4, 2008
   @author FINALE
*/
public class BlockFadeOut implements Animation {
    private Block block;
    private int duration;
    private int time;
    /**
       A quick animation (5 frames)
     */
    public static final int QUICK = 5;
    private GameView view;
    
    /**
       @param v : GameView to draw the fading blocks on
       @param blk :  the block to be faded
       @param dur : the duration of the fade
     */
    public BlockFadeOut(GameView v, Block blk, int dur) {
        view = v;
        block = blk;
        duration = dur;
        time = 0;
    }

    public void draw(Graphics2D g, Rectangle b, Rectangle field) {
        float alpha = 1 - (float)time / duration;

        Composite original = g.getComposite();
        
        Rectangle rect = view.gridToScreen(block.getLocation(), field);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.drawImage(view.getBlockImage(block.getColor()),
            rect.x, rect.y, rect.width, rect.height, null);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha*.5f));
        g.setColor(Color.WHITE);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
        
        g.setComposite(original);
    }

    public Block[] getBlocksToHide() {
        return null;
    }
   
    public boolean step() {
        time++;
        if (time > duration) 
            return false;   // animation done
         
        else 
            return true;
        
    }

}
