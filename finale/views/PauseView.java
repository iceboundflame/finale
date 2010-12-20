package finale.views;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import finale.View;
import finale.controllers.PauseController;

/**
 * Handles all the drawing for the pause menu.  Maintains a reference to the old
 * GameView so that any Animations that did not complete continue drawing while
 * the pause menu is on.
 * 
 * @author Team FINALE
 */
public class PauseView implements View {
	
	private PauseController ctl;
	private ResourceManager imgs = ResourceManager.getInstance();
	private static String pauseMessage = "-=PAUSED=-";
	private Font font;
	private FontMetrics metrics;
	private float defaultFontSize = 24f;
	private GameView oldView;
	private int strWidth;
	private int time;
	private int rectX, rectY, rectWidth, rectHeight;
	private int selDrawTime, finalSel;
	private boolean drawSelectionFall = false;
	
	//Option one for entrance draw
	//Lines come in and pause menu fades in
	private static int menuBoxEntranceDur = 7;
	private static int menuFadeInDur = 20;
	//Option two for entrance draw
	//Pause Menu falls and expands
	private static int menuBoxFallDur = 10;
	private static int menuExpandDur = 20;
	private static int menuItemFadeDur = 30;
	
	private static int selDrawDur = 10;
	
	//The option for drawing the menu entrance
	private int draw = 1;
	
	/**
	 * @param ctl : The PauseController for this PauseView
	 * @param oldView : The old GameView
	 */
	public PauseView(PauseController ctl, GameView oldView)
	{
		this.ctl = ctl;
		this.oldView = oldView;
		
        font = imgs.getFont("FeaturedItem.ttf").deriveFont(defaultFontSize);
	}

	public void draw(Graphics2D g, Rectangle b) {
		oldView.draw(g, b);
		
		g.setColor(Color.BLACK);
		g.setFont(font);
		metrics = g.getFontMetrics();
		strWidth = metrics.stringWidth( ctl.getItems()[0] );

		rectWidth = strWidth + 120;
		rectX = b.x + b.width/2 - (rectWidth/2);
		rectY = b.y + b.height/4;
		rectHeight = b.height/2;
		if (drawSelectionFall) {
			drawSelectionFall(g, b, finalSel);
		}
		else {
			time++;
			if (draw == 1) {
				if (time <= menuBoxEntranceDur)
					drawMenuBoxEntrance(g, b);
				else if (time <= menuFadeInDur)
					drawMenuFadeIn(g, b);
				else {
					drawPauseRect(g, b);
					drawMenuItems(g, b);
				}
			}
			else if (draw == 2) {
				if (time <= menuBoxFallDur) {
					drawMenuBoxFall(g, b);
				}
				else if (time <= menuExpandDur) {
				    drawMenuExpand(g, b);
				}
				else if (time <= menuItemFadeDur) {
				    drawPauseRect(g, b);
				    drawMenuItemFade(g, b);
				}
				else {
		    		drawPauseRect(g, b);
		    		
		    		drawMenuItems(g, b);
				}
			}
			g.setColor(Color.white);
			g.drawString(pauseMessage, b.x+b.width/2 - metrics.stringWidth( pauseMessage )/2, b.y+b.height/2-60);
		}
	}

	private void drawMenuBoxEntrance(Graphics2D g, Rectangle b) {
		g.setStroke(new BasicStroke(2f));
		g.drawLine(rectX*time/menuBoxEntranceDur, rectY, rectX*time/menuBoxEntranceDur + rectWidth, rectY);
		g.drawLine(b.width - rectWidth - rectX*time/menuBoxEntranceDur, rectY + rectHeight, b.width - rectX*time/menuBoxEntranceDur, rectY + rectHeight);
		g.drawLine(rectX, rectY*time/menuBoxEntranceDur, rectX, rectY*time/menuBoxEntranceDur + rectHeight);
		g.drawLine(rectX + rectWidth, b.height - rectHeight - rectY*time/menuBoxEntranceDur, rectX + rectWidth, b.height - rectY*time/menuBoxEntranceDur);
	}
	
	private void drawMenuFadeIn(Graphics2D g, Rectangle b) {
		int fadeInTime = time - menuBoxEntranceDur;
		Composite original = g.getComposite();
		float alpha = (float)0.75*fadeInTime/(menuFadeInDur - menuBoxEntranceDur);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		Rectangle pauseRect = new Rectangle(rectX, rectY, rectWidth, rectHeight);
		g.fill(pauseRect);
		String[] items = ctl.getItems();
        g.setColor(Color.white);
        for (int i = 0; i < items.length; i++) {
    		int itemFadeInTime = (fadeInTime - (i+1)*4)*2;
    		if (itemFadeInTime < 0)
    			itemFadeInTime = 0;
    		float alpha2 = (float)0.75*itemFadeInTime/(menuFadeInDur - menuBoxEntranceDur);
    		if (alpha2 > 0.75)
    			alpha2 = 0.75f;
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha2));
    		
        	int y = i*30;
            g.drawString(items[i], b.x + b.width/2-metrics.stringWidth(items[i])/2, b.y + b.height/2 + y);
        }
        g.setComposite(original);
	}

	private void drawMenuBoxFall(Graphics2D g, Rectangle b) {
		// TODO Auto-generated method stub
		
	}

	private void drawPauseRect(Graphics2D g, Rectangle b) {
	    Composite original = g.getComposite();
        float alpha = 0.75f;
        
        Rectangle rect = new Rectangle(rectX, rectY, rectWidth, rectHeight);
	    
	    g.setColor(Color.BLACK);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.fill(rect);
        
        g.setComposite(original);
	}
	
	private void drawMenuItems(Graphics2D g, Rectangle b) {
		DrawUtil.drawMenu(g, ctl.getItems(), ctl.getSelectionIndex(),
				time, b.x+b.width/2, b.y+b.height/2);
	}
	
	private void drawMenuExpand(Graphics2D g, Rectangle b) {
		int expandTime = time - menuBoxFallDur;
	    Composite original = g.getComposite();
	    g.setColor(Color.BLACK);
	    g.setComposite(AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.75f ));
	    Rectangle rect = new Rectangle(b.x + b.width/2 - (rectWidth/2)*expandTime/(menuExpandDur - menuBoxFallDur), b.y + b.height/4, rectWidth*expandTime/(menuExpandDur - menuBoxFallDur), b.height/2);
	    g.fill(rect);
	    
	    g.setComposite( original );
	}
	
	private void drawMenuItemFade(Graphics2D g, Rectangle b) {
	    Composite original = g.getComposite();
	    g.setColor(Color.WHITE);
	    float alpha = (float)(time-menuExpandDur)/(menuItemFadeDur - menuExpandDur);
	    g.setComposite(AlphaComposite.getInstance( AlphaComposite.SRC_OVER, alpha ));
	    drawMenuItems(g, b);
	    
	    g.setComposite(original);
	    
	}
	
	private void drawSelectionFall(Graphics2D g, Rectangle b, int selection) {
		selDrawTime++;
		if (selDrawTime > selDrawDur) {
			ctl.notifySelDrawDone();
			return;
		}
		
		String menuItem = ctl.getItems()[selection];
		g.setColor(Color.cyan);
		
		int x = b.x + b.width/2 - metrics.stringWidth(menuItem)/2;
		int y = b.y + b.height/2 + selection*30;
		int yoff = (b.height - y)*selDrawTime/selDrawDur;
		
		g.drawString(menuItem, x, y + yoff);
	}
	
	public void drawSelection(int selection) {
		finalSel = selection;
		selDrawTime = 0;
		drawSelectionFall = true;
	}
}
