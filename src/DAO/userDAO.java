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
}
