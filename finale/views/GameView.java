package finale.views;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import finale.View;
import finale.animation.Animation;
import finale.controllers.GameController;
import finale.gameModel.ActiveSquare;
import finale.gameModel.Block;
import finale.gameModel.Board;
import finale.gameModel.Level;
import finale.gameModel.Location;
import finale.gameModel.TimeBar;
import finale.gameModel.powerUps.DestroyerBlock;
import finale.gameModel.powerUps.PowerUp;
import finale.gameModel.powerUps.PowerUpContainerBlock;

/**
 * Handles all the drawing for the game
 * 
 * @author Team FINALE
 */
public class GameView implements View
{
    private GameController ctl;
    private Board board;
    
    private Font font;
    private Font timeFont;
    private float defaultFontSize = 10f;
    private float defaultTimeFontSize = 20f;
    
    private List<Animation> animations = new LinkedList<Animation>();
    private Queue<Animation> newanimations = new LinkedList<Animation>();
    
    private int sqWidth, sqHeight;
    
    private ResourceManager imgs = ResourceManager.getInstance();
    private static final String IMG_BG = "bg";
    private static final String IMG_TIMEBAR = "timebar";
    private static final String IMG_BLOCK1 = "block1";
    private static final String IMG_BLOCK2 = "block2";
    private Level level;

	private Set<Location> hide = new TreeSet<Location>();
    
//    private MP3 bg = new MP3("finale/resources/sounds/bg.mp3");
    
    public GameView(GameController ctl, Board board) {
        this.ctl = ctl;
        this.board = board;
        
    	font = imgs.getFont("xirod.ttf").deriveFont(defaultFontSize);
    	timeFont = imgs.getFont("FeaturedItem.ttf").deriveFont(defaultTimeFontSize);
    }
    
    private void loadBlocksHiddenByAnimation() {
    	hide.clear();
    	for (Animation a : animations) {
            Block blksToHide[] = a.getBlocksToHide();
            if (blksToHide == null) continue;
            for (Block blk : blksToHide) {
            	hide.add(blk.getLocation());
            }
        }
    }
    private boolean isHidden(Location loc) {
    	return hide.contains(loc);
    }
    
    private Rectangle calculateDimensions(Rectangle b) {
    	int widthInSquares = (board.getCols() + 6);
    	int heightInSquares = (board.getRows() + 2);
    	
    	Rectangle closestSize = new Rectangle(
			b.x,
			b.y,
			b.width - b.width % widthInSquares,
			b.height - b.height % heightInSquares
		);
    	
        int newSqW = b.width / widthInSquares;
        int newSqH = b.height / heightInSquares;
        if (newSqW == 0) newSqW = 1;
        if (newSqH == 0) newSqH = 1;
        
        sqWidth = newSqW;
        sqHeight = newSqH;
        return closestSize;
    }

    public void draw( Graphics2D g, Rectangle b )
    {
    	level = ctl.getLevel();
    	
    	g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    	
//    	g.setColor( Color.BLACK );
//    	g.fill( b );
//    	// FIXME: HACK: hardcoded size
//    	b.width = 806;
//    	b.height = 434;
    	
        b = calculateDimensions(b);
        
//        g.drawImage(bgImg, 0,0, b.width, b.height, null);
//        g.drawImage(bgImg, b.x, b.y, b.width, b.height, null);
        g.drawImage(imgs.get(level.getThemeBase()+IMG_BG, b.width, b.height), b.x, b.y, null);
        
        g.setFont(font);

        Rectangle field = new Rectangle(
            b.x + 4 * sqWidth,
            b.y + 2 * sqHeight,
            b.width - 6 * sqWidth,
            b.height - 2 * sqHeight
        );
        
//        g.setColor( Color.BLACK );
//        g.fill( field );

        loadBlocksHiddenByAnimation();
        
        drawBlocks(g, field);
        
        drawActiveSquare(g, field);
        
        drawMatches(g, field);
        drawSingleMatches(g, field);
        
        drawTimeBar(g, field);
        
        drawUpcoming(g, b);
        
        drawScore(g, b);
        
        while (!newanimations.isEmpty()) {
        	animations.add(newanimations.remove());
        }
        Iterator<Animation> it = animations.iterator();
        while (it.hasNext()) {
            Animation a = it.next();
            if (a.step()) {
                a.draw(g, b, field);
            } else {
                it.remove();
            }
        }
    }
    
