package finale.gameModel.powerUps;

import finale.controllers.GameController;
import finale.gameModel.Board;
import finale.gameModel.Location;

public class Magnet extends PowerUp {
	private int lifetime = 200;
	private int magnetPulsePeriod = 10;
	private int time = 0;
	private boolean rightPolarity;
	
	public String getName() {
		return "Magnetized!";
	}
	public String getShortName() {
		return "magnet";
	}
	
	public Magnet() {
		rightPolarity = (Math.random() < 0.5);
	}
	private static boolean squareContains(Location squareLoc, Location loc) {
		int sr = squareLoc.getRow(), sc = squareLoc.getCol();
		int lr = loc.getRow(), lc = loc.getCol();
		return
			(sr   == lr  &&  sc   == lc) ||
			(sr+1 == lr  &&  sc   == lc) ||
			(sr   == lr  &&  sc+1 == lc) ||
			(sr+1 == lr  &&  sc+1 == lc);
	}
	public boolean activate(GameController ctl) {
		++time;
		
		if (time % magnetPulsePeriod == 0) {
			Board board = ctl.getBoard();
			int rows = board.getRows(), cols = board.getCols();
			int endCol = cols-1;
	        for (int r = 0; r < rows; ++r) {
	            boolean lastWasEmpty = false;
	            for (int c = 0; c < cols; ++c) {
	                Location loc = new Location(r, rightPolarity?endCol-c:c);
	                if (board.get(loc) == null) {
	                	lastWasEmpty = !squareContains(
	                			ctl.getActiveSquare().getLocation(), loc);
	                } else if (lastWasEmpty) {
	                    board.get(loc).moveTo(new Location(r,
	                    		rightPolarity ? endCol-c+1 : c-1));
	                    lastWasEmpty = true;
	                }
	            }
	        }
			board.gravitate();
		}
		
		return time < lifetime;
	}

}
