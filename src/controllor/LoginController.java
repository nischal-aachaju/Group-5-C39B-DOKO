/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllor;

import DAO.userDAO;
import Model.userData;
import view.login;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import view.Sender_Dashboard;

public class LoginController {
    
    private final login loginView;
    private final userDAO userDao;

    public LoginController(login loginView) {
        this.loginView = loginView;
        this.userDao = new userDAO();
        
        this.loginView.addOrderSearchListener(new SearchOrderListener());
        
        // Connect the login button in the View to the LoginListener logic below
        this.loginView.addLoginListener(new LoginListener());
        this.loginView.addRegisterListener(new SwitchToRegisterListener());
        this.loginView.addForgotPassword(new  OpenForgotPassword());
    }

    // Method to open the login window
    public void open() {
        this.loginView.setVisible(true);
    }

    // Method to close the login window
    public void close() {
        Window window = SwingUtilities.getWindowAncestor(this.loginView);
        if (window != null) {
            window.dispose();
        } else {
            this.loginView.setVisible(false);
        }
    }

    // --- Action Listener for the Login Button ---
    class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            // 1. Get text from the view
            String username = loginView.getUsername();
            String password = loginView.getPassword();

            // 2. Validate empty fields
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginView, "Please enter both Username and Password!");
                return;
            }

            // 3. Get the user object back from the DAO
            userData loggedInUser = userDao.loginUser(username, password);

            // 4. Check if login was successful
            if (loggedInUser != null) {
                JOptionPane.showMessageDialog(loginView, "Login Successful! Welcome, " + loggedInUser.getUsername());
                
                // Close the login window
                close(); 
                
                // 5. Route to the correct dashboard based on role
                String role = loggedInUser.getRole().toLowerCase();
                
                switch (role) {
                case "admin":
                        System.out.println("Routing to Admin Dashboard...");
                        
                        // 1. Create the View
                        view.Admin_Dashboard adminView = new view.Admin_Dashboard();
                        
                        // 2. Pass the view AND the loggedInUser object to the new controller
                        controllor.AdminController adminController = new controllor.AdminController(adminView, loggedInUser);
                        
                        // 3. Open the dashboard!
                        adminController.open();
                        break;
                        
                    case "manager":
                        System.out.println("Routing to Manager Dashboard...");

                        // Create the View
                        view.Manager_Dashboard managerView = new view.Manager_Dashboard();
                        
                        // Pass the view AND the loggedInUser object to the new controller
                        controllor.ManagerController managerController = new controllor.ManagerController(managerView, loggedInUser);

                        // Open the dashboard!
                        managerController.open();
                        break;


                        
                    case "employee":
                        System.out.println("Routing to Employee Dashboard...");
                        
                        // 1. Create the View
                        view.Employee_Dashboard employeeView = new view.Employee_Dashboard();
                        
                        // 2. Pass the view AND the loggedInUser object to the new controller
                        controllor.EmployeeController employeeController = new controllor.EmployeeController(employeeView, loggedInUser);
                        
                        // 3. Open the dashboard!
                        employeeController.open();
                        break;
                      
                        
                    case "user":

                        System.out.println("Routing to User Dashboard...");
                        
                        // 1. Create the View
                        view.Sender_Dashboard userView = new view.Sender_Dashboard();
                        
                        // 2. Pass the view AND the loggedInUser object to the new controller
                        controllor.UserController userController = new controllor.UserController(userView, loggedInUser);
                        
                        // 3. Open the dashboard!
                        userController.open();
                        break;
                        
                    default:
                        JOptionPane.showMessageDialog(loginView, "Error: Unknown role assigned to this user.");
                        break;
                }
                
            } else {
                // Login Failed
                JOptionPane.showMessageDialog(loginView, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
            
        }
    }
    
    // Add this inside LoginController.java
    class SwitchToRegisterListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            // 1. Close the current Login window
            close(); 
            
            // 2. Initialize and open the Sign-Up window
            view.sign_up signupView = new view.sign_up();
            controllor.SignupController signupController = new controllor.SignupController(signupView);
            
            signupController.open();
        }
    }
    class OpenForgotPassword implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            // 1. Close the current Login window
            close(); 
            
            // 2. Initialize and open the Sign-Up window
            view.forgotPassword forgotPass = new view.forgotPassword();
            controllor.ForgotPasswordController signupController = new controllor.ForgotPasswordController(forgotPass);
            
            signupController.open();
        }
    }
    // =========================================================================
    // PUBLIC ORDER TRACKING LOGIC
    // =========================================================================

    class SearchOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String trackingId = loginView.getTrackingInput();
            
            if (trackingId.isEmpty()) {
                JOptionPane.showMessageDialog(loginView, "Please enter a Tracking ID to search.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            DAO.OrderDAO dao = new DAO.OrderDAO();
            java.sql.ResultSet rs = dao.getPublicTrackingDetails(trackingId);
            
            try {
                if (rs != null && rs.next()) {
                    // Extract data from the database
                    String tId = rs.getString("tracking_id");
                    String name = rs.getString("receiver_name");
                    String email = rs.getString("receiver_email");
                    String sender = rs.getString("street");
                    String receiver = rs.getString("receiver_location");
                    String cost = String.valueOf(rs.getDouble("total_cost"));
                    String orderStatus = rs.getString("status").toUpperCase(); // Uppercase for UI emphasis
                    
                    // Inject into the locked UI text fields
                    loginView.setTrackingResult(tId, name, email, sender, receiver, cost, orderStatus);
                } else {
                    JOptionPane.showMessageDialog(loginView, "No order found with Tracking ID: " + trackingId, "Not Found", JOptionPane.ERROR_MESSAGE);
                    loginView.lockTrackingDisplayFields(); // Reset fields to empty
                }
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(loginView, "Database error while searching.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
