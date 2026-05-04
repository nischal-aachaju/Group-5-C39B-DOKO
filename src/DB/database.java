/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Db;


import java.sql.Connection;
import java.sql.ResultSet;
/**
 *
 * @author nischal
 */
public interface database {
    Connection openConnection();
    void closeConnection(Connection conn);
    ResultSet runQuery();
    int executeUpdate();
}