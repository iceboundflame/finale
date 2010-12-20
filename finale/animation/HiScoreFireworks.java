package finale.animation;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import finale.controllers.GameController;
import finale.gameModel.Block;
import finale.gameModel.Board;
import finale.gameModel.Location;
import finale.views.GameView;

public class HiScoreFireworks implements Animation {
	private GameController ctl;
    private Board board;
    private int time;
    public static final int DURATION = 120;
    private GameView view;
    
    public HiScoreFireworks(GameView v, GameController ctl) {
        view = v;
        this.ctl = ctl;
        board = ctl.getBoard();
        time = 0;

        row = board.getRows()-2;
        col = 0;
    }

    public void draw(Graphics2D g, Rectangle b, Rectangle field) {
        float alpha = (float)time / DURATION;
        if (alpha > 1) alpha = 1;

        Composite original = g.getComposite();
        
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setColor(Color.WHITE);
        g.fillRect(b.x, b.y, b.width, b.height);
        
        g.setComposite(original);
    }

    private List<Block> hide = new ArrayList<Block>();
    public Block[] getBlocksToHide() {
    	return hide.toArray(new Block[hide.size()]);
    }

	private static final int SPEED = 1000;
	private int row, col;
	private static final int explodeNColsAtATime = 2;
	private static final int explodeNRowsAtATime = 2;
	private static final int explosionsEvery = 2;
	
    public boolean step() {
        time++;
        if (time % explosionsEvery != 0) return true;
        
        if (row < 0) return true;
        // hold this animation object open so that we can
        // continue hiding exploded blocks, and blank the screen.

        boolean exploded = false;
        while (!exploded && row >= 0) {
			for (int c = 0; c < explodeNColsAtATime; ++c) {
				for (int r = 0; r < explodeNRowsAtATime; ++r) {
					Block blk = board.get(new Location(row+r, col+c));
					if (blk != null) {
						hide.add(blk);
						exploded = true;
					}
				}
			}
			col += explodeNColsAtATime;
			if (col >= board.getCols()) {
				col = 0;
				row -= 2;
			}
        }
        if (exploded) {
			for (int i = 0; i < 30; ++i) {
				view.animate(new ExplosionParticle(ctl, view, SPEED-2*SPEED*Math.random(), -SPEED*Math.random(),
					new Location(
							row + i % explodeNRowsAtATime,
							col + i % explodeNColsAtATime),
					(int)(50*Math.random())));
			}
        }
		return true;
    }

}
