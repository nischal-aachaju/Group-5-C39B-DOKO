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
        this.employeeView.addMyProfileListener(new OpenEmployeeProfileListener());
        this.employeeView.addManageOrdersListener(new OpenManageOrdersListener());
        this.employeeView.addOrdersHistoryListener(new OpenOrdersHistoryListener());
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
    class OpenEmployeeProfileListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            // 1. Close the Employee Dashboard
            close(); 
            
            // 2. Create the Employee Profile View
            view.Employee_profile profileView = new view.Employee_profile();
            
            // 3. Pass it to your dedicated Employee Profile Controller
            controllor.Employee_ProfileController profileController = new controllor.Employee_ProfileController(profileView, currentUser);
            
            // 4. Open it
            profileController.open();
        }
    }
        class OpenOrdersHistoryListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            // 1. Close the Employee Dashboard
            close(); 
            
            // 2. Create the Employee Profile View
            view.Employee_Order_History profileView = new view.Employee_Order_History();
            
            // 3. Pass it to your dedicated Employee Profile Controller
            controllor.EmployeeOrderHistoryController profileController = new controllor.EmployeeOrderHistoryController(profileView, currentUser);
            
            // 4. Open it
            profileController.open();
        }
    }
            class OpenManageOrdersListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            // 1. Close the Employee Dashboard
            close(); 
            
            // 2. Create the Employee Profile View
            view.EmployeeOrderEdit profileView = new view.EmployeeOrderEdit();
            
            // 3. Pass it to your dedicated Employee Profile Controller
            controllor.EmployeeOrderEditController profileController = new controllor.EmployeeOrderEditController(profileView, currentUser);
            
            // 4. Open it
            profileController.open();
        }
    }
}