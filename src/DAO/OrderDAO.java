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
            return true; 
        }
    }

    public Order getOrderByTrackingIdAndSender(String trackingId, int senderId) {
        String sql = "SELECT * FROM orders WHERE tracking_id = ? AND sender_id = ?";

        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, trackingId);
            pstmt.setInt(2, senderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    
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
                    
                    // FIXED: Load the delivery cost!
                    foundOrder.setDeliveryCost(rs.getDouble("delivery_cost"));
                    foundOrder.setStatus(rs.getString("status"));
                    
                    return foundOrder;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean cancelOrder(String trackingId, int senderId) {
        String sql = "UPDATE orders SET status = 'cancelled' WHERE tracking_id = ? AND sender_id = ?";
        
        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, trackingId);
            pstmt.setInt(2, senderId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; 
            
        } catch (SQLException e) {
            System.out.println("SQL Error during cancellation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public java.util.List<Model.Order> getAllOrdersBySender(int senderId) {
        
        java.util.List<Model.Order> orderList = new java.util.ArrayList<>();
        String sql = "SELECT * FROM orders WHERE sender_id = ? ORDER BY order_id DESC";
        
        try (java.sql.Connection conn = db.openConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, senderId);
            
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    
                    Model.Order order = new Model.Order(
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
                    
                    // FIXED: Load the delivery cost!
                    order.setDeliveryCost(rs.getDouble("delivery_cost"));
                    order.setStatus(rs.getString("status"));
                    
                    orderList.add(order);
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        
        return orderList;
    }

    public java.util.List<Model.Order> getOrdersByStatus(int senderId, String status) {
        
        java.util.List<Model.Order> orderList = new java.util.ArrayList<>();
        String sql = "SELECT * FROM orders WHERE sender_id = ? AND status = ? ORDER BY order_id DESC";
        
        try (java.sql.Connection conn = db.openConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, senderId);
            pstmt.setString(2, status); 
            
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    
                    Model.Order order = new Model.Order(
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
                    
                    // FIXED: Load the delivery cost!
                    order.setDeliveryCost(rs.getDouble("delivery_cost"));
                    order.setStatus(rs.getString("status"));
                    
                    orderList.add(order);
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        
        return orderList;
    }

    public java.util.Map<String, Integer> getDashboardStats(int senderId) {
        
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        stats.put("total", 0);
        stats.put("pending", 0);
        stats.put("cancelled", 0);
        stats.put("delivered", 0);
        stats.put("intransit", 0);
        stats.put("return", 0);
        
        String sql = "SELECT status, COUNT(order_id) as status_count FROM orders WHERE sender_id = ? GROUP BY status";
        
        try (java.sql.Connection conn = db.openConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, senderId);
            
            int totalOrders = 0;
            
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("status").toLowerCase();
                    int count = rs.getInt("status_count");
                    
                    stats.put(status, count);
                    totalOrders += count;
                }
            }
            
            stats.put("total", totalOrders);
            
        } catch (java.sql.SQLException e) {
            System.out.println("Error fetching dashboard stats: " + e.getMessage());
            e.printStackTrace();
        }
        
        return stats;
    }

    public java.util.List<Model.Order> getRecent5OrdersBySender(int senderId) {
        
        java.util.List<Model.Order> orderList = new java.util.ArrayList<>();
        String sql = "SELECT * FROM orders WHERE sender_id = ? ORDER BY order_id DESC LIMIT 5";
        
        try (java.sql.Connection conn = db.openConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, senderId);
            
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    
                    Model.Order order = new Model.Order(
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
                    
                    // FIXED: Load the delivery cost!
                    order.setDeliveryCost(rs.getDouble("delivery_cost"));
                    order.setStatus(rs.getString("status"));
                    
                    orderList.add(order);
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        
        return orderList;
    }
    
    public java.sql.ResultSet getPendingOrders() {
        String sql = "SELECT tracking_id, receiver_name, receiver_contact, receiver_email, receiver_location, total_cost FROM orders WHERE status = 'Pending'";
        try {
            java.sql.Connection conn = db.openConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            return pstmt.executeQuery(); 
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean assignOrderToEmployee(int employeeId, String trackingId) {
        String insertSql = "INSERT INTO assignedOrders (usersID, ordersTrackingID) VALUES (?, ?)";
        String updateSql = "UPDATE orders SET status = 'intransit' WHERE tracking_id = ?";
        
        try (java.sql.Connection conn = db.openConnection()) {
            conn.setAutoCommit(false); 
            
            try (java.sql.PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, employeeId);
                insertStmt.setString(2, trackingId);
                insertStmt.executeUpdate();
            }
            
            try (java.sql.PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, trackingId);
                updateStmt.executeUpdate();
            }
            
            conn.commit(); 
            return true;
            
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public java.sql.ResultSet getFilteredOrders(String statusFilter) {
        String sql;
        
        if (statusFilter == null || statusFilter.equalsIgnoreCase("All")) {
            sql = "SELECT tracking_id, receiver_name, receiver_contact, receiver_location, status, delivery_cost, total_cost FROM orders";
        } else {
            sql = "SELECT tracking_id, receiver_name, receiver_contact, receiver_location, status, delivery_cost, total_cost FROM orders WHERE status = ?";
        }
        
        try {
            java.sql.Connection conn = db.openConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            
            if (statusFilter != null && !statusFilter.equalsIgnoreCase("All")) {
                pstmt.setString(1, statusFilter);
            }
            
            return pstmt.executeQuery(); 
            
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int[] getEmployeeWorkloadStats(int employeeId) {
        int[] stats = new int[5]; 
        
        String sql = "SELECT "
                   + "COUNT(*) as total, "
                   + "SUM(CASE WHEN status = 'intransit' THEN 1 ELSE 0 END) as active, "
                   + "SUM(CASE WHEN status = 'delivered' THEN 1 ELSE 0 END) as delivered, "
                   + "SUM(CASE WHEN status = 'cancelled' THEN 1 ELSE 0 END) as cancelled, "
                   + "SUM(CASE WHEN status = 'return' THEN 1 ELSE 0 END) as returned "
                   + "FROM orders o "
                   + "JOIN assignedOrders a ON o.tracking_id = a.ordersTrackingID "
                   + "WHERE a.usersID = ?";
                   
        try (java.sql.Connection conn = db.openConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, employeeId);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    stats[0] = rs.getInt("total");
                    stats[1] = rs.getInt("active");
                    stats[2] = rs.getInt("delivered");
                    stats[3] = rs.getInt("cancelled");
                    stats[4] = rs.getInt("returned");
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public java.sql.ResultSet getEmployeeAssignedOrdersList(int employeeId) {
        String sql = "SELECT o.tracking_id, o.receiver_name, o.receiver_location, o.status "
                   + "FROM orders o "
                   + "JOIN assignedOrders a ON o.tracking_id = a.ordersTrackingID "
                   + "WHERE a.usersID = ?";
        try {
            java.sql.Connection conn = db.openConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            return pstmt.executeQuery();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public java.sql.ResultSet getOrderByTrackingId(String trackingId) {
        String sql = "SELECT receiver_name, receiver_email, receiver_contact, street, receiver_location, total_cost FROM orders WHERE tracking_id = ?";
        try {
            java.sql.Connection conn = db.openConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            return pstmt.executeQuery(); 
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateOrderDetails(String trackingId, String name, String contact, String address, double totalCost) {
        String sql = "UPDATE orders SET receiver_name = ?, receiver_contact = ?, receiver_location = ?, total_cost = ? WHERE tracking_id = ?";
        
        try (java.sql.Connection conn = db.openConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, name);
            pstmt.setString(2, contact);
            pstmt.setString(3, address);
            pstmt.setDouble(4, totalCost);
            pstmt.setString(5, trackingId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateOrderStatus(String trackingId, String newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE tracking_id = ?";
        
        try (java.sql.Connection conn = db.openConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, newStatus);
            pstmt.setString(2, trackingId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean adminUpdateOrder(String trackingId, String name, String email, String senderAddr, String receiverAddr, double cost) {
        String sql = "UPDATE orders SET receiver_name = ?, receiver_email = ?, street = ?, receiver_location = ?, total_cost = ? WHERE tracking_id = ?";
        
        try (java.sql.Connection conn = db.openConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, senderAddr);
            pstmt.setString(4, receiverAddr);
            pstmt.setDouble(5, cost);
            pstmt.setString(6, trackingId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int[] getAdminDashboardStats() {
        int[] stats = new int[6]; 
        
        String sql = "SELECT "
                   + "COUNT(*) as total, "
                   + "SUM(CASE WHEN status = 'intransit' THEN 1 ELSE 0 END) as active, "
                   + "SUM(CASE WHEN status = 'pending' THEN 1 ELSE 0 END) as pending, "
                   + "SUM(CASE WHEN status = 'delivered' THEN 1 ELSE 0 END) as delivered, "
                   + "SUM(CASE WHEN status = 'cancelled' THEN 1 ELSE 0 END) as cancelled, "
                   + "SUM(CASE WHEN status = 'return' THEN 1 ELSE 0 END) as returned "
                   + "FROM orders";
                   
        try (java.sql.Connection conn = db.openConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = pstmt.executeQuery()) {
             
            if (rs.next()) {
                stats[0] = rs.getInt("total");
                stats[1] = rs.getInt("active");
                stats[2] = rs.getInt("pending");
                stats[3] = rs.getInt("delivered");
                stats[4] = rs.getInt("cancelled");
                stats[5] = rs.getInt("returned");
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public java.sql.ResultSet getRecentOrders(int limit) {
        String sql = "SELECT tracking_id, receiver_name, receiver_contact, status, total_cost FROM orders ORDER BY order_date DESC LIMIT ?";
        try {
            java.sql.Connection conn = db.openConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            return pstmt.executeQuery();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public java.sql.ResultSet getEmployeeOrderHistory(int employeeId, String statusFilter) {
        String sql;
        
        if (statusFilter == null || statusFilter.equalsIgnoreCase("All")) {
            sql = "SELECT o.tracking_id, o.receiver_name, o.receiver_location, o.status, o.order_date, o.total_cost "
                + "FROM orders o "
                + "JOIN assignedOrders a ON o.tracking_id = a.ordersTrackingID "
                + "WHERE a.usersID = ? "
                + "ORDER BY o.order_date DESC";
        } else {
            sql = "SELECT o.tracking_id, o.receiver_name, o.receiver_location, o.status, o.order_date, o.total_cost "
                + "FROM orders o "
                + "JOIN assignedOrders a ON o.tracking_id = a.ordersTrackingID "
                + "WHERE a.usersID = ? AND o.status = ? "
                + "ORDER BY o.order_date DESC";
        }
        
        try {
            java.sql.Connection conn = db.openConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setInt(1, employeeId);
            
            if (statusFilter != null && !statusFilter.equalsIgnoreCase("All")) {
                pstmt.setString(2, statusFilter);
            }
            
            return pstmt.executeQuery(); 
            
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public java.sql.ResultSet getEmployeeOrderByTrackingId(String trackingId, int employeeId) {
        String sql = "SELECT o.receiver_name, o.receiver_email, o.street, o.receiver_location, o.total_cost " +
                     "FROM orders o " +
                     "JOIN assignedOrders a ON o.tracking_id = a.ordersTrackingID " +
                     "WHERE o.tracking_id = ? AND a.usersID = ?";
        try {
            java.sql.Connection conn = db.openConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            pstmt.setInt(2, employeeId);
            return pstmt.executeQuery(); 
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean employeeUpdateOrderDetails(String trackingId, String name, String email, String address, double totalCost) {
        String sql = "UPDATE orders SET receiver_name = ?, receiver_email = ?, receiver_location = ?, total_cost = ? WHERE tracking_id = ?";
        
        try (java.sql.Connection conn = db.openConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, address);
            pstmt.setDouble(4, totalCost);
            pstmt.setString(5, trackingId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public java.sql.ResultSet getPublicTrackingDetails(String trackingId) {
        String sql = "SELECT tracking_id, receiver_name, receiver_email, street, receiver_location, total_cost, status FROM orders WHERE tracking_id = ?";
        try {
            java.sql.Connection conn = db.openConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            return pstmt.executeQuery(); 
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int[] getEmployeeDashboardStats(int employeeId) {
        int[] stats = new int[5]; 
        
        String sql = "SELECT "
                   + "COUNT(*) as total, "
                   + "SUM(CASE WHEN o.status = 'intransit' THEN 1 ELSE 0 END) as active, "
                   + "SUM(CASE WHEN o.status = 'delivered' THEN 1 ELSE 0 END) as delivered, "
                   + "SUM(CASE WHEN o.status = 'return' THEN 1 ELSE 0 END) as returned, "
                   + "SUM(CASE WHEN o.status = 'cancelled' THEN 1 ELSE 0 END) as cancelled "
                   + "FROM orders o "
                   + "JOIN assignedOrders a ON o.tracking_id = a.ordersTrackingID "
                   + "WHERE a.usersID = ?";
                   
        try (java.sql.Connection conn = db.openConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, employeeId);
            
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    stats[0] = rs.getInt("total");
                    stats[1] = rs.getInt("active");
                    stats[2] = rs.getInt("delivered");
                    stats[3] = rs.getInt("returned");
                    stats[4] = rs.getInt("cancelled");
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public java.sql.ResultSet getRecentEmployeeOrders(int employeeId, int limit) {
        String sql = "SELECT o.tracking_id, o.receiver_name, o.receiver_contact, o.receiver_location, o.status "
                   + "FROM orders o "
                   + "JOIN assignedOrders a ON o.tracking_id = a.ordersTrackingID "
                   + "WHERE a.usersID = ? "
                   + "ORDER BY o.order_date DESC LIMIT ?";
        try {
            java.sql.Connection conn = db.openConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            pstmt.setInt(2, limit);
            return pstmt.executeQuery();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}