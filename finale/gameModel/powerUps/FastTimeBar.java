package finale.gameModel.powerUps;

import finale.controllers.GameController;
/**
This power up will make the timeBar scan very fast for a period of time.

@author Team FINALE
*/
public class FastTimeBar extends PowerUp {
	
	private int timer = 0;
	private static final int lifetime = 200;

	public String getName() {
		return "Loss of time";
	}
	public String getShortName() {
		return "fastbar";
	}
	   /**
	    * When activated make the timeBar advance more rapidly to scan and
	    *  remove matched blocks faster
	    *@param ctl : GameController linked to the TimeBar
	  */
	public boolean activate(GameController ctl) {
		if (timer < lifetime) {
			timer++;
			int newPeriod = ctl.getLevel().getTimebarAdvancePeriod() / 3;
			if (newPeriod < 1)
				newPeriod = 1;
			if (timer % newPeriod == 0) {
				ctl.advanceTimeBar();
			}
			ctl.setTimeBarFrozen(true); // don't allow GameController to advance it, we're taking over
			return true;
		} else {
			ctl.setTimeBarFrozen(false); // resume normal timebar advancing
			return false;
		}
	}

}
