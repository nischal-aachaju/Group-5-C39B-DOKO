package controllor;

import Model.userData;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ManagerAssignOrderController {
    
    private final view.assignedorder assignView;
    private final userData currentUser;
    
    // We must track the searched employee so we know who to assign the order to!
    private Model.userData searchedEmployee = null;

    public ManagerAssignOrderController(view.assignedorder assignView, userData currentUser) {
        this.assignView = assignView;
        this.currentUser = currentUser;
        
        this.assignView.setUsernameLabel(currentUser.getUsername());
        this.assignView.setRoleLabel(currentUser.getRole());
        
        // 1. Reset the label by default
        this.assignView.setEmployeeNameLabel("---");
        
        // 2. Load the pending orders into the table immediately
        loadPendingOrders();
        
        // 3. Connect all buttons
        this.assignView.addSearchListener(new SearchEmployeeListener());
        this.assignView.addSubmitAssignmentListener(new SubmitAssignmentListener()); // The Bulk Assign Button!
        
        this.assignView.addDashboardListener(new OpenDashboardListener());
        this.assignView.addLogoutListener(new LogoutListener());
        this.assignView.addManageUserListener(new OpenManageUserListener());
        this.assignView.addWorkloadListener(new OpenWorkloadListener() );
        this.assignView.addActiveOrdersListener(new OpenActiveOrdersListener() );
        this.assignView.addManageOrdersListener(new OpenManageOrdersListener());
        this.assignView.addMyProfileListener(new OpenManagerProfileListener());
    }

    public void open() {
        this.assignView.setVisible(true);
        this.assignView.setLocationRelativeTo(null);
    }

    public void close() {
        this.assignView.dispose();
    }

    // =========================================================================
    // LOAD TABLE DATA
    // =========================================================================

    private void loadPendingOrders() {
        DAO.OrderDAO dao = new DAO.OrderDAO();
        java.sql.ResultSet rs = dao.getPendingOrders();
        
        // 1. Clear any existing data out of the table first
        assignView.clearTable();
        
        try {
            while (rs != null && rs.next()) {
                // 2. Grab the data from the database
                boolean isAssigned = false; // Checkbox starts unchecked
                String trackingId = rs.getString("tracking_id"); 
                String recipient = rs.getString("receiver_name"); 
                String contact = rs.getString("receiver_contact"); 
                String email = rs.getString("receiver_email"); 
                String address = rs.getString("receiver_location"); 
                String cdo = rs.getString("total_cost"); 
                
                // 3. Inject it directly into the NetBeans table!
                assignView.addTableRow(new Object[]{isAssigned, trackingId, recipient, contact, email, address, cdo});
            }
            
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================================================================
    // SEARCH LISTENER
    // =========================================================================

    class SearchEmployeeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchInput = assignView.getSearchId();
            
            if (searchInput.isEmpty()) {
                JOptionPane.showMessageDialog(assignView, "Please enter an Employee ID.");
                return;
            }
            
            try {
                int searchId = Integer.parseInt(searchInput);
                DAO.userDAO dao = new DAO.userDAO();
                searchedEmployee = dao.getUserById(searchId); 
                
                if (searchedEmployee != null && "Employee".equalsIgnoreCase(searchedEmployee.getRole())) {
                    assignView.setEmployeeNameLabel(searchedEmployee.getUsername());
                } else {
                    JOptionPane.showMessageDialog(assignView, "No valid Employee found with that ID.");
                    assignView.setEmployeeNameLabel("---");
                    searchedEmployee = null;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(assignView, "ID must be a number.");
            }
        }
    }

    // =========================================================================
    // BULK ASSIGN & EMAIL TRIGGER LOGIC
    // =========================================================================

    class SubmitAssignmentListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            // 1. Ensure an employee is selected
            if (searchedEmployee == null) {
                JOptionPane.showMessageDialog(assignView, "You must search and select an Employee first!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            javax.swing.table.DefaultTableModel model = assignView.getTableModel();
            DAO.OrderDAO dao = new DAO.OrderDAO();
            int assignedCount = 0; 
            
            // This list will hold the details of the customers we need to email
            java.util.List<String[]> emailQueue = new java.util.ArrayList<>();

            // 2. Loop through every row in the table
            for (int i = 0; i < model.getRowCount(); i++) {
                
                // Read the checkbox in Column 0
                boolean isChecked = (boolean) model.getValueAt(i, 0);

                // 3. If it is checked, assign it to the database!
                if (isChecked) {
                    String trackingId = (String) model.getValueAt(i, 1); // Tracking ID is in Column 1
                    String recipientName = (String) model.getValueAt(i, 2); // Name is in Column 2
                    String recipientEmail = (String) model.getValueAt(i, 4); // Email is in Column 4
                    
                    boolean success = dao.assignOrderToEmployee(searchedEmployee.getUserID(), trackingId);
                    
                    if (success) {
                        assignedCount++;
                        // Add the customer details to our email queue
                        emailQueue.add(new String[]{recipientEmail, recipientName, trackingId});
                    }
                }
            }

            // 4. Show success message, refresh table, and trigger emails
            if (assignedCount > 0) {
                JOptionPane.showMessageDialog(assignView, "Successfully assigned " + assignedCount + " order(s) to " + searchedEmployee.getUsername() + "!");
                loadPendingOrders(); // Reload the table so the assigned orders disappear
                
                // ====================================================================
                // PROCESS BULK EMAILS IN A SINGLE BACKGROUND THREAD
                // ====================================================================
                final String driverName = searchedEmployee.getUsername();
                final String driverPhone = searchedEmployee.getPhone();
                
                new Thread(() -> {
                    for (String[] customerData : emailQueue) {
                        try {
                            String email = customerData[0];
                            String name = customerData[1];
                            String tracking = customerData[2];
                            
                            // Using "intransit" as the new status when an order is assigned
                            controllor.EmailService.sendStatusUpdateEmail(email, name, tracking, "intransit", driverName, driverPhone);
                            
                            // Sleep for 1 second between emails to prevent SMTP spam blocks
                            Thread.sleep(1000); 
                            
                        } catch (Exception ex) {
                            System.out.println("Failed to send bulk email: " + ex.getMessage());
                        }
                    }
                }).start();
                // ====================================================================
                
            } else {
                JOptionPane.showMessageDialog(assignView, "No orders were selected. Please tick the boxes to assign orders.");
            }
        }
    }

    // =========================================================================
    // NAVIGATION LISTENERS
    // =========================================================================

    class OpenDashboardListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close(); 
            view.Manager_Dashboard dashboardView = new view.Manager_Dashboard();
            new controllor.ManagerController(dashboardView, currentUser).open();
        }
    }
    
    class LogoutListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close(); 
            view.login loginView = new view.login();
            new controllor.LoginController(loginView).open();
        }
    }
    
    class OpenManageUserListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            close(); 
            view.Useraccountmanagement manageUserView = new view.Useraccountmanagement();
            new controllor.ManageUserController(manageUserView, currentUser).open();
        }
    }
    
    class OpenWorkloadListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            close(); 
            view.Manager_Workload WorkloadView = new view.Manager_Workload();
            new controllor.ManagerWorkloadController(WorkloadView, currentUser).open();
        }
    }
    
    class OpenActiveOrdersListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            close(); 
            view.Manager_active_orders activeorderView = new view.Manager_active_orders();
            new controllor.ManagerActiveOrdersController(activeorderView, currentUser).open();
        }
    }
    
    class OpenManageOrdersListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            close(); 
            view.ManagerOrderEdit managerOrderEditView = new view.ManagerOrderEdit();
            new controllor.ManagerOrderEditController(managerOrderEditView, currentUser).open();
        }
    }
    
    class OpenManagerProfileListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            close(); 
            view.Manager_profileEdit profileView = new view.Manager_profileEdit();
            new controllor.Manager_ProfileController(profileView, currentUser).open();
        }
    }
}