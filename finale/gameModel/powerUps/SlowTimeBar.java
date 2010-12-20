package finale.gameModel.powerUps;

import finale.controllers.GameController;
/**
   This power up freezes the timeBar so the player can get as many matches
   as possible
    
    @author Team FINALE
*/
public class SlowTimeBar extends PowerUp {
	
	private int timer = 0;
	private static final int lifetime = 200;

	public String getName() {
		return "Slow TimeBar";
	}
	public String getShortName() {
		return "slowbar";
	}
	 /**
	    * When activated it freezes the  timeBar
	    *@param ctl : GameController linked to timeBar
	  */
	public boolean activate(GameController ctl) {
		if (timer < lifetime) {
			timer++;
//			int newPeriod = ctl.getLevel().getTimebarAdvancePeriod() * 2;
//			if (timer % newPeriod == 0) {
//				ctl.advanceTimeBar();
//			}
			ctl.setTimeBarFrozen(true); // don't allow GameController to advance it, we're taking over
			return true;
		} else {
			ctl.setTimeBarFrozen(false); // resume normal timebar advancing
			return false;
		}
	}

}