    /**
     * Adds a new Animation to the queue of animations
     * @param anim : The new Animation to be added
     */
    public void animate(Animation anim) {
        newanimations.add(anim);
    }

    private void drawSingleBlock(
    	Graphics2D g, Rectangle field, Block blk, Location loc) {
    	
    	drawSingleBlock(g, field, blk, loc, false);
    }
    
    private void drawSingleBlock(
    	Graphics2D g, Rectangle field, Block blk,
    	Location loc, boolean ignoreHide) {
    	
        if (blk != null) {
        	if (!ignoreHide && isHidden(loc)) return;
        	
            Rectangle rect = gridToScreen(loc, field);
            g.drawImage(getBlockImage(blk.getColor()), rect.x, rect.y, rect.width, rect.height, null);
            if (blk instanceof PowerUpContainerBlock) {
            	PowerUp power = ((PowerUpContainerBlock)blk).getPowerUp();
            	BufferedImage powerImg = ResourceManager.getInstance().get(
            			"power_"+power.getShortName(),
            			rect.width-6, rect.height-6
            	);
            	g.drawImage(powerImg, rect.x+3, rect.y+3, null);
            } else if (blk instanceof DestroyerBlock) {
            	BufferedImage powerImg = ResourceManager.getInstance().get(
            			"power_destroyer",
            			rect.width-6, rect.height-6
            	);
            	g.drawImage(powerImg, rect.x+3, rect.y+3, null);
            }
        }
    }
    private void drawSingleBlock(Graphics2D g, Rectangle field, Block blk) {
        if (blk != null) {
            drawSingleBlock(g, field, blk, blk.getLocation());
        }
    }
    private void drawBlocks(Graphics2D g, Rectangle field) {
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                Location loc = new Location(r, c);
                drawSingleBlock(g, field, board.get(loc));
            }
        }
    }
    private void drawActiveSquare(Graphics2D g, Rectangle field) {
        ActiveSquare sq = ctl.getActiveSquare();
        Block[] blks = sq.getBlocks();
        for (Block blk : blks) {
            drawSingleBlock(g, field, blk);
        }
    }
    
    private void drawSingleMatches(Graphics2D g, Rectangle field) {
        Set<Location> smatches = board.getSingleMatches();
        
        for (Location matchLoc : smatches) {
            Rectangle rect = gridToScreen(matchLoc, field);
            g.setColor( new Color(255,255,255,180) );
            g.fillRect( rect.x+5, rect.y+5, rect.width-10, rect.height-10 );
        }
    }
    
    private void drawMatches(Graphics2D g, Rectangle field) {
        Set<Location> matches = board.getMatches();
        
        Level level = ctl.getLevel();
        for (Location matchLoc : matches) {
        	if (isHidden(matchLoc)) continue;
        	
            Rectangle match1 = gridToScreen(matchLoc, field);
            Rectangle match2 = gridToScreen(new Location(matchLoc.getRow()+1, matchLoc.getCol()+1), field);
            Rectangle box = new Rectangle(match1.x, match1.y+match1.height,
                    match2.x+match2.width - match1.x,
                    match2.y - match1.y -match1.height);
            box = normalizeRect(box);
            
            Composite oldComp = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            if (board.get(matchLoc).getColor()) {
            	g.setColor(Color.decode(level.getBlock1MatchColor()));
            } else {
            	g.setColor(Color.decode(level.getBlock2MatchColor()));
            }
            g.fillRect( box.x, box.y, box.width, box.height );
            g.setComposite(oldComp);
            
//            System.out.println("MATCH: "+matchLoc + " => "+box);
            g.setStroke(new BasicStroke(3.0f));
            g.setColor(Color.RED);
            g.draw(box);
        }
    }
    
    private void drawTimeBar(Graphics2D g, Rectangle field) {
        TimeBar bar = ctl.getTimeBar();
        double micropos = ctl.getTimeBarMicroPosition();
        
        double x = bar.getLocation() + micropos;
        
        int w = sqWidth*2;
        int h = sqWidth*12;
        g.drawImage(imgs.get(level.getThemeBase()+IMG_TIMEBAR, w, h),
        		(int)(field.x + (x-1)*sqWidth-5), field.y+ sqWidth-5, w, h, null);
        Set<Block> marked = ctl.getTimeBar().getMarked();
        for (Block blk : marked) {
        	if (isHidden(blk.getLocation())) continue;
        	
            Rectangle rect = gridToScreen(blk.getLocation(), field);
            g.setColor( new Color(255,0,0,180) );
            g.fillRect( rect.x+5, rect.y+5, rect.width-10, rect.height-10 );
        }
        g.setFont(timeFont);
        g.setColor(Color.BLACK);
        int strx = (int)(field.x +x*sqWidth);
        int stry = field.y - 12 +2*sqWidth;
        DrawUtil.drawMultilineStringCentered(g, strx+2, stry+2,
        		"" + bar.getNumDeleted());
        g.setColor(Color.WHITE);
        DrawUtil.drawMultilineStringCentered(g, strx, stry,
        		"" + bar.getNumDeleted());
    }
    
    private void drawUpcoming(Graphics2D g, Rectangle b) {
        Rectangle squareBounds = (Rectangle)b.clone();
        squareBounds.x += sqWidth;
        squareBounds.width = 2 * sqWidth;
        squareBounds.y += 7 * sqHeight;
        squareBounds.height = 2 * sqHeight;

        List<ActiveSquare> upcoming = ctl.getUpcoming();
        for (ActiveSquare sq : upcoming) {
            Block[] blks = sq.getBlocks();
            Location upLeft = blks[1].getLocation();
            for (int i = 0; i < 4; i++) {
                Location realLoc = blks[i].getLocation();
                Location fauxLoc = new Location(
                    realLoc.getRow()+1 - upLeft.getRow(),
                    realLoc.getCol() - upLeft.getCol()
                );

                drawSingleBlock(g, squareBounds, blks[i], fauxLoc, true);
            }
            squareBounds.y += 2.5 * sqHeight;
        }
    }
    private void drawScore(Graphics2D g, Rectangle b) {
        g.setFont(font);
        int points = ctl.getScore();
        int gameSeconds = ctl.getGameTime()/30;
//        int ptsPerMinute = 0;
//        if (gameSeconds > 0)
//            ptsPerMinute = points * 60 / gameSeconds;
        
        int statsX = sqWidth*1;
        int statsY = sqHeight*7/2;
        String scoreStr =
        	"SCORE:\n "+points+"\n{}\nTIME:\n "+gameSeconds+
        	"\n{}\nLEVEL:\n "+ctl.getLevelNum()+"\n{}\nNEXT:";

        g.setColor(Color.BLACK);
        DrawUtil.drawMultilineString(g, statsX+1, statsY+1, scoreStr);
        g.setColor(Color.WHITE);
        DrawUtil.drawMultilineString(g, statsX, statsY, scoreStr);

//        g.drawString(""+points,
//        		statsX, statsY);
//        g.drawString(""+gameSeconds,      ( 4+ (board.getCols())+1)*sqWidth +12, 9*sqHeight +10);
//        g.drawString(""+ctl.getLevelNum(),( 4+ (board.getCols())+1)*sqWidth +12, 7*sqHeight +10);
        
      //  g.drawString("SCORE: " , ( 4+ board.getCols())*sqWidth +10 , 4*sqHeight +10 );
       // g.drawString("Time: " , ( 4+ board.getCols())*sqWidth +10 , 6*sqHeight +10 );
      //  g.drawString("Points/Minute: " , ( 4+ board.getCols())*sqWidth +10 , 8*sqHeight +10 );
      //  g.drawString( ""+ ptsPerMinute,( 4+ board.getCols())*sqWidth +10, 9*sqHeight +10);
    }

    private static Rectangle normalizeRect(Rectangle r) {
        Rectangle norm = r;
        if (norm.width < 0) {
            norm.x += norm.width;
            norm.width = -norm.width;
        }
        if (norm.height < 0) {
            norm.y += norm.height;
            norm.height = -norm.height;
        }
        return norm;
    }
    
    /**
     * Converts a grid location to a Rectangle representing its screen coordinates
     * @param loc : The Location on the grid
     * @param field : The grid
     * @return A Rectangle representing the screen coordinates for the grid location.
     */
    public Rectangle gridToScreen(Location loc, Rectangle field) {
        int xL = field.x + loc.getCol() * sqWidth;
        int xR = xL + sqWidth;
        int yB = field.y + field.height - loc.getRow() * sqHeight;
        int yT = yB - sqHeight;
        return new Rectangle(xL, yT, xR-xL, yB-yT);
    }
    
    public Image getBlockImage(boolean color) {
        return imgs.get(level.getThemeBase() + (color ? IMG_BLOCK1 : IMG_BLOCK2),
        		sqWidth, sqHeight);
    }
    
    public Font getFont() { return font; }
    public GameController getController() { return ctl; }
}
