/*
 * DisplayBoard.java
 *
 * Created on January 4, 2007, 4:29 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package MicroTetris;

import javax.microedition.lcdui.*;
import java.util.TimerTask;
import java.util.Vector;
import java.util.Random;
import java.util.TimerTask;
import java.util.Timer;


/**
 *
 * @author torpid
 */
public class DisplayBoard extends TimerTask {
    
    private int
            width = 1,
            height = 1,
            imageHeight = 1,
            imageWidth = 1,
            blockHeight = 1,
            blockWidth = 1,
            startingX = 4,
            startingY = 1;
    
    private Block[][] blockMatrix;
    private Block[][] previewMatrix;
    
    private Image imgCanvas;
    
    private int 
            colorBlack =    0x00000000,
            colorGrey =     0x00B4B4B4,
            colorWhite =    0x00FFFFFF,
            colorRed =      0x00FF0000,
            colorGreen =    0x0000FF00,
            colorBlue =     0x000000FF,
            colorYellow =   0x00FFFF00,
            colorOrange =   0x00FF8000;
    
    private Vector shapeList = new Vector(4);
    
    private Shape[] shapeL = new Shape[4];
    private Shape[] shapeBox = new Shape[4];
    private Shape[] shapel = new Shape[4];
    private Shape[] shapeRightZag = new Shape[4];
    private Shape[] shapeLeftZag = new Shape[4];
    
    private Shape currentShape;
    private Shape[] currentShapeList;
    private int shapeIndex = 0;
    
    private Shape previewShape;
    
    private Graphics g;
    
    private Random rnd = new Random();
    
    private boolean stateGameOver = false;
    private boolean statePaused = false;
    private boolean running = false;
    
    private Canvas parent;
    
    private Image[] previewPanes = new Image[3];
    private int previewWidth = 0;
    private int previewHeight = 0;
    private int previewSize = 5;
    private int previewBlockSize = 1;
    
    private Vector shapeQueue;
    
    /** Creates a new instance of DisplayBoard */
    public DisplayBoard(int imageWidth, int imageHeight, int width, int height, Canvas parent) {
        this.width = width;
        this.height = height;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        blockHeight = imageHeight / height;
        blockWidth = imageWidth / width;
        shapeQueue = new Vector();
        int previewAreaWidth;
        int previewAreaHeight;
        
        imgCanvas = Image.createImage(imageWidth + 1, imageHeight + 1);
        
        previewAreaWidth = (imgCanvas.getWidth() / 2);
        previewAreaHeight = previewAreaWidth;
        
        while(previewWidth < previewAreaWidth)
            previewWidth += (previewSize * previewBlockSize);
        
        while(previewWidth > previewAreaWidth)
            previewWidth -= (previewSize * previewBlockSize);
        
        while((previewHeight + parent.getHeight() / 20) * 3 < imgCanvas.getHeight())
            previewHeight += (previewSize * previewBlockSize);
        
        while((previewHeight + parent.getHeight() / 20) * 3 > imgCanvas.getHeight())
            previewHeight -= (previewSize * previewBlockSize);
        
        System.out.println(previewWidth);
        System.out.println(previewHeight);
        
        if(previewWidth < previewHeight)
            previewHeight = previewWidth;
        
        if(previewWidth > previewHeight)
            previewWidth = previewHeight;
            
        
        for(int i = 0; i < previewPanes.length; i ++)
            previewPanes[i] = Image.createImage(previewWidth, previewHeight);
        
        this.parent = parent;
        
        blockMatrix = new Block[width][height];
        for(int xi = 0; xi < blockMatrix.length; xi ++)
            for(int yi = 0; yi < blockMatrix[0].length; yi ++)
                blockMatrix[xi][yi] = new Block(xi, yi);
        
        previewMatrix = new Block[5][5];
        for(int xi = 0; xi < previewMatrix.length; xi ++)
            for(int yi = 0; yi < previewMatrix[0].length; yi ++)
                previewMatrix[xi][yi] = new Block(xi, yi);
        
        buildShapes();
        
        for(int i = 0; i < previewPanes.length; i ++)
            shapeQueue.addElement(copyShapeList((Shape[])shapeList.elementAt(rnd.nextInt(shapeList.size()))));
        
        newShape();
        
        Timer timer = new Timer();
        timer.schedule(this, 1500, 750);
        
        g = imgCanvas.getGraphics();
    }
    
