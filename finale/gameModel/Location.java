package finale.gameModel;

/**
 * Holds a location on a Board.
 * 
 * @author david
 *
 */
public class Location implements Comparable<Location>
{
    private int r;

    private int c;
    
    /** The number of instantiations */
    public static int creations = 0;

    /**
     * @param r : The row of the Location
     * @param c : The column of the Location
     */
    public Location( int r, int c )
    {
        this.r = r;
        this.c = c;
        creations++;
    }

    public int getRow()
    {
        return r;
    }

    public int getCol()
    {
        return c;
    }

    public boolean equals( Object other )
    {
        Location otherloc = (Location)other;
        return r == otherloc.r && c == otherloc.c;
    }

    public int compareTo( Location other )
    {
        if ( r != other.r )
            return r - other.r;
        else
        	return c - other.c;
    }
    
    public String toString() {
    	return "Loc["+r+","+c+"]";
    }
}
