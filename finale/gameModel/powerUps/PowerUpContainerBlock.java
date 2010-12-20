package finale.gameModel.powerUps;

import finale.events.PowerUpActivated;
import finale.gameModel.Block;
import finale.gameModel.Board;
import finale.gameModel.Location;
/**
    A Block that activates a PowerUp when it is deleted.
    
    @author Team FINALE
*/
public class PowerUpContainerBlock extends Block {
	private PowerUp power;
	
    /**
       @param power : the PowerUp
       @param board : the Board
       @param loc : the Location of the PowerUp
       @param color : true = color for block1  false = color for block2 
     */
    public PowerUpContainerBlock(PowerUp power, Board board, Location loc, boolean color) {
    	super(board, loc, color);
        this.power = power;
    }
    /**
        @param power : the type of power up
       @param board : the board of the game
       @param loc : the location of the power up
     */
    public PowerUpContainerBlock(PowerUp power, Board board, Location loc) {
        this(power, board, loc, (Math.random() < 0.5));//randomly picked color
    }
    /**
       @param power : the type of power up
       @param loc : the location of the power up
       @param color : true = color for block1  false = color for block2 
     */
    public PowerUpContainerBlock(PowerUp power, Location loc, boolean color) {
        this(power, null, loc, color);
    }
    /**
       @param power : the type of power up
       @param loc : the location of the power up
     */
    public PowerUpContainerBlock(PowerUp power, Location loc) {
        this(power, loc, (Math.random() < 0.5));
    }
    
    public void deleted() {
    	Board b = getBoard();
		System.out.println("PowerUp deleted " + power.getName());
    	if (b != null) {
    		b.addEvent(new PowerUpActivated(power));
    		System.out.println("PowerUp Go! " + power.getName());
    	}
    }
    
    /**
       @return the powerUp 
     */
    public PowerUp getPowerUp() {
    	return power;
    }
}
