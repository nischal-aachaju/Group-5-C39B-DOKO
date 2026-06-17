/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nischal
 */
package controllor;

import Model.userData;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
//import view.assignedorder;

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
        
        // 3. Connect the Search button
        this.assignView.addSearchListener(new SearchEmployeeListener());
        this.assignView.addDashboardListener(new OpenDashboardListener());
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

    // =========================================================================
    // LOAD TABLE DATA (DIRECT INJECTION)
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
            
        // Put this inside your ManagerAssignOrderController constructor
    this.assignView.addSubmitAssignmentListener(new SubmitAssignmentListener());
            
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
                searchedEmployee = dao.getUserById(searchId); // Reusing your existing method!
                
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
    // CHECKBOX TICK LISTENER (Triggers instantly when clicked)
    // =========================================================================

    class CheckboxTickListener implements TableModelListener {
        @Override
        public void tableChanged(TableModelEvent e) {
            // Check if the change happened in the Checkbox column (Column 2)
            if (e.getColumn() == 2) {
                int row = e.getFirstRow();
                javax.swing.table.DefaultTableModel model = assignView.getTableModel();
                
                boolean isChecked = (boolean) model.getValueAt(row, 2);
                String trackingId = (String) model.getValueAt(row, 0);

                if (isChecked) {
                    // Validation: Did they search for an employee first?
                    if (searchedEmployee == null) {
                        JOptionPane.showMessageDialog(assignView, "You must search and select an Employee first!", "Assignment Error", JOptionPane.ERROR_MESSAGE);
                        model.setValueAt(false, row, 2); // Uncheck the box automatically
                        return;
                    }
                    
                    // Assign the order!
                    DAO.OrderDAO dao = new DAO.OrderDAO();
                    boolean success = dao.assignOrderToEmployee(searchedEmployee.getUserID(), trackingId);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(assignView, "Order " + trackingId + " successfully assigned to " + searchedEmployee.getUsername() + "!");
                        // Reload table so the assigned order disappears from the pending list
                        loadPendingOrders(); 
                    } else {
                        JOptionPane.showMessageDialog(assignView, "Database error. Failed to assign order.");
                        model.setValueAt(false, row, 2); // Uncheck
                    }
                }
            }
        }
    }
        class OpenDashboardListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 1. Close the Manager Profile screen
            close(); 
            
            // 2. Route directly back to the Manager Dashboard
            view.Manager_Dashboard dashboardView = new view.Manager_Dashboard();
            
            // Assuming your controller for the manager dashboard is named ManagerController
            new controllor.ManagerController(dashboardView, currentUser).open();
        }
    }
        // =========================================================================
    // BULK ASSIGN BUTTON LOGIC
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
            int assignedCount = 0; // Keep track of how many we assign

            // 2. Loop through every row in the table
            for (int i = 0; i < model.getRowCount(); i++) {
                
                // Read the checkbox in Column 0
                boolean isChecked = (boolean) model.getValueAt(i, 0);

                // 3. If it is checked, assign it to the database!
                if (isChecked) {
                    String trackingId = (String) model.getValueAt(i, 1); // Tracking ID is in Column 1
                    
                    // Re-use the exact DAO method we wrote earlier
                    boolean success = dao.assignOrderToEmployee(searchedEmployee.getUserID(), trackingId);
                    
                    if (success) {
                        assignedCount++;
                    }
                }
            }

            // 4. Show success message and refresh the table
            if (assignedCount > 0) {
                JOptionPane.showMessageDialog(assignView, "Successfully assigned " + assignedCount + " order(s) to " + searchedEmployee.getUsername() + "!");
                loadPendingOrders(); // Reload the table so the assigned orders disappear
            } else {
                JOptionPane.showMessageDialog(assignView, "No orders were selected. Please tick the boxes to assign orders.");
            }
        }
    }
}