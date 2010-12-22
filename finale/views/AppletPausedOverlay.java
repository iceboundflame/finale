package finale.views;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class AppletPausedOverlay {
	private static float defaultFontSize = 24f;

	public static void draw(Graphics2D g, Rectangle b) {
		// hopefully deriveFont is mad fast or is cached
		Font font = ResourceManager.getInstance()
			.getFont("FeaturedItem.ttf").deriveFont(defaultFontSize);
		
		Composite original = g.getComposite();
        float alpha = 0.75f;
        
	    g.setColor(Color.BLACK);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.fill(b);
        
        g.setComposite(original);
		
		g.setColor(Color.WHITE);
		g.setFont(font);
		DrawUtil.drawMultilineStringCentered(g,
				b.x+b.width/2,
				b.y+b.height/2 - g.getFontMetrics().getHeight(),
				"Click to resume");
	}
}
