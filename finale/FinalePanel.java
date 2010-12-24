package finale;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JPanel;

import finale.controllers.MenuController;
import finale.gameModel.Location;
import finale.utils.FilteredKeyListener;
import finale.views.AppletPausedOverlay;

/**
   The FinalePanel runs the loop, updating the game state and drawing the screen.
   Statistics are printed for performance tuning.
  
   @author  David Liu, Brandon Liu, Yuzhi Zheng
   @version Jun 4, 2008
   @author FINALE
   @author Sources - Killer Game Programming in Java
 */
public class FinalePanel extends JPanel implements Runnable, ControllerChangeListener {
	// Maximum number of frames that can be rendered without sleeping
	// before a forced yield
    private static final int MAX_FRAMES_WITHOUT_SLEEPING = 4;

    // Number of renders that can be skipped in any one animation loop
    // i.e max number of game updates that can run per render
    private static final int MAX_FRAME_SKIPS = 5;

    private Thread animator;
    private volatile boolean running = false;
    private volatile boolean isPaused = false;

    public static final int FRAME_RATE = 30;
    private long period; // period between drawing in _nanosecs_

    // off-screen rendering
    private Graphics2D dbg;
    private Image dbImage = null;
    
    private Controller c;
    
    // [dcl] Variables for tracking statistics 
    private int rendersInLastSecond = 0, updatesInLastSecond = 0;
    private long totalSleepInLastSecond = 0;
    private long statsStartTime = 0;
    
    Queue<KeyEvent> keyEventQueue = new LinkedList<KeyEvent>();

    /**
       @param period the period of time between drawing, in nanoseconds
     */
    public FinalePanel() {
        period = (long)1e9/FRAME_RATE; // nanoseconds per frame

        setDoubleBuffered(false); // we have DIY double-buffering
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(744, 434));
        setIgnoreRepaint(true); // active rendering takes control

        setFocusable(true);
        requestFocus(); // the JPanel now has focus, so receives key events

        addMouseListener(new MouseAdapter() {
        	public void mousePressed(MouseEvent e) {
            	FinaleApplet applet = FinaleApplet.getInstance();
            	if (applet != null) applet.goOn();
    			requestFocus();
        	}
		});
        