    public void run() {
        if(stateGameOver || statePaused || running)
            return;
        
        /*if(running)
            return;
            while(running)
                Thread.yield();
         */
        
        running = true;
        
        System.out.println("Running");
        
        if(currentShape != null)
            currentShape.moveDown();
        
        parent.repaint();
        
        running = false;
    }
    
    private synchronized void hitBottom() {
        newShape();
        if(!currentShape.assertMove(currentShape.x, currentShape.y))
            gameOver();
        
        watchLines();
        
        notifyAll();
    }
    
    private void watchLines() {
        if(stateGameOver)
            return;
        
        for(int yi = 0; yi < blockMatrix[0].length; yi ++) {
            boolean whitespaceFound = false;
            for(int xi = 0; xi < blockMatrix.length; xi ++) {
                if(!blockMatrix[xi][yi].isOn()) {
                    whitespaceFound = true;
                    break;
                }
            }
            if(!whitespaceFound) {

                currentShape.clearShape();
                
                for(int yii = yi; yii >= 0; yii --)
                    for(int xii = 0; xii <= blockMatrix.length - 1; xii ++) {
                        if(yii > 0)
                            if(!blockMatrix[xii][yii - 1].isOn())
                                blockMatrix[xii][yii].setOff();
                            else
                                blockMatrix[xii][yii].setOn(blockMatrix[xii][yii - 1].setOff());
                    }
                currentShape.draw();
            }
        }
    }
    
    private synchronized void newShape() {        
        currentShapeList = (Shape[]) shapeQueue.elementAt(0);
        shapeQueue.removeElementAt(0);
        
        shapeQueue.addElement(copyShapeList((Shape[])shapeList.elementAt(rnd.nextInt(shapeList.size()))));
        
        shapeIndex = rnd.nextInt(currentShapeList.length);
        currentShape = currentShapeList[shapeIndex];
        
        //newPreviewShape();
        
        notifyAll();
    }
    
    public Image[] getPreviewImages() {
        return previewPanes;
    }
    
    public Image getImage() {
        updateImage();
        
        return imgCanvas;
    }
    
    public void updateImage() {
        if(currentShape != null)
            currentShape.draw();
        
        drawBlocks();
    }
    
    private void buildShapes() {
        /*******************
         *Important not to self:
         *Some of these blocks are redundant.
         *Find a way to copy a Shape graph.
         */
        
        //The different aspects of "L"
        shapeL[0] = 
                new Shape(colorRed).
                newUp().
                getHead().
                newDown().
                newRight().
                getHead();
        
        shapeL[1] = 
                new Shape(colorRed).
                newLeft().
                newDown().
                getHead().
                newRight().
                getHead();
        
        shapeL[2] = 
                new Shape(colorRed).
                newUp().
                newLeft().
                getHead().
                newDown().
                getHead();
        
        shapeL[3] = 
                new Shape(colorRed).
                newLeft().
                getHead().
                newRight().
                newUp().
                getHead();
        
        shapeList.addElement(shapeL);
        
        //The Box shape
        shapeBox[0] =
                new Shape(colorBlue).
                newLeft().
                newUp().
                newRight().
                getHead();
        
        shapeBox[1] = 
                new Shape(colorBlue).
                newLeft().
                newUp().
                newRight().
                getHead();
        
        shapeBox[2] = 
                new Shape(colorBlue).
                newLeft().
                newUp().
                newRight().
                getHead();
        
        shapeBox[3] = 
                new Shape(colorBlue).
                newLeft().
                newUp().
                newRight().
                getHead();
        
        shapeList.addElement(shapeBox);
        
        //Lower case "l"
        shapel[0] =
                new Shape(colorRed).
                newDown().
                newDown().
                getHead().
                newUp().
                getHead();
        
        shapel[1] = 
                new Shape(colorRed).
                newLeft().
                getHead().
                newRight().
                newRight().
                getHead();
        
        shapel[2] = 
                new Shape(colorRed).
                newDown().
                newDown().
                getHead().
                newUp().
                getHead();
        
        shapel[3] = 
                new Shape(colorRed).
                newLeft().
                getHead().
                newRight().
                newRight().
                getHead();
        
        shapeList.addElement(shapel);
        
        //Shape right zig-zag
        shapeRightZag[0] =
                new Shape(colorOrange).
                newUp().
                newLeft().
                getHead().
                newRight().
                getHead();
        
        shapeRightZag[1] =
                new Shape(colorOrange).
                newUp().
                getHead().
                newLeft().
                newDown().
                getHead();
        
        shapeRightZag[2] = 
                new Shape(colorOrange).
                newUp().
                newLeft().
                getHead().
                newRight().
                getHead();
                
        shapeRightZag[3] = 
                new Shape(colorOrange).
                newUp().
                getHead().
                newLeft().
                newDown().
                getHead();
        
        shapeList.addElement(shapeRightZag);
        
        //Shape Left zigzag
        
        shapeLeftZag[0] = 
                new Shape(colorGreen).
                newRight().
                getHead().
                newDown().
                newLeft().
                getHead();
        
        shapeLeftZag[1] = 
                new Shape(colorGreen).
                newDown().
                getHead().
                newLeft().
                newUp().
                getHead();
        
        shapeLeftZag[2] =
                new Shape(colorGreen).
                newRight().
                getHead().
                newDown().
                newLeft().
                getHead();
        
        shapeLeftZag[3] =
                new Shape(colorGreen).
                newDown().
                getHead().
                newLeft().
                newUp().
                getHead();
        
        shapeList.addElement(shapeLeftZag);
        
    }
    
