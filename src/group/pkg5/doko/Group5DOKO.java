package group.pkg5.doko;

//import controllor.SignupController; 
//import view.sign_up ; 
import controllor.LoginController;
 import  view.login;


/**
 * Main application runner starting with the Register page
 * @author nischal
 */
public class Group5DOKO {

    public static void main(String[] args) {
           
//            sign_up signupView = new sign_up();
//            

//            SignupController controller = new SignupController(signupView);
//            
//             controller.open();   


            login loginView = new login();
            
            // 2. Initialize the Signup Controller and pass the view to it
            LoginController controller = new LoginController(loginView);
            
            // 3. Open the registration window!
            controller.open(); 
    }
}