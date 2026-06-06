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
 

}