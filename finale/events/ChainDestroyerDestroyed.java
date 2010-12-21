package finale.events;

import finale.animation.ExplosionParticle;
import finale.controllers.GameController;
import finale.gameModel.Location;
import finale.views.GameView;
/**
An event that is called when blocks are destroyed. Draws explosion and play sound 

@author  David Liu, Brandon Liu, Yuzhi Zheng
@version June 4th, 2008
@author team FINALE
*/
public class ChainDestroyerDestroyed implements GameEvent {
	private int size;
	
	public ChainDestroyerDestroyed(int size) {
		this.size = size;
	}

	public void action(GameController ctl, GameView view) {
		ctl.chainDestroyerDestroyed(size);
	}
}
