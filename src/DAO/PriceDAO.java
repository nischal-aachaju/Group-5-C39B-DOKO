package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import DB.Dbconnector; // Ensure this matches your actual DB connection file!

public class PriceDAO {
    
    private Dbconnector db = new Dbconnector();

    // =========================================================================
    // ADMIN: SAVE NEW PRICES
    // =========================================================================
    public boolean updatePricing(double perKg, double perMeter) {
        String sql = "UPDATE price_config SET price_per_kg = ?, price_per_meter = ? WHERE config_id = 1";
        
        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setDouble(1, perKg);
            pstmt.setDouble(2, perMeter);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================================
    // SENDER: FETCH LIVE PRICES FOR CALCULATOR
    // =========================================================================
    public double[] getLivePricing() {
        // Index 0 will hold the Price Per KG
        // Index 1 will hold the Price Per Meter
        double[] prices = new double[]{50.0, 0.05}; // Fallback defaults just in case
        
        String sql = "SELECT price_per_kg, price_per_meter FROM price_config WHERE config_id = 1";
        
        try (Connection conn = db.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            if (rs.next()) {
                prices[0] = rs.getDouble("price_per_kg");
                prices[1] = rs.getDouble("price_per_meter");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return prices;
    }
}