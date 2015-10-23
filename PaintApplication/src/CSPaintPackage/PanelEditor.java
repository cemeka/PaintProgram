package CSPaintPackage;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 * A PanelEditor is the preeminent party responsible for making changes to an
 * image. It is "the Editor" with the power to draw lines and crop images. It
 * also takes care of opening and saving images, among other things. It is an
 * essential part of the CSPaintPackage.
 */
public class PanelEditor extends java.awt.event.MouseAdapter implements java.awt.event.ActionListener{

    GUI guiToEdit;
    DrawingBoard drawingPanel;
//---------The two main fields for this class are above. Everything else is extra.

    private boolean imageEdited = false; //tells us whether the image in our panel has been edited.
    private String operationToPerform = "nothing"; //this string holds information about the task at hand. the default
    //task is to do nothing when a user drags the mouse, until an operation has been specified by clicking one of the menu options. 
    private java.awt.Cursor cursor;
    private java.awt.Point beginPoint;
    private java.awt.Point currentPoint;
    private java.awt.Point endPoint;
    private java.awt.image.BufferedImage croppedImage;
    private int pictureLocation[] = new int[4]; //first two boxes contain image topmost x and y locations. next two contain image width and height respectively
    private java.awt.image.BufferedImage originalImage;
    private java.awt.image.BufferedImage imageCopy;
    private java.awt.Color previousColor;

    /**
     * PanelEditor constructor here. It takes a GUI object as a parameter
     */
    public PanelEditor(GUI guiToEdit) {
        if (guiToEdit != null) {
            this.guiToEdit = guiToEdit;
            this.drawingPanel = guiToEdit.drawingBoard;
        } else {
            throw new Error("Unacceptable null reference. No gui given to edit. PanelEditor.java file");
        }
    }

    /**
     * This is the actionPerformed method. It informs whoever is listening(that
     * is, whoever cares) about an event or sets of events that something of
     * interest has happened
     */
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {

        javax.swing.JMenuItem clickedItem = findEventSource(e); //determine the source of the event
        String operationToPerform = clickedItem.getText(); //query the source of the event for description of its request.
        handleEvent(operationToPerform); //handle the event, given that we've determined exactly what user wants.

    }

    /**
     * This method determines the source of the event. That is, which option the
     * user clicked.
     */
    public javax.swing.JMenuItem findEventSource(java.awt.event.ActionEvent e) {
        boolean searchBothMenus = true;
        javax.swing.JMenuItem eventMaker = null;

        final Object eventSource = e.getSource();
        for (javax.swing.JMenuItem fileMenuItem : this.guiToEdit.fileMenuOptions) {
            if (eventSource == fileMenuItem) {
                searchBothMenus = false; //no need to search both menus since object has been found. saves time
                eventMaker = fileMenuItem;
                break;
            }
        }
        //handles the same procedure for the edit menu that the file menu does.  
        if (searchBothMenus == true) {
            for (javax.swing.JMenuItem editMenuItem : this.guiToEdit.editMenuOptions) {
                if (eventSource == editMenuItem) {
                    eventMaker = editMenuItem;
                    break;
                }
            }
        }
        return eventMaker;
    }

    /**
     * This method summons the appropriate methods for handling various events
     */
    private void handleEvent(String whatToDo) {
        whatToDo = whatToDo.toLowerCase();
        /*Be very careful here. do NOT use capital letters here. everything used for comparison is in small letters*/
        switch (whatToDo) {
            case "new file":
                //create a new file
                newFile();
                break;

            case "open local file":
                //open a local file. 
                openLocalFile();
                break;

            case "open file from web":
                //open file from web
                openFileFromWeb();
                break;

            case "save file":
                //save file
                saveFile();
                break;

            case "save file as...":
                //save file as...
                saveFileAs();
                break;

            case "quit":
                //quit
                quit();
                break;
        }

        //if event not was not handled by now, then move on to edit menu
        switch (whatToDo) {

            case "draw straight line":
                drawStraightLine();
                break;

            case "draw with pencil":
                drawWithPencil();
                break;

            case "clean with eraser":
                cleanWithEraser();
                break;
            case "set drawing color":
                setDrawingColor();
                break;

            case "fill picture with paint":
                fillPictureWithPaint();
                break;
            case "paint background":
                paintBackground();
                break;

            case "select tool":
                cropImage();
                break;

            case "move cropped image":
                moveCroppedImage();
                break;
        }

    }

