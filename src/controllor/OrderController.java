/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllor;

/**
 *
 * @author Acer
 */

import DAO.orderFilterdao;

public class OrderController {

    private orderFilterdao d;

    public OrderController() {

        d = new orderFilterdao();
    }

    public void showAllOrders() {

        d.getAllOrders();
    }

    public void filterByStatus(String status) {

        d.getOrdersByStatus(status);
    }

    public void filterByBranch(String branch) {

        d.getOrdersByBranch(branch);
    }

    public void filterByStatusAndBranch(
            String status,
            String branch) {

        d.getOrdersByStatusAndBranch(
                status,
                branch);
    }
}