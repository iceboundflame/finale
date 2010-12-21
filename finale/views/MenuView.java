package finale.views;

import java.awt.Font;
import java.awt.FontFormatException;
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
    
    /**
     * @param ctl : The MenuController for this MenuView
     */
    public MenuView(MenuController ctl) {
        this.ctl = ctl;

        font = imgs.getFont("FeaturedItem.ttf").deriveFont(defaultFontSize);
    }
    
    public void draw(Graphics2D g, Rectangle b) {
    	g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        time++;
    	g.drawImage(imgs.get("menu.jpg", b.width, b.height), b.x, b.y, null);
        String[] items = ctl.getItems();
        int sel = ctl.getSelectionIndex();
        g.setFont(font);
        DrawUtil.drawMenu(g, items, sel, time, b.x+b.width/2, b.y+b.height/2-30);
    }
}
