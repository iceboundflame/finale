package finale.controllers;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import finale.Controller;
import finale.ControllerChangeListener;
import finale.FinalePanel;
import finale.PerfTracker;
import finale.animation.Announce;
import finale.animation.FadeToWhiteThemeChange;
import finale.animation.HiScoreFireworks;
import finale.events.BlockDestroyed;
import finale.events.GameEvent;
import finale.events.MatchMade;
import finale.events.SquareMoved;
import finale.gameModel.ActiveSquare;
import finale.gameModel.Board;
import finale.gameModel.Level;
import finale.gameModel.Location;
import finale.gameModel.TimeBar;
import finale.gameModel.powerUps.Magnet;
import finale.gameModel.powerUps.PowerUp;
import finale.remote.ScoreReporter;
import finale.views.GameView;
import finale.views.ResourceManager;

/**
   This GameController controls all aspects of the game.  It updates the
   game state each frame and processes key input.
  
   @author  David Liu, Brandon Liu, Yuzhi Zheng
   @version June 4th, 2008
   @author FINALE
 */
public class GameController implements Controller
{
    /** The default location of a new ActiveSquare */
	private static final Location DEFAULT_ACTIVESQUARE_LOC = new Location(10, 9);
    /** The frequency of powerups appearing */
    private static double POWERUP_FREQUENCY = (1.0/36);
    private static double CHAIN_DESTROYER_FREQUENCY = (1.0/36);

    private Board board;
    private GameView view;
    private TimeBar timeBar;
    private ActiveSquare square;
    private Level level;
    private LinkedList<ActiveSquare> upcoming = new LinkedList<ActiveSquare>();

    private boolean gameOver = false;
    private int gameTime = 0;
    private int gameScore = 0;
    private int squareLifeTime = 0;
    private boolean timeBarFrozen = false;
    private boolean activeSquareFrozen = false;
    private List<PowerUp> activePowerUps = new LinkedList<PowerUp>();
    private boolean advancingLevel = false; // set to true when an animation is in progress to advance level

    private double microadvance = 0; // fractional timebar position [0,1)
    
    private KeyHandler keys = new KeyHandler();
    private ControllerChangeListener changeListener;
    private boolean cheated = false;
    
    private Queue<GameEvent> events = new LinkedList<GameEvent>();
    
    private static final int KEY_DROP = KeyEvent.VK_DOWN;
    private static final int KEY_LEFT = KeyEvent.VK_LEFT;
    private static final int KEY_RIGHT = KeyEvent.VK_RIGHT;
    private static final int KEY_ROTATECW = KeyEvent.VK_UP;
    private static final int KEY_ROTATECCW = KeyEvent.VK_CONTROL;
    private static final int KEY_HARDDROP = KeyEvent.VK_SPACE;

    /**
       Creates a new GameController.
     */
	public GameController() {
		board = new Board(12, 18);
		view = new GameView(this, board);
		timeBar = new TimeBar(board);
		square = new ActiveSquare(board, DEFAULT_ACTIVESQUARE_LOC);
		for (int i = 0; i < 3; i++) {
			upcoming.add(new ActiveSquare(board, DEFAULT_ACTIVESQUARE_LOC));
		}
		level = Level.loadLevel(1);
		
		ScoreReporter.logInBackground("game_started");
		PerfTracker.getInstance().reset();
	}
    
	public void step() {
		if (gameOver)
			return;

        gameTime++;

        activatePowerUps();

        // Key Handling / Move the ActiveSquare
        processSquareControlKeys();
        keys.advance();
        
        // Advance TimeBar
        if (!timeBarFrozen) {
        	microadvanceTimeBar();
        	if (gameTime % level.getTimebarAdvancePeriod() == 0) {
        		advanceTimeBar();
        	}
        }

        // Drop the ActiveSquare
        if (!activeSquareFrozen) {
	        squareLifeTime++;
	        if (squareLifeTime >= level.getHoldTime()) {
	            int dropTime = squareLifeTime - level.getHoldTime();
	            if (dropTime % level.getDropPeriod() == 0)
	                dropActive();
	        }
        }
        
        // Level Up
        int minscore = level.getScoreToAdvance();
        if (minscore > 0 && gameScore >= minscore) {
        	advanceLevel();
        }
        
        // Handle generated GameEvents
        events.addAll(board.getNewEvents());
        boolean deletions = false, matches = false;
        while (!events.isEmpty()) {
            GameEvent ev = events.remove();
            ev.action(this, view);
            if (ev instanceof BlockDestroyed)
            	deletions = true;
            else if (ev instanceof MatchMade)
            	matches = true;
        }
        if (deletions)
        	ResourceManager.getInstance().playSound("deletion");
        
        if (matches)
        	ResourceManager.getInstance().playSound("match");
    }