    /* The methods used for editing the drawing board and handling files are below */
    /*
     * This method imitates Microsoft Paint, among other paint programs, in that
     * it creates a fresh platform for drawing
     */
    private void newFile() {
        this.askWhatToDoIfImageIsEdited();
        drawingPanel.boardImage = drawingPanel.provideImage(drawingPanel.boardImageInfo.DEFAULT_IMAGE_LOCATION);
        this.imageCopy = duplicateImage(drawingPanel.boardImage); //this sets the image copy to the current new image
        imageEdited = false;
        drawingPanel.repaint();
    }

    /**
     * This method allows a user to open a file stored locally on a computer
     */
    private void openLocalFile() {
        this.askWhatToDoIfImageIsEdited();
        javax.swing.JFileChooser fileChooser = this.prepareAndReturnImageFileChoser();

        int returnVal = fileChooser.showOpenDialog(guiToEdit.frame);

        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {

            try {
                String imageLocation = fileChooser.getSelectedFile().getAbsolutePath(); //fetching the absolute path of the file you want to open
                drawingPanel.boardImage = drawingPanel.provideImage(imageLocation);
                this.imageCopy = duplicateImage(drawingPanel.boardImage);
                guiToEdit.frame.validate();
            } catch (Exception exc) {
                javax.swing.JOptionPane.showMessageDialog(guiToEdit.frame, "Unable to load file because it is not an image");
            }
        }
    }

    /**
     * This method allows user to open an image from the Internet
     */
    private void openFileFromWeb() {
        this.askWhatToDoIfImageIsEdited();
        String imagePosition = "";
        try {

            imagePosition = javax.swing.JOptionPane.showInputDialog(guiToEdit.frame, "Please enter the URL of the file you wish to open", "Get Image from Web", javax.swing.JOptionPane.QUESTION_MESSAGE); //location of the image, that is, the image url
            if (imagePosition.length() > 300) {
                throw new Error("You're URL is unusually long. It is not possible to retrieve its contents at this time"); //maybe change this code so that is shows message rather than throw an error. 
            }

            java.net.URL url = new java.net.URL(imagePosition);  //forming a new URL object with the online location of the image
            drawingPanel.boardImage = javax.imageio.ImageIO.read(url);
            drawingPanel.boardImageInfo.imageFromWeb = true; 
            guiToEdit.frame.validate(); //this makes sure the changes are loaded

        } catch (Exception exc) { //this is an error message to be displayed if the image cannot be retrieved. 
            javax.swing.JOptionPane.showMessageDialog(guiToEdit.frame, "At this time, it is not possible to open and display a picture found at\n \"" + imagePosition + "\". This could "
                    + "be because the text you entered is not a URL\nor you do not have the appropriate permissions needed to access the file.", "Alert", javax.swing.JOptionPane.OK_OPTION);
            //a new drawing background is to be provided if a url cannot be retrieved. 
            drawingPanel.boardImage = drawingPanel.provideImage(drawingPanel.boardImageInfo.DEFAULT_IMAGE_LOCATION);
        }
    }

   
    private void saveFile() {
        
        if (drawingPanel.boardImageInfo.imageFromWeb == false){ //this tells us that the image wasn't gotten from the web. 
            if (drawingPanel.boardImageInfo.DEFAULT_IMAGE_LOCATION.equals(drawingPanel.boardImageInfo.imagePath)){ 
                //the user is trying to save to the default image, which isn't allowed. 
                //give them the 'Save File As' option instead. 
                saveFileAs();
                //attempts were made to prevent user from saving to the default image. However, a bug exists because saveFileAs()
                //still allows users to override the default image with a new file.
                
            }else{ //if the current path is the same path as that of the DEFAULT IMAGE, then we can't save a picture there.                                                                                                     //"the save file as" option will be called. 
                saveImage(drawingPanel.boardImageInfo.imagePath);
                this.imageEdited = false; //we set imageEdited to false because the image has been saved. 
            }
             
        }else{
              saveFileAs(); // We use "save file as" because this file was loaded directly from the Internet. 
                           //There is no file path to it on the local machine yet. 
        }
    }

