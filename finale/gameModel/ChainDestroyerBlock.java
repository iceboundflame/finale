package finale.gameModel;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

/**
    A Block that destroys neighboring blocks.
    
    @author Team FINALE
*/
public class ChainDestroyerBlock extends Block {
    /**
       @param board : the Board
       @param loc : the Location of the PowerUp
       @param color : true = color for block1  false = color for block2 
     */
    public ChainDestroyerBlock(Board board, Location loc, boolean color) {
    	super(board, loc, color);
    	System.out.println("NewDestroyer");
    }
    /**
       @param board : the board of the game
       @param loc : the location of the power up
     */
    public ChainDestroyerBlock(Board board, Location loc) {
        this(board, loc, (Math.random() < 0.5));//randomly picked color
    }
    /**
       @param loc : the location of the power up
       @param color : true = color for block1  false = color for block2 
     */
    public ChainDestroyerBlock(Location loc, boolean color) {
        this(null, loc, color);
    }
    /**
       @param loc : the location of the power up
     */
    public ChainDestroyerBlock(Location loc) {
        this(loc, (Math.random() < 0.5));
    }

    protected void matched() {
    	Set<Location> matches = new TreeSet<Location>();
    	
    	Board board = getBoard();
    	System.out.println("DESTROY!1");
    	if (board == null) {
    		System.err.println("Warning: Destroyer matched but no board associated");
    		return;
    	}
		System.out.println("DESTROY!2");

// 1. Set Q to the empty queue.
		Queue<Location> q = new LinkedList<Location>();
// 2. If the color of node is not equal to target-color, return.
		boolean color = this.getColor();
// 3. Add node to Q.
		q.add(this.getLocation());
//		Location curLoc = this.getLocation();
//		q.add(new Location(curLoc.getRow() + 1, curLoc.getCol()));
//		q.add(new Location(curLoc.getRow() - 1, curLoc.getCol()));
		
// 4. For each element n of Q:
		while (!q.isEmpty()) {
			Location n = q.remove();
//			if (matches.contains(n))
//				continue;
			Block x = board.get(n);	// also checks location for validity
// 5.  If the color of n is equal to target-color:
			if (!matches.contains(n) && x != null && x.getColor() == color) {
				matches.add(n);
				q.add(new Location(n.getRow() + 1, n.getCol()));  //note: don't have to check for location validity yet
				q.add(new Location(n.getRow() - 1, n.getCol()));
				
				// probe to left and right
				for (int dir : new int[] {-1, 1}) {
					Location probe = n;
					boolean advanced = true;
					while (advanced) {
						advanced = false;
						probe = new Location(probe.getRow(), probe.getCol() + dir);
						Block blk = board.get(probe);
						if (!matches.contains(probe) && blk != null && blk.getColor() == color) {
							advanced = true;
							matches.add(probe);
							Location north = new Location(probe.getRow() + 1, probe.getCol());
							Location south = new Location(probe.getRow() - 1, probe.getCol());
							q.add(north);  //note: don't have to check for location validity yet
							q.add(south);
						}
					}
				}
			}
		}
//12. Continue looping until Q is exhausted.
//13. Return.
		
		for (Location m : matches)
			board.addSingleMatch(m);
    }
    
//    // FIXME: This doesn't work if neighboring squares are deleted first, before this event is triggered.
//    public void deleted() {
//    	Board b = getBoard();
//    	System.out.println("DESTROY!1");
//    	if (b != null) {
//    		System.out.println("DESTROY!2");
//    		
//// 1. Set Q to the empty queue.
//    		Queue<Location> q = new LinkedList<Location>();
//    		
//// 2. If the color of node is not equal to target-color, return.
//    		boolean color = this.getColor();
//    		
//// 3. Add node to Q.
//    		q.add(this.getLocation());
//    		
//// 4. For each element n of Q:
//			while (!q.isEmpty()) {
//				Location n = q.remove();
//				Block x = b.get(n);
//// 5.  If the color of n is equal to target-color:
//				if (x != null && x.getColor() == color) {
//					for (int dir : new int[] {-1, 1}) {
//    					Location probe = n;
//    					boolean advanced;
//    					do {
//    						advanced = false;
//    						probe = new Location(probe.getRow(), probe.getCol() + dir);
//    						Block blk = b.get(probe);
//    						if (blk != null && blk.getColor() == color) {
//    							advanced = true;
//    							blk.removeFromBoard();
//    							Location north = new Location(probe.getRow() + 1, probe.getCol());
//    							Location south = new Location(probe.getRow() - 1, probe.getCol());
//    							q.add(north);
//    							q.add(south);
//    						}
//    					} while (advanced);
//					}
//				}
//			}
////12. Continue looping until Q is exhausted.
////13. Return.
//    	}
//    }
}
