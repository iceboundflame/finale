package finale.animation;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import finale.controllers.GameController;
import finale.gameModel.Block;
import finale.gameModel.Location;
import finale.views.GameView;

/**
   An ExplosionParticle represents a particle in an explosion
   triggered by the destruction of a matched block.  Explosions
   usually contain multiple ExplosionParticles, often 30.
   
   <p>ExplosionParticle includes some simple physics, with acceleration,
   air drag, and gravity.
  
   @author  David Liu, Brandon Liu, Yuzhi Zheng
   @version June 4th, 2008
   @author FINALE
 */
public class ExplosionParticle implements Animation{
	
	private double dx;
	private double dy;
	private int duration;
	private int time;
	private Location loc;
	private GameView view;
	private GameController ctl;
	private double xoff, yoff;
	private long lastLoopTime;
	private int generation = 0;
	
	private static final int INITIAL_SIZE = 50;
	private double airDrag = 0.85;
	private static final double GRAVITY = 500.0;
	public static final int QUICK = 50;
	
	/**
	 * @param ctl : The GameController for this Animation
	 * @param view : The GameView for this Animation
	 * @param dx : The initial x-velocity for this ExplosionParticle
	 * @param dy : The initial y-velocity for this ExplosionParticle
	 * @param loc : The location on the grid where the ExplosionParticle should be emitted
	 * @param duration : The duration of the ExplosionParticle.
	 */
	public ExplosionParticle(GameController ctl, GameView view, double dx, double dy,
			Location loc, int duration, double xoff, double yoff, int generation)
	{
		this.ctl = ctl;
		this.view = view;
		this.dx = dx;
		this.dy = dy;
		this.loc = loc;
		this.duration = duration;
		this.generation = generation;
		time = 1;
		this.xoff = xoff;
		this.yoff = yoff;
		lastLoopTime = System.currentTimeMillis();
	}
	
	public ExplosionParticle(GameController ctl, GameView view, double dx, double dy,
			Location loc, int duration)
	{
		this(ctl, view, dx, dy, loc, duration, 0, 0, 0);
	}

	public void draw(Graphics2D g, Rectangle b, Rectangle field) {
		long deltaTime = System.currentTimeMillis()-lastLoopTime;
		updatePosition(deltaTime);
		Composite original = g.getComposite();
		Rectangle grid = view.gridToScreen(loc, field);
		int x = field.x + (loc.getCol() + 1)*grid.width - grid.width/2;
		int y = field.y + (ctl.getBoard().getRows() - loc.getRow())*grid.height - grid.height/2;
		
//		g.setColor(generation == 0 ? Color.RED : Color.WHITE);
		g.setColor(Color.WHITE);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
		//Rectangle particle = new Rectangle(x + (int)xoff, y + (int)yoff, INITIAL_SIZE/time, INITIAL_SIZE/time);
        //Shape particle = new Ellipse2D.Float(INITIAL_SIZE/time, INITIAL_SIZE/time, INITIAL_SIZE/time, INITIAL_SIZE/time);
        //g.fill(particle);
        g.fillRoundRect(x + (int)xoff, y + (int)yoff, INITIAL_SIZE/time, INITIAL_SIZE/time, INITIAL_SIZE/time, INITIAL_SIZE/time);
		
		g.setComposite(original);
		lastLoopTime = System.currentTimeMillis();
	}
	
	private void updatePosition(long deltaTime) {
		dx *= airDrag;
		dy *= airDrag;
		dy += GRAVITY*deltaTime/1000;
		xoff += dx*deltaTime/1000;
		yoff += dy*deltaTime/1000;
	}

	public Block[] getBlocksToHide() {
		return null;
	}

	public boolean step() {
		time++;
		double SPEED = 500;
		if (time == 2*duration/3 && generation < 1) {
//			view.animate(new ExplosionParticle(ctl, view, SPEED-2*SPEED*Math.random(), -SPEED*Math.random(), loc, duration, xoff, yoff, generation+1));
//			view.animate(new ExplosionParticle(ctl, view, SPEED-2*SPEED*Math.random(), -SPEED*Math.random(), loc, duration, xoff, yoff, generation+1));
		}
		if (time > duration)
			return false;
		else
			return true;
	}
	
}