	private void activatePowerUps() {
        Iterator<PowerUp> it = activePowerUps.iterator();
        while (it.hasNext()) {
        	PowerUp pup = it.next();
        	if (! pup.activate(this))
        		it.remove();
        }
    }
    
    /**
       Advances the TimeBar and increments the game score.  Gives 100 points 
       for the first block, 200 points for the second, etc.
     */
    public void advanceTimeBar() {
        int matches = timeBar.advance();
        if (matches!= 0) {
            gameScore += 100 * (1 + matches) * matches / 2;	// 100, 100+200, 100+200+300, ...
        }
        microadvance = 0;
    }
    
    /**
     * Called from ChainDestroyerDestroyed event to increment score.
     * @param size number of blocks cleared
     */
    public void chainDestroyerDestroyed(int size) {
    	gameScore += 25 * size;
    }
    
    /**
       Advances the TimeBar partially.
     */
    public void microadvanceTimeBar() {
        microadvance += 1.0 / level.getTimebarAdvancePeriod();
    }
    
    private void dropActive() {
        if (!square.drop()) {
            if (square.getLocation().getRow() >= board.getRows() - 2) {
                notifyGameOver();
            } else {
                square.finish();
                square = upcoming.remove();
                int special = ActiveSquare.NORMAL;
                
                double rand = Math.random();
                if (rand < POWERUP_FREQUENCY)
                	special = ActiveSquare.POWERUP;
                else if (rand < POWERUP_FREQUENCY+CHAIN_DESTROYER_FREQUENCY)
                	special = ActiveSquare.CHAIN_DESTROYER;
                
                upcoming.add(new ActiveSquare(board, DEFAULT_ACTIVESQUARE_LOC, special));
                squareLifeTime = 0;
                keys.resetKey(KEY_DROP);
            }
        } else {
            if (squareLifeTime < level.getHoldTime())
                squareLifeTime = level.getHoldTime();
        }
    }

	private void processSquareControlKeys() {
		KeyHandler.KeyState left = keys.getState(KEY_LEFT);
		if (left.isActive()) {
            square.moveLeft();
//            if (left.isRepeat())
//            	square.moveLeft();	// move 2x as fast when repeating
            
            if (left.isFirstPress()) {
	            addEvent(new SquareMoved(SquareMoved.DIR_LEFT, SquareMoved.TYPE_FIRST));
            } else if (left.isStartRepeat()) {
            	addEvent(new SquareMoved(SquareMoved.DIR_LEFT, SquareMoved.TYPE_STARTREPEAT));
            }
		}
		
		KeyHandler.KeyState right = keys.getState(KEY_RIGHT);
		if (right.isActive()) {
            square.moveRight();
//            if (right.isRepeat())
//            	square.moveRight();	// move 2x as fast when repeating
            
            if (right.isFirstPress()) {
	            addEvent(new SquareMoved(SquareMoved.DIR_RIGHT, SquareMoved.TYPE_FIRST));
            } else if (right.isStartRepeat()) {
            	addEvent(new SquareMoved(SquareMoved.DIR_RIGHT, SquareMoved.TYPE_STARTREPEAT));
            }
		}

        if (keys.getState(KEY_DROP).isPressed()) {
        	gameScore++;
            dropActive();
        }
        
        if (keys.getState(KEY_ROTATECW).isActive())
            square.rotate(ActiveSquare.CLOCKWISE);
        if (keys.getState(KEY_ROTATECCW).isActive())
            square.rotate(ActiveSquare.COUNTERCLOCKWISE);
        
        if (keys.getState(KEY_HARDDROP).isFirstPress()) {
        	int startrow = square.getLocation().getRow();
            while (square.drop())
            	; // drop the square until it hits something
            int endrow = square.getLocation().getRow();
            gameScore += (startrow - endrow) * 2;
            dropActive();
        }
	}
    
    private void advanceLevel() {
    	if (advancingLevel)
    		return;
    	int nextLevelNum = getLevelNum()+1;
    	if (!Level.levelExists(nextLevelNum))
    		return;
    	
    	advancingLevel = true;
        System.out.println("Advanced to level "+nextLevelNum);
    	Level nextLevel = Level.loadLevel(nextLevelNum);
        view.animate(new FadeToWhiteThemeChange(
        		this, view, FadeToWhiteThemeChange.QUICK, nextLevel
        ));
    }
    
    public void setControllerChangeListener(ControllerChangeListener c) {
        changeListener = c;
    }

    /**
     * Returns the board
     * 
     * @return the board
     */
    public Board getBoard()
    {
        return board;
    }

