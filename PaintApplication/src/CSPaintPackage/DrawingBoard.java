
/**A drawingBoard is really a JPanel, with added functionality. It is the platform for displaying and editing images.*/

package CSPaintPackage;

 
import java.awt.Color;



public class DrawingBoard extends javax.swing.JPanel{
   java.awt.Graphics2D jpanelGraphics; //jpanel graphics. image is drawn on jpanel
   java.awt.Graphics2D graphicsPainter; //image graphics. lines, shapes & other things done directly on image by using image graphics
   java.awt.image.BufferedImage boardImage;
   ImageInfo boardImageInfo = new ImageInfo();
  
  
  
   public java.awt.Color drawingColor = null;
   
   
   //(MUST GET REFERENCE TO BOARD IMAGE AND CHANGE IT DIRECTLY) 
   public DrawingBoard(){
         boardImage = this.provideImage(boardImageInfo.DEFAULT_IMAGE_LOCATION); //board image is set to default immediately 
                                                                               //program runs.
          
         
         this.setSize(boardImage.getWidth(), boardImage.getHeight()); //this code sets the size of the DrawingBoard to 
                                                                      //the size of the image
   }
   
   /**This method is responsible for retrieving a BufferedImage from a specified directory*/
   public final java.awt.image.BufferedImage provideImage(String imageAbsolutePath){
       java.awt.image.BufferedImage image  = null;
       try {
            image =  javax.imageio.ImageIO.read(new java.io.File(imageAbsolutePath));
            boardImageInfo.imagePath = imageAbsolutePath;
            boardImageInfo.boardImageType = boardImageInfo.getImageExtension(imageAbsolutePath);
            boardImageInfo.imageFromWeb = false; //we set this to false because we know the image was gotten from a local directory, else this method wouldn't run. 
       } catch (java.io.IOException ex) {
           throw new RuntimeException("The image was not found. provideImage(String imagePath) method. drawingBoard.java\n"
                   + "Abort immediately!!! Program has crashed because the image was not retrieved.");
       }
       
       return image;
   }
   
   /**This method takes the path of an image and returns the extension of the image as a string **/
   // The method should only be called by the provideImage method. It assumes that the string given is an image file path. 
   
   
   
   
     /**References to the graphics object which is used for painting is gotten here. */ 
     @Override
     public void paintComponent(java.awt.Graphics g){ //this is the method that is called to paint on a JPanel 
       
         super.paintComponent(g);
         this.jpanelGraphics = (java.awt.Graphics2D) g;  //the graphics handler stores reference to graphic object of JPanel. 
         
         this.setSize(boardImage.getWidth(), boardImage.getHeight()); //This line of code makes paintComponent run twice.
                                                                      //Don't remove! Leads to display problem. 
         //Line of code above resizes the DrawingBoard (which is a JPanel) to the size of the image.
         
         jpanelGraphics.drawImage(boardImage, 1, 1, this); //draws the image using the graphics context of 
                                                           //the drawingBoard (which is a subclass of JPanel)
         graphicsPainter = boardImage.createGraphics(); 
         
         
         //Important condition. If drawing color has not been set by user, provide a default color of black.
         //If color has been set, use the chosen color
         if(this.drawingColor != null){  //drawing color chosen by user. 
             graphicsPainter.setColor(this.drawingColor);
         }else if(this.drawingColor == null){ //drawing color not chosen by user. 
             graphicsPainter.setColor(Color.BLACK);
         }
         
    }
       
        
   
    
}