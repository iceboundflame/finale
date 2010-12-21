package finale.gameModel;

import java.util.Set;
import java.util.TreeSet;

import finale.events.BlockDestroyed;
import finale.events.ChainDestroyerDestroyed;

public class TimeBar {
	private Board board;
	private int locColumn;
	private Set<Block> markedBlocks;
	private Set<Location> markedMatchLocations, markedChainDestroyerLocations;
	private int numDeleted;
	
	public TimeBar(Board b) {
		board = b;
		locColumn = 0;
		markedBlocks = new TreeSet<Block>();
		markedMatchLocations = new TreeSet<Location>();
		markedChainDestroyerLocations = new TreeSet<Location>();
		numDeleted = 0;
	}

	/**
	   Advance the TimeBar one block and returns the number of matches if
	   the TimeBar has returned to the start of the grid
	   @return number of matches at the end of the scan of the grid
	 */
	public int advance() {
		if (locColumn == board.getCols() - 1) { // at end of board
			doDelete();
			locColumn = 0;
			
			int dels = numDeleted;
			numDeleted = 0;
			return dels;
		} else {
			boolean markedAny = markToDelete();
			if (!markedAny)
				doDelete(); // delete once we're past any group of matches
			
			++locColumn;
			return 0;
		}
	}

	private boolean markToDelete() {
		boolean marked = false;
		final int offsets[] = {0, -1};
		
		// Iterate through each of the locations in this column.
		for (int r = 0; r < board.getRows(); r++) {
			Location thisLoc = new Location(r, locColumn);
			Block thisBlk = board.get(thisLoc);
			
			if (board.getSingleMatches().contains(thisLoc)) {
				// deleting single match (usually from ChainDestroyerBlock)
			    markedChainDestroyerLocations.add(thisLoc);
				markedBlocks.add(thisBlk);
				marked = true;
			}
			
			// look for match squares that would include this location
			outer:
			for (int offsetR : offsets) {
				for (int offsetC : offsets) {
					Location matchloc = new Location(r+offsetR, locColumn+offsetC);
					if (board.getMatches().contains(matchloc)) {
					    markedMatchLocations.add(matchloc);
						markedBlocks.add(thisBlk);
						marked = true;
						break outer;
					}
				}
			}
		}
//		System.err.println("Marked "+(marked?"":"no ")+ "blocks");
		return marked;
	}

	private void doDelete() {
		if (markedBlocks.isEmpty())
			return;
		
//		System.err.println("Doing deletions");
		for (Block blk : markedBlocks) {
			blk.removeFromBoard();
			
			int type = BlockDestroyed.TYPE_NORMAL;
			if (markedChainDestroyerLocations.contains(blk.getLocation()))
				type = BlockDestroyed.TYPE_CHAIN;
			board.addEvent(new BlockDestroyed(blk.getLocation(), blk.getColor(), type));
		}
		markedBlocks.clear();
		
		board.addEvent(new ChainDestroyerDestroyed(
				markedChainDestroyerLocations.size()));
		markedChainDestroyerLocations.clear();
		
		numDeleted += markedMatchLocations.size();
		markedMatchLocations.clear();
		
		board.gravitate();
	}
	
	public Set<Block> getMarked() {
		return markedBlocks;
	}
	public int getNumDeleted() {
	    return numDeleted;
	}
	public int getLocation() {
		return locColumn;
	}
}
