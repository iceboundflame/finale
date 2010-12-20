package finale.views;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import finale.View;
import finale.animation.Animation;
import finale.controllers.MenuController;

/**
 * Handles all the drawing for the start-up menu
 * 
 * @author Team FINALE
 */
public class MenuView implements View {

    private MenuController ctl;
    private ResourceManager imgs = ResourceManager.getInstance();
    private Font font;
    private static float defaultFontSize = 24f;
    private int time = 0;
    private List<Animation> animations = new LinkedList<Animation>();
    
    /**
     * @param ctl : The MenuController for this MenuView
     */
    public MenuView(MenuController ctl) {
        this.ctl = ctl;

        try {
        	InputStream is = getClass().getClassLoader().getResourceAsStream(
				"finale/resources/fonts/FeaturedItem.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, is)
            			.deriveFont(defaultFontSize);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FontFormatException e) {
            e.printStackTrace();
        }
    }
    
    public void draw(Graphics2D g, Rectangle b) {
    	g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        time++;
    	g.drawImage(imgs.get("menu", b.width, b.height), b.x, b.y, null);
        String[] items = ctl.getItems();
        int sel = ctl.getSelectionIndex();
        g.setFont(font);
        DrawUtil.drawMenu(g, items, sel, time, b.x+b.width/2, b.y+b.height/2-30);
//        int y = -60;
//        for (int i = 0; i < items.length; i++) {
//            y += 30;
//            int strWidth = fontMetrics.stringWidth(items[i]);
//            if (i == sel) {
//            	int bright = (int)( 180 + (255-180) * Math.abs(Math.sin(Math.PI * time / 30) ));
//                g.setColor(new Color(bright, bright, bright));
//                Composite orig = g.getComposite();
//                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
////                g.fillRoundRect(b.x + b.width/2-50, b.y + b.height/2 + y - 25, 150, 33, 50, 30);
//                g.fillRoundRect(b.x + b.width/2-strWidth/2-25, b.y + b.height/2 + y - 25, strWidth+50, 33, 30, 30);
//                g.setComposite(orig);
//                
//                g.setColor(Color.BLACK);
//            } else {
//                g.setColor(Color.WHITE);
//            }
//            g.drawString(items[i], b.x + b.width/2-strWidth/2, b.y + b.height/2 + y);
//        }
    }
    
    //Brandon's random stuff, pathetically failed attempt for menu animations.
    private void animate(Animation anim)
    {
        //TODO: FIXME: concurrentmodexp if added while animation drawing.
        animations.add( anim );
    }
}
