/**This is the main class. It has the main() method, which is where execution of the program starts.
 This class in itself does not do much. Its primary duty is to serve as a first point of entry for the Java Virtual Machine
 (JVM).*/

package CSPaintPackage;




public class CSPaint {
    
    /**This is the main thread. Code execution starts here*/
    public static void main(String[] args) {
        GUI gui = new GUI(); //A GUI object is created here
        gui.launchProgram(); //launch the program. GUI is set up
    }
    
}