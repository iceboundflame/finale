package finale;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JPanel;

import finale.controllers.MenuController;
import finale.gameModel.Location;
import finale.utils.FilteredKeyListener;

/**
   The FinalePanel runs the loop, updating the game state and drawing the screen.
   Statistics are printed for performance tuning.
  
   @author  David Liu, Brandon Liu, Yuzhi Zheng
   @version Jun 4, 2008
   @author FINALE
   @author Sources - Killer Game Programming in Java
 */
public class FinalePanel extends JPanel implements Runnable, ControllerChangeListener {
    private static final int NO_DELAYS_PER_YIELD = 16;
    /*
     * Number of frames with a delay of 0 ms before the animation thread yields
     * to other running threads.
     */
    private static final int MAX_FRAME_SKIPS = 5;
    // no. of frames that can be skipped in any one animation loop
    // i.e the games state is updated but not rendered

    private Thread animator; // the thread that performs the animation
    private volatile boolean running = false; // used to stop the animation
    // thread
    private volatile boolean isPaused = false;

    private long period; // period between drawing in _nanosecs_

    // off-screen rendering
    private Graphics dbg;
    private Image dbImage = null;
    
    private Controller c;
    
    // [dcl] Variables for tracking statistics 
    private int framesInLastSecond = 0, updatesInLastSecond = 0;
    private long totalSleepInLastSecond = 0;
    private long statsStartTime = 0;
    
    Queue<KeyEvent> keyEventQueue = new LinkedList<KeyEvent>();

    /**
       @param period the period of time between drawing, in nanoseconds
     */
    public FinalePanel(long period) {
        this.period = period;

        setDoubleBuffered(false);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(806, 434));
        setIgnoreRepaint(true);

        setFocusable(true);
        requestFocus(); // the JPanel now has focus, so receives key events

        addKeyListener(new FilteredKeyListener() {
            public void KeyPressed(KeyEvent e) {
                keyEventQueue.add(e);
            }
            public void KeyReleased(KeyEvent e) {
            	keyEventQueue.add(e);
            }
            public void KeyTyped(KeyEvent e) {
            	keyEventQueue.add(e);
            }
        });
        
        transferControl(new MenuController());
//        transferControl(new GameController());
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
        int noDelays = 0;
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
            } else { // sleepTime <= 0; the frame took longer than the period
                excess -= sleepTime; // store excess time value
                overSleepTime = 0L;

                if (++noDelays >= NO_DELAYS_PER_YIELD) {
                    Thread.yield(); // give another thread a chance to run
                    noDelays = 0;
                }
            }

            beforeTime = System.nanoTime();

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
                float fps = (float)framesInLastSecond * 1000000000/statsElapsed;
                float sleepPercent = (float)totalSleepInLastSecond / statsElapsed;
                float load = 100 * (1 - sleepPercent);
                int drops = updatesInLastSecond-framesInLastSecond;
                
                System.out.print("Load: "+load+"%");
                System.out.print(" [ FPS: "+fps+" FrameDrops:"+drops+" Frames:"+framesInLastSecond+" ]\n");

                float locPerSec = (float)Location.creations * 1000000000/statsElapsed;
//                System.out.println("  Location instantiations: "+locPerSec+"/sec");
                Location.creations = 0;
                
                framesInLastSecond = updatesInLastSecond = 0;
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
        Dimension size = this.getSize();
        Rectangle b = new Rectangle(0,0, (int)size.getWidth(), (int)size.getHeight());
        if (dbImage == null || !lastSize.equals(size)) {
            lastSize = size;
            if (size.width == 0 || size.height == 0)
                return;
            dbImage = createImage(size.width, size.height);
            if (dbImage == null) {
                System.out.println("dbImage is null");
                return;
            } else
                dbg = dbImage.getGraphics();
        }
        c.getView().draw((Graphics2D)dbg, b);
        
        framesInLastSecond++;
    }

    // use active rendering to put the buffered image on-screen
    private void paintScreen() {
        Graphics g;
        try {
            g = this.getGraphics();
            if ((g != null) && (dbImage != null))
                g.drawImage(dbImage, 0, 0, null);
            // Sync the display on some systems.
            // (on Linux, this fixes event queue problems)
            Toolkit.getDefaultToolkit().sync();
            g.dispose();
        } catch (Exception e) {
            System.out.println("Graphics context error: " + e);
        }
    }

    public void transferControl(Controller newController) {
        c = newController;
        newController.setControllerChangeListener(this);
    }
}
