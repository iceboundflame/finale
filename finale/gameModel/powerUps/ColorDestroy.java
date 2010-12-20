package finale.gameModel.powerUps;

import finale.controllers.GameController;
import finale.gameModel.Block;
import finale.gameModel.Board;
import finale.gameModel.Location;
/**
    This power up destroys all of the blocks of one color.
    
    @author Team FINALE
*/
public class ColorDestroy extends PowerUp {
	
	private boolean colorToDestroy;

	public String getName() {
		return "Color Destroyer";
	}
	public String getShortName() {
		return "destroyer";
	}
	
	/**
	   @param colorToDestroy : true = color of block1 in that level, 
	   false = color of block2 in that level
	 */
	public ColorDestroy(boolean colorToDestroy) {
		this.colorToDestroy = colorToDestroy;
	}
   /**
    * When activated it will remove blocks of colorToDestroy
    *@param ctl : GameController linked to destroy blocks
  */
	public boolean activate(GameController ctl) {
		Board brd = ctl.getBoard();
		for (int r = 0; r < brd.getRows(); r++) {
			for (int c = 0; c < brd.getCols(); c++) {
				Location loc = new Location(r,c);
				Block blk = brd.get(loc);
				if (blk != null && blk.getColor() == colorToDestroy) {
					blk.removeFromBoard();
				}
			}
		}
		brd.gravitate();
		return false;
	}

}
