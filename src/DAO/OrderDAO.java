///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package DAO;
//
///**
// *
// * @author nischal
// */
//
//
//import DB.Dbconnector;
//import Model.Order;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import Model.Order;
//
//
//public class OrderDAO {
//    Dbconnector db=new Dbconnector();
//    public boolean saveOrder(Order order, int senderId) {
//        String sql = "INSERT INTO orders (sender_id, tracking_id, receiver_name, receiver_email, "
//                   + "receiver_contact, receiver_location, street, weight, delivery_cost, total_cost, description) "
//                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//                   
//        try (Connection conn = db.openConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            
//            pstmt.setInt(1, senderId);
//            pstmt.setString(2, order.getTrackingId());
//            pstmt.setString(3, order.getReceiverName());
//            pstmt.setString(4, order.getReceiverEmail());
//            pstmt.setString(5, order.getReceiverContact());
//            pstmt.setString(6, order.getReceiverLocation());
//            pstmt.setString(7, order.getStreet());
//            pstmt.setDouble(8, order.getWeight());
//            pstmt.setDouble(9, order.getDeliveryCost());
//            pstmt.setDouble(10, order.getFinalBillAmount());
//            pstmt.setString(11, order.getDescription());
//            
//            int rowsAffected = pstmt.executeUpdate();
//            return rowsAffected > 0;
//            
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
// // ABSTRACTION: The Controller doesn't need to know how to write a SELECT query.
//    // It just asks this method "True or False?" and gets an answer.
//    public boolean isTrackingIdExists(String trackingId) {
//        String sql = "SELECT 1 FROM orders WHERE tracking_id = ?";
//        
//        try (Connection conn =  db.openConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            
//            pstmt.setString(1, trackingId);
//            
//            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
//                // If rs.next() is true, it found a match (ID is taken!)
//                return rs.next(); 
//            }
//            
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return true; // If the database fails, return true to prevent accidental saving
//        }
//    }
//    // Inside DAO.OrderDAO
////    public Model.Order getOrderByTrackingIdAndSender(String trackingId, int senderId) {
////        Model.Order foundOrder = null;
////        
////        // SECURITY FIX: The query now requires both parameters to match!
//////        String query = "SELECT * FROM orders WHERE tracking_id = ? AND sender_id = ?";
////           String query = "SELECT * FROM orders WHERE tracking_id = ?";
////        try {
////            // Assuming you have your standard DB connection setup here
////            java.sql.Connection conn =  db.openConnection();
////            java.sql.PreparedStatement pst = conn.prepareStatement(query);
////            
////            pst.setString(1, trackingId);
//////            pst.setInt(2, senderId); // Inject the logged-in user's ID
////            
////            java.sql.ResultSet rs = pst.executeQuery();
////            
////            if (rs.next()) {
////                // Map your database columns to your Order model variables here
////                // foundOrder = new Model.Order( ... );
////            }
////            
////        } catch (java.sql.SQLException e) {
////            e.printStackTrace();
////        }
////        
////        return foundOrder;
////    }
//
//        public Model.Order getOrderByTrackingIdAndSender(String trackingId, int senderId) {
//    Model.Order foundOrder = null;
//    String query = "SELECT * FROM orders WHERE tracking_id = ? AND sender_id = ?";
//
//    try (Connection conn = db.openConnection();
//         PreparedStatement pst = conn.prepareStatement(query)) {
//
//        pst.setString(1, trackingId);
//        pst.setInt(2, senderId);  // security check restored
//
//        try (java.sql.ResultSet rs = pst.executeQuery()) {
//            if (rs.next()) {
//    foundOrder = new Model.Order(
//        rs.getString("tracking_id"),
//        rs.getString("receiver_name"),
//        rs.getString("receiver_email"),
//        rs.getString("receiver_contact"),
//        rs.getString("receiver_location"),
//        rs.getString("street"),
//        rs.getDouble("weight"),
//        rs.getDouble("total_cost"),
//        rs.getString("description")
//    );
//            }
//        }
//
//    } catch (SQLException e) {
//        e.printStackTrace();
//    }
//
//    return foundOrder;
//}
//    }

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

    public Order getOrderByTrackingIdAndSender(String trackingId, int senderId) {
        String sql = "SELECT * FROM orders WHERE tracking_id = ? AND sender_id = ?";

        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, trackingId);
            pstmt.setInt(2, senderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Order(
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
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}