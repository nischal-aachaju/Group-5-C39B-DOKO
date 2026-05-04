/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package group.pkg5.doko;

import Db.Dbconnector;
import Db.database;
import java.sql.Connection;
/**
 *
 * @author nischal
 */
public class Group5DOKO {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        database dbConnetion= new Dbconnector();
        dbConnetion.openConnection();
    }
    
}