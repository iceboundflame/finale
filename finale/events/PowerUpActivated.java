package finale.events;

import finale.animation.Announce;
import finale.controllers.GameController;
import finale.gameModel.powerUps.PowerUp;
import finale.views.GameView;
/**
event when a power up block was destroyed

@author  David Liu, Brandon Liu, Yuzhi Zheng
@version June 4th, 2008
@author team FINALE
*/
public class PowerUpActivated implements GameEvent {
	private PowerUp power;
	
	/**
	   @param power : the specific power up that was activated
	 */
	public PowerUpActivated(PowerUp power) {
		this.power = power;
	}
	
	public void action(GameController ctl, GameView view) {
		ctl.addPowerUp(power);
		view.animate(new Announce(ctl, view, power.getName()));
	}

}
