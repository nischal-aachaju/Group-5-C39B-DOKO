
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
    // Inside DAO.OrderDAO
    public java.util.List<Model.Order> getAllOrdersBySender(int senderId) {
        
        java.util.List<Model.Order> orderList = new java.util.ArrayList<>();
        
        // Pull all orders for this user, ordering by ID so the newest ones are at the bottom/top
        String sql = "SELECT * FROM orders WHERE sender_id = ? ORDER BY order_id DESC";
        
        try (java.sql.Connection conn = db.openConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, senderId);
            
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    
                    // Extract data exactly as it matches your Order constructor
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
                    
                    // Attach the status manually
                    order.setStatus(rs.getString("status"));
                    
                    // Add it to the list
                    orderList.add(order);
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        
        return orderList;
    }
    // Inside DAO.OrderDAO
    public java.util.List<Model.Order> getOrdersByStatus(int senderId, String status) {
        
        java.util.List<Model.Order> orderList = new java.util.ArrayList<>();
        
        // This query strictly filters by both Sender AND Status
        String sql = "SELECT * FROM orders WHERE sender_id = ? AND status = ? ORDER BY order_id DESC";
        
        try (java.sql.Connection conn = db.openConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, senderId);
            pstmt.setString(2, status); // Inject the requested status
            
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
                    
                    order.setStatus(rs.getString("status"));
                    orderList.add(order);
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        
        return orderList;
    }
     // Inside DAO.OrderDAO
    public java.util.Map<String, Integer> getDashboardStats(int senderId) {
        
        // Create a map and set default values to 0 to prevent NullPointer errors
        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        stats.put("total", 0);
        stats.put("pending", 0);
        stats.put("cancelled", 0);
        stats.put("delivered", 0);
        stats.put("intransit", 0);
        stats.put("return", 0);
        
        // This query counts how many orders exist for EACH status for this specific sender
        String sql = "SELECT status, COUNT(order_id) as status_count FROM orders WHERE sender_id = ? GROUP BY status";
        
        try (java.sql.Connection conn = db.openConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, senderId);
            
            int totalOrders = 0;
            
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("status").toLowerCase();
                    int count = rs.getInt("status_count");
                    
                    // Put the count into the map using the status as the key
                    stats.put(status, count);
                    
                    // Add to the total count
                    totalOrders += count;
                }
            }
            
            // Save the grand total into the map
            stats.put("total", totalOrders);
            
        } catch (java.sql.SQLException e) {
            System.out.println("Error fetching dashboard stats: " + e.getMessage());
            e.printStackTrace();
        }
        
        return stats;
    }
    // Inside DAO.OrderDAO
    public java.util.List<Model.Order> getRecent5OrdersBySender(int senderId) {
        
        java.util.List<Model.Order> orderList = new java.util.ArrayList<>();
        
        // LIMIT 5 tells the database to only give us the 5 most recent rows!
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
                    
                    order.setStatus(rs.getString("status"));
                    orderList.add(order);
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        
        return orderList;
    }
    
    // 1. Fetch all pending orders for the table
    public java.sql.ResultSet getPendingOrders() {
        // Assuming your status column is named 'status' and holds 'Pending'
//        String sql = "SELECT tracking_id, recipient_name, status FROM orders WHERE status = 'Pending'";

    String sql = "SELECT tracking_id, receiver_name, receiver_contact, receiver_email, receiver_location, total_cost FROM orders WHERE status = 'Pending'";
        try {
            java.sql.Connection conn = db.openConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            return pstmt.executeQuery(); // The controller will read this and close it
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 2. Assign the order and update the order status
    public boolean assignOrderToEmployee(int employeeId, String trackingId) {
        String insertSql = "INSERT INTO assignedOrders (usersID, ordersTrackingID) VALUES (?, ?)";
        String updateSql = "UPDATE orders SET status = 'intransit' WHERE tracking_id = ?";
        
        try (java.sql.Connection conn = db.openConnection()) {
            // Start a transaction so both queries succeed, or neither do
            conn.setAutoCommit(false); 
            
            // Insert into bridging table
            try (java.sql.PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, employeeId);
                insertStmt.setString(2, trackingId);
                insertStmt.executeUpdate();
            }
            
            // Update the main orders table status
            try (java.sql.PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, trackingId);
                updateStmt.executeUpdate();
            }
            
            conn.commit(); // Save changes!
            return true;
            
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // =========================================================================
    // MANAGER: FETCH ACTIVE / FILTERED ORDERS
    // =========================================================================

    public java.sql.ResultSet getFilteredOrders(String statusFilter) {
        String sql;
        
        // If they ask for "All" or pass null, fetch everything
        if (statusFilter == null || statusFilter.equalsIgnoreCase("All")) {
            sql = "SELECT tracking_id, receiver_name, receiver_contact, receiver_location, status, delivery_cost, total_cost FROM orders";
        } else {
            // Otherwise, fetch only the specific status
            sql = "SELECT tracking_id, receiver_name, receiver_contact, receiver_location, status, delivery_cost, total_cost FROM orders WHERE status = ?";
        }
        
        try {
            java.sql.Connection conn = db.openConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            
            // If we are filtering by a specific status, inject it into the '?'
            if (statusFilter != null && !statusFilter.equalsIgnoreCase("All")) {
                pstmt.setString(1, statusFilter);
            }
            
            return pstmt.executeQuery(); 
            
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
// =========================================================================
    // MANAGER: EMPLOYEE WORKLOAD DATA
    // =========================================================================

    // 1. Fetch the total counts for the top cards
    public int[] getEmployeeWorkloadStats(int employeeId) {
        // Index mapping: 0=Total, 1=Active(intransit), 2=Delivered, 3=Cancelled, 4=Returned
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

    // 2. Fetch the actual list of orders for the table
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
// =========================================================================
    // MANAGER: EDIT ORDER METHODS
    // =========================================================================

    // 1. Fetch a single order by Tracking ID
    public java.sql.ResultSet getOrderByTrackingId(String trackingId) {
        String sql = "SELECT receiver_name, receiver_email, receiver_contact, street, receiver_location, total_cost FROM orders WHERE tracking_id = ?";
        try {
            java.sql.Connection conn = db.openConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            return pstmt.executeQuery(); // Controller will handle closing this
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 2. Update the specific editable fields
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
    // =========================================================================
    // UPDATE ORDER STATUS
    // =========================================================================

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
    // =========================================================================
    // ADMIN: FULL ORDER EDIT & STATUS UPDATE
    // =========================================================================

    public boolean adminUpdateOrder(String trackingId, String name, String email, String senderAddr, String receiverAddr, double cost) {
        // 'street' is used for Sender Address based on your schema
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
    // =========================================================================
    // ADMIN DASHBOARD: STATS AND RECENT ORDERS
    // =========================================================================

    public int[] getAdminDashboardStats() {
        // Index mapping: 0=Total, 1=Active(intransit), 2=Pending, 3=Delivered, 4=Cancelled, 5=Returned
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
        // Fetches the most recent orders based on order_date
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
    // =========================================================================
    // EMPLOYEE: GET ASSIGNED ORDER HISTORY
    // =========================================================================

    public java.sql.ResultSet getEmployeeOrderHistory(int employeeId, String statusFilter) {
        String sql;
        
        // If "All", fetch everything assigned to this employee
        if (statusFilter == null || statusFilter.equalsIgnoreCase("All")) {
            sql = "SELECT o.tracking_id, o.receiver_name, o.receiver_location, o.status, o.order_date, o.total_cost "
                + "FROM orders o "
                + "JOIN assignedOrders a ON o.tracking_id = a.ordersTrackingID "
                + "WHERE a.usersID = ? "
                + "ORDER BY o.order_date DESC";
        } else {
            // Otherwise, filter by specific status
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
    // =========================================================================
    // EMPLOYEE: SECURE SEARCH & EDIT
    // =========================================================================

    // 1. Fetch order ONLY if it is assigned to this specific employee
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

    // 2. Update only the allowed fields
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
}