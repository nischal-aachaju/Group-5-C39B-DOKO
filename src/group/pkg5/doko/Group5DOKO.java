package group.pkg5.doko;

import controllor.LoginController; // Capital L here
import view.login;
import javax.swing.SwingUtilities;

/**
 * Main application runner
 * @author nischal
 */
public class Group5DOKO {

    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(() -> {
            
            // 1. Create the Login View
            login loginView = new login();
            
            // 2. Initialize the Login Controller and pass the view to it
            LoginController controller = new LoginController(loginView); // Capital L here
            
            // 3. Open the login window!
            controller.open();
            
        });
    }
}