    	addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				pauseGame();
			}
			public void focusGained(FocusEvent e) {
				resumeGame();
			}
		});
    	
        addKeyListener(new FilteredKeyListener() {
            public void filteredKeyPressed(KeyEvent e) {
                if (!isPaused) keyEventQueue.add(e);
            }
            public void filteredKeyReleased(KeyEvent e) {
            	if (!isPaused) keyEventQueue.add(e);
            }
            public void filteredKeyTyped(KeyEvent e) {
            	if (!isPaused) keyEventQueue.add(e);
            }
        });
        
        transferControl(new MenuController());
    }

    // Pass off keystroke input to the active controller
    private void processKey(KeyEvent e) {
        c.processKey(e);
    }

    // wait for the JPanel to be added to the JFrame before starting
    public void addNotify() {
        super.addNotify(); // creates the peer
        startGame(); // start the game loop thread
    }

    // initialize and start the thread
    private void startGame() {
        if (animator == null || !running) {
            animator = new Thread(this, "GameLoop");
            animator.start();
        }
    }

    // ------------- game life cycle methods ------------
    // called by the JFrame's window listener methods

    // called when the JFrame is activated / deiconified
    public void resumeGame() {
        isPaused = false;
    }

    // called when the JFrame is deactivated / iconified
    public void pauseGame() {
        isPaused = true;
    }

    // called when the JFrame is closing
    public void stopGame() {
        running = false;
    }

    // ----------------------------------------------
    // GAME LOOP THREAD
    // ----------------------------------------------

    // The frames of the animation are drawn inside the while loop.
    public void run() {
        long beforeTime, afterTime, timeDiff, sleepTime;
        long overSleepTime = 0L;
        int framesRenderedWithoutSleeping = 0;
        long excess = 0L;

        beforeTime = statsStartTime = System.nanoTime();

        running = true;

        while (running) {
            gameUpdate();
            gameRender();
            paintScreen();

            afterTime = System.nanoTime();
            timeDiff = afterTime - beforeTime;
            sleepTime = (period - timeDiff) - overSleepTime;

            if (sleepTime > 0) { // some time left in this cycle
                try {
                    Thread.sleep(sleepTime / 1000000L); // nano -> ms
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
                framesRenderedWithoutSleeping = 0;
            } else { // sleepTime <= 0; the frame took longer than the period
                excess += -sleepTime; // store excess time value
                overSleepTime = 0L;

                if (++framesRenderedWithoutSleeping >= MAX_FRAMES_WITHOUT_SLEEPING) {
                    Thread.yield(); // give another thread a chance to run
                    framesRenderedWithoutSleeping = 0;
                }
            }

            beforeTime = System.nanoTime();

            // cap accumulated excess time, so that if the program
            // momentarily stops running (e.g. computer goes to sleep),
            // we don't overcompensate
            if (excess > period * MAX_FRAME_SKIPS)
            	excess = period * MAX_FRAME_SKIPS;
            
            /*
             * If frame animation is taking too long, update the game state
             * without rendering it, to get the updates/sec nearer to the
             * required FPS.
             */
            int skips = 0;
            while ((excess > period) && (skips < MAX_FRAME_SKIPS)) {
                excess -= period;
                gameUpdate(); // update state but don't render
                skips++;
//              System.out.println("["+(System.nanoTime()/1000000) + "] dropped frame ("+skips+")");
            }
            
            // David's statistics calculations for performance tuning
            if (sleepTime > 0)
                totalSleepInLastSecond += sleepTime;
            long statsElapsed = System.nanoTime() - statsStartTime;
            if (statsElapsed > 1000000000) {    // show stats every second
                float fps = (float)rendersInLastSecond * 1000000000/statsElapsed;
                float sleepPercent = (float)totalSleepInLastSecond / statsElapsed;
                float load = 100 * (1 - sleepPercent);
                int drops = updatesInLastSecond-rendersInLastSecond;
                
                System.out.print("Load: "+load+"%");
                System.out.print(" [ FPS: "+fps+" FrameDrops:"+drops+" Frames:"+rendersInLastSecond+" ]\n");
                PerfTracker.getInstance().addSample(drops);

//                float locPerSec = (float)Location.creations * 1000000000/statsElapsed;
//                System.out.println("  Location instantiations: "+locPerSec+"/sec");
                Location.creations = 0;
                
                rendersInLastSecond = updatesInLastSecond = 0;
                totalSleepInLastSecond = 0;
                statsStartTime = System.nanoTime();
            }
            /////////////////////////
        }
        System.exit(0); // so window disappears
    }

    private void gameUpdate() {
    	while (!keyEventQueue.isEmpty()) {
    		KeyEvent e = keyEventQueue.remove();
    		processKey(e);
    	}
        if (!isPaused) {
            c.step();
        }
        
        updatesInLastSecond++;
    }

    private Dimension lastSize = null;
    private void gameRender() {
    	// simulate slow render.
//    	try {
//			Thread.sleep(50);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	
    	Dimension size;
    	try {
    		FinaleApplet applet = FinaleApplet.getInstance();
	    	size = new Dimension(
	    		Integer.parseInt(applet.getParameter("fixedwidth")),
	    		Integer.parseInt(applet.getParameter("fixedheight"))
	    	);
    	} catch (Exception e) {
    		size = this.getSize();
    	}
    	Rectangle b = new Rectangle(size);
        if (dbImage == null || !lastSize.equals(size)) {
            lastSize = size;
            if (size.width == 0 || size.height == 0)
                return;
            dbImage = createImage(size.width, size.height);
            if (dbImage == null) {
                System.out.println("dbImage is null");
                return;
            } else
                dbg = (Graphics2D)dbImage.getGraphics();
        }
        c.getView().draw(dbg, b);
        
        if (isPaused) {
        	AppletPausedOverlay.draw(dbg, b);
        }
        
        rendersInLastSecond++;
    }

    // use active rendering to put the buffered image on-screen
    private void paintScreen() {
        Graphics g = this.getGraphics();
        if (g != null) {
            if (dbImage != null)
            	g.drawImage(dbImage, 0, 0, null);
            g.dispose();
        }
        // Sync the display on some systems.
        // (on Linux, this fixes event queue problems)
        Toolkit.getDefaultToolkit().sync();
    }

    public void transferControl(Controller newController) {
        c = newController;
        newController.setControllerChangeListener(this);
    }
}
