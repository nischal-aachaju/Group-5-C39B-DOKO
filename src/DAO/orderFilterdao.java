/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package DAO;

import Db.Dbconnector;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class orderFilterdao {

    public void getAllOrders() {

        try {

            Dbconnector db = new Dbconnector();
            Connection con = db.openConnection();

            Statement stmt = con.createStatement();

            ResultSet rs =
                    stmt.executeQuery(
                            "SELECT * FROM orders");

            while (rs.next()) {

                System.out.println(
                        rs.getInt("order_id")
                        + " "
                        + rs.getString("customer_name")
                        + " "
                        + rs.getString("branch")
                        + " "
                        + rs.getString("status")
                );
            }

        } catch (Exception e) {

            System.out.println(e);
        }
    }

    public void getOrdersByStatus(String status) {

        try {

            Dbconnector db = new Dbconnector();
            Connection con = db.openConnection();

            Statement stmt = con.createStatement();

            ResultSet rs =
                    stmt.executeQuery(
                            "SELECT * FROM orders "
                            + "WHERE status='"
                            + status + "'");

            while (rs.next()) {

                System.out.println(
                        rs.getInt("order_id")
                        + " "
                        + rs.getString("customer_name")
                        + " "
                        + rs.getString("branch")
                        + " "
                        + rs.getString("status")
                );
            }

        } catch (Exception e) {

            System.out.println(e);
        }
    }

    public void getOrdersByBranch(String branch) {

        try {

            Dbconnector db = new Dbconnector();
            Connection con = db.openConnection();

            Statement stmt = con.createStatement();

            ResultSet rs =
                    stmt.executeQuery(
                            "SELECT * FROM orders "
                            + "WHERE branch='"
                            + branch + "'");

            while (rs.next()) {

                System.out.println(
                        rs.getInt("order_id")
                        + " "
                        + rs.getString("customer_name")
                        + " "
                        + rs.getString("branch")
                        + " "
                        + rs.getString("status")
                );
            }

        } catch (Exception e) {

            System.out.println(e);
        }
    }

    public void getOrdersByStatusAndBranch(
            String status,
            String branch) {

        try {

            Dbconnector db = new Dbconnector();
            Connection con = db.openConnection();

            Statement stmt = con.createStatement();

            ResultSet rs =
                    stmt.executeQuery(
                            "SELECT * FROM orders "
                            + "WHERE status='"
                            + status
                            + "' AND branch='"
                            + branch
                            + "'");

            while (rs.next()) {

                System.out.println(
                        rs.getInt("order_id")
                        + " "
                        + rs.getString("customer_name")
                        + " "
                        + rs.getString("branch")
                        + " "
                        + rs.getString("status")
                );
            }

        } catch (Exception e) {

            System.out.println(e);
        }
    }
}