/*
 * Stars.java
 *
 * Created on December 30, 2006, 10:09 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Stars;

import java.lang.Thread;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import javax.microedition.lcdui.*;


/**
 * This is the main class of the Star Demo midlet
 * @author unqueued
 */
public class Stars extends MIDlet implements CommandListener, ItemStateListener {
    
    private Command exitCmd = new Command("Exit",Command.EXIT,1);
    private Command optionsCmd = new Command("Options", Command.OK,2);
    private Command okCmd = new Command("OK", Command.OK, 4);
    private Command aboutCmd = new Command("About", Command.HELP, 5);
    private Command dismissCmd = new Command("OK", Command.OK, 1);
    private Gauge speedGauge;
    private StringItem speedLabel;
    private Gauge layerGauge;
    private StringItem layerLabel;
    private Gauge starGauge;
    private StringItem starLabel;
    
    private StarBoard starBoard;
    
    private boolean optionsTainted = false;
    
    /** Creates a new instance of Stars */
    public Stars() {
        starBoard = new StarBoard();
        starBoard.addCommand(exitCmd);
        starBoard.addCommand(optionsCmd);
        starBoard.addCommand(aboutCmd);
        starBoard.setCommandListener(this);
    }
    
    
    /**
     * Called when the Midlet is started
     */
    protected void startApp() {
        Display.getDisplay(this).setCurrent(starBoard);

	try {
	    // Start the game in its own thread
	    Thread myThread = new Thread(starBoard);
	    myThread.start();
            starBoard.animationOn();
            starBoard.logicOn();
	} catch (Error e) {
	    destroyApp(false);
	    notifyDestroyed();
	}
    }
    
    /**
     * called when the VM needs the app to be disposed of
     * @param unconditional whether the abort should be unconditional
     */
    protected void destroyApp(boolean unconditional) {
        Display.getDisplay(this).setCurrent((Displayable)null);
    }
    
    /**
     * Called when the VM needs the app to pause
     */
    protected void pauseApp() {
        
    }
    
    /**
     * I use this method to have StringItems display an exact number value of the sliders, because I felt the sliders didn't give enough information.
     * I also catch if the layer slider is set below 0, and force its value back to 1.
     * @param i a pointer to the item that generated the event
     */
    public void itemStateChanged(Item i) {
        optionsTainted = true;
        if(i == layerGauge)
            if(layerGauge.getValue() > 0) {
                starBoard.setLayers(layerGauge.getValue());
                layerLabel.setText("" + layerGauge.getValue());
            } else
                layerGauge.setValue(1);
        else if(i == starGauge) {
            starBoard.setStars(starGauge.getValue());
            starLabel.setText("" + starGauge.getValue());
        }
    }
    
    /**
     * I capture all button events here.
     * When the Help or Options buttons are hit, I instantiate the forms that they pull up on the fly, and assign Command buttons to them, and then caputre the command buttons.
     * @param c A pointer to the calling Command.
     * @param d A pointer to the Displayable object containging the calling Command
     */
    public void commandAction(Command c, Displayable d) {
        if(c == exitCmd) {
            destroyApp(false);
            notifyDestroyed();
        } else if (c == optionsCmd) {
            starBoard.logicOff();
            starBoard.animationOff();
            Item[] formItems = {
                speedGauge = new Gauge("Star Speed", false, 7, 0),
                speedLabel = new StringItem("", "Inactive"),
                layerGauge = new Gauge("Star layers", true, 5, starBoard.getLayers()),
                layerLabel = new StringItem("", "" + layerGauge.getValue()),
                starGauge = new Gauge("Number of stars per layer", true, 30, starBoard.getStars()),
                starLabel = new StringItem("","" + starGauge.getValue())
            };
            Form f = new Form("Options", formItems);
            f.addCommand(okCmd);
            f.setCommandListener(this);
            f.setItemStateListener(this);
            Display.getDisplay(this).setCurrent(f);
        } else if(c == okCmd) {
            Display.getDisplay(this).setCurrent(starBoard);
            if(optionsTainted) {
                starBoard.restart();
                optionsTainted = false;
            } else {
                starBoard.logicOn();
                starBoard.animationOn();
            }
        } else if(c == aboutCmd) {
            starBoard.animationOff();
            starBoard.logicOff();
            Item[] formItems = {
                new StringItem("You can control the speed, amount, and number of layers of scrolling stars\n"
                        )
            };
            Form f = new Form("About", formItems);
            f.addCommand(dismissCmd);
            f.setCommandListener(this);
            Display.getDisplay(this).setCurrent(f);
        } else if(c == dismissCmd) {
            Display.getDisplay(this).setCurrent(starBoard);
            starBoard.animationOn();
            starBoard.logicOn();
        }
    }
}
