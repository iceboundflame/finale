package finale.gameModel;

import java.util.Set;
import java.util.TreeSet;

import finale.events.BlockDestroyed;

public class TimeBar {
	private Board board;
	private int locColumn;
	private Set<Block> deletions;
	private Set<Location> matchMarked;
	private int numDeleted;
	public TimeBar(Board b) {
		board = b;
		locColumn = 0;
		deletions = new TreeSet<Block>();
		matchMarked = new TreeSet<Location>();
		numDeleted = 0;
	}

	/**
	   Advance the TimeBar one block and returns the number of matches if
	   the TimeBar has returned to the start of the grid
	   @return number of matches at the end of the scan of the grid
	 */
	public int advance() {
		// if no new blocks are marked, or we have reached the end of the board, then do deletions
		if (!markToDelete() || locColumn == board.getCols() - 1)
			doDelete();
		locColumn = (locColumn + 1) % board.getCols();
		if (locColumn == 0)
		{
		    int temp = numDeleted;
		    numDeleted= 0;
		    return temp;
		}
		return 0;
	}

	private boolean markToDelete() {
		boolean marked = false;
		final int offsets[] = {0, -1};
		
		// Iterate through each of the locations in this column.
		for (int r = 0; r < board.getRows(); r++) {
			Location thisLoc = new Location(r, locColumn);
			Block thisBlk = board.get(thisLoc);
			
			if (board.getSingleMatches().contains(thisLoc)) {
				// deleting single match (usually from DestroyerBlock)
			    matchMarked.add(thisLoc);
				deletions.add(thisBlk);
				marked = true;
			} else {
				// look for match squares that would include this location
				outer:
				for (int offsetR : offsets) {
					for (int offsetC : offsets) {
						Location matchloc = new Location(r+offsetR, locColumn+offsetC);
						if (board.getMatches().contains(matchloc)) {
						    matchMarked.add(matchloc);
							deletions.add(thisBlk);
							marked = true;
							break outer;
						}
					}
				}
			}
		}
//		System.err.println("Marked "+(marked?"":"no ")+ "blocks");
		return marked;
	}

	private void doDelete() {
		if (deletions.isEmpty())
			return;
		
//		System.err.println("Doing deletions");
		for (Block blk : deletions) {
			blk.removeFromBoard();
			board.addEvent(new BlockDestroyed(blk.getLocation()));
		}
		numDeleted += matchMarked.size();
		matchMarked.clear();
		deletions.clear();
		board.gravitate();
	}
	
	public Set<Block> getMarked() {
//		return new TreeSet<Block>(deletions);	// prevent concurrent exception; FIXME find real reason
		return deletions;
	}
	public int getNumDeleted() {
	    return numDeleted;
	}
	public int getLocation() {
		return locColumn;
	}
}
