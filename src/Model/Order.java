/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.util.Random;

public class Order {
    private String trackingId;
    private String receiverName;
    private String receiverEmail;
    private String receiverContact;
    private String receiverLocation;
    private String street;
    private double weight;
    private double declaredCost; 
    private String description;
    private String status;

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }
    private final double deliveryCost = 200.0; // FIXED DEFAULT COST
    private double finalBillAmount; 

public Order(String trackingId, String name, String email, String contact, String location, 
                 String street, double weight, double declaredCost, String description) {
        
        this.trackingId = trackingId; // Set from the parameter now!
        
        this.receiverName = name;
        this.receiverEmail = email;
        this.receiverContact = contact;
        this.receiverLocation = location;
        this.street = street;
        this.weight = weight;
        this.declaredCost = declaredCost;
        this.description = description;
        
        this.finalBillAmount = this.declaredCost + this.deliveryCost; 
    }

    // --- GETTERS ---
    public String getTrackingId(){return trackingId;}
    public String getReceiverName() { return receiverName; }
    public String getReceiverEmail() { return receiverEmail; }
    public String getReceiverContact() { return receiverContact; }
    public String getReceiverLocation() { return receiverLocation; }
    public String getStreet() { return street; }
    public double getWeight() { return weight; }
    public double getDeclaredCost() { return declaredCost; }
    public String getDescription() { return description; }
    public double getDeliveryCost() { return deliveryCost; }
    public double getFinalBillAmount() { return finalBillAmount; }
    
}