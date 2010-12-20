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
public class BlockDestroyed implements GameEvent {
	
	/** The initial speed of the ExplosionParticles of the explosion */
	private static final int SPEED = 1000;
	private Location loc;
	
	/**
	   @param loc : The location of the destroyed Block.
	 */
	public BlockDestroyed(Location loc) {
		this.loc = loc;
	}

	public void action(GameController ctl, GameView view) {
		for (int i = 0; i < 30; i++) {
			view.animate(new ExplosionParticle(ctl, view, SPEED-2*SPEED*Math.random(), -SPEED*Math.random(), loc, (int)(50*Math.random())));
		}
	}

}