    private void drawBlocks() {
        drawBlocks(g);
    }
    
    private void drawBlocks(Graphics g) {
        
        g.setColor(colorWhite);
        g.fillRect(0, 0, imgCanvas.getWidth(), imgCanvas.getHeight());
        
        for(int xi = 0; xi < blockMatrix.length; xi ++)
            for(int yi = 0; yi < blockMatrix[0].length; yi ++)
                blockMatrix[xi][yi].draw(g, blockWidth, blockHeight);
        
        if(stateGameOver) {
            String gameOverMessage = new String("Game over!");
            int gameOverMessageLength = g.getFont().
                    charsWidth(gameOverMessage.toCharArray(), 0, gameOverMessage.length());
            g.setColor(0x00FFFFFF);
            g.drawString(gameOverMessage,
                        (imgCanvas.getWidth() / 2) - (gameOverMessageLength / 2),
                        imgCanvas.getHeight() / 2,
                        0
                    );
        }
    }
    
    public synchronized void inputUp() {
        if(statePaused || stateGameOver || running)
            return;
        
        /*if(running)
            while(running)
                Thread.yield();*/
        
        running = true;
        
        rotate();
        
        running = false;
        
        notifyAll();
    }
    
    public synchronized void inputDown() {
        if(statePaused || stateGameOver || running)
            return;
        
        /*if(running)
            while(running)
                Thread.yield();*/
        
        running = true;
        
        currentShape.moveDown();
        
        running = false;
        
        notifyAll();
    }
    
    public synchronized void inputLeft() {
        if(statePaused || stateGameOver || running)
            return;
        
        /*if(running)
            while(running)
                Thread.yield();*/
        
        running = true;
        
        currentShape.moveLeft();
        
        running = false;
        
        notifyAll();
    }
    
    public synchronized void inputRight() {
        if(statePaused || stateGameOver || running)
            return;
        
        /*if(running)
            while(running)
                Thread.yield();
         */
        
        running = true;
        
        currentShape.moveRight();
        
        running = false;
        
        notifyAll();
    }
    
    private void gameOver() {
        statePaused = true;
        stateGameOver = true;
        System.out.println("Game over!");
        clearBoard();
    }
    
    private void clearBoard() {
        clearBoard(colorRed);
    }
    
    private void clearBoard(int color) {
        currentShape.clearShape();
        currentShape = null;
        
        for(int yi = 0; yi < blockMatrix[0].length; yi ++)
            for(int xi = 0; xi < blockMatrix.length; xi ++)
                blockMatrix[xi][yi].setOn(color);
        
        drawBlocks();
    }
    
    private void rotate() {
        int x, y, shapeIndex;
        Shape currentShape;
        
        if(this.shapeIndex >= currentShapeList.length - 1)
            shapeIndex = 0;
        else
            shapeIndex = this.shapeIndex + 1;
        
        x = this.currentShape.x;
        y = this.currentShape.y;
        
        currentShape = (Shape)currentShapeList[shapeIndex];
        
        this.currentShape.clearShape();
        
        if(currentShape.move(x, y)) {
            this.shapeIndex = shapeIndex;
            this.currentShape = currentShape;
        } else
            this.currentShape.draw();
        
    }
    
    private class Block {
        
