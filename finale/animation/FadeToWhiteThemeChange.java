package finale.animation;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import finale.controllers.GameController;
import finale.gameModel.Block;
import finale.gameModel.Level;
import finale.views.GameView;

/**
    Fades the screen to white and calls setLevel() in the GameController at the instant
    when the screen is fully white.
    
    @author  David Liu, Brandon Liu, Yuzhi Zheng
    @version Jun 4, 2008
    @author FINALE
*/  
public class FadeToWhiteThemeChange implements Animation {
	/**
	   
	 */
	public static final int QUICK = 30;
	
	private GameController ctl;
	private GameView view;
	private int duration;
	private int time;
	private Level newLevel;

	/**
	   @param v : GameView to draw animation on
	   @param dur : duration of the white fade
	   @param newThemeBase : string name for a new level 
	 */
	public FadeToWhiteThemeChange(GameController ctl, GameView v, int dur, Level newLevel) {
		this.ctl = ctl;
		this.view = v;
		this.duration = dur;
		this.newLevel = newLevel;
		time = 1;
	}
	
	public void draw(Graphics2D g, Rectangle b, Rectangle field) {
		int halfDur = duration / 2;
		float alpha;
		if (time < halfDur) {
			alpha = (float)time / halfDur;
		} else {
			alpha = 1.0f - (float)(time - halfDur) / (duration - halfDur);
		}
		Composite oldComp = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g.setColor(Color.WHITE);
		g.fill(b);
		g.setComposite(oldComp);
	}

	public Block[] getBlocksToHide() {
		return null;
	}

	public boolean step() {
        time++;
        if (time > duration)
        	return false;
        else {
        	if (time == duration / 2) {
        		ctl.setLevel(newLevel);
        	}
        	return true;
        }
	}

}
