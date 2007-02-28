/*
 * GameBoard.java
 *
 * Created on January 4, 2007, 2:19 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package MicroTetris;

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
 *
 * @author torpid
 */
public class GameBoard extends Canvas {
    
    private Image offScreen;
    
    private final int 
            WIDTH = this.getWidth(),
            HEIGHT = this.getHeight(),
            WIDTHMARGIN = WIDTH / 10,
            HEIGHTMARGIN = HEIGHT / 10,
            BOARDWIDTH = 8,
            BOARDHEIGHT = 16;
    
    private int BLOCKSIZE = 10;
    
    protected int displayBoardTopLeft;
    protected int displayBoardTop;
            
    
    Image outputImage;
    Image[] outputPreviewImage;
    DisplayBoard displayBoard;
    
    /** Creates a new instance of GameBoard */
    public GameBoard() {
        while(HEIGHT > BOARDHEIGHT * BLOCKSIZE)
            BLOCKSIZE ++;
        
        while(HEIGHT - HEIGHTMARGIN < BOARDHEIGHT * BLOCKSIZE)
            BLOCKSIZE --;
        
        displayBoard = new DisplayBoard(BOARDWIDTH * BLOCKSIZE, BOARDHEIGHT * BLOCKSIZE, BOARDWIDTH, BOARDHEIGHT, this);
                //new DisplayBoard(80, 160, 8, 16, this);
        outputImage = displayBoard.getImage();
        //outputPreviewImage = displayBoard.getPreviewImages();
        
        displayBoardTopLeft = this.getWidth() - outputImage.getWidth() - WIDTHMARGIN;
        displayBoardTop = HEIGHTMARGIN / 2;
        
        if(!isDoubleBuffered()) {
            offScreen = Image.createImage(this.getWidth(), this.getHeight());
        }
    }
    
    public void paint(Graphics g) {
        
        String message = new String("TODO: Add scores");
        Font myFont = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        
        Graphics saved = g;
        
        if(offScreen != null)
            g = offScreen.getGraphics();
        
        g.setColor(180,180,180);
        g.fillRect(0,0,this.getWidth(),this.getHeight());
        
        g.setFont(myFont);
        g.setColor(0x00FFFFFF);
        g.drawString(message, 0, HEIGHT - HEIGHTMARGIN, 0);
        
        displayBoard.updateImage();
        
        //g.setColor(0x00000000);
        //g.drawString("Is doublebuffered: "+this.isDoubleBuffered(),0,0,0);
        //System.out.println("Is doublebuffered: "+this.isDoubleBuffered());
        
        g.drawImage(outputImage,
                displayBoardTopLeft,
                displayBoardTop,
                0);
        /*
        for(int i = 0; i < outputPreviewImage.length; i ++)
            g.drawImage(
                    outputPreviewImage[i],
                    WIDTHMARGIN,
                    (HEIGHTMARGIN / 2) + (i * outputPreviewImage[0].getHeight()),
                    0);*/
        
        if(saved != g)
            saved.drawImage(offScreen, 0, 0, 0);
    }
    
    public void keyPressed(int keyCode) {
        repaint();
        switch (getGameAction(keyCode)) {
            
            case Canvas.LEFT:
                displayBoard.inputLeft();
                break;
            
            case Canvas.RIGHT:
                displayBoard.inputRight();
                break;
            
            case Canvas.UP:
                displayBoard.inputUp();
                break;
                
            case Canvas.DOWN:

                displayBoard.inputDown();
                break;
                
            case 0:
                switch (keyCode) {
                    case Canvas.KEY_NUM2:
                        System.out.println("Up");
                        break;
                        
                    case Canvas.KEY_NUM8:
                        System.out.println("Down");
                        break;
                        
                    case Canvas.KEY_NUM4:
                        System.out.println("Left");
                        break;
                        
                    case Canvas.KEY_NUM6:
                        System.out.println("Right");
                        break;
                }
        }
    }
}
