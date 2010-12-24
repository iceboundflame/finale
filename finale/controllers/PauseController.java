package finale.controllers;

import java.awt.event.KeyEvent;

import finale.Controller;
import finale.ControllerChangeListener;
import finale.PerfTracker;
import finale.View;
import finale.remote.ScoreReporter;
import finale.views.GameView;
import finale.views.PauseView;

/**
The PauseController keeps the old GameController so that the game is resumed
when the pause menu is exited.  It handles and controls all aspects of the pause
menu.

@author  David Liu, Brandon Liu, Yuzhi Zheng
@version June 3rd, 2008
@author team FINALE
*/
public class PauseController implements Controller {
	
	private GameController oldCtl;
	private PauseView view;
	private ControllerChangeListener changeListener;
	private String[] menuitems = {
			"Resume",
			"Restart",
			"Menu"
	};
	private int selection = 0;
	private int finalSelection;
	
	public PauseController(GameController oldCtl, GameView oldView)
	{
		PerfTracker.getInstance().stop();
		this.oldCtl = oldCtl;
		view = new PauseView(this, oldView);
	}

	public View getView() {
		return view;
	}

	public void processKey(KeyEvent e) {
		if (e.getID() != KeyEvent.KEY_PRESSED)
			return;
		
		if (e.getKeyCode() == KeyEvent.VK_P || e.getKeyCode() == KeyEvent.VK_ESCAPE)
			changeListener.transferControl(oldCtl);

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (selection == 0)
                    selection = menuitems.length-1;
                else
                	selection--;
                break;
            case KeyEvent.VK_DOWN:
                selection = (selection+1) % menuitems.length;
                break;
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
            	finalSelection = selection;
                view.drawSelection(finalSelection);
                break;
        }
	}
	
	private void selected(int sel)
	{
		PerfTracker perf = PerfTracker.getInstance();
		
		System.out.println(menuitems[sel]);
		switch (sel) {
			case 0:
				perf.start();
				changeListener.transferControl(oldCtl);
				break;
			case 1:
				System.out.println(perf.toString());
				ScoreReporter.logInBackground("game_restarted "
						+ perf.toString());
				changeListener.transferControl(new ChallengeGameController());
				break;
			case 2:
				System.out.println(perf.toString());
				ScoreReporter.logInBackground("game_quit "
						+ perf.toString());
				changeListener.transferControl(new MenuController());
				break;
		}
	}
	
	public String[] getItems()
	{
		return menuitems;
	}
	
	public int getSelectionIndex() { 
		return selection; 
	}

	public void setControllerChangeListener(ControllerChangeListener c) {
		changeListener = c;
	}

	public void step() {
		//
	}

	public void notifySelDrawDone() {
		selected(finalSelection);
	}

}
