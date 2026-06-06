package group.pkg5.doko;

import controllor.SignupController; // Import the SignupController
import view.sign_up    ;     // Import the Signup View


/**
 * Main application runner starting with the Register page
 * @author nischal
 */
public class Group5DOKO {

    public static void main(String[] args) {
           
            // 1. Create the Sign-Up/Register View first
            sign_up signupView = new sign_up();
            
            // 2. Initialize the Signup Controller and pass the view to it
            SignupController controller = new SignupController(signupView);
            
            // 3. Open the registration window!
            controller.open();       
    }
}