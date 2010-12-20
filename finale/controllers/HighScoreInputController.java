package finale.controllers;

import java.awt.event.KeyEvent;

import finale.Controller;
import finale.ControllerChangeListener;
import finale.FinaleApplet;
import finale.View;
import finale.views.HighScoreInputView;
/**
This HighScoreInputController controls name input for new high scores.  It will 
save the entered name and high score

@author  David Liu, Brandon Liu, Yuzhi Zheng
@version June 4th, 2008
@author FINALE
*/
public class HighScoreInputController implements Controller {
	
	private HighScoreInputView view;
	private int score;
	private boolean cheated;
	private String playerName = "";
//	private HighScoreList highScores = new HighScoreList();
	private ControllerChangeListener changeListener;
	private ScoreResult scoreResult = null;
	private boolean isDoneSubmitting = false;

	private String[] menuitems = {
		"Post to Facebook",
		"Main Menu",
	};
	private int selection = 0;

	/**
	   @param gameCtl : controller of the game for the score
	 */
	public HighScoreInputController(GameController gameCtl) {
		playerName = FinaleApplet.getInstance().getParameter("playername");
		if (playerName == null) playerName = "you";
		
		view = new HighScoreInputView(this, gameCtl.getView());
		score = gameCtl.getScore();
		cheated = gameCtl.getCheated();
		if (!cheated) {
			final ScoreReporter report = new ScoreReporter();
			
			new Thread(new Runnable() {
				public void run() {
					scoreResult = report.submitScore(score);
					report.refreshPageScores();
//					report.postToFacebook(scoreResult);
					isDoneSubmitting = true;
				}
			}).start();
		} else {
			isDoneSubmitting = true;
		}
	}
	
	/**
	   @return score of the last game
	 */
	public int getScore() {
		return score;
	}
	public boolean isDoneSubmitting() {
		return isDoneSubmitting;
	}
	
	/**
	   @return whether score is a new high score
	 */
	public ScoreResult getScoreResult() {
		return scoreResult;
	}

	public boolean isCheated() {
		return cheated;
	}
	
	/**
	   @return the name entered for the high score
	 */
	public String getPlayerName() {
		return playerName;
	}
	
	public View getView() {
		return view;
	}

	public void processKey(KeyEvent e) {
		if (!isDoneSubmitting)
			return;
		
		if (e.getID() != KeyEvent.KEY_PRESSED)
			return;

		if (!cheated && scoreResult != null) { // showing submit to FB? prompt
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
	            	if (selection == 0) // Submit
	            		new ScoreReporter().postToFacebook(scoreResult);
	            	
	            	changeListener.transferControl(new MenuController());
	                break;
	        }
		} else {
			changeListener.transferControl(new MenuController());
		}
	}
	public int getSelectionIndex() { 
		return selection; 
	}
	public String[] getItems() {
		return menuitems;
	}

	public void setControllerChangeListener(ControllerChangeListener c) {
		changeListener = c;
	}

	public void step() {
		// TODO Auto-generated method stub
	}

}
