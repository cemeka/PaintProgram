/**The GUI class is at the center of the Paint Application. It creates and assembles components of the User Interface; it registers the event generators with the appropriate listener
   and serves as a platform through which a client can interact with the program.*/



package CSPaintPackage;


import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.*;

public class GUI {
  JFrame frame = new JFrame();
  DrawingBoard drawingBoard = new DrawingBoard();  //A DrawingBoard is a JPanel (as it extends JPanel) which is specially prepared for drawing for this Paint application 
  PanelEditor guiEditor;                           //A PanelEditor is the editor of the GUI. It has the power to make changes to the
                                                   // drawingBoard and other parts of the GUI.
  
  JMenuBar menuBar = new JMenuBar();
  
  JMenu fileMenu = new JMenu("File Menu");
  ArrayList<JMenuItem> fileMenuOptions = new ArrayList<>(); //An arraylist of all fileMenuOptions. This is more efficient and manageable than having each fileMenu option have a 
                                                            //reference variable. The fileMenu items can be accessed by using list. 
  
  JMenu editMenu = new JMenu("Edit Menu");             
  ArrayList<JMenuItem> editMenuOptions = new ArrayList<>(); //Same thing with fileMenuOptions. The only difference is that this list stores editMenu list items, such as a menu item 
                                                            //for drawing a line. 
  
  java.awt.Font generalFont = new java.awt.Font("sans-serif", java.awt.Font.BOLD, 14); //font used for file & edit menu options
  
  
  public GUI(){
      this.finalizeAllComponents(); //add all components to frame. 
      
  }
 
 /**This method prepares the fileMenu components. The arrayList is filled with objects in a loop (the amount of objects to be created has been predetermined by programmer).
  The objects are then given unique names that can be used to identify them. After this, they are all added into the fileMenuOptions list (an arrayList) with the aid of another
  loop. This helps to reduce probability of error & code to be typed, as well as make code more efficient. Also, it limits the amount of instance variables floating around in a 
  program by making only the list of objects an instance variable and then filling that list with local menuItem objects which makes them accessible outside their method of origin*/
  private void prepareFileMenuComponents(){
      for(int i=0; i < 6; i++){
          JMenuItem newItem = new JMenuItem();
          newItem.setFont(generalFont);
          fileMenuOptions.add(newItem);
          newItem = null;
      }
      
      fileMenuOptions.get(0).setText("New File");
      fileMenuOptions.get(1).setText("Open Local File");
      fileMenuOptions.get(2).setText("Open File from Web");
      fileMenuOptions.get(3).setText("Save File");
      fileMenuOptions.get(4).setText("Save File as...");
      fileMenuOptions.get(5).setText("Quit");
      
      for (JMenuItem fileMenuOption : fileMenuOptions) {
          fileMenu.add(fileMenuOption);
      }
  }
  
  /**This method is identical to the prepareFileMenuComponents method. The main difference is that it prepares  editMenu components.*/
  private void prepareEditMenuComponents(){
      for(int i=0; i<8; i++){
          JMenuItem newItem = new JMenuItem();
          newItem.setFont(generalFont);
          editMenuOptions.add(newItem);
          newItem = null;
      }
      
      editMenuOptions.get(0).setText("Draw Straight Line");
      editMenuOptions.get(1).setText("Draw with Pencil");
      editMenuOptions.get(2).setText("Clean with Eraser");
      editMenuOptions.get(3).setText("Set Drawing Color");
      editMenuOptions.get(4).setText("Fill Picture with Paint");
      editMenuOptions.get(5).setText("Paint Background");
      editMenuOptions.get(6).setText("Select tool");
      editMenuOptions.get(7).setText("Move Cropped Image");
      
      for(JMenuItem editMenuOption: editMenuOptions){
          editMenu.add(editMenuOption);
      }
      
  }
  
  /**Because all fileMenu objects are stored in a list, they can be iterated through very quickly and assigned listeners. This happens when this method is called during the course
   of preparing the GUI*/
  private void prepareFileMenuListeners(){
       for (JMenuItem fileMenuOption : fileMenuOptions) {
             fileMenuOption.addActionListener(guiEditor);
      }
  }
  
  /**Because all editMenu objects are stored in a list, they can be iterated through very quickly and assigned listeners.*/
  private void prepareEditMenuListeners(){   
        for (JMenuItem editMenuOption : editMenuOptions) {
             editMenuOption.addActionListener(guiEditor);
      }
  }
  
  /**This is the method that finally launches program when GUI is ready.*/
  public void launchProgram(){
      frame.getContentPane().add(menuBar, BorderLayout.NORTH);
      frame.setSize(600, 600);   //initial size set. The size of the frame can be modified at any point in time by the user. 
      frame.setTitle("Paint Program"); 
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true); //this makes the frame visible
  }
  
  /**finalizeComponents is where the other GUI methods are synthesized to form a useful product*/
  private void finalizeAllComponents(){
      guiEditor = new PanelEditor(this); //BE VERY CAREFUL. THIS MUST BE THE FIRST LINE. It is used in prepareFileMenuListeners
                                         //method. do NOT move this line of code anywhere else!
      
      this.drawingBoard.addMouseListener(guiEditor);  //the drawingBoard has a mouseListener for itself, to observe and response to mouse events under it. 
      this.drawingBoard.addMouseMotionListener(guiEditor); //the drawingBoard has a mouse motion listener attached as well to handle mouse motion. 
      
      this.prepareFileMenuComponents();  
      this.prepareFileMenuListeners();
   
      fileMenu.setFont(new Font("serif", Font.BOLD, 17)); //set font of fileMenu. This is just an aesthetic feature. 
      menuBar.add(fileMenu);
  
      //repeat process for edit menu
      
      this.prepareEditMenuComponents();
      this.prepareEditMenuListeners();
      
      editMenu.setFont(new Font("serif", Font.BOLD, 17));
      menuBar.add(editMenu);
      
      //done with setting up file and edit menu now. all listeners prepared and attached.  
      //should prepare drawingBoard before adding it to frame. 
     
     // drawingBoard.provideDefaultImage();  //provides a default image for use. 
      
      frame.getContentPane().add(drawingBoard, BorderLayout.WEST); //the drawingBoard is added to the west portion of the frame. 
      
     
  }
  
  
}