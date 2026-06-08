/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllor;

import Model.userData;
import view.Employee_Dashboard; 

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;

public class EmployeeController {
    
    private final Employee_Dashboard employeeView;
    private final userData currentUser;

    public EmployeeController(Employee_Dashboard employeeView, userData currentUser) {
        this.employeeView = employeeView;
        this.currentUser = currentUser;
        
        // 1. Instantly set the Name and Role labels on the screen
        this.employeeView.setUsernameLabel(currentUser.getUsername());
        this.employeeView.setRoleLabel(currentUser.getRole());
        
        // 2. Connect the logout button
        this.employeeView.addLogoutListener(new LogoutListener());
    }

    public void open() {
        this.employeeView.setVisible(true);
    }

    public void close() {
        Window window = SwingUtilities.getWindowAncestor(this.employeeView);
        if (window != null) {
            window.dispose();
        } else {
            this.employeeView.setVisible(false);
        }
    }

    // --- Action Listener for Logout Button ---
    class LogoutListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            // 1. Close the Employee dashboard
            close(); 
            
            // 2. Re-open the Login window safely
            view.login loginView = new view.login();
            controllor.LoginController loginController = new controllor.LoginController(loginView);
            loginController.open();
        }
    }
}