package finale.controllers;

import java.awt.event.KeyEvent;

import finale.Controller;
import finale.ControllerChangeListener;
import finale.View;
import finale.views.InstructionsView;
/**
This controller handles and controls the the instruction menu

@author  David Liu, Brandon Liu, Yuzhi Zheng
@version June 4th, 2008
@author team FINALE
*/
public class InstructionsController implements Controller {
    
    private InstructionsView view = new InstructionsView(this);
    private int page = 1;
    private int numPages = 3;
    
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
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_UP:
                if (page > 1)
                	page--;
                break;
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_DOWN:
                if (page == numPages)
                	changeListener.transferControl(new MenuController());
                else
                	page++;
                break;
            case KeyEvent.VK_ESCAPE:
            	changeListener.transferControl(new MenuController());
                break;
        }
    }

    public void step() {
        // TODO Auto-generated method stub

    }
    
    /**
       @return the page number of the current page that is displayed
     */
    public int getPageNumber() {
        return page;
    }

}
