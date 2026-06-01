/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

//import DB.Dbconnector;
import Db.Dbconnector;
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
    
}
