/*
 * StarBoard.java
 *
 * Created on December 30, 2006, 10:13 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Stars;

import java.lang.System;
import java.lang.Runnable;
import java.lang.InterruptedException;

import java.util.Random;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import javax.microedition.media.Player;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.ToneControl;

/**
 * Extends Canvas, acting as a pallette for the main graphics of this program.
 * Also contains the run() method for the driver thread.
 * @author unqueued
 */
public class StarBoard extends Canvas implements Runnable {
    
    private boolean doAnimation = false;
    private boolean doLogic = false;
    
    private int HEIGHT = this.getHeight();
    private int WIDTH = this.getWidth();
    
    private int stars = 15;
    private int layers = 3;
    
    private int starX[][];
    private int starY[][];
    
    private Random rnd;
    
    private Image offScreen;
    
    /** Creates a new instance of StarBoard */
    public StarBoard() {
        if(!isDoubleBuffered())
            offScreen = Image.createImage(this.getWidth(), this.getHeight());
        
        rnd = new Random();
        initialize();
    }
    
    /**
     * Reloads the values and rebuilds the animation matricies, and then restards the animation and logic.
     */
    public void restart() {
        initialize();
        animationOn();
        logicOn();
    }
    
    private void initialize() {
        starX = new int[stars][layers];
        starY = new int[stars][layers];
        
        for(int layer = 0; layer < starX[0].length; layer ++)
            for(int star = 0; star < starX.length; star ++) {
                starX[star][layer] = rnd.nextInt(WIDTH);
                starY[star][layer] = rnd.nextInt(HEIGHT);
            }
    }
    
    /**
     * Sets the number of star layers.
     * @param layers the value to assign to this.layers
     */
    public void setLayers(int layers) {
        this.layers = layers;
    }
    
    /**
     * sets the number of stars per layer
     * @param stars the value to assign to this.stars
     */
    public void setStars(int stars) {
        this.stars = stars;
    }
    
    /**
     * returns the number of layers
     * @return number of star layers
     */
    public int getLayers() {
        return layers;
    }
    
    /**
     * Returns number of stars per layer
     * @return Number of stars per layer
     */
    public int getStars() {
        return stars;
    }
    
    /**
     * first, the black background is draw with a fillRect(), and then the stars are drawn with the drawStars() method
     * @param g The pointer to the current graphical context
     */
    public void paint(Graphics g) {
        Graphics saved = g;
        
        if(offScreen != null)
            g = offScreen.getGraphics();
        
        g.setColor(0, 0, 0);
        g.fillRect(0, 0, HEIGHT, WIDTH);
        
        g.setColor(255,255,255);
        drawStars(g);
        
        if(saved != g)
            saved.drawImage(offScreen, 0, 0, 0);
    }
    
    private void drawStars(Graphics g) {
        if(doAnimation)
            for(int layer = 0; layer < starX[0].length; layer ++)
                for(int star = 0; star < starX.length; star ++)
                    g.drawRect(starX[star][layer], starY[star][layer], 1, 1);
    }
    
    private void updateStars() {
        if(doLogic)
            for(int layer = 0; layer < starX[0].length; layer ++)
                for(int star = 0; star < starX.length; star ++)
                    if((starX[star][layer] += layer + 1) > WIDTH) {
                        starX[star][layer] = layer + 1;
                        starY[star][layer] = rnd.nextInt(HEIGHT);
                    }
    }
    
    /**
     * The method to be run in a new thread
     */
    public void run() {
        while(true) {
            try {
                Thread.sleep(1);
                updateStars();
                repaint();
            } catch (java.lang.InterruptedException ie) {
                
            }
        }
    }
    
    /**
     * Toggles animation processing on.
     * This is called if the animation has been disabled with animationOff() and needs to be re-enabled.
     */
    public void animationOn() {
        doAnimation = true;
    }
    
    /**
     * Toggles animation processing off.
     * This is useful to preserve the state of the graphics being drawn if the current display is set somewhere else.
     */
    public void animationOff() {
        doAnimation = false;
    }
    
    /**
     * Suspends game logic processing.
     * This is called if the animation has been disabled with logicOff() and needs to be re-enabled.
     */
    public void logicOn() {
        doLogic = true;
    }
    
    /**
     * Toggles logic processing off.
     * This is useful to preserve the state of the graphics being drawn if the current display is set somewhere else.
     */
    public void logicOff() {
        doLogic = false;
    }
}
