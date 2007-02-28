/*
 * MicroTetris.java
 *
 * Created on January 4, 2007, 2:13 AM
 */

package MicroTetris;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.Alert;

/**
 * 
 * @author torpid
 * @version 
 */
public class MicroTetris extends MIDlet 
        implements CommandListener {
    
    private GameBoard gameBoard;
    
    public MicroTetris() {
        gameBoard = new GameBoard();
        gameBoard.setCommandListener(this);
    }
    
    public void startApp() {
        preFlightChecks();
        
        Display.getDisplay(this).setCurrent(gameBoard);
    }
    
    private void preFlightChecks() {
        /*System.out.println("Pre-flight checks..");
        Alert a = new Alert("Error");
        a.setString("This resolution is not yet supported");
        a.setTimeout(Alert.FOREVER);
        Display.getDisplay(this).setCurrent(a);
        
        if(gameBoard.getWidth() < 240) {
           
        }*/
    }
    
    public void pauseApp() {
    }
    
    public void destroyApp(boolean unconditional) {
    }
    
    public void commandAction(Command c, Displayable d) {
        
    }
}