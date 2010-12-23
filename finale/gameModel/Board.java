package finale.gameModel;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import finale.events.GameEvent;
import finale.events.MatchMade;

/**
 * 
 * @author David Liu, Brandon Liu, Yuzhi Zheng
 * @author Team FINALE
 * @version June 3rd, 2008
 *
 */
public class Board
{
    private Block[][] board;
    
    private int rows;
    
    private int cols;

    /** An ArrayList of locations of the lower-left block of 2x2 matched squares*/
    private Set<Location> matches;
    
    private Set<Location> singleMatches;
    
    private List<GameEvent> eventQueue = new LinkedList<GameEvent>();

    /**
     * @param rows : The number of rows for the Board
     * @param cols : The number of columns for the Board
     */
	public Board(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		board = new Block[rows][cols];
		matches = new TreeSet<Location>();
		singleMatches = new TreeSet<Location>();
	}
    
    /**
     * Creates many random blocks on the top quarter of the field
     * and drops them.  Used for testing and debugging purposes.
     */
    public void generateRandomTest() {
        for (int r = 3*rows/4; r < rows-2; r++) {
            for (int c = 0; c < cols; c++) {
                if (Math.random()<0.9)
                    new Block(this, new Location(r, c));
            }
        }
        gravitate();
    }
    
    /**
     * @param loc : The Location to be checked
     * @return true if the Location is valid on this Board
     */
    public boolean isValid(Location loc) {
        return loc.getRow() >= 0 && loc.getRow() < rows &&
            loc.getCol() >= 0 && loc.getCol() < cols;
    }
    
    /**
     * @param loc : The Location to be checked
     * @return true if the specified Location on the Board is empty.
     */
    public boolean isEmpty(Location loc) {
        return get(loc)==null;
    }
    
    /**
     * @param loc : The Location to be checked
     * @return true if the specified Location is both valid and empty on this Board
     */
    public boolean isValidAndEmpty(Location loc) {
        return isValid(loc) && isEmpty(loc);
    }
    
    public int getRows()
    {
        return rows;
    }
    
    public int getCols()
    {
        return cols;
    }
    
    /**
     * @param loc : The Location to get the Block from
     * @return The Block at this Location.  Returns null if the Locations is inValid
     * 			or if the Location is empty.
     */
    public Block get(Location loc)
    {
        if (!isValid(loc))
            return null;
        return board[loc.getRow()][loc.getCol()];
    }
    
    /**
     * Puts the Block at the Location and returns the old Block
     * @param loc : The Location for the Block to be put
     * @param block : The Block to be put
     * @return the old Block that was originally at this Location.
     */
    public Block put(Location loc, Block block)
    {
        Block old = board[loc.getRow()][loc.getCol()];
        board[loc.getRow()][loc.getCol()] = block;
        return old;
    }
    
    /**
     * Removes the Block at the Location from the Board and returns it
     * @param loc : The Location of the Block to be removed
     * @return The Block at the Location
     */
    public Block remove(Location loc)
    {
        Block old = board[loc.getRow()][loc.getCol()];
        board[loc.getRow()][loc.getCol()] = null;
        return old;
    }
    
    /**
     * Gravitates the Board.  Every Block is dropped until it reaches either
     * the bottom of the field or reaches another Block under it.
     */
    public void gravitate()
    {
        for (int c = 0; c < cols; c++) {
            int firstEmpty = -1;
            for (int r = 0; r < rows; r++) { // 0 is bottom of grid
                Location loc = new Location(r,c);
                if (get(loc) == null) {
                    if (firstEmpty == -1)
                        firstEmpty = r;
                } else if (firstEmpty > -1) {
                	// we found a block and at least one empty space below it
                    // TODO: Generate Block Dropped event
                    get(loc).moveTo(new Location(firstEmpty, c));
                    
                    ++firstEmpty;
                }
            }
        }
        findMatches();
    }
    
    private void findMatches()
    {
        Set<Location> oldMatches = new TreeSet<Location>(matches);
        matches.clear();
        
        singleMatches.clear();
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Location parentLoc = new Location(r,c);
                Block parent = get(parentLoc);
                
                if (parent == null)
                    continue;
                
                int offsets[] = {0, 1};
                boolean match = true;
            
                outer:
                for (int offsetR : offsets) {
                    for (int offsetC : offsets) {
                        if (offsetR == 0 && offsetC == 0) continue;
                        Block child = get(new Location(r+offsetR, c+offsetC));
                        if (child == null || child.getColor() != parent.getColor()) {
                            match = false;
                            break outer;    // exit early
                        }
                    }
                }
                
                if (match) {
                    for (int offsetR : offsets)
                        for (int offsetC : offsets)
                        	get(new Location(r+offsetR, c+offsetC)).matched();
                    
                    matches.add(parentLoc);
                    if (!oldMatches.contains( parentLoc )) {
                    	addEvent(new MatchMade(parentLoc));
                    }
                }
            }
        }
    }
    
    public Set<Location> getMatches() {
        return matches;
    }
    
    /**
     * Adds a new GameEvent to Board's eventQueue.  The GameEvents are 
     * eventually passed on to GameController's eventQueue.
     * @param e : 
     */
    public void addEvent(GameEvent e) {
        eventQueue.add(e);
    }
    
    public List<GameEvent> getNewEvents() {
        List<GameEvent> list = new LinkedList<GameEvent>(eventQueue);
        eventQueue.clear();
        return list;
    }
    
    public String toString() {
        String result = "";
        for (int r = rows-1; r >= 0; r--) {
            for (int c = 0; c < cols; c++) {
                Block blk = get(new Location(r,c));
                if (blk == null)
                    result += ".";
                else if (blk.getColor())
                    result += "#";
                else
                    result += "0";
            }
            result += "\n";
        }
        return result;
    }
    
    public void addSingleMatch(Location m) {
    	singleMatches.add(m);
    }
    public Set<Location> getSingleMatches() {
    	return singleMatches;
    }
}
