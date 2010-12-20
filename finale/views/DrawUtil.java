package finale.views;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class DrawUtil {
	public static void drawMenu(Graphics2D g, String items[], int sel, int time, int xcenter, int ystart) {
	    FontMetrics fontMetrics = g.getFontMetrics();
	    for (int i = 0; i < items.length; i++) {
	        int strWidth = fontMetrics.stringWidth(items[i]);
	        if (i == sel) {
	        	int bright = (int)( 180 + (255-180) * Math.abs(Math.sin(Math.PI * time / 30) ));
	            g.setColor(new Color(bright, bright, bright));
	            Composite orig = g.getComposite();
	            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
	//            g.fillRoundRect(b.x + b.width/2-50, b.y + b.height/2 + y - 25, 150, 33, 50, 30);
	            g.fillRoundRect(xcenter-strWidth/2-25, ystart + 30*i - 25, strWidth+50, 33, 30, 30);
	            g.setComposite(orig);
	            
	            g.setColor(Color.BLACK);
	        } else {
	            g.setColor(Color.WHITE);
	        }
	        g.drawString(items[i], xcenter-strWidth/2, ystart+i*30);
	    }
	}

	public static void drawMultilineString(Graphics2D g, int x, int y, String s) {
		String lines[] = s.split("\n");
		FontMetrics metrics = g.getFontMetrics();
		for (int i = 0; i < lines.length; ++i) {
			if (lines[i].equals("{}")) { // secret code for half-line space
				y += metrics.getHeight()/2;
			} else {
				g.drawString(lines[i], x, y);
				y += metrics.getHeight();
			}
		}
	}
	public static void drawMultilineStringCentered(Graphics2D g, int x, int y, String s) {
		String lines[] = s.split("\n");
		FontMetrics metrics = g.getFontMetrics();
		for (int i = 0; i < lines.length; ++i) {
			String line = lines[i];
			int sW = metrics.stringWidth(line);
			g.drawString(line, x - sW/2, y + i * metrics.getHeight());
		}
	}
}
