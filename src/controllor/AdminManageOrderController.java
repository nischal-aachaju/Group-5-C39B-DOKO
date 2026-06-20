package controllor;

import Model.userData;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class AdminManageOrderController {

    private final view.AdminOrderEdit view; // Replace with your exact UI class name
    private final userData currentUser;
    private String currentTrackingId = null; 

    public AdminManageOrderController(view.AdminOrderEdit view, userData currentUser) {
        this.view = view;
        this.currentUser = currentUser;
        
        this.view.setTopBar(currentUser.getUsername(), currentUser.getRole());
        this.view.setFormEditable(false); // Lock by default
        
        // Actions
        this.view.addSearchListener(new SearchOrderListener());
        this.view.addEditListener(new EditOrderListener());
        this.view.addSaveListener(new SaveOrderListener());
        this.view.addReturnListener(new ReturnOrderListener());
        
        // Navigation
        this.view.addDashboardListener(new DashboardNavListener());
        this.view.addMyProfileListener(new ProfileNavListener());
        this.view.addLogoutListener(new LogoutNavListener());
        this.view.addbranchNetwork(new branchNetworkListener());
        this.view.addPriceConfiguration(new PriceConfigurationListener());
        this.view.addWorkloadListener(new openworkloadListener());
        this.view.addManageUserListener(new openManageUserListenerListener());


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
    // CRUD ACTIONS
    // =========================================================================

    class SearchOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String trackingId = view.getSearchInput();
            if (trackingId.isEmpty()) return;
            
            DAO.OrderDAO dao = new DAO.OrderDAO();
            ResultSet rs = dao.getOrderByTrackingId(trackingId); // Reusing the Manager search DAO
            
            try {
                if (rs != null && rs.next()) {
                    currentTrackingId = trackingId;
                    view.setOrderDetails(
                        trackingId,
                        rs.getString("receiver_name"),
                        rs.getString("receiver_email"),
                        rs.getString("street"),
                        rs.getString("receiver_location"),
                        String.valueOf(rs.getDouble("total_cost"))
                    );
                    view.setFormEditable(false);
                } else {
                    JOptionPane.showMessageDialog(view, "Tracking ID not found.");
                    currentTrackingId = null;
                }
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    class EditOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentTrackingId != null) {
                view.setFormEditable(true); // Admin unlocks everything except Tracking ID
            }
        }
    }

    class SaveOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentTrackingId == null) return;
            
            String name = view.getUpdatedName();
            String email = view.getUpdatedEmail();
            String sender = view.getUpdatedSender();
            String receiver = view.getUpdatedReceiver();
            
            try {
                double cost = Double.parseDouble(view.getUpdatedCost());
                DAO.OrderDAO dao = new DAO.OrderDAO();
                
                if (dao.adminUpdateOrder(currentTrackingId, name, email, sender, receiver, cost)) {
                    JOptionPane.showMessageDialog(view, "Order successfully updated!");
                    view.setFormEditable(false);
                } else {
                    JOptionPane.showMessageDialog(view, "Failed to update order.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(view, "Cost must be a valid number.");
            }
        }
    }

    class ReturnOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentTrackingId == null) return;
            
            int confirm = JOptionPane.showConfirmDialog(view, "Mark order as RETURNED?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                DAO.OrderDAO dao = new DAO.OrderDAO();
                
                if (dao.updateOrderStatus(currentTrackingId, "return")) {
                    JOptionPane.showMessageDialog(view, "Status updated to RETURNED.");
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

    class ProfileNavListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
            view.NewAdmin_Profile profileView = new view.NewAdmin_Profile();
            new controllor.AdminProfileController(profileView, currentUser).open();
        }
    }

    class LogoutNavListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
            new controllor.LoginController(new view.login()).open();
        }
    }
    
    
    class ManageOrdersListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
    
            view.AdminOrderEdit ManageOrder = new view.AdminOrderEdit();
            controllor.AdminManageOrderController moc = new controllor.AdminManageOrderController(ManageOrder, currentUser);
            moc.open();
        }
    }
    class branchNetworkListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
    
            view.adminBranchNetworks adminBranchOrder = new view.adminBranchNetworks();
            controllor.AdminBranchController aboc = new controllor.AdminBranchController(adminBranchOrder, currentUser);
            aboc.open();
        }
    }
    class PriceConfigurationListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
            view.priceConfiguration priceConfig = new view.priceConfiguration();
            controllor.PriceConfigController pcc = new controllor.PriceConfigController(priceConfig, currentUser);
            pcc.open();
        }
    } 
    class openworkloadListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
            view.WorkLoad wl = new view.WorkLoad();
            controllor.AdminWorkloadController wlc = new controllor.AdminWorkloadController(wl, currentUser);
            wlc.open();
        }
    }
    class openManageUserListenerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
            view.ManageUser uam = new view.ManageUser();
            controllor.AdminManageUserController wlc = new controllor.AdminManageUserController(uam, currentUser);
            wlc.open();
        }
    }
}
