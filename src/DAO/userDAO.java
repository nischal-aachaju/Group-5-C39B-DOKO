package DAO;

import DB.Dbconnector;
import java.sql.Connection;
import Model.userData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class userDAO {
    
    Dbconnector db = new Dbconnector();
    
    public void createUser(userData user){
        Connection conn = db.openConnection();
        String sql = "INSERT INTO users (username,email,phone,address,userPassword, role) values(?,?,?,?,?,?)";
        try (PreparedStatement pstm = conn.prepareStatement(sql)){
            pstm.setString(1, user.getUsername());
            pstm.setString(2, user.getEmail());    
            pstm.setString(3, user.getPhone());
            pstm.setString(4, user.getAddress());    
            pstm.setString(5, user.getPassword());
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
            
            if (result.next()) {
                userData user = new userData(
                    result.getString("username"),
                    result.getString("email"),
                    result.getString("phone"),
                    result.getString("address"),
                    result.getString("userPassword"),
                    result.getString("role")
                );
                user.setUserID(result.getInt("user_id")); 
                return user; 
            }
            
        } catch (SQLException ex) {
            System.out.println("Error during login: " + ex.getMessage());
        } finally {
            db.closeConnection(conn);
        }
        return null; 
    }

    public boolean updateProfile(int userId, String phone, String address) {
        String sql = "UPDATE users SET phone = ?, address = ? WHERE user_id = ?"; 
        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, phone);
            pstmt.setString(2, address);
            pstmt.setInt(3, userId); 
            
            return pstmt.executeUpdate() > 0; 
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUserProfile(int userId, String email, String phone, String address) {
        String sql = "UPDATE users SET email = ?, phone = ?, address = ? WHERE user_id = ?";
        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, email);
            pstmt.setString(2, phone);
            pstmt.setString(3, address);
            pstmt.setInt(4, userId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean changeUserPassword(int userId, String currentPassword, String newPassword) {
        String sql = "UPDATE users SET userPassword = ? WHERE user_id = ? AND userPassword = ?";
        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);
            pstmt.setString(3, currentPassword);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================================
    // ADMIN: MANAGE USER METHODS (NEW & UNIFIED)
    // =========================================================================

    public String[] getEmployeeDetails(int employeeId) {
        // Fetches data specifically for the AdminManageUserController
        String sql = "SELECT username, email, phone, role FROM users WHERE user_id = ?";
        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("role")
                    };
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }

    public boolean updateEmployeeContact(int employeeId, String newEmail, String newPhone) {
        // Updates data directly from the AdminManageUserController
        String sql = "UPDATE users SET email = ?, phone = ? WHERE user_id = ?";
        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, newEmail);
            pstmt.setString(2, newPhone);
            pstmt.setInt(3, employeeId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Model.userData getUserById(int searchId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, searchId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Model.userData user = new Model.userData();
                    user.setUserID(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setRole(rs.getString("role"));
                    return user; 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }

    public boolean updateUserEmailAndPhone(int userId, String newEmail, String newPhone) {
        String sql = "UPDATE users SET email = ?, phone = ? WHERE user_id = ?";
        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, newEmail);
            pstmt.setString(2, newPhone);
            pstmt.setInt(3, userId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================================
    // ADMIN DASHBOARD & PASSWORD RECOVERY
    // =========================================================================

    public int getActiveEmployeeCount() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'Employee'";
        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean verifyUserForReset(String fullname, String email) {
        String sql = "SELECT * FROM users WHERE username = ? AND email = ?";
        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, fullname);
            pstmt.setString(2, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean resetPassword(String email, String newPassword) {
        String sql = "UPDATE users SET userPassword = ? WHERE email = ?";
        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, newPassword);
            pstmt.setString(2, email);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}