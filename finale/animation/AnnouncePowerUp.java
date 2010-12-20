package finale.animation;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import finale.controllers.GameController;
import finale.gameModel.Block;
import finale.gameModel.powerUps.PowerUp;
import finale.views.GameView;
import finale.views.ResourceManager;

public class AnnouncePowerUp implements Animation {
	
	public static final int QUICK = 20;
	
	private String powName;
	private int duration;
	private int time;
	private Font font;
	private float fontSize;
	private float alpha;
	
	public AnnouncePowerUp(GameController ctl, GameView v, PowerUp pow, int dur) {
		this.duration = dur;
		time = 0;
		powName = pow.getName();
	}

	public void draw(Graphics2D g, Rectangle b, Rectangle field) {
		font = ResourceManager.getInstance().getFont("xirod.ttf").deriveFont(fontSize);
		
        g.setFont(font);
        g.setColor(Color.BLACK);
        FontMetrics fontMetrics = g.getFontMetrics();
        int strWidth = fontMetrics.stringWidth(powName);
        Composite original = g.getComposite();
        
		if (time < (duration - 5)) {
			fontSize = (float)(3*time);
			alpha = 0.75f;
		}
		else {
			alpha = 0.75f*(duration - time)/5;
		}
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.drawString(powName, field.x + field.width/2 - strWidth/2, field.y + field.height/2);
        g.setComposite(original);
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
