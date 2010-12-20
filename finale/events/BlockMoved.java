package finale.events;

import finale.animation.BlockFadeOut;
import finale.controllers.GameController;
import finale.gameModel.Block;
import finale.gameModel.Location;
import finale.views.GameView;
/**
An event that is called when blocks are moved to trigger the animation for moving blocks

@author  David Liu, Brandon Liu, Yuzhi Zheng
@version June 4th, 2008
@author FINALE
*/
public class BlockMoved implements GameEvent {
    private Block phantomBlock;

    /**
       @param blk : the block that was moved
       @param from : location to leave a fading phantom block 
     */
    public BlockMoved(Block blk, Location from) {
        phantomBlock = new Block(from, blk.getColor());
    }
    
    public void action(GameController ctl, GameView view) {
        view.animate(new BlockFadeOut(view, phantomBlock, BlockFadeOut.QUICK));
    }
}