    /*The method  below is very important. It is used to save the file. All other save methods call on this. 
     one to do the main work*/
    private void saveImage(String imageAbsoluteStoragePath) {
        java.io.File file = new java.io.File(imageAbsoluteStoragePath); //ideally this is supposed to 
        //be a directory
        try {
            javax.imageio.ImageIO.write(drawingPanel.boardImage, drawingPanel.boardImageInfo.boardImageType, file);
        } catch (java.io.IOException ex) {
            System.out.println("image could not be saved. drawingBoard.java");
        }
    }

    /**
     * This method allows user to save a file to any path of their choosing.
     */
    private void saveFileAs() {

        javax.swing.JFileChooser fileChooser = this.prepareAndReturnImageFileChoser();
        int returnVal = fileChooser.showSaveDialog(guiToEdit.frame);
        String newFileLocation = fileChooser.getCurrentDirectory().getAbsolutePath() + "\\";

        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) { 
            //the user must give a valid file name. The fileChooser ensures this. 
            String fileName = fileChooser.getSelectedFile().getName() + "." + drawingPanel.boardImageInfo.boardImageType;
            saveImage(newFileLocation + fileName); //image file is saved here.
            this.imageEdited = false; //since changes have been saved, we now say that the file has not been edited   
            drawingPanel.boardImage = drawingPanel.provideImage(newFileLocation + fileName); //Change to the image we just saved. the image previously on screen had intended modifications, but they weren't saved on that particular image. 
                                                                                             //the image that is on screen after this method is actually an updated image with all changes saved.
            
        }

    }

    /**
     * This method allows a user to quit an application. The user is asked if
     * (s)he wants to save changes to image in the case that the image has been edited
     */
    private void quit() { //this method may be adjusted later so that it is  called by the main exiting mechanism i.e when we exit by clicking the "X" button. 
                              
        if (imageEdited == false) {
            javax.swing.JOptionPane.showMessageDialog(guiToEdit.frame, "Thank you for using this program.");
            System.exit(0);
        } else if (imageEdited == true) {
            this.handleExitWhenFileNotSaved("Save work before exit?", "Paint");
        }
        
         
    }

    /**
     * This method allows a user to draw a straight line at any point in any
     * color on the picture.
     */
    private void drawStraightLine() {
        this.operationToPerform = "draw straight line";
        this.imageCopy = duplicateImage(drawingPanel.boardImage);
        this.cursor = this.getSimpleCursor();
        drawingPanel.setCursor(cursor);
    }

    /**
     * This method allows user to draw free form with a pencil, in any chosen color.
     */
    private void drawWithPencil() {
        this.operationToPerform = "draw with pencil";
        this.cursor = this.getPencilCursor(); //pencil cursor provided
        drawingPanel.setCursor(cursor);
    }

    /**
     * This method allows a user to clean a drawn line with an eraser. User can
     * set the color of the eraser by calling setDrawingColor()
     */
    private void cleanWithEraser() {
        this.operationToPerform = "clean with eraser";
        this.cursor = this.getEraserCursor();
        drawingPanel.setCursor(cursor);
        drawingPanel.drawingColor = java.awt.Color.WHITE;
    }

    /**
     * This method fills the image itself with a chosen color.
     */
    private void fillPictureWithPaint() {
        //operationToPerform does not really need to be documented here. only for paintEditor methods that deal with the mouse and motion. 
        int width = drawingPanel.boardImage.getWidth();
        int height = drawingPanel.boardImage.getHeight();

        java.awt.Color previousColor = drawingPanel.graphicsPainter.getColor();
        
        
        java.awt.Color newColor = javax.swing.JColorChooser.showDialog(
                guiToEdit.frame,
                "Choose MultiPurpose Color",
                guiToEdit.frame.getBackground());
        if (newColor == null){
            //do nothing
        }else{
        drawingPanel.drawingColor = newColor;
        //the drawing board automatically recognizes once this field has been set. 
        drawingPanel.graphicsPainter.setColor(drawingPanel.drawingColor);
        drawingPanel.graphicsPainter.fillRect(0, 0, width, height);
        drawingPanel.repaint();

        drawingPanel.drawingColor = previousColor; //this data in this field will be used the next time paintComponent() is 
        //called on a a drawing board
    }
    }

    /**
     * This method can be used to paint a background in a particular color
     */
    private void paintBackground() {
        //operationToPerform does not really need to be documented here. only for paintEditor methods that deal with the mouse and motion. 
        guiToEdit.frame.getContentPane().setBackground(selectColor());
    }

    /**
     * This method is used to set up drawing color
     */
    private void setDrawingColor() {
        //operationToPerform does not really need to be documented here. only for paintEditor methods that deal with the mouse and motion. 
        java.awt.Color color = selectColor();
        drawingPanel.drawingColor = color;
        drawingPanel.graphicsPainter.setColor(color);
        this.previousColor = color;
        // drawingPanel.repaint();
        //guiToEdit.frame.validate();
    }

    /**
     * This method handles a request by the user to crop an image.
     */
    private void cropImage() {
        if (!operationToPerform.equalsIgnoreCase("select tool")) {  //if statement here for efficiency. i.e. to make sure we are not repeating process
            this.operationToPerform = "select tool"; //telling the editor that subsequent operations to perform will be with select tool
            originalImage = drawingPanel.boardImage;
            this.cursor = this.getSimpleCursor();
            drawingPanel.setCursor(cursor);
        }
    }

    /**
     * This method handles request by user to move a cropped image. Note that
     * there must be a cropped image before this request can be fulfilled
     */
    private void moveCroppedImage() {
        if (this.croppedImage != null) { //meaning that select tool has done its part
            this.operationToPerform = "move cropped image";

        } else {
            javax.swing.JOptionPane.showMessageDialog(guiToEdit.frame, "Please use the select tool to crop an image before attempting to move it.");
        }
    }

    /*MouseEvent handlers below.*/
    @Override //takes care of things when pencil is clicked
    public void mouseClicked(java.awt.event.MouseEvent e) {
        //Hard coding below. Had to come up with some numbers!
       if(this.operationToPerform.equalsIgnoreCase("draw with pencil")){   //this sets image edited to true only if a pencil
                                                                           //is used to draw on the image
         this.imageEdited = true;  
       }
        
        if (operationToPerform.equalsIgnoreCase("draw with pencil")) { //this implies that we must be drawing with a pencil
            drawingPanel.graphicsPainter.drawOval(e.getX(), e.getY(), 3, 3);
            drawingPanel.repaint();
        }
    }

    @Override //get the first point of contact with the screen.
    public void mousePressed(java.awt.event.MouseEvent e) {
      
        beginPoint = e.getPoint();
        currentPoint = beginPoint;
        //mouse pressed does not call repaint on it's own
    }

    /**
     * An activity is carried out based on the value of OperationToPerform as
     * soon as the mouse is dragged.
     */
    public void mouseDragged(java.awt.event.MouseEvent e) {
        endPoint = e.getPoint();
        
        if (this.operationToPerform.equalsIgnoreCase("draw straight line")) { //tells us to draw straight line.
            //this says, draw a copy of a original image on the original image itself. Since they have the same
            //parameters and data, they will blend in perfectly. Then drawingPanel.graphicsPainter.drawLine(....)
            //tells computer to draw line. Since the line is still being drawn, mouseDragged runs several times. 
            //Note that every time the method runs, the identical  copy of the image (which has not been edited)
            //is supplied again, and its changes are NOT saved. it is only when the mouse is released that we 
            //escape the loop and the final line drawn is shown on screen. Then "imageCopy" is created again 
            //by duplicating our new bufferedImage (in mouseReleased), which now has a new line drawn on it. The imageCopy 
            //remains an identical copy of bufferedImage, and hence when we start to draw a line on it again, the method starts 
            //from the beginning and goes through the same process...
            drawingPanel.boardImage.getGraphics().drawImage(imageCopy, 0, 0, drawingPanel);
            drawingPanel.repaint();
            drawingPanel.graphicsPainter.drawLine(beginPoint.x, beginPoint.y, e.getX(), e.getY());
        } else if (this.operationToPerform.equalsIgnoreCase("draw with pencil")) { //tells us to draw with pencil
            drawingPanel.graphicsPainter.drawLine((int) currentPoint.getX(), (int) currentPoint.getY(), (int) endPoint.getX(), (int) endPoint.getY());
            currentPoint = endPoint;
        } else if (this.operationToPerform.equalsIgnoreCase("clean with eraser")) { //tells us to clean with eraser when mouse is dragged
            //hard coding used here to determine size of eraser marks
            
            drawingPanel.graphicsPainter.setStroke(new java.awt.BasicStroke(10)); //setting up the width of the eraser
            drawingPanel.graphicsPainter.drawLine(currentPoint.x, currentPoint.y, endPoint.x, endPoint.y);
            drawingPanel.repaint();                                 //request refresh to see changes
            currentPoint = endPoint;
 
        } else if (this.operationToPerform.equalsIgnoreCase("select tool")) {
           //select tool makes use of doublebuffering (identical to "draw straigt line"). It works in conjunction with 
            //"select tool" conditional (if/else) block in mouseReleased(); 
            int xStartPoint = beginPoint.x;
            int yStartPoint = beginPoint.y;
            int xWidth = Math.abs(endPoint.x - beginPoint.x) + 1;
            int yWidth = Math.abs(endPoint.y - beginPoint.y) + 1;

            imageCopy = duplicateImage(originalImage);

            drawingPanel.boardImage = imageCopy;
            this.croppedImage = duplicateImage(drawingPanel.boardImage.getSubimage(xStartPoint, yStartPoint, xWidth, yWidth));

            imageCopy.getGraphics().drawRect(xStartPoint, yStartPoint, xWidth, yWidth);

           //SAVE LOCATION OF CROPPED IMAGE HERE
            pictureLocation[0] = beginPoint.x;
            pictureLocation[1] = beginPoint.y;
            pictureLocation[2] = Math.abs(endPoint.x - beginPoint.x) + 1;
            pictureLocation[3] = Math.abs(endPoint.y - beginPoint.y) + 1;

        } else if (this.operationToPerform.equalsIgnoreCase("move cropped image")) {
            //cropped image moved
            imageCopy = duplicateImage(originalImage);
            drawingPanel.boardImage = imageCopy;
            imageCopy.getGraphics().drawImage(croppedImage, e.getX(), e.getY(), drawingPanel);
        }
        drawingPanel.repaint();

    }

    /**
     * Events to perform when mouse is released are listed here. There are a
     * number of conditional statements. Only code that performs a desired
     * operation, as specified by this.operationToPerform, will run here.
     */
    public void mouseReleased(java.awt.event.MouseEvent e) {

     if (!this.operationToPerform.equalsIgnoreCase("nothing")){
        this.imageEdited = true;
     }
        if (this.operationToPerform.equalsIgnoreCase("draw straight line")) {
            imageCopy = duplicateImage(drawingPanel.boardImage);
        } else if (this.operationToPerform.equalsIgnoreCase("select tool")) {
            drawingPanel.boardImage = originalImage;

            java.awt.Color previousColor = drawingPanel.graphicsPainter.getColor();
            originalImage.getGraphics().setColor(java.awt.Color.WHITE);
            originalImage.getGraphics().drawRect(pictureLocation[0], pictureLocation[1], pictureLocation[2], pictureLocation[3]);

            drawingPanel.repaint();

            imageCopy = null;
            drawingPanel.graphicsPainter.setColor(previousColor);
            // originalImage = null;
            operationToPerform = "nothing"; //operationToPerform must be set to null
            //to make sure you go back and start from the right place to edit an image

        } else if (this.operationToPerform.equalsIgnoreCase("move cropped image")) {
             //finalizes a request by user to move a cropped image. It works in conjunction with
            //a chunk of code in mouseDragged to ensure smooth movement of the image.
            //please see mouseDragged for more details.

            drawingPanel.repaint();
            drawingPanel.boardImage = originalImage;
            drawingPanel.repaint();
            java.awt.Color previousColor = drawingPanel.graphicsPainter.getColor();
            drawingPanel.boardImage.getGraphics().setColor(java.awt.Color.WHITE); //this makes sure a plain white background is left out when 
            //an image is cropped
            drawingPanel.boardImage.getGraphics().fillRect(pictureLocation[0], pictureLocation[1], pictureLocation[2], pictureLocation[3]);
            drawingPanel.boardImage.getGraphics().drawImage(croppedImage, e.getX(), e.getY(), drawingPanel);
            drawingPanel.repaint();
            this.operationToPerform = "nothing";
            imageCopy = null;
            originalImage = null;
            this.croppedImage = null;
            drawingPanel.graphicsPainter.setColor(previousColor);
        }
        //all points set to null. they will be given values when needed, particularly in the mouse event methods.
        beginPoint = null;
        currentPoint = null;
        endPoint = null;
    }

    /* End of methods used for editing drawing board and handling files */
    
    
    /* ---Beginning of helper methods. These are simply supporting methods that help to reduce verbosity in code 
     and divide tasks into clear and concise categories*/
    
    /**
     * This method prepares a JFileChooser and supplies it to whosoever demands
     * one. It is called primarily by the openLocalFile() method
     */
    private javax.swing.JFileChooser prepareAndReturnImageFileChoser() { //helper method
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JPEG file, PNG file, GIF file", "jpg", "jpeg", "png", "gif")); //this is the default filter. it allows you to pick images which are of the 3 most common file formats: jpeg, png, and gif
        return fileChooser;
    }

    public java.awt.Color selectColor() { //this method allows a user to select the color used for drawings through a GUI. It sets the default color to black if the user cancels the operation in the process
        java.awt.Color newColor = javax.swing.JColorChooser.showDialog(
                guiToEdit.frame,
                "Choose MultiPurpose Color",
                guiToEdit.frame.getBackground());

        if (newColor == null) { //if no color was chosen, then set default color to black. 
            newColor = java.awt.Color.BLACK;
        } else {
        }
        return newColor;
    }

    /**
     * If a client does not enter in a valid file name when prompted, this
     * method is charged with generating one randomly for the user.
     */
    private String generateRandomFileName() {

        double x = Math.random();
        String numberConvertedToText = x + "";
        return numberConvertedToText;
    }
    /*This method is not to be shared with any other class. It has a "guiToEdit.frame" which is an instance variable of this particular class.
     This can be removed without any harm. It's the first line after the else if statement. This method asks client if (s)he wants
     to save work before exiting the application. It takes action based on user's response*/

    
    /**This is a recursive method that ensures the user consciously chooses to save or not to save their work before exiting**/
    private void handleExitWhenFileNotSaved(String message, String messageBoxTitle) {
        int response = javax.swing.JOptionPane.showConfirmDialog(guiToEdit.frame, message, messageBoxTitle, javax.swing.JOptionPane.YES_NO_OPTION);
        if (response == javax.swing.JOptionPane.NO_OPTION) {
            javax.swing.JOptionPane.showMessageDialog(guiToEdit.frame, "Thank you and good bye.");
            System.exit(0);
        } else if (response == javax.swing.JOptionPane.YES_OPTION) {
            saveFileAs();
            if (imageEdited == true){ //this means the image still hasn't still been saved. if the client had used saveFileAs() properly, imageEdited would be false. 
                                      //continue to call the handleExitWhenFileNotSaved method recursively until either (1) user saves the file or (2)user 
                                      //explicitly says they don't want to save the file. 
               handleExitWhenFileNotSaved(message, messageBoxTitle); 
            }
             System.exit(0);
            
        }

    }

    /**
     * This method does essentially the same thing as the
     * handleExitWhenFileNotSaved method, but has a minor difference in that it
     * does not shut down the program.
     */
    private void askToSaveImage(String message, String messageBoxTitle) {
        int response = javax.swing.JOptionPane.showConfirmDialog(guiToEdit.frame, message, messageBoxTitle, javax.swing.JOptionPane.YES_NO_OPTION);
        if (response == javax.swing.JOptionPane.NO_OPTION) {
            //user does not want to save image 
        } else if (response == javax.swing.JOptionPane.YES_OPTION) {
            saveFileAs();
        }
    }

    /**
     * This method supplies a pencil cursor to whosoever demands one
     */
    private java.awt.Cursor getPencilCursor() {
     //this method retrieves a cursor whose icon is stored in the current working directory, specifically in the CSPaintProgram Package
        java.awt.Toolkit kit = java.awt.Toolkit.getDefaultToolkit();
        String location = System.getProperty("user.dir") + "\\src\\CSPaintPackage\\";
        java.awt.Image image = kit.getImage(location + "pencil.png");
        java.awt.Point hotSpot = new java.awt.Point(0, 27);
        java.awt.Cursor newCursor = kit.createCustomCursor(image, hotSpot, "Pencil");
        return newCursor;
    }

    /**
     * This method supplies an eraser cursor to whosoever demands one
     */
    private java.awt.Cursor getEraserCursor() {
       //this method retrieves a cursor whose icon is stored in the current working directory, specifically in the CSPaintProgram Package
        java.awt.Toolkit kit = java.awt.Toolkit.getDefaultToolkit();
        String location = System.getProperty("user.dir") + "\\src\\CSPaintPackage\\";
        java.awt.Image image = kit.getImage(location + "eraser.png");
        java.awt.Point hotSpot = new java.awt.Point(0, 27);
        java.awt.Cursor newCursor = kit.createCustomCursor(image, hotSpot, "Eraser");
        return newCursor;
    }

    /**
     * This method supplies a default cursor (presumably an arrow)
     */
    private java.awt.Cursor getSimpleCursor() {
        return java.awt.Cursor.getDefaultCursor();
    }

    /**
     * this method takes an image as a parameter and returns a copy of it
     */
    public static java.awt.image.BufferedImage duplicateImage(java.awt.image.BufferedImage image) {

        java.awt.image.BufferedImage j = new java.awt.image.BufferedImage(image.getWidth(), image.getHeight(), java.awt.image.BufferedImage.TYPE_INT_RGB);
        j.setData(image.getData());
        return j;
    }
    
    /**Gives the user the ability to save an edited image before opening a new one*/
    private void askWhatToDoIfImageIsEdited(){
        if (this.imageEdited == true) {
            this.askToSaveImage("Do you want to save the current image before you open another one?", "Paint");
        }
    }
 
    
}