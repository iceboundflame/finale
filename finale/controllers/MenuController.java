package finale.controllers;

import java.awt.event.KeyEvent;

import finale.Controller;
import finale.ControllerChangeListener;
import finale.View;
import finale.views.MenuView;
/**
Controls actions in the start menu

@author  David Liu, Brandon Liu, Yuzhi Zheng
@version June 4th, 2008
@author team FINALE
*/
public class MenuController implements Controller {
    
    private View view = new MenuView(this);
    private int selection = 0;
    
    private String[] menuitems = {
        "Play Now!",
//        "Time Attack",
        "Instructions",
    };

    private ControllerChangeListener changeListener;
    
    public void setControllerChangeListener(ControllerChangeListener c) {
        changeListener = c;
    }
    
    public View getView() {
        return view;
    }

    public void processKey(KeyEvent e) {
        if (e.getID() != KeyEvent.KEY_PRESSED)
            return;
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (selection == 0)
                    selection = menuitems.length-1;
                else
                	selection--;
                break;
            case KeyEvent.VK_DOWN:
                selection = (selection+1) % menuitems.length;
                break;
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
                selected(selection);
                break;
        }
    }
    
    private void selected(int sel) {
        System.out.println(menuitems[sel]);
        switch (sel) {
            case 0: // new challenge game
                changeListener.transferControl(new ChallengeGameController());
                break;
//            case 1: // new time attack game
//                changeListener.transferControl(new TimeAttackGameController(3600));//3600 frames = 120 seconds = 2 minutes
//                break;
            case 1: // instructions
            	changeListener.transferControl(new InstructionsController());
            	break;
        }
    }

    public void step() {
        // TODO Auto-generated method stub

    }
    
    /**
       @return the string array of all menu items
     */
    public String[] getItems() {
        return menuitems;
    }
    
    /**
       @return the index of the current selected item
     */
    public int getSelectionIndex() {
        return selection;
    }

}
