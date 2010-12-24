package finale.animation;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import finale.controllers.GameController;
import finale.gameModel.Block;
import finale.views.DrawUtil;
import finale.views.GameView;
import finale.views.ResourceManager;

public class Announce implements Animation {
	
	public static final int QUICK = 30;
	
	private String announcement;
	private int duration;
	private int time;
	private Font font;
	private BufferedImage precomposite;
	private final int ENTER_DURATION = 5;
	private final int HANG_DURATION = 15;
	private final int EXIT_DURATION = 5;
	private final float NOMINAL_FONT_SIZE = 48f;
	private final float NOMINAL_ALPHA = 1f;
	private final float NOMINAL_SHADOW_OFFSET = 2f;
	private Color color;
	
	public Announce(GameController ctl, GameView v, String text, Color col) {
		duration = ENTER_DURATION+HANG_DURATION+EXIT_DURATION;
		time = 0;
		announcement = text;
		color = col;
	}
	public Announce(GameController ctl, GameView v, String text) {
		this(ctl, v, text, Color.WHITE);
	}

	public void draw(Graphics2D g, Rectangle b, Rectangle field) {
		if (precomposite == null
				|| precomposite.getWidth() != b.width
				|| precomposite.getHeight() != b.height) {
			
			precomposite = new BufferedImage(
					b.width, b.height, BufferedImage.TYPE_4BYTE_ABGR);
		}

		float fontSize, alpha, shadowOffset;
		if (time < ENTER_DURATION) {
			float percent = (float)time/ENTER_DURATION;
			
			fontSize = (float)Math.pow(percent, 1./3) * NOMINAL_FONT_SIZE;
			alpha = percent * NOMINAL_ALPHA;
			shadowOffset = percent * NOMINAL_SHADOW_OFFSET;
		} else if (time - ENTER_DURATION < HANG_DURATION) {
			fontSize = NOMINAL_FONT_SIZE;
			alpha = NOMINAL_ALPHA;
			shadowOffset = NOMINAL_SHADOW_OFFSET;
		} else if (time - ENTER_DURATION - HANG_DURATION < EXIT_DURATION) {
			int exitTime = time - ENTER_DURATION - HANG_DURATION;
			float percent = (float)exitTime/EXIT_DURATION;

			float fontMult = (1 + percent);
			fontMult = fontMult*fontMult*fontMult;
			fontSize = fontMult * NOMINAL_FONT_SIZE;
			alpha = (1 - percent) * NOMINAL_ALPHA;
			shadowOffset = fontMult * NOMINAL_SHADOW_OFFSET;
		} else {
			return;
		}

		Graphics2D preG2D = null;
		try {
			preG2D = (Graphics2D)precomposite.getGraphics();
			preG2D.setFont(font);
			
			font = ResourceManager.getInstance()
					.getFont("FeaturedItem.ttf").deriveFont(fontSize);
			preG2D.setFont(font);
			int x = b.width/2, y = b.height*12/14;
			
			Composite original = preG2D.getComposite();
			preG2D.setComposite(
					  AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
			preG2D.fillRect(0,0,b.width,b.height);
			preG2D.setComposite(original);
			
			preG2D.setColor(Color.BLACK);
			int shadow = (int)shadowOffset;
			if (shadow < 1) shadow = 1;
			DrawUtil.drawMultilineStringCentered(preG2D, x+shadow, y+shadow, announcement);
			preG2D.setColor(color);
			DrawUtil.drawMultilineStringCentered(preG2D, x, y, announcement);
			
			original = g.getComposite();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			g.drawImage(precomposite, b.x, b.y, null);
			g.setComposite(original);
		} finally {
			if (preG2D != null)
				preG2D.dispose();
		}
	}

	public Block[] getBlocksToHide() {
		return null;
	}

	public boolean step() {
        time++;
        if (time > duration)
        	return false;
        else
        	return true;
	}

}
