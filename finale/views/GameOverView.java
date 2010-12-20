package finale.views;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import finale.View;
import finale.animation.HiScoreFireworks;
import finale.controllers.GameOverController;
import finale.remote.ScoreResult;

/**
 * Handles all the drawing for inputting highscores.  It saves a reference
 * to the old GameView so that any animations that were not finished are
 * completed in the background.
 * 
 * @author Team FINALE
 */
public class GameOverView implements View {
	
	private GameOverController ctl;
	private ResourceManager imgs = ResourceManager.getInstance();
	private Font font;
	private float defaultFontSize = 24f;
	private GameView oldView;

    private int time = 0; // for animating menuitem
	
	/**
	 * @param ctl : The HighScoreInputController for this HighScoreInputView
	 * @param oldView : The old GameView
	 */
	public GameOverView(GameOverController ctl, GameView oldView)
	{
		this.ctl = ctl;
		this.oldView = oldView;
		
		font = imgs.getFont("FeaturedItem.ttf").deriveFont(defaultFontSize);
	}

	private boolean wasDoneSubmitting = false;
	public void draw(Graphics2D g, Rectangle b) {
		oldView.draw(g, b);

		g.setFont(font);
		
		float alpha = 0.75f;
		Composite original = g.getComposite();
		Rectangle rect = new Rectangle(
				b.x + b.width / 4,
				b.y + b.height / 6,
				b.width / 2, 4*b.height / 6);
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g.setColor(Color.black);
		g.fill(rect);
		
		g.setComposite(original);
		g.setColor(Color.WHITE);
		
		int ctrX = b.x+b.width/2;
		int startY = b.y+b.height/4;

		if (!ctl.isDoneSubmitting()) {
			DrawUtil.drawMultilineStringCentered(
					g, ctrX, startY,
					"Submitting score!\nPatience, "+ctl.getPlayerName()+"...");
			wasDoneSubmitting = false;
		} else {
			ScoreResult res = ctl.getScoreResult();
//			res.isNewHigh=true;
			if (res != null && res.isNewHigh && !wasDoneSubmitting)
		    	oldView.animate(new HiScoreFireworks(oldView, ctl.getOldCtl()));
			// We have to instantiate the fireworks here, and not in the
			// ScoreReporter thread because the animation queue is not
			// thread-safe.
			
			if (ctl.isCheated()) {
				DrawUtil.drawMultilineStringCentered(
						g, ctrX, startY,
						"You cheated, "+ctl.getPlayerName()+".\nHope you had fun.\n"+
						"Your score: "+ctl.getScore()+"\n"+
						"Level: "+ctl.getLevelNum()+"\n\n"+
						"Press any key\nto return to menu.");
			} else if (res == null) {
				DrawUtil.drawMultilineStringCentered(
						g, ctrX, startY,
						"Couldn't submit score.\n"+
						"Your score: "+ctl.getScore()+"\n"+
						"Level: "+ctl.getLevelNum()+"\n\n"+
						"Press any key\nto return to menu.");
			} else {
				String youBeat = "You beat\n";
				String friendBeat0="", friendBeat1="";
				if (res.numFriendsBeat >= 1)
					friendBeat0 = res.friendsNames.get(res.friendsBeat.get(0));
				if (res.numFriendsBeat >= 2)
					friendBeat1 = res.friendsNames.get(res.friendsBeat.get(1));
//				res.numFriendsBeat=6;
//				friendBeat0="DDDDDD";friendBeat1="SDFSDFSDF";

				if (res.numFriendsBeat == 1) {
					youBeat += friendBeat0+"!\n";
				} else if (res.numFriendsBeat == 2) {
					youBeat += friendBeat0+" and\n"+friendBeat1+"!\n";
				} else if (res.numFriendsBeat > 2) {
					int nMore = res.numFriendsBeat-1;
					youBeat += friendBeat0+" and\n"+friendBeat1+" and\n"
							+nMore+" more friend"+(nMore > 1 ? "s" : "")+"!\n";
				}
				
				if (res.isNewHigh) {
					String message =
						"New High Score!  "+ctl.getScore()+"\n"+
						"Level: "+ctl.getLevelNum()+"\n\n";
					if (res.numFriendsBeat > 0)
						message += youBeat;
					
					DrawUtil.drawMultilineStringCentered(
							g, ctrX, startY, message);
				} else {
					String message =
						"Your score: "+ctl.getScore()+"\n"+
						"Level: "+ctl.getLevelNum()+"\n\n";

					if (res.numFriendsBeat > 0)
						message += youBeat;
					else
						message += "Better luck next time.\n";
					
					DrawUtil.drawMultilineStringCentered(
							g, ctrX, startY, message);
				}
				
				time++;
				DrawUtil.drawMenu(
						g, ctl.getItems(), ctl.getSelectionIndex(), time,
		        		ctrX,
		        		(int)(startY + g.getFontMetrics().getHeight()*8.5));
			}
			wasDoneSubmitting = true;
		}
	}
}
