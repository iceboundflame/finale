package finale.events;

import java.awt.Color;

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
	private boolean color;
	private int type;
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_CHAIN = 1;
	
	/**
	   @param loc : The location of the destroyed Block.
	 */
	public BlockDestroyed(Location loc, boolean color) {
		this(loc, color, TYPE_NORMAL);
	}
	public BlockDestroyed(Location loc, boolean color, int type) {
		this.loc = loc;
		this.color = color;
		this.type = type;
	}

	public void action(GameController ctl, GameView view) {
		Color drawColor = Color.WHITE;
		if (type == TYPE_CHAIN)
			drawColor = Color.decode(ctl.getLevel().getBlockMatchColor(color));
		
		for (int i = 0; i < 30; i++) {
			view.animate(new ExplosionParticle(
					ctl, view,
					SPEED-2*SPEED*Math.random(), -SPEED*Math.random(), loc,
					(int)(50*Math.random()),
					drawColor
				)
			);
		}
	}

}
