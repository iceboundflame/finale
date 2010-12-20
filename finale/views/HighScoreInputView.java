package finale.views;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import finale.View;
import finale.controllers.HighScoreInputController;
import finale.controllers.ScoreResult;

/**
 * Handles all the drawing for inputting highscores.  It saves a reference
 * to the old GameView so that any animations that were not finished are
 * completed in the background.
 * 
 * @author Team FINALE
 */
public class HighScoreInputView implements View {
	
	private HighScoreInputController ctl;
	private ResourceManager imgs = ResourceManager.getInstance();
	private Font font;
	private float defaultFontSize = 24f;
	private GameView oldView;

    private int time = 0; // for animating menuitem
	
	/**
	 * @param ctl : The HighScoreInputController for this HighScoreInputView
	 * @param oldView : The old GameView
	 */
	public HighScoreInputView(HighScoreInputController ctl, GameView oldView)
	{
		this.ctl = ctl;
		this.oldView = oldView;
		
		font = imgs.getFont("FeaturedItem.ttf").deriveFont(defaultFontSize);
	}

	public void draw(Graphics2D g, Rectangle b) {
		oldView.draw(g, b);

		g.setFont(font);
		
		float alpha = 0.75f;
		Composite original = g.getComposite();
		Rectangle rect = new Rectangle(b.x + b.width / 4, b.y + b.height / 6, b.width / 2, 3*b.height / 4);
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g.setColor(Color.black);
		g.fill(rect);
		
		g.setComposite(original);
		g.setColor(Color.WHITE);
		
		if (!ctl.isDoneSubmitting()) {
			DrawUtil.drawMultilineStringCentered(
					g, b.x + b.width/2, b.y+b.height/3,
					"Submitting score!\nPatience, "+ctl.getPlayerName()+"...");
		} else {
			ScoreResult res = ctl.getScoreResult();
			if (ctl.isCheated()) {
				DrawUtil.drawMultilineStringCentered(
						g, b.x + b.width/2, b.y+b.height/3,
						"You cheated, "+ctl.getPlayerName()+".\nHope you had fun.\n"+
						"Your score: "+ctl.getScore()+"\n\n"+
						"Press any key\nto return to menu.");
			} else if (res == null) {
				DrawUtil.drawMultilineStringCentered(
						g, b.x + b.width/2, b.y+b.height/3,
						"Couldn't submit score.\n"+
						"Your score: "+ctl.getScore()+"\n\n"+
						"Press any key\nto return to menu.");
			} else {
				String youBeat = "You beat\n";
				String friendBeat0="", friendBeat1="";
				if (res.numFriendsBeat >= 1)
					friendBeat0 = res.friendsNames.get(res.friendsBeat.get(0));
				if (res.numFriendsBeat >= 2)
					friendBeat1 = res.friendsNames.get(res.friendsBeat.get(1));

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
					String message = "New High Score!  "+ctl.getScore()+"\n\n";
					if (res.numFriendsBeat > 0)
						message += youBeat;
					
					DrawUtil.drawMultilineStringCentered(
							g, b.x + b.width/2, b.y+b.height/3, message);
				} else {
					String message = "Your score: " + ctl.getScore()+"\n\n";

					if (res.numFriendsBeat > 0)
						message += youBeat;
					else
						message += "Better luck next time.\n";
					
					DrawUtil.drawMultilineStringCentered(
							g, b.x + b.width/2, b.y+b.height/3, message);
				}
				
				time++;
				DrawUtil.drawMenu(
						g, ctl.getItems(), ctl.getSelectionIndex(), time,
		        		b.x+b.width/2,
		        		b.y+b.height/3+g.getFontMetrics().getHeight()*7);
			}
		}
	}
}
