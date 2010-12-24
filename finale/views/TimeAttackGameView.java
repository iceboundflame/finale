package finale.views;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import finale.controllers.TimeAttackGameController;
import finale.gameModel.Board;

public class TimeAttackGameView extends GameView {
//	private TimeAttackGameController timeCtl;
	
	public TimeAttackGameView(TimeAttackGameController ctl, Board b) {
		super (ctl, b);
//		this.timeCtl = ctl;
	}
	
	protected void drawScore(Graphics2D g, Rectangle b) {
//		super.drawScore(g, b);
//		
//		g.setFont(getFont());
//		FontMetrics fontMetrics = g.getFontMetrics();
//		g.drawString("Time: ", b.x + b.width - fontMetrics.stringWidth("Time: ") - 40, b.y + fontMetrics.getHeight() + 10);
//		String remainingTime = "" + timeCtl.getRemainingTime();
//		g.drawString(remainingTime, b.x + b.width - fontMetrics.stringWidth(remainingTime) - 40, b.y + 2*fontMetrics.getHeight() + 20);
	}
}
