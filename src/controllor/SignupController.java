package controllor;

import DAO.userDAO;
import Model.userData;
import java.awt.Window;
import view.sign_up;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class SignupController {
    private final userDAO userDao = new userDAO();
    private final sign_up userView;
    
    // 1. ADD THIS MEMORY VARIABLE
    private String storedPassword = ""; 

public SignupController(sign_up userView) {
        this.userView = userView;
        userView.addAddUserListener(new AddUserListener());
        userView.addVerifyListener(new VerifyListener());
        // Add this line where you connect your other register/login routing buttons
        this.userView.addOrderSearchListener(new SearchOrderListener());
        
        // ADD THIS LINE: Connect the login button
        userView.addLoginListener(new SwitchToLoginListener());
    }

    public void open() {
        this.userView.setVisible(true);
    }

    public void close() {
        Window window = SwingUtilities.getWindowAncestor(this.userView);
        if (window != null) {
            window.dispose();
        } else {
            this.userView.setVisible(false);
        }
    }
   
    class VerifyListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = userView.getEmailField().getText();
            String name = userView.getNameField().getText(); 
            
            if(name.isEmpty() || name.equals("Full Name *")) {
                JOptionPane.showMessageDialog(userView, "Please enter your Full Name first!");
                return;
            }
            if(email.isEmpty() || !email.contains("@")) {
                JOptionPane.showMessageDialog(userView, "Please enter a valid email address!");
                return;
            }
            
            String newPassword = Emailhelper.generatePassword();
            
            // 2. SAVE THE PASSWORD TO OUR MEMORY VARIABLE
            storedPassword = newPassword; 
            
            boolean success = Emailhelper.sendEmail(email, newPassword, name);
            
            if(success) {
                JOptionPane.showMessageDialog(userView, "Password sent to " + email + ". Please check your inbox.");
            } else {
                JOptionPane.showMessageDialog(userView, "Failed to send email. Check your console for errors.");
            }
        }
    }
 class AddUserListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // 1. Terms and conditions check
                if (!userView.getTermsAndCondition().isSelected()) {
                    JOptionPane.showMessageDialog(userView, "You must agree to the Terms and Conditions to register!");
                    return; 
                }

                String enteredPassword = userView.getPasswordField().getText();
                String enteredAddress = userView.getAddressField().getText().trim();
                // 2. Check if verify was clicked and passwords match
                if (storedPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(userView, "Please click Verify to get your password first!");
                    return;
                }
                if (!enteredPassword.equals(storedPassword)) {
                    JOptionPane.showMessageDialog(userView, "Incorrect Password! Please enter the exact password sent to your email.");
                    return; 
                }
                if (enteredAddress.isEmpty() || enteredAddress.equals("Address *") ||enteredAddress.equals("Address ") ) {
                    JOptionPane.showMessageDialog(userView, "Please enter your address!");
                    return; // Stop registration
                }
                // Gather all the text field data
                String name = userView.getNameField().getText();
                String email = userView.getEmailField().getText();
                String phone = userView.getPhoneNum().getText();
                String address = userView.getAddressField().getText();
                String role = (String) userView.getRole().getSelectedItem();

                // ---> NEW: PHONE NUMBER VALIDATION <---
                // This checks if the phone exactly matches 97 or 98, followed by 8 more digits
                if (!phone.matches("^(97|98)[0-9]{8}$")) {
                    JOptionPane.showMessageDialog(userView, "Invalid Phone Number! It must be exactly 10 digits and start with 97 or 98.");
                    return; // Stop registration if the phone is invalid
                }

                // If everything is perfect, save them to the database!
                userData user = new userData(name, email, phone, address, enteredPassword, role);
                
                boolean check = userDao.checkUser(user);

                if (check) {
                    JOptionPane.showMessageDialog(userView, "An account with this email already exists!");
                } else {
                    userDao.createUser(user);
                    JOptionPane.showMessageDialog(userView, "Registration Successful!");
                    
                    // Clear the stored password so they can't register twice with it
                    storedPassword = ""; 
                }
            } catch (Exception ex) {
                System.out.println("Error adding user: " + ex.getMessage());
            }
        }
    }
 // --- Add this inside SignupController.java ---
    class SwitchToLoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 1. Close the current Sign-up window
            close(); 
            // 2. Initialize and open the Login window
            view.login loginView = new view.login();
            controllor.LoginController loginController = new controllor.LoginController(loginView);
            
            loginController.open();
        }
    }
 // =========================================================================
    // PUBLIC ORDER TRACKING LOGIC
    // =========================================================================

    class SearchOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Note: Change 'signupView' to the actual name of your view variable in this controller
            String trackingId = userView.getTrackingInput(); 
            
            if (trackingId.isEmpty()) {
                JOptionPane.showMessageDialog(userView, "Please enter a Tracking ID to search.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            DAO.OrderDAO dao = new DAO.OrderDAO();
            java.sql.ResultSet rs = dao.getPublicTrackingDetails(trackingId); // Reusing the exact same DAO method!
            
            try {
                if (rs != null && rs.next()) {
                    String tId = rs.getString("tracking_id");
                    String name = rs.getString("receiver_name");
                    String email = rs.getString("receiver_email");
                    String sender = rs.getString("street");
                    String receiver = rs.getString("receiver_location");
                    String cost = String.valueOf(rs.getDouble("total_cost"));
                    String orderStatus = rs.getString("status").toUpperCase();
                    
                    userView.setTrackingResult(tId, name, email, sender, receiver, cost, orderStatus);
                } else {
                    JOptionPane.showMessageDialog(userView, "No order found with Tracking ID: " + trackingId, "Not Found", JOptionPane.ERROR_MESSAGE);
                    userView.lockTrackingDisplayFields(); // Reset fields to empty
                }
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(userView, "Database error while searching.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}