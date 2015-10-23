/*
 * The ImageInfo.java class is used to store information about an image that the built-in BufferedImage.java class doesn't keep. 
 */
package CSPaintPackage;

/**
 *
 * @author Chinedu
 */
public class ImageInfo {
    public String boardImageType; //The board image type is the extension of the image (e.g jpg, png, gif)
    public String imagePath;
    public boolean imageFromWeb = false; //This variable is used to tell us if an image was gotten from the web. 
                                         //If the image was gotten from the web, then it has no path on the local machine yet. 
                                         //this is set to false if the image is saved on the local machine; once the image is saved on the local machine 
    public final String DEFAULT_IMAGE_LOCATION; //this is the location of the default image of the application
    
    public ImageInfo(){
          String location = System.getProperty("user.dir") + ("\\src\\CSPaintPackage\\defaultImage.jpg"); 
          this.imagePath = location; 
          this.DEFAULT_IMAGE_LOCATION = location;
    }
    
    public String getImageExtension(String pathOfImage){
       int start = pathOfImage.lastIndexOf(".");
       String imageType = pathOfImage.substring(start + 1);
       return imageType;
   }
}
