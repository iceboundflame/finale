package finale.events;

import finale.animation.MatchZoom;
import finale.controllers.GameController;
import finale.gameModel.Location;
import finale.views.GameView;
/**
This event is called when a match is made

@author  David Liu, Brandon Liu, Yuzhi Zheng
@version June 4th, 2008
@author team FINALE
*/
public class MatchMade implements GameEvent {
	
	private Location loc;
	
	/**
	   @param loc : location of the lower-left corner of the matched square
	 */
	public MatchMade(Location loc) {
		this.loc = loc;
	}
	/**
    Calls MatchZoom to draw a zoom in box for the match
  */
	public void action(GameController ctl, GameView view) {
		view.animate(new MatchZoom(ctl, view, loc, MatchZoom.QUICK));
	}

}
