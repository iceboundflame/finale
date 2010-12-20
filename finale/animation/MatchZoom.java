package finale.animation;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import finale.controllers.GameController;
import finale.gameModel.Block;
import finale.gameModel.Location;
import finale.views.GameView;
/**
Draws box that zooms to the new match that was made

@author  David Liu, Brandon Liu, Yuzhi Zheng
@version Jun 4, 2008
@author FINALE
*/
public class MatchZoom implements Animation {
	
	/**
	   A fast animation (10 frames)
	 */
	public static final int QUICK = 10;
//	public static final int QUICK = 50;
	
	private Location loc;
	private int duration;
	private int time;
	private GameView view;
	/**
	   @param ctl : GameController of match
	   @param v : GameView to draw the zooming boxes
	   @param loc : location of the lower-left corner of the matched square
	   @param dur : duration of the animation
	 */
	public MatchZoom(GameController ctl, GameView v, Location loc, int dur) {
		this.view = v;
		this.loc = loc;
		this.duration = dur;
		time = 1;
	}
	
    public void draw(Graphics2D g, Rectangle b, Rectangle field) {
    	Rectangle bottomLeftBlock = view.gridToScreen(loc, field);
    	int blockWidth = bottomLeftBlock.width;
    	
//    	rectX = field.x + loc.getCol()*blockWidth - (SIZE/2 - blockWidth)*(duration - time)/duration;
//    	rectY = field.y + (ctl.getBoard().getRows()-loc.getRow() - 2)*gridHeight - (SIZE/2 - gridHeight)*(duration-time)/duration;
//    	rectWidth = (SIZE - 2*blockWidth)*(duration - time)/duration + 2*blockWidth;
//    	rectHeight = (SIZE - 2*gridHeight)*(duration - time)/duration + 2*gridHeight;
    	
        float percent = (float)time / duration;
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
        		(float)Math.sqrt(percent)));
        g.setStroke(new BasicStroke((float)interpolateSqrt(16, 3, percent)));
        g.setColor(new Color(1f,1f-percent,1f-percent));
//        g.drawRect(rectX, rectY, rectWidth, rectHeight);
        
        double radius = interpolateSqrt(blockWidth * 5, blockWidth * Math.sqrt(2), percent);
        double theta  = interpolateSqrt(0, Math.PI/2, percent);
        drawRotatedSquare(g, radius, theta, new Point2D.Double(bottomLeftBlock.getX() + blockWidth, bottomLeftBlock.getY()));
//        drawRotatedSquare(g, blockWidth*3, Math.PI/3, new Point2D.Double(bottomLeftBlock.getX() + blockWidth, bottomLeftBlock.getY()));
        
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
    }
    
    private static double interpolateSqrt(double start, double finish, double percent) {
    	return (finish - start) * Math.sqrt(percent) + start;
    }
    
    private static void drawRotatedSquare(Graphics2D g, double radius, double theta, Point2D center) {
    	theta += Math.PI/4;	// 45 degrees
    	Point2D p1 = new Point2D.Double(
    			center.getX() + radius * Math.cos(theta),
    			center.getY() + radius * Math.sin(theta)
    	);
    	for (int i = 0; i < 4; i++) {
    		theta += Math.PI/2;	// 90 degrees
        	Point2D p2 = new Point2D.Double(
        			center.getX() + radius * Math.cos(theta),
        			center.getY() + radius * Math.sin(theta)
        	);
        	
        	g.draw(new Line2D.Double(p1, p2));

        	p1 = p2;
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
