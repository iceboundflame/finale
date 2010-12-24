package finale.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import finale.gameModel.Location;

public class TestLocation {
    @Test
    public void testEquals() {
        //test equals()
        assertEquals(new Location(3,5), new Location(3,5));
        assertTrue(new Location(3,5).equals(new Location(3, 5)));
        assertFalse(new Location(0,0).equals(new Location(1, 1)));
    }

    @Test
    public void testGetters() {
        //test getRow() and getCol() methods
        Location loc = new Location(3, 5);
        assertEquals(loc.getRow(), 3);
        assertEquals(loc.getCol(), 5);
    }
    @Test
    public void testCompareTo() {
        //test compareTo()
        assertTrue(new Location(4, 4).compareTo(new Location(5, 5)) < 0);
        assertTrue(new Location(5, 5).compareTo(new Location(4, 4)) > 0);
        assertEquals(new Location(4, 5).compareTo(new Location(4, 5)), 0);
        assertTrue(new Location(4, 4).compareTo(new Location(4, 5)) < 0);
    }

    @Test
    public void testCreations() {
        //test the instantiations counter
    	Location.creations = 0;
        new Location(5, 6);
        assertEquals(Location.creations, 1);
    }
}
