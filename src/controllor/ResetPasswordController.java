/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllor;

import Model.userData;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResetPasswordController {
    
    // Make sure 'view.Reset_Password' matches the actual name of your JFrame file!
    private final view.OTPBasedPasswordReset resetView;
    private final userData currentUser;

    public ResetPasswordController(view.OTPBasedPasswordReset resetView, userData currentUser) {
        this.resetView = resetView;
        this.currentUser = currentUser;
        
        // Pre-fill the name field using the logged-in user's data
        this.resetView.setFullName(currentUser.getUsername());
        
        // Connect the save button
        this.resetView.addChangePasswordListener(new ChangePasswordListener());
    }

    public void open() {
        this.resetView.setVisible(true);
        // Centers the window on the screen
        this.resetView.setLocationRelativeTo(null); 
    }

    public void close() {
        if (this.resetView != null) {
            this.resetView.dispose();
        }
    }

    // =========================================================================
    // LOGIC TO VALIDATE AND SAVE THE NEW PASSWORD
    // =========================================================================

    class ChangePasswordListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            // 1. Grab all the inputs from the View
            String currentPass = resetView.getCurrentPassword();
            String newPass = resetView.getNewPassword();
            String confirmPass = resetView.getConfirmNewPassword();
            boolean acceptedTerms = resetView.isTermsAccepted();
            
            // 2. Validation Check: Did they check the box?
            if (!acceptedTerms) {
                javax.swing.JOptionPane.showMessageDialog(resetView, 
                        "You must agree to the Terms and Conditions to change your password.", 
                        "Validation Error", javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 3. Validation Check: Are any fields empty?
            if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(resetView, 
                        "All password fields must be filled out.", 
                        "Validation Error", javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 4. Validation Check: Do the new passwords match?
            if (!newPass.equals(confirmPass)) {
                javax.swing.JOptionPane.showMessageDialog(resetView, 
                        "The new passwords do not match. Please type them carefully.", 
                        "Validation Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 5. Send to DAO to update the database
            DAO.userDAO dao = new DAO.userDAO();
            boolean success = dao.changeUserPassword(currentUser.getUserID(), currentPass, newPass);
            
            // 6. Handle the result
            if (success) {
                javax.swing.JOptionPane.showMessageDialog(resetView, 
                        "Password successfully updated!", 
                        "Success", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                
                // Update the memory object just in case
                currentUser.setPassword(newPass); 
                
                // Close the reset window
                close(); 
            } else {
                javax.swing.JOptionPane.showMessageDialog(resetView, 
                        "Incorrect Current Password. Failed to update.", 
                        "Security Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}