/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

//import DB.Dbconnector;
import DB.Dbconnector;
import java.sql.Connection;
import Model.userData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
// data access object
/**
 *
 * @author nischal
 */
public class userDAO {
    Dbconnector db=new Dbconnector();
    
    public void createUser(userData user){
            Connection conn=db.openConnection();
//            String sql="INSERT INTO users (username,email,phone,address,userPassword) values(?,?,?,?,?)";
            String sql="INSERT INTO users (username,email,phone,address,userPassword, role) values(?,?,?,?,?,?)";
        try (PreparedStatement pstm=conn.prepareStatement(sql)){
            pstm.setString(1,user.getUsername());
            pstm.setString(2,user.getEmail());    
            pstm.setString(3,user.getPhone());
            pstm.setString(4,user.getAddress());    
            pstm.setString(5,user.getPassword());
            pstm.setString(6, user.getRole());
            
             pstm.executeUpdate();
        } catch(Exception e){
            System.out.println(e);
            
        } finally{
            db.closeConnection(conn);
        }
    }
    public boolean checkUser(userData user){
        Connection conn = db.openConnection();
        String sql = "SELECT * FROM users where email = ? ";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getEmail());
            ResultSet result = pstmt.executeQuery();
            return result.next();
        } catch (SQLException ex) {
           System.out.print(ex);
        } finally {
            db.closeConnection(conn);
        }
        return false;
    }
    
public userData loginUser(String username, String password) {
        Connection conn = db.openConnection();
        String sql = "SELECT * FROM users WHERE username = ? AND userPassword = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet result = pstmt.executeQuery();
            
            // If a match is found, create a userData object with their info
            if (result.next()) {
                userData user = new userData(
                    result.getString("username"),
                    result.getString("email"),
                    result.getString("phone"),
                    result.getString("address"),
                    result.getString("userPassword"),
                    result.getString("role")
                );
                // Set the ID from the database
                user.setUserID(result.getInt("user_id")); 
                
                return user; // Return the fully populated user object
            }
            
        } catch (SQLException ex) {
            System.out.println("Error during login: " + ex.getMessage());
        } finally {
            db.closeConnection(conn);
        }
        
        return null; // Return null if login fails (wrong username or password)
    }

// Add this method inside your userDAO class
  // Notice we removed name and email from the parameters!
    public boolean updateProfile(int userId, String phone, String address) {
        
        // IMPORTANT: Change 'user_id' below to perfectly match your actual database column name!
        String sql = "UPDATE users SET phone = ?, address = ? WHERE user_id = ?"; 
        
        try (java.sql.Connection conn = db.openConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, phone);
            pstmt.setString(2, address);
            pstmt.setInt(3, userId); // This matches the WHERE clause
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; 
            
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // =========================================================================
    // UPDATE PROFILE DETAILS
    // =========================================================================

    public boolean updateUserProfile(int userId, String email, String phone, String address) {
        // Change column names here if your database uses different names!
        String sql = "UPDATE users SET email = ?, phone = ?, address = ? WHERE user_id = ?";
        
        try (java.sql.Connection conn = db.openConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, email);
            pstmt.setString(2, phone);
            pstmt.setString(3, address);
            pstmt.setInt(4, userId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
   // Inside DAO.userDAO
    public boolean changeUserPassword(int userId, String currentPassword, String newPassword) {
        
        // This query updates the password ONLY if the old password matches the database
        String sql = "UPDATE users SET userPassword = ? WHERE user_id = ? AND userPassword = ?";
        
        try (java.sql.Connection conn = db.openConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);
            pstmt.setString(3, currentPassword);
            
            // executeUpdate returns the number of rows affected. 
            // If it returns 1, the password was successfully changed!
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // =========================================================================
    // MANAGER DATA METHODS
    // =========================================================================

    // 1. Fetch a single user by their ID
    public Model.userData getUserById(int searchId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (java.sql.Connection conn = db.openConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, searchId);
            
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Model.userData user = new Model.userData();
                    user.setUserID(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setRole(rs.getString("role"));
                    return user; // Return the found user
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if user doesn't exist
    }

    // 2. Update specifically the Email and Phone
    public boolean updateUserEmailAndPhone(int userId, String newEmail, String newPhone) {
        String sql = "UPDATE users SET email = ?, phone = ? WHERE user_id = ?";
        
        try (java.sql.Connection conn = db.openConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, newEmail);
            pstmt.setString(2, newPhone);
            pstmt.setInt(3, userId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // =========================================================================
    // ADMIN DASHBOARD: ACTIVE EMPLOYEE COUNT
    // =========================================================================

    public int getActiveEmployeeCount() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'Employee'";
        try (java.sql.Connection conn = db.openConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = pstmt.executeQuery()) {
             
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
    