
package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


// NOTE: Make sure this import matches your actual database connection class!
// It might be 'import DB.database;' or 'import DB.Dbconnector;'
import DB.Dbconnector;

public class BranchDAO {
    
    private final Dbconnector db = new Dbconnector();

    // =========================================================================
    // ASSIGN ORDER TO A BRANCH
    // =========================================================================
    public boolean assignOrderToBranch(int branchId, String trackingId) {
        // This query inserts the newly generated tracking ID and the selected branch ID
        // into the branch_orders bridge table.
        String insertSql = "INSERT INTO branch_orders (branch_id, tracking_id) VALUES (?, ?)";
        
        try (Connection conn = db.openConnection()) {
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                
                insertStmt.setInt(1, branchId);
                insertStmt.setString(2, trackingId);
                
                // Returns true if the insertion was successful
                return insertStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================================
    // GET ALL BRANCHES FOR DROPDOWN
    // =========================================================================
    public ResultSet getAllBranches() {
        String sql = "SELECT branch_id, branch_name FROM branches";
        
        try {
            Connection conn = db.openConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            return pstmt.executeQuery(); 
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // =========================================================================
    // OTHER BRANCH METHODS (Create, Update, Get Stats, etc.)
    // =========================================================================
    
    public boolean createBranch(String name, String email, String phone, String address) {
        String sql = "INSERT INTO branches (branch_name, branch_email, branch_phone, branch_address) VALUES (?, ?, ?, ?)";
        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setString(4, address);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBranch(int branchId, String name, String email, String phone, String address) {
        String sql = "UPDATE branches SET branch_name = ?, branch_email = ?, branch_phone = ?, branch_address = ? WHERE branch_id = ?";
        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setString(4, address);
            pstmt.setInt(5, branchId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public ResultSet getBranchDetailsWithStats(int branchId) {
        String sql = "SELECT b.branch_id, b.branch_name, b.branch_email, b.branch_phone, b.branch_address, " +
                     "(SELECT COUNT(*) FROM branch_employees WHERE branch_id = b.branch_id) AS total_employees, " +
                     "(SELECT COUNT(*) FROM branch_orders WHERE branch_id = b.branch_id) AS total_orders " +
                     "FROM branches b WHERE b.branch_id = ?";
        try {
            Connection conn = db.openConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, branchId);
            return pstmt.executeQuery(); 
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}