        private int fgColor = colorBlack;
        private int bgColor = colorWhite;
        private int offColor = bgColor;
        private int currentColor = bgColor;
        private int x = 0;
        private int y = 0;
        private boolean empty = true;
        
        public Block(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public int setOff() {
            empty = true;
            return currentColor;
        }
        
        public void setOn(int color) {
            empty = false;
            currentColor = color;
        }
        
        public boolean isOn() {
            return !empty;
        }
        
        public boolean isOff() {
            return empty;
        }
        
        public void draw(Graphics g, int blockHeight, int blockWidth) {
            if(empty) {
                g.setColor(offColor);
            } else {
                g.setColor(currentColor);
                g.fillRect(x * blockHeight, y * blockWidth, blockWidth - 1, blockHeight - 1);
                g.setColor(fgColor);
                g.drawRect(x * blockHeight, y * blockWidth, blockWidth - 1, blockHeight - 1);
            }    
        }
    }
    
    private class Shape {
        protected Shape left;
        protected Shape right;
        protected Shape up;
        protected Shape down;
        protected Shape parent;
        protected Shape head;
        protected int color = colorBlue;
        protected int x = 0;
        protected int y = 0;
        protected Block[][] matrix;
        
        public Shape() {
            x = startingX;
            y = startingY;
            head = this;
            matrix = blockMatrix;
        }
        
        public Shape(Block[][] matrix) {
            x = startingX;
            y = startingY;
            head = this;
            this.matrix = matrix;
        }
        
        public Shape(int color) {
            x = startingX;
            y = startingY;
            this.color = color;
            head = this;
            matrix = blockMatrix;
        }
        
        private Shape(Shape parent) {
            this.parent = parent;
            head = parent.getHead();
            color = head.getColor();
            matrix = blockMatrix;
        }
        
        public void setMatrix(Block[][] matrix) {
            this.matrix = matrix;
        }
        
        public void absorb(Shape from) {
            color = from.color;
            
            if(from.left != null) {
                newLeft();
                left.absorb(from.left);
            }
            
            if(from.right != null) {
                newRight();
                right.absorb(from.right);
            }
            
            if(from.up != null) {
                newUp();
                up.absorb(from.up);
            }
            
            if(from.down != null) {
                newDown();
                down.absorb(from.down);
            }
        }
        
        public Shape newLeft() {
            left = new Shape(this);
            return left;
        }
        
        public Shape newRight() {
            right = new Shape(this);
            return right;
        }
        
        public Shape newUp() {
            up = new Shape(this);
            return up;
        }
        
        public Shape newDown() {
            down = new Shape(this);
            return down;
        }
        
        public Shape getHead() {
            return head;
        }
        
        public int getColor() {
            return color;
        }
        
        public synchronized void moveTo(int x, int y) {
            if(parent != null) {
                System.out.println("This method may only be called from a head node");
                notifyAll();
                return;
            }
            
            this.x = x;
            this.y = y;
            
            notifyAll();
        }
        
        public synchronized boolean move(int x, int y) {
            if(parent != null) {
                System.out.println("This method may only be called from a head node");
                notifyAll();
                return false;
            }
            if(assertMove(x, y)) {
                this.x = x;
                this.y = y;
                draw();
                notifyAll();
                return true;
            } else {
                notifyAll();
                return false;
            }
        }
        
        public synchronized void moveDown() {
            if(parent != null) {
                System.out.println("This method may only not be called from a leaf node.");
                return;
            }
            
            if(statePaused || stateGameOver)
                return;
            
            clearShape(x, y);
            if(assertMove(x, y + 1))
                y ++;
            else {
                draw();
                hitBottom();
            }
            notifyAll();
        }
        
        public synchronized void moveUp() {
            if(parent != null) {
                System.out.println("This method may only not be called from a leaf node.");
                return;
            }
            
            if(statePaused || stateGameOver)
                return;
            
            clearShape(x, y);
            if(assertMove(x, y - 1))
                y --;
            else
                draw();
            
            notifyAll();
        }
        
        public synchronized void moveLeft() {
            if(parent != null) {
                System.out.println("This method may only not be called from a leaf node.");
                return;
            }
            
            if(statePaused || stateGameOver)
                return;
            
            clearShape(x, y);
            if(assertMove(x - 1, y))
                x --;
            else
                draw();
            
            notifyAll();
        }
        
        public synchronized void moveRight() {
            if(parent != null) {
                System.out.println("This method may only not be called from a leaf node.");
                return;
            }
            
            clearShape(x, y);
            if(assertMove(x + 1, y))
                x ++;
            else {
                draw();
            }
            
            notifyAll();
        }
        
        public synchronized void draw() {
            if(parent == null)
                draw(x, y);
            else
                System.out.println("draw() may not be called from a leaf node");
            
            notifyAll();
        }
        
        private synchronized void draw(int x, int y) {
            if(x > head.matrix.length - 1 || y > head.matrix[0].length - 1) {
                String output = new String();
                output.concat("Error with:" + head.matrix + "\n");
                if(head.matrix == blockMatrix)
                    output.concat("It is blockMatrix!\n");
                if(head.matrix == previewMatrix)
                    output.concat("It's previewMatrix!\n");
                
                output.concat("Tried to address");
                output.concat("x: " + x + "\n");
                output.concat("y: " + y + "\n");
                output.concat("head.matrix[" + head.matrix.length + "][" + head.matrix[0].length + "]\n");
                
                for(int xi = 0; xi < head.matrix.length; xi ++) {
                    for(int yi = 0; yi < head.matrix[0].length; yi ++)
                        output.concat(""+head.matrix[xi][yi]);
                    output.concat("\n");
                }
                
                g.drawString(output, 0, 0, 0);
                
                notifyAll();
                
                return;
            }
            
            head.matrix[x][y].setOn(color);
            
            if(up != null)
                up.draw(x, y - 1);
            if(down != null)
                down.draw(x, y + 1);
            if(left != null)
                left.draw(x - 1, y);
            if(right != null)
                right.draw(x + 1, y);
            
        }
        
        public synchronized void clearShape() {
            if(parent != null) {
                System.out.println("This method must be called from the head node!");
                notifyAll();
                return;
            } else {
                clearShape(x, y);
            }
            
            notifyAll();
        }
        
        private synchronized void clearShape(int x, int y) {
            head.matrix[x][y].setOff();
            
            if(up != null)
                up.clearShape(x, y - 1);
            if(down != null)
                down.clearShape(x, y + 1);
            if(left != null)
                left.clearShape(x - 1, y);
            if(right != null)
                right.clearShape(x + 1, y);
            
            notifyAll();
        }
        
        public boolean assertMove(int x, int y) {
            
            if(y < 0 || y > matrix[0].length  - 1)
                return false;
            
            if(x < 0 || x > matrix.length - 1)
                return false;
            
            if(matrix[x][y].isOn())
                return false;
            
            if(left != null)
                if(!left.assertMove(x - 1, y))
                    return false;
            
            if(right != null)
                if(!right.assertMove(x + 1, y))
                    return false;
            
            if(up != null)
                if(!up.assertMove(x, y - 1))
                    return false;
            
            if(down != null)
                if(!down.assertMove(x, y + 1))
                    return false;
            
            return true;
        }
    }

    private Shape[] copyShapeList(Shape[] fromShapeList) {
        Shape[] newShapeList = new Shape[fromShapeList.length];
        
        for(int i = 0; i < newShapeList.length; i ++) {
            newShapeList[i] = new Shape();
            newShapeList[i].absorb(fromShapeList[i]);
        }
        
        return newShapeList;
    }

    private void drawPreviewMatricies(int i) {
        Graphics g;
        
        g = previewPanes[i].getGraphics();
            
        g.setColor(colorGrey);
        g.fillRect(0, 0, previewWidth, previewHeight);
            
        for(int xi = 0; xi < previewMatrix.length; xi ++)
            for(int yi = 0; yi < previewMatrix[0].length; yi ++)
                previewMatrix[xi][yi].draw(g, previewPanes[0].getWidth() / 5, previewPanes[0].getHeight() / 5);
    }
    
    private void newPreviewShape() {
        Shape[] nextShapeList;
        
        for(int i = 0; i < previewPanes.length; i++) {
            clearPreview();
            previewShape = new Shape(previewMatrix);
            nextShapeList = (Shape[])shapeQueue.elementAt(i);
            previewShape.absorb(nextShapeList[0]);
            previewShape.moveTo(2, 2);
            previewShape.draw();
            drawPreviewMatricies(i);
        }
    }

    public void clearPreview() {
        if(previewShape != null) {
            previewShape.clearShape();
            previewShape = null;
            for(int xi = 0; xi < previewMatrix.length; xi ++)
                for(int yi = 0; yi < previewMatrix[0].length; yi ++)
                    previewMatrix[xi][yi].setOff();
        }
    }

}