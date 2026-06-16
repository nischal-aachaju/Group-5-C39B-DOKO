/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllor;

import Model.userData;
import view.Admin_Dashboard;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;

public class AdminController {
    
    private final Admin_Dashboard adminView;
    private final userData currentUser;

    public AdminController(Admin_Dashboard adminView, userData currentUser) {
        this.adminView = adminView;
        this.currentUser = currentUser;
        
        // 1. Instantly set the Name and Role labels on the screen
        this.adminView.setUsernameLabel(currentUser.getUsername());
        
         this.adminView.setRoleLabel(currentUser.getRole());
        
        // 2. Connect the logout button

         this.adminView.addLogoutListener(new LogoutListener());
       
    }

    public void open() {
        this.adminView.setVisible(true);
    }

    public void close() {
        Window window = SwingUtilities.getWindowAncestor(this.adminView);
        if (window != null) {
            window.dispose();
        } else {
            this.adminView.setVisible(false);
        }
    }

    // --- Action Listener for Logout Button ---
    class LogoutListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            // 1. Close the Admin dashboard
            close(); 
            
            // 2. Re-open the Login window safely
            view.login loginView = new view.login();
            controllor.LoginController loginController = new controllor.LoginController(loginView);
            loginController.open();
        }
    }
}