package finale;

/**
   A ControllerChangeListener transfers control to a new Controller when
   asked.
  
   @author  David Liu, Brandon Liu, Yuzhi Zheng
   @version May 30th, 2008
   @author FINALE
 */
public interface ControllerChangeListener {
    /**
       Transfers control to a new Controller
       @param newController the new Controller
     */
    void transferControl(Controller newController);
}
