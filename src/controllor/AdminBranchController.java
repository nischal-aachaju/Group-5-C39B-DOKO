package controllor;

import Model.userData;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class AdminBranchController {

    private final view.adminBranchNetworks view; 
    private final userData currentUser;
    
    // Tracks what the Admin is currently trying to do
    private String currentMode = "VIEW"; 
    private int currentBranchId = -1; // -1 means no branch selected

    public AdminBranchController(view.adminBranchNetworks view, userData currentUser) {
        this.view = view;
        this.currentUser = currentUser;
        
        // 1. Setup Identity and lock form
        this.view.setTopBar(currentUser.getUsername(), currentUser.getRole());
        this.view.setFormState("VIEW");
        
        // 2. Connect Actions
        this.view.addSearchListener(new SearchBranchListener());
        this.view.addEditListener(new EditBranchListener());
        this.view.addCreateListener(new CreateBranchListener());
        this.view.addSaveListener(new SaveBranchListener());
        
        // 3. Connect Navigators
        this.view.addDashboardListener(new DashboardNavListener());
        this.view.addLogoutListener(new LogoutNavListener());
    }

    public void open() {
        this.view.setVisible(true);
        this.view.setLocationRelativeTo(null);
    }

    public void close() {
        Window window = SwingUtilities.getWindowAncestor(this.view);
        if (window != null) {
            window.dispose();
        } else {
            this.view.setVisible(false);
        }
    }

    // =========================================================================
    // BRANCH ACTIONS
    // =========================================================================

    class SearchBranchListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchInput = view.getSearchInput();
            
            if (searchInput.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Please enter a Branch ID to search.");
                return;
            }
            
            try {
                // Assuming they type "#250455" or just "250455"
                int branchId = Integer.parseInt(searchInput.replace("#", ""));
                
                DAO.BranchDAO dao = new DAO.BranchDAO();
                ResultSet rs = dao.getBranchDetailsWithStats(branchId);
                
                if (rs != null && rs.next()) {
                    currentBranchId = branchId;
                    currentMode = "VIEW";
                    
                    view.setBranchStats(
                        String.valueOf(rs.getInt("total_orders")), 
                        String.valueOf(rs.getInt("total_employees"))
                    );
                    
                    view.setBranchDetails(
                        "#" + rs.getInt("branch_id"), 
                        rs.getString("branch_name"), 
                        rs.getString("branch_email"), 
                        rs.getString("branch_phone"), 
                        rs.getString("branch_address")
                    );
                    
                    view.setFormState("VIEW"); // Locks the form
                    
                } else {
                    JOptionPane.showMessageDialog(view, "No branch found with ID: " + branchId);
                    currentBranchId = -1;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(view, "Branch ID must be a number.");
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    class EditBranchListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentBranchId != -1) {
                currentMode = "EDIT";
                view.setFormState("EDIT"); // Unlocks the text fields
            } else {
                JOptionPane.showMessageDialog(view, "Please search for a branch to edit first.");
            }
        }
    }

    class CreateBranchListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            currentMode = "CREATE";
            currentBranchId = -1; // Reset tracking ID since it's brand new
            view.setFormState("CREATE"); // Clears the form and unlocks fields
        }
    }

    class SaveBranchListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = view.getBranchNameInput();
            String email = view.getBranchEmailInput();
            String phone = view.getBranchPhoneInput();
            String address = view.getBranchAddressInput();
            
            // Basic Validation
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(view, "All fields must be filled out.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            DAO.BranchDAO dao = new DAO.BranchDAO();
            
            if (currentMode.equals("CREATE")) {
                // Save a brand new branch
                if (dao.createBranch(name, email, phone, address)) {
                    JOptionPane.showMessageDialog(view, "New branch created successfully!");
                    view.setFormState("VIEW"); // Lock form
                    currentMode = "VIEW";
                } else {
                    JOptionPane.showMessageDialog(view, "Database Error. Failed to create branch.");
                }
                
            } else if (currentMode.equals("EDIT") && currentBranchId != -1) {
                // Update an existing branch
                if (dao.updateBranch(currentBranchId, name, email, phone, address)) {
                    JOptionPane.showMessageDialog(view, "Branch successfully updated!");
                    view.setFormState("VIEW"); // Lock form
                    currentMode = "VIEW";
                } else {
                    JOptionPane.showMessageDialog(view, "Database Error. Failed to update branch.");
                }
            }
        }
    }

    // =========================================================================
    // NAVIGATION
    // =========================================================================

    class DashboardNavListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
            view.Admin_Dashboard dashView = new view.Admin_Dashboard();
            new controllor.AdminController(dashView, currentUser).open();
        }
    }

    class LogoutNavListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
            new controllor.LoginController(new view.login()).open();
        }
    }
}