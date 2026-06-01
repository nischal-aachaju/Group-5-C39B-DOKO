/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
//package group.pkg5.doko;
package group.pkg5.doko;
import controllor.SignupController;
import javax.swing.JFrame;
import view.Sign_up_design;
/**
 *
 * @author nischal
 */
public class Group5DOKO {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         // TODO code application logic here
//        Sign_up_design signupForm = new  Sign_up_design();                 
//        SignupController controller= new SignupController(signupForm);  
//        controller.open();  

// 1. Create the main window (JFrame)
        JFrame frame = new JFrame("DOKO App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

        // 2. Create your view (JPanel)
        Sign_up_design signupForm = new Sign_up_design();
        
        // 3. Add the panel to the window
        frame.add(signupForm);
        
        // 4. Size the window and center it
        frame.pack();
        frame.setLocationRelativeTo(null); 

        // 5. Initialize your controller
        SignupController controller = new SignupController(signupForm);
        controller.open(); 
        // 6. Make the window visible!
        frame.setVisible(true);
}
}