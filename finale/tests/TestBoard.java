package finale.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import finale.gameModel.Block;
import finale.gameModel.Board;
import finale.gameModel.Location;

public class TestBoard {
    @Test
    public void testStringify() {
        Board brd = new Board(10, 18);
        assertEquals(brd.toString(),
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"
            );
        
        new Block(brd, new Location(2,5), true);
        assertEquals(brd.toString(),
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            ".....#............\n"+
            "..................\n"+
            "..................\n"
            );
    }
    
    @Test
    public void testGravity() {
        Board brd = new Board(10, 18);
        new Block(brd, new Location(2,5), true);
        assertEquals(brd.toString(),
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            ".....#............\n"+
            "..................\n"+
            "..................\n"
            );
        brd.gravitate();
        assertEquals(brd.toString(),
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            ".....#............\n"
            );

        new Block(brd, new Location(2,5), false);
        assertEquals(brd.toString(),
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            ".....0............\n"+
            "..................\n"+
            ".....#............\n"
            );
        brd.gravitate();
        assertEquals(brd.toString(),
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            ".....0............\n"+
            ".....#............\n"
            );
    }

    @Test
    public void testMatchmaker() {
        Board brd = new Board(10, 18);
        
        new Block(brd, new Location(2,5), true);
        new Block(brd, new Location(3,5), true);
        new Block(brd, new Location(3,6), true);
        new Block(brd, new Location(4,6), true);
        assertEquals(brd.toString(),
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "......#...........\n"+
            ".....##...........\n"+
            ".....#............\n"+
            "..................\n"+
            "..................\n"
            );
        assertEquals(brd.getMatches().size(), 0);
        brd.gravitate();
        assertEquals(brd.toString(),
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            "..................\n"+
            ".....##...........\n"+
            ".....##...........\n"
            );
        assertArrayEquals(brd.getMatches().toArray(), new Location[] { new Location(0,5) });
    }
}
