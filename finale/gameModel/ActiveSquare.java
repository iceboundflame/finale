package finale.gameModel;

import java.util.LinkedList;
import java.util.List;

import finale.events.BlockMoved;
import finale.gameModel.powerUps.DestroyerBlock;
import finale.gameModel.powerUps.PowerUp;
import finale.gameModel.powerUps.PowerUpContainerBlock;

public class ActiveSquare
{
    private Board board;

    /** The location of the lower-left block in this ActiveSquare */
    private Location location;

    private List<Block> blocks = new LinkedList<Block>();

    /** The static variable for rotating clockwise */
    public static final boolean CLOCKWISE = true;

    /** The static variable for rotating counterclockwise */
    public static final boolean COUNTERCLOCKWISE = false;
    
    public static final int NORMAL = 0;
    public static final int POWERUP = 1;
    public static final int DESTROYER = 2;

    /**
     * Creates a new ActiveSquare with the given parameters.
     * 
       @param board the Board for this ActiveSquare
       @param loc the Location of this ActiveSquare
       @param color the color configuration for this ActiveSquare
     */
    public ActiveSquare( Board board, Location loc, int special, int color )
    {
        if ( color < 0 || color > 15 )
            throw new IllegalArgumentException( "Invalid color, must be between 0 and 15" );
        
        for (int i = 0; i < 4; i++) {
        	Block blk;
        	boolean blockColor = ((color & 0x1) == 1);
        	if (i == 0 && special == POWERUP)
        		blk = new PowerUpContainerBlock(PowerUp.createRandomPowerUp(), new Location(0,0), blockColor);
        	else if (i == 0 && special == DESTROYER)
        		blk = new DestroyerBlock(new Location(0,0), blockColor);
        	else
        		blk = new Block(new Location(0,0), blockColor);
        	blocks.add(blk);	// block's location will get updated later
        	color >>= 1;
        }

        this.board = board;
        this.location = loc;
        
        updateBlockLocations();
    }

    public ActiveSquare( Board board, Location loc, int special )
    {
        this( board, loc, special, (int)( Math.random() * 16 ) ); // 0 <= colorConfig <= 15
    }

    public ActiveSquare( Board board, Location loc )
    {
        this( board, loc, NORMAL ); // 0 <= colorConfig <= 15
    }

    /**
     * Rotates the ActiveSquare clockwise or counterclockwise. Use the static
     * variables:
     * 
     * ActiveSquare.CLOCKWISE for clockwise rotations.
     * ActiveSquare.COUNTERCLOCKWISE for counterclockwise rotations.
     * 
     * @param clockwise
     *            true for a clockwise rotation, false for a counterclockwise
     *            rotation
     */
    public void rotate( boolean clockwise )
    {
        if ( clockwise == CLOCKWISE )
        {
            blocks.add(0, blocks.remove(3));
        }
        else if ( clockwise == COUNTERCLOCKWISE )
        {
            blocks.add(3, blocks.remove(0));
        }
        updateBlockLocations();
    }

    /**
     * Checks for the validity and vacancy of blocks to the right, and shifts
     * right if possible.
     * 
     * @return returns true if the move to the right was successful
     */
    public boolean moveRight()
    {
        Location rightLoc1 = new Location( location.getRow(),
            location.getCol() + 2 );
        Location rightLoc2 = new Location( location.getRow() + 1,
            location.getCol() + 2 );
        if ( board.isValidAndEmpty( rightLoc1 )
            && board.isValidAndEmpty( rightLoc2 ) )
        {
            location = new Location( location.getRow(), location.getCol() + 1 );
            
            Location oldloc = new Location(location.getRow(), location.getCol()-1);
            board.addEvent(new BlockMoved(blocks.get(0), oldloc));

            oldloc = new Location(location.getRow()+1, location.getCol()-1);
            board.addEvent(new BlockMoved(blocks.get(1), oldloc));
            
            updateBlockLocations();
            return true;
        }
        return false;
    }

    /**
     * Checks for the validity and vacancy of blocks to the left, and shifts
     * left if possible.
     * 
     * @return returns true if the move to the left was successful
     */
    public boolean moveLeft()
    {
        Location leftLoc1 = new Location( location.getRow(),
            location.getCol() - 1 );
        Location leftLoc2 = new Location( location.getRow() + 1,
            location.getCol() - 1 );
        if ( board.isValidAndEmpty( leftLoc1 )
            && board.isValidAndEmpty( leftLoc2 ) )
        {
            location = new Location( location.getRow(), location.getCol() - 1 );
            
            Location oldloc = new Location(location.getRow(), location.getCol()+2);
            board.addEvent(new BlockMoved(blocks.get(3), oldloc));

            oldloc = new Location(location.getRow()+1, location.getCol()+2);
            board.addEvent(new BlockMoved(blocks.get(2), oldloc));

            updateBlockLocations();
            return true;
        }
        return false;
    }

    /**
     * Tries to drop the ActiveSquare. Returns true if the drop was successful;
     * false if there are blocks under the ActiveSquare preventing the drop
     * 
     * @return true if the drop was successful.
     */
    public boolean drop()
    {
        Location lowerLoc1 = new Location( location.getRow() - 1,
            location.getCol() );
        Location lowerLoc2 = new Location( location.getRow() - 1,
            location.getCol() + 1 );
        if ( board.isValidAndEmpty( lowerLoc1 )
            && board.isValidAndEmpty( lowerLoc2 ) )
        {
            location = new Location( location.getRow() - 1, location.getCol() );

            Location oldloc = new Location(location.getRow()+2, location.getCol());
            board.addEvent(new BlockMoved(blocks.get(1), oldloc));

            oldloc = new Location(location.getRow()+2, location.getCol()+1);
            board.addEvent(new BlockMoved(blocks.get(2), oldloc));

            updateBlockLocations();
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Places all of the Blocks held by the ActiveSquare onto the Board.
     */
    public void finish()
    {
        if ( location.getRow() < board.getRows() - 2 )
        {
            blocks.get(0).putOnBoard(board);
            blocks.get(3).putOnBoard(board);
        }
        if ( location.getRow() + 1 < board.getRows() - 2 )
        {
            blocks.get(1).putOnBoard(board);
            blocks.get(2).putOnBoard(board);
        }

        board.gravitate();
    }

    private void updateBlockLocations() {
    	blocks.get(0).moveTo(new Location( location.getRow(),     location.getCol() ));
    	blocks.get(1).moveTo(new Location( location.getRow() + 1, location.getCol()));
    	blocks.get(2).moveTo(new Location( location.getRow() + 1, location.getCol() + 1 ));
    	blocks.get(3).moveTo(new Location( location.getRow(),     location.getCol() + 1 ));
    }
    
    /**
     * Returns an array of blocks in the square, (note, these "phantom blocks" are
     * not added onto the board.). Index 0 is the lower-left block, and
     * continues clockwise.
     * 
     * @return the Blocks held by the ActiveSquare
     */
    public Block[] getBlocks()
    {
        return blocks.toArray( new Block[blocks.size()] );
    }

    /**
     * Get the base location of the square (lower-left location)
     * 
     * @return the base location of the square
     */
    public Location getLocation()
    {
        return location;
    }
}