    /**
     * Returns the TimeBar for this controller
     * 
     * @return the TimeBar
     */
    public TimeBar getTimeBar()
    {
        return timeBar;
    }
    
    public double getTimeBarMicroPosition() {
    	return microadvance;
    }

    /**
     * Returns the ActiveSquare for this controller
     * 
     * @return the ActiveSquare
     */
    public ActiveSquare getActiveSquare()
    {
        return square;
    }

    /**
     * Returns the current score
     * 
     * @return the current score
     */
    public int getScore()
    {
        return gameScore;
    }

    /**
     * Returns the list of upcoming ActiveSquares.
     * 
     * @return the list of upcoming ActiveSquares
     */
    public List<ActiveSquare> getUpcoming()
    {
        return upcoming;
    }

    public GameView getView()
    {
		return view;
    }

    public void processKey( KeyEvent e )
    {
        keys.processKey( e );

        if (e.getID() == KeyEvent.KEY_PRESSED) {
        	boolean wasCheater = cheated;
	    	switch(e.getKeyCode()) {
				case KeyEvent.VK_ESCAPE:
				case KeyEvent.VK_P:
					changeListener.transferControl(new PauseController(this, view));
					break;
				case KeyEvent.VK_B:
					timeBar.advance();
					cheated = true;
					break;
				case KeyEvent.VK_R:
					board.generateRandomTest();
					cheated = true;
					break;
				case KeyEvent.VK_J:
					advanceLevel();
					cheated = true;
				    break;
				case KeyEvent.VK_F:
				    timeBarFrozen = !timeBarFrozen;
				    activeSquareFrozen = !activeSquareFrozen;
				    cheated = true;
				    break;
				case KeyEvent.VK_S:
					// sound test
				    ResourceManager.getInstance().playSound("deletion");
				    break;
				case KeyEvent.VK_H:
					// animation test
				    view.animate(new HiScoreFireworks(view, this));
				    break;
				case KeyEvent.VK_O:
					// animation test
				    view.animate(new Announce(this, view, "Test Announcement"));
				    break;
				case KeyEvent.VK_X:
					square = new ActiveSquare(
							board, DEFAULT_ACTIVESQUARE_LOC, ActiveSquare.POWERUP);
	                squareLifeTime = 0;
					cheated = true;
					break;
				case KeyEvent.VK_Z:
					square = new ActiveSquare(
							board, DEFAULT_ACTIVESQUARE_LOC, ActiveSquare.CHAIN_DESTROYER);
	                squareLifeTime = 0;
					cheated = true;
					break;
				case KeyEvent.VK_C:
					square = new ActiveSquare(
							board, DEFAULT_ACTIVESQUARE_LOC,
							ActiveSquare.POWERUP, new Magnet());
	                squareLifeTime = 0;
					cheated = true;
					break;
				case KeyEvent.VK_M:
					boolean newState = !ResourceManager.getInstance().isMute();
					ResourceManager.getInstance().setMute(newState);
					view.animate(new Announce(this, view,
							newState ? "Muted" : "Unmuted"));
					break;
	    	}
	    	if (!wasCheater && cheated) {
	    		view.animate(new Announce(this, view,
	    				"Cheat activated", Color.RED));
	    	}
        }
    }

    /**
     * Returns true if the game is over
     * 
     * @return true if the game is over
     */
    public boolean isGameOver()
    {
        return gameOver;
    }
    
    private void notifyGameOver()
    {
    	System.out.println("Game Over!");
    	gameOver = true;
    	changeListener.transferControl(new GameOverController(this));
    }
    
    public int getGameTime() {
        return gameTime / FinalePanel.FRAME_RATE;
    }
    
    /**
       Adds a GameEvent to GameController's EventQueue.
       @param e the GameEvent to be added.
     */
    public void addEvent(GameEvent e) {
        events.add(e);
    }
    
    /**
       Returns the current level of the game
       @return the current level of the game
     */
    public Level getLevel() {
    	return level;
    }
    
    public void setLevel(Level newLevel) {
    	level = newLevel;
    	advancingLevel = false;
    }

    /**
       Freezes the timebar if true is passed, or de-freezes the timebar if false.
       @param timeBarFrozen true to freeze the timeBar, false to de-freeze.
     */
    public void setTimeBarFrozen(boolean frz) {
    	timeBarFrozen = frz;
    }
    public void setActiveSquareFrozen(boolean frz) {
    	activeSquareFrozen = frz;
    }

    public int getLevelNum() {
    	return level.getLevelNum();
    }
    
    public List<PowerUp> getActivePowerUps() {
    	return activePowerUps;
    }
    
    public void addPowerUp(PowerUp power) {
    	activePowerUps.add(power);
    }
    
    public boolean getCheated() {
    	return cheated;
    }
}
