package Model;

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
    private double deliveryCost;

    // CONSTRUCTOR
    public Order(String trackingId, String name, String email, String contact, String location, 
                 String street, double weight, double declaredCost, String description) {
        
        this.trackingId = trackingId; 
        this.receiverName = name;
        this.receiverEmail = email;
        this.receiverContact = contact;
        this.receiverLocation = location;
        this.street = street;
        this.weight = weight;
        this.declaredCost = declaredCost;
        this.description = description;
        this.deliveryCost = 0.0; // Defaults to 0 until the DAO sets it
    }

    // --- GETTERS & SETTERS ---
    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }
    
    public void setDeliveryCost(double deliveryCost) { this.deliveryCost = deliveryCost;}
    public double getDeliveryCost() { return deliveryCost; }

    public String getTrackingId() { return trackingId; }
    public String getReceiverName() { return receiverName; }
    public String getReceiverEmail() { return receiverEmail; }
    public String getReceiverContact() { return receiverContact; }
    public String getReceiverLocation() { return receiverLocation; }
    public String getStreet() { return street; }
    public double getWeight() { return weight; }
    public double getDeclaredCost() { return declaredCost; }
    public String getDescription() { return description; }
    
    // --- DYNAMIC MATH ---
    // This perfectly calculates the COD no matter when the delivery cost is loaded!
    public double getFinalBillAmount() { 
        return this.declaredCost + this.deliveryCost; 
    }
}