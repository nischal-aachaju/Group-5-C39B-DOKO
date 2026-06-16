package controllor;

import Model.userData;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ManageUserController {
    
    // Make sure this matches your exact JFrame UI file name!
    private final view.Useraccountmanagement manageView;
    private final userData currentUser;
    
    // We keep track of the currently searched user
    private Model.userData searchedUser = null; 

    public ManageUserController(view.Useraccountmanagement manageView, userData currentUser) {
        this.manageView = manageView;
        this.currentUser = currentUser;
        
        // 1. Set top bar labels
        this.manageView.setUsernameLabel(currentUser.getUsername());
        this.manageView.setRoleLabel(currentUser.getRole());
        
        // 2. Clear the screen by default
        this.manageView.setEmployeeDetails("---", "---", "---", "---");
        
        // 3. Connect the action buttons
        this.manageView.addSearchListener(new SearchUserListener());
        this.manageView.addEditListener(new EditUserListener());
        this.manageView.addSaveListener(new SaveUserListener());
        
        // 4. Connect Navigation
        this.manageView.addDashboardListener(new OpenDashboardListener());
    }

    public void open() {
        this.manageView.setVisible(true);
        this.manageView.setLocationRelativeTo(null); // Centers window
    }

    public void close() {
        this.manageView.dispose();
    }

    // =========================================================================
    // SEARCH LOGIC
    // =========================================================================

    class SearchUserListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchInput = manageView.getSearchId();
            
            if (searchInput.isEmpty()) {
                JOptionPane.showMessageDialog(manageView, "Please enter a User ID to search.");
                return;
            }
            
            try {
                int searchId = Integer.parseInt(searchInput);
                DAO.userDAO dao = new DAO.userDAO();
                searchedUser = dao.getUserById(searchId); // Using the DAO method we built last time!
                
                if (searchedUser != null) {
                    // Populate the UI with the found user's data
                    manageView.setEmployeeDetails(
                        String.valueOf(searchedUser.getUserID()),
                        searchedUser.getUsername(),
                        searchedUser.getEmail(),
                        searchedUser.getPhone()
                    );
                } else {
                    JOptionPane.showMessageDialog(manageView, "No user found with ID: " + searchId);
                    manageView.setEmployeeDetails("---", "---", "---", "---");
                    searchedUser = null;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(manageView, "User ID must be a valid number.");
            }
        }
    }

    // =========================================================================
    // EDIT LOGIC (Pop-up Boxes)
    // =========================================================================

    class EditUserListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (searchedUser == null) {
                JOptionPane.showMessageDialog(manageView, "Please search for a user first.");
                return;
            }
            
            // 1. Prompt for new Email
            String currentEmail = manageView.getDisplayedEmail();
            String newEmail = JOptionPane.showInputDialog(manageView, "Edit Email:", currentEmail);
            
            // 2. Prompt for new Phone
            String currentPhone = manageView.getDisplayedPhone();
            String newPhone = JOptionPane.showInputDialog(manageView, "Edit Phone Number:", currentPhone);
            
            // 3. Temporarily update the screen labels
            if (newEmail != null && newPhone != null) {
                manageView.setEmployeeDetails(
                    String.valueOf(searchedUser.getUserID()),
                    searchedUser.getUsername(),
                    newEmail, 
                    newPhone  
                );
            }
        }
    }

    // =========================================================================
    // SAVE LOGIC
    // =========================================================================

    class SaveUserListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (searchedUser == null) {
                JOptionPane.showMessageDialog(manageView, "No user data to save.");
                return;
            }
            
            // Grab the text currently sitting in the UI labels
            String emailToSave = manageView.getDisplayedEmail();
            String phoneToSave = manageView.getDisplayedPhone();
            
            DAO.userDAO dao = new DAO.userDAO();
            boolean success = dao.updateUserEmailAndPhone(searchedUser.getUserID(), emailToSave, phoneToSave);
            
            if (success) {
                JOptionPane.showMessageDialog(manageView, "User account successfully updated in the database!");
            } else {
                JOptionPane.showMessageDialog(manageView, "Failed to update database.");
            }
        }
    }
    
    // =========================================================================
    // NAVIGATION
    // =========================================================================

    class OpenDashboardListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close(); 
            view.Manager_Dashboard dashboardView = new view.Manager_Dashboard();
            new controllor.ManagerController(dashboardView, currentUser).open();
        }
    }
}