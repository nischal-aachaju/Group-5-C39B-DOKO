package controllor;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ForgotPasswordController {

    private final view.forgotPassword view;
    private String generatedOTP = null;  // Secretly stores the true OTP in memory
    private String verifiedEmail = null; // Stores the email to ensure they reset the right account

    public ForgotPasswordController(view.forgotPassword view) {
        this.view = view;
        
        // Attach Action Listeners
        this.view.addSendOtpListener(new SendOtpListener());
        this.view.addChangePasswordListener(new ChangePasswordListener());
        this.view.addBackToLogin(new OpenBackToLogin());
        
    }

    public void open() {
        this.view.setVisible(true);
        this.view.setLocationRelativeTo(null); // Centers the window on screen
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
    // SEND OTP LOGIC
    // =========================================================================

    class SendOtpListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String fullname = view.getFullname();
            String email = view.getEmail();
            
            if (fullname.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Please enter your Fullname and Email first.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 1. Check database to see if this user actually exists
            DAO.userDAO dao = new DAO.userDAO();
            if (!dao.verifyUserForReset(fullname, email)) {
                JOptionPane.showMessageDialog(view, "No account found matching that Name and Email.", "Verification Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. User exists! Generate OTP
            generatedOTP = Emailhelper.generatePassword();
            System.out.println("DEBUG OTP (Remove before production): " + generatedOTP); 
            
            // 3. Send the email using your helper class
            boolean emailSent = Emailhelper.sendEmail(email, generatedOTP, fullname);
            
            if (emailSent) {
                verifiedEmail = email; // Lock in the email for the database update
                JOptionPane.showMessageDialog(view, "An OTP has been sent to " + email);
                view.setFormEditableAfterOTP(); // Unlock the password fields
            } else {
                JOptionPane.showMessageDialog(view, "Failed to send email. Check your internet connection or email configuration.", "Network Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // CHANGE PASSWORD LOGIC
    // =========================================================================

    class ChangePasswordListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String enteredOtp = view.getEnteredOTP();
            String newPass = view.getNewPassword();
            String confirmPass = view.getConfirmPassword();
            
            // 1. Verify Terms Checkbox
            if (!view.isTermsChecked()) {
                JOptionPane.showMessageDialog(view, "You must agree to the Terms & Conditions.");
                return;
            }
            
            // 2. Verify OTP Match
            if (generatedOTP == null || !generatedOTP.equals(enteredOtp)) {
                JOptionPane.showMessageDialog(view, "Invalid OTP! Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 3. Verify Password Requirements
            if (newPass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Password fields cannot be empty.");
                return;
            }
            
            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(view, "New passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 4. Update the Database!
            DAO.userDAO dao = new DAO.userDAO();
            boolean success = dao.resetPassword(verifiedEmail, newPass);
            
            if (success) {
                JOptionPane.showMessageDialog(view, "Password successfully changed! You may now log in.");
                close();
                
                // Route them back to the login page
                new controllor.LoginController(new view.login()).open();
            } else {
                JOptionPane.showMessageDialog(view, "Database Error. Failed to update password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    class OpenBackToLogin implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            // 1. Close the current Login window
            close(); 
            
            // 2. Initialize and open the Sign-Up window
            view.login loginPage = new view.login();
            controllor.LoginController loginPageController = new controllor.LoginController(loginPage);
            loginPageController.open();
        }
    }
}