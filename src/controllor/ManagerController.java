/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package controllor;

import Model.userData;
import view.Manager_Dashboard;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;



public class ManagerController {
    
    private final Manager_Dashboard managerView;
    private final userData currentUser;

    // Notice we pass the loggedInUser data into this constructor!
    public ManagerController(Manager_Dashboard managerView, userData currentUser) {
        this.managerView = managerView;
        this.currentUser = currentUser;
        
        // 1. Instantly set the Name and Role labels on the screen
        this.managerView.setUsernameLabel(currentUser.getUsername());
        this.managerView.setRoleLabel(currentUser.getRole());
        
        // 2. Connect the navigation buttons
        this.managerView.addLogoutListener(new LogoutListener());
        this.managerView.addMyProfileListener(new OpenManagerProfileListener());
        this.managerView.addManageUserListener(new OpenManageUserListener());
        this.managerView.addManageOrdersListener(new OpenManageOrdersListener());
        this.managerView.addAssiggnedOrdersListener(new OpenAssiggnedrdersListener() );
    }

    public void open() {
        this.managerView.setVisible(true);
    }

    public void close() {
        Window window = SwingUtilities.getWindowAncestor(this.managerView);
        if (window != null) {
            window.dispose();
        } else {
            this.managerView.setVisible(false);
        }
    }

    // --- Action Listener for Logout Button ---
    class LogoutListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 1. Close the manager dashboard
            close(); 
            
            // 2. Re-open the Login window safely
            view.login loginView = new view.login();
            controllor.LoginController loginController = new controllor.LoginController(loginView);
            loginController.open();
        }
    }
    
    // =========================================================================
    // NAVIGATION LISTENERS
    // =========================================================================

    class OpenManagerProfileListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            // 1. Close the current Manager Dashboard
            close(); 
            
            // 2. Create the exact Manager Profile View
            view.Manager_profileEdit profileView = new view.Manager_profileEdit();
            
            // 3. Pass it entirely to your dedicated Manager Profile Controller
            controllor.Manager_ProfileController profileController = new controllor.Manager_ProfileController(profileView, currentUser);
            
            // 4. Open the profile page!
            profileController.open();
        }
    }

    class OpenManageUserListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            // 1. Close the current Manager Dashboard
            close(); 
            
            // 2. Create the User Management View
            view.Useraccountmanagement manageUserView = new view.Useraccountmanagement();
            
            // 3. Pass it entirely to your dedicated Manage User Controller
            controllor.ManageUserController manageUserController = new controllor.ManageUserController(manageUserView, currentUser);
            
            // 4. Open the User Management page!
            manageUserController.open();
        }
    }

    class OpenManageOrdersListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            // 1. Close the current Manager Dashboard
            close(); 
            
            // 2. Create the Manager Order Edit View
            view.ManagerOrderEdit managerOrderEditView = new view.ManagerOrderEdit();
            
            // 3. Fixed spelling from "controller" to "controllor" to perfectly match your package structure
            controllor.ManagerOrderEditController managerOrderEditController = new controllor.ManagerOrderEditController(managerOrderEditView, currentUser);
            
            // 4. Open the Manager Order Edit page!
            managerOrderEditController.open();
        }
    }
    class OpenAssiggnedrdersListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            // 1. Close the current Manager Dashboard
            close(); 
            
            // 2. Create the Manager Order Edit View
            view.assignedorder assignedorderView = new view.assignedorder();
            
            // 3. Fixed spelling from "controller" to "controllor" to perfectly match your package structure
            controllor.ManagerAssignOrderController managerAssignOrderController = new controllor.ManagerAssignOrderController(assignedorderView, currentUser);
            
            // 4. Open the Manager Order Edit page!
            managerAssignOrderController.open();
        }
    }
}