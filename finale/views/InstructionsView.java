package finale.views;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import finale.View;
import finale.controllers.InstructionsController;

/**
 * Handles all the drawing for the instructions page
 * 
 * @author Team FINALE
 */
public class InstructionsView implements View {
    private InstructionsController ctl;
    private ResourceManager imgs = ResourceManager.getInstance();
    
    /**
     * @param ctl : The InstructionsController for this InstructionsView
     */
    public InstructionsView(InstructionsController ctl) {
    	this.ctl = ctl;
    }
    
    public void draw(Graphics2D g, Rectangle b) {
//    	g.drawImage(imgs.get("instructionsBG.png", b.width, b.height),
//    			b.x, b.y, null);
    	g.drawImage(imgs.get("instructions"+ctl.getPageNumber(), b.width, b.height),
    			b.x, b.y, null);
    }
}
