
package DAO;

import DB.Dbconnector;
import Model.Order;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDAO {
    
    private final Dbconnector db = new Dbconnector();

    public boolean saveOrder(Order order, int senderId) {
        String sql = "INSERT INTO orders (sender_id, tracking_id, receiver_name, receiver_email, "
                   + "receiver_contact, receiver_location, street, weight, delivery_cost, total_cost, description) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, senderId);
            pstmt.setString(2, order.getTrackingId());
            pstmt.setString(3, order.getReceiverName());
            pstmt.setString(4, order.getReceiverEmail());
            pstmt.setString(5, order.getReceiverContact());
            pstmt.setString(6, order.getReceiverLocation());
            pstmt.setString(7, order.getStreet());
            pstmt.setDouble(8, order.getWeight());
            pstmt.setDouble(9, order.getDeliveryCost());
            pstmt.setDouble(10, order.getFinalBillAmount());
            pstmt.setString(11, order.getDescription());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isTrackingIdExists(String trackingId) {
        String sql = "SELECT 1 FROM orders WHERE tracking_id = ?";

        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, trackingId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return true; // fail-safe: prevent duplicate saves if DB errors
        }
    }

//    public Order getOrderByTrackingIdAndSender(String trackingId, int senderId) {
//        String sql = "SELECT * FROM orders WHERE tracking_id = ? AND sender_id = ?";
//
//        try (Connection conn = db.openConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setString(1, trackingId);
//            pstmt.setInt(2, senderId);
//
//            try (ResultSet rs = pstmt.executeQuery()) {
//                if (rs.next()) {
//                    return new Order(
//                        rs.getString("tracking_id"),
//                        rs.getString("receiver_name"),
//                        rs.getString("receiver_email"),
//                        rs.getString("receiver_contact"),
//                        rs.getString("receiver_location"),
//                        rs.getString("street"),
//                        rs.getDouble("weight"),
//                        rs.getDouble("total_cost"),
//                        rs.getString("description")
//                    );
//                }
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
    
    public Order getOrderByTrackingIdAndSender(String trackingId, int senderId) {
        String sql = "SELECT * FROM orders WHERE tracking_id = ? AND sender_id = ?";

        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, trackingId);
            pstmt.setInt(2, senderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    
                    // 1. Create the order using your clean constructor
                    Order foundOrder = new Order(
                        rs.getString("tracking_id"),
                        rs.getString("receiver_name"),
                        rs.getString("receiver_email"),
                        rs.getString("receiver_contact"),
                        rs.getString("receiver_location"),
                        rs.getString("street"),
                        rs.getDouble("weight"),
                        rs.getDouble("total_cost"), 
                        rs.getString("description")
                    );
                    
                    // 2. --- CRITICAL FIX FOR CANCEL LOGIC ---
                    // Pull the status from the DB so the controller knows if it's cancelled
                    foundOrder.setStatus(rs.getString("status"));
                    
                    // 3. Return the fully packed order
                    return foundOrder;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    // Inside DAO.OrderDAO
    public boolean cancelOrder(String trackingId, int senderId) {
        // SECURITY: We only update if BOTH the tracking ID and the sender ID match
        String sql = "UPDATE orders SET status = 'cancelled' WHERE tracking_id = ? AND sender_id = ?";
        
        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, trackingId);
            pstmt.setInt(2, senderId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Returns true if the status was successfully changed
            
        } catch (SQLException e) {
            System.out.println("SQL Error during cancellation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}