package finale.gameModel.powerUps;

import finale.controllers.GameController;
import finale.gameModel.ActiveSquare;
/**
This power up will constantly rotate activeSquare to confuse the player for a 
period of time.

@author Team FINALE
*/
public class ConfusionMode extends PowerUp {
	
	private int timer = 0;
	private static final int lifetime = 200;
	private static final int spinPeriod = 2;

	public String getName() {
		return "Confusion Mode";
	}
	public String getShortName() {
		return "confusion";
	}
	   /**
	    * If activated, it will rotate activeSquare rapidly for the length of lifetime
	    *@param ctl : GameController linked to control the activeSquare
	  */
	public boolean activate(GameController ctl) {
		if (timer < lifetime) {
			timer++;
			if (timer % spinPeriod == 0) {
				ctl.getActiveSquare().rotate(ActiveSquare.CLOCKWISE);
			}
			return true;
		} else {
			return false;
		}
	}

}
