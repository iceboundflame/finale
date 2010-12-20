package finale.gameModel.powerUps;

import finale.controllers.GameController;
/**
 * Alters the behaviour of the game during its lifetime.
 * 
 * @author Team FINALE
*/
public abstract class PowerUp {
	/**
	   when this is called, the power ups will start its specific special effects
	   @param ctl : GameController to make the changes for the effects
	 * @return : if power up is activated or not
	 */
	public abstract boolean activate(GameController ctl);
	
	/**
	  Gets name of the power up
	   @return : "Generic PowerUp"
	 */
	public String getName() {
		return "Generic PowerUp";
	}
	/**
	  Gets the short name of the power up
	   @return  :"generic"
	 */
	public String getShortName() {
		return "generic";
	}
	
	/**
	   Picks a random PowerUp to be made
	   @return : a random PowerUp 
	 */
	public static PowerUp createRandomPowerUp() {
		switch((int)(Math.random() * 4)) {
			case 0:
				return new FastTimeBar();
			case 1:
				return new SlowTimeBar();
			case 2:
				return new ConfusionMode();
			case 3:
				return new ColorDestroy(Math.random() < 0.5);
		}
		return null;
	}
}
