package controllor;

import view.priceConfiguration;
import DAO.PriceDAO;
import Model.userData; // Assuming you pass the logged-in admin user around
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class PriceConfigController {
    
    private final priceConfiguration configView;
    private final userData currentAdmin;

    public PriceConfigController(priceConfiguration configView, userData currentAdmin) {
        this.configView = configView;
        this.currentAdmin = currentAdmin;
        
        // 1. Load the current prices from the database as soon as the page opens!
        loadCurrentPrices();
        
        // 2. Connect the buttons
        this.configView.addSaveListener(new SavePricingListener());
        this.configView.addDashboardListener(new DashboardNavListener());
        this.configView.addLogoutListener(new LogoutListener());
    }

    public void open() {
        this.configView.setVisible(true);
        this.configView.setLocationRelativeTo(null);
    }
    
    public void close() {
        this.configView.dispose();
    }

    // =========================================================================
    // LOAD CURRENT PRICES
    // =========================================================================
    private void loadCurrentPrices() {
        PriceDAO priceDao = new PriceDAO();
        double[] currentPrices = priceDao.getLivePricing();
        
        // Put the numbers from the database directly into the text boxes
        configView.setPricePerKgInput(String.valueOf(currentPrices[0]));
        configView.setPricePerMeterInput(String.valueOf(currentPrices[1]));
    }

    // =========================================================================
    // SAVE NEW PRICES
    // =========================================================================
    class SavePricingListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String kgStr = configView.getPricePerKgInput();
            String meterStr = configView.getPricePerMeterInput();
            
            if (kgStr.isEmpty() || meterStr.isEmpty()) {
                JOptionPane.showMessageDialog(configView, "Please fill in both price fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                // Convert the text to decimals
                double perKg = Double.parseDouble(kgStr);
                double perMeter = Double.parseDouble(meterStr);
                
                // Save to Database
                PriceDAO priceDao = new PriceDAO();
                if (priceDao.updatePricing(perKg, perMeter)) {
                    JOptionPane.showMessageDialog(configView, "Pricing successfully updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(configView, "Database Error: Could not update pricing.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (NumberFormatException ex) {
                // This catches it if the Admin accidentally types letters like "Rs. 50" instead of just "50"
                JOptionPane.showMessageDialog(configView, "Please enter valid numeric values only (e.g., 50.5). Do not include text.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // NAVIGATION LISTENERS
    // =========================================================================
    class DashboardNavListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
             
             view.Admin_Dashboard dashView = new view.Admin_Dashboard();
             new controllor.AdminController(dashView, currentAdmin).open();
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
}