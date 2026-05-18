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
// data access object
/**
 *
 * @author nischal
 */
public class userDAO {
    Dbconnector db=new Dbconnector();
    
    public void createUser(userData user){
            Connection conn=db.openConnection();
            String sql="INSERT INTO users (username,email,password) values(?,?,?)";
        try (PreparedStatement pstm=conn.prepareStatement(sql)){
            pstm.setString(1,user.getUsername());
            pstm.setString(2,user.getEmail());    
            pstm.setString(3,user.getPassword());
            
        } catch(Exception e){
            System.out.println(e);
            
        } finally{
            db.closeConnection(conn);
        }
    }
    
    
}
