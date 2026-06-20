package controllor;

import Model.userData;
import DAO.userDAO; // Ensure this matches your DAO file name exactly

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import view.ManageUser;

public class AdminManageUserController {
    
    private ManageUser manageView;
    private final userData currentUser;
    private int currentLoadedEmployeeId = -1; // Keeps track of the searched user

    public AdminManageUserController(ManageUser manageView, userData currentUser) {
        this.manageView = manageView;
        this.currentUser = currentUser;
        
        // 1. Sync Profile Tags
        this.manageView.setUsernameLabel(currentUser.getUsername());
        this.manageView.setRoleLabel(currentUser.getRole());
        
        // 2. Clear labels on startup
        clearLabels();
        
        // 3. Attach Core Logic Buttons
        this.manageView.addSearchListener(new SearchEmployeeListener());
        this.manageView.addEditListener(new EditEmployeeListener());
        this.manageView.addSaveListener(new SaveChangesListener());
        
        // 4. Attach Navigation
        this.manageView.addLogoutListener(new LogoutListener());
        this.manageView.addDashboardListener(new NavToDashboardListener());
        this.manageView.addWorkloadListener(new NavToWorkloadListener());
    }

    public void open() {
        if (this.manageView != null) {
            this.manageView.setVisible(true);
            this.manageView.setLocationRelativeTo(null);
        }
    }

    public void safeClose() {
        if (this.manageView != null) {
            this.manageView.setVisible(false);
            this.manageView.dispose();
            this.manageView = null; 
        }
    }

    private void clearLabels() {
        manageView.setEmployeeIdLabel("---");
        manageView.setEmployeeNameLabel("---");
        manageView.setEmployeeEmailLabel("---");
        manageView.setEmployeePhoneLabel("---");
    }

    // =========================================================================
    // SEARCH BUTTON LOGIC
    // =========================================================================
    class SearchEmployeeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String rawInput = manageView.getSearchIdInput();
            
            if (rawInput.isEmpty()) {
                JOptionPane.showMessageDialog(manageView, "Please enter an Employee ID.");
                return;
            }
            
            try {
                int searchId = Integer.parseInt(rawInput);
                userDAO userDao = new userDAO();
                String[] details = userDao.getEmployeeDetails(searchId); // [0]=Name, [1]=Email, [2]=Phone, [3]=Role
                
                if (details != null) {
                    currentLoadedEmployeeId = searchId;
                    manageView.setEmployeeIdLabel("#" + searchId);
                    manageView.setEmployeeNameLabel(details[0]);
                    manageView.setEmployeeEmailLabel(details[1]);
                    manageView.setEmployeePhoneLabel(details[2]);
                } else {
                    JOptionPane.showMessageDialog(manageView, "Employee not found!");
                    clearLabels();
                    currentLoadedEmployeeId = -1;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(manageView, "Invalid ID format. Numbers only.");
            }
        }
    }

    // =========================================================================
    // EDIT BUTTON LOGIC (Popup Method)
    // =========================================================================
    class EditEmployeeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentLoadedEmployeeId == -1) {
                JOptionPane.showMessageDialog(manageView, "Please search for an employee first.");
                return;
            }
            
            // Popups to safely get new data without needing JTextFields on the screen
            String currentEmail = manageView.getCurrentDisplayedEmail();
            String currentPhone = manageView.getCurrentDisplayedPhone();
            
            String newEmail = JOptionPane.showInputDialog(manageView, "Update Email:", currentEmail);
            if (newEmail != null && !newEmail.trim().isEmpty()) {
                manageView.setEmployeeEmailLabel(newEmail.trim());
            }
            
            String newPhone = JOptionPane.showInputDialog(manageView, "Update Phone Number:", currentPhone);
            if (newPhone != null && !newPhone.trim().isEmpty()) {
                manageView.setEmployeePhoneLabel(newPhone.trim());
            }
        }
    }

    // =========================================================================
    // SAVE BUTTON LOGIC
    // =========================================================================
    class SaveChangesListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentLoadedEmployeeId == -1) {
                JOptionPane.showMessageDialog(manageView, "No employee loaded to save.");
                return;
            }
            
            // Grab the data currently sitting in the labels
            String updatedEmail = manageView.getCurrentDisplayedEmail();
            String updatedPhone = manageView.getCurrentDisplayedPhone();
            
            userDAO userDao = new userDAO();
            if (userDao.updateEmployeeContact(currentLoadedEmployeeId, updatedEmail, updatedPhone)) {
                JOptionPane.showMessageDialog(manageView, "Employee details saved to database successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(manageView, "Database error: Could not save details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // NAVIGATION
    // =========================================================================
    class LogoutListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            safeClose(); 
            view.login loginView = new view.login();
            new controllor.LoginController(loginView).open();
        }
    }
    
    class NavToDashboardListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            safeClose();
            // view.Admin_Dashboard dashView = new view.Admin_Dashboard();
            // new controllor.AdminController(dashView, currentUser).open();
        }
    }

    class NavToWorkloadListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            safeClose();
            view.WorkLoad workView = new view.WorkLoad();
            new controllor.AdminWorkloadController(workView, currentUser).open();
        }
    }
}