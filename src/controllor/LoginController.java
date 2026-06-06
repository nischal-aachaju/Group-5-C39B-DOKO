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

public class LoginController {
    
    private final login loginView;
    private final userDAO userDao;

    public LoginController(login loginView) {
        this.loginView = loginView;
        this.userDao = new userDAO();
        
        // Connect the login button in the View to the LoginListener logic below
        this.loginView.addLoginListener(new LoginListener());
        this.loginView.addRegisterListener(new SwitchToRegisterListener());
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

                        // new AdminController(new AdminView(), loggedInUser).open();
                        break;
                        
                    case "manager":
                        System.out.println("Routing to Manager Dashboard...");
//                         new ManagerController(new ManagerView(), loggedInUser).open();
                        break;
                        
                    case "employee":
                        System.out.println("Routing to Employee Dashboard...");
                        // new EmployeeController(new EmployeeView(), loggedInUser).open();
                        break;
                      
                        
                    case "user":
                        System.out.println("Routing to User Dashboard...");
                        // new UserController(new UserView(), loggedInUser).open();
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
            view.Sign_up_design signupView = new view.Sign_up_design();
            controllor.SignupController signupController = new controllor.SignupController(signupView);
            
            signupController.open();
        }
    }
}
