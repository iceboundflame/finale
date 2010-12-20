package finale.gameModel;

/**
 * The basic Block.  Holds a Location and a boolean variable to represent
 * its color out of two possible color choices
 * 
 * @author David Liu, Brandon Liu, Yuzhi Zheng
 * @author Team FINALE
 *
 */
public class Block implements Comparable<Block> {
    private Board board = null;
    private Location loc;
    private boolean color;
    
    /**
     * @param board : The Board for the Block
     * @param loc : The Location of the Block
     * @param color : The Color of the Block
     */
    public Block(Board board, Location loc, boolean color) {
        this.board = board;
        this.loc = loc;
        this.color = color;
        if (board != null)
            putOnBoard(board);
    }
    public Block(Board board, Location loc) {
        this(board, loc, (Math.random() < 0.5));
    }
    public Block(Location loc, boolean color) {
        this(null, loc, color);
    }
    public Block(Location loc) {
        this(loc, (Math.random() < 0.5));
    }
    
    /**
     * Puts the Block on the new Board.
     * @param newboard : The new Board
     */
    public void putOnBoard(Board newboard) {
        removeFromBoard();
        if (newboard != null) {
            board = newboard;
            newboard.put(loc, this);
        }
    }
    
    /**
     * Removes this Block from its current Board.
     */
    public void removeFromBoard() {
        if (board != null) {
        	deleted();
            board.remove(loc);
            board = null;
        }
    }
    public Board getBoard() {
    	return board;
    }
    
    /**
     * Moves this Block to a new Location by removing from and putting
     * this Block on the Board.
     * @param newloc : The new Location to be moved to.
     */
    public void moveTo(Location newloc) {
        if (board != null) {
            board.remove(loc);
            board.put(newloc, this);
        }
        loc = newloc;
    }
    public Location getLocation() {
        return loc;
    }
    public void setColor(boolean color) {
        this.color = color;
    }
    public boolean getColor() {
        return color;
    }
    public String toString() {
        return "Block[" + loc + " " + (color?"WHITE":"black") + " " + (board == null ? "nullboard" : "OnBoard") + "]";
    }
    
    /**
     * Compares two Blocks according to their Locations
     * @see Location.compareTo()
     */
    public int compareTo(Block o) {
//      if (! (o instanceof Block))
//          throw new IllegalArgumentException("Block was compared to non-Block object");
        return loc.compareTo( o.loc );
    }
    
    /**
     * Performs any actions that should be performed upon deletion of this Block.
     */
    protected void deleted() {
    	// subclasses should extend this
    }
    
    /**
     * Performs any actions that should be performed upon matching of this Block.
     */
    protected void matched() {
    	// subclasses should extend this
    }
}
