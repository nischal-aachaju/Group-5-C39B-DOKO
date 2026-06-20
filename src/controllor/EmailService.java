package controllor; // Put this in a utility folder/package

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import Model.Order;

public class EmailService {

    // IMPORTANT: Replace with your actual system email and Google App Password!
    private static final String SYSTEM_EMAIL ="sthanischal5060@gmail.com";
    private static final String SYSTEM_PASSWORD = "zxuo xzci pzfo xreq"; 

    private static Session getEmailSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Required for Gmail

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SYSTEM_EMAIL, SYSTEM_PASSWORD);
            }
        });
    }

    // 1. EMAIL TRIGGERED ON CREATION (Pending Status)
    public static void sendOrderCreatedEmail(Order order) {
        try {
            Message message = new MimeMessage(getEmailSession());
            message.setFrom(new InternetAddress(SYSTEM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(order.getReceiverEmail()));
            message.setSubject("DOKO Logistics: Order Received! (Tracking ID: " + order.getTrackingId() + ")");

            String htmlContent = "<h3>Hello " + order.getReceiverName() + ",</h3>"
                    + "<p>Great news! Your order is currently being prepared and is awaiting assignment to a delivery driver.</p>"
                    + "<b>Order Details:</b><br>"
                    + "<ul>"
                    + "<li><b>Tracking ID:</b> " + order.getTrackingId() + "</li>"
                    + "<li><b>Description:</b> " + order.getDescription() + "</li>"
                    + "<li><b>Total Cost (COD):</b> Rs. " + order.getFinalBillAmount() + "</li>"
                    + "<li><b>Status:</b> Pending Assignment</li>"
                    + "</ul>"
                    + "<p>We will notify you as soon as a driver picks up your package!</p>"
                    + "<br><p>Thank you,<br>The DOKO Team</p>";

            message.setContent(htmlContent, "text/html");
            Transport.send(message);
            System.out.println("Creation email sent successfully to " + order.getReceiverEmail());

        } catch (Exception e) {
            System.out.println("Failed to send creation email: " + e.getMessage());
        }
    }

    // 2. EMAIL TRIGGERED ON STATUS CHANGE (In Transit, Delivered, etc.)
    public static void sendStatusUpdateEmail(String receiverEmail, String receiverName, String trackingId, String newStatus, String employeeName, String employeePhone) {
        try {
            Message message = new MimeMessage(getEmailSession());
            message.setFrom(new InternetAddress(SYSTEM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail));
            message.setSubject("DOKO Logistics: Status Update for Order #" + trackingId);

            String htmlContent = "<h3>Hello " + receiverName + ",</h3>"
                    + "<p>The status of your package (Tracking ID: <b>" + trackingId + "</b>) has been updated.</p>"
                    + "<h2>New Status: <span style='color:blue;'>" + newStatus.toUpperCase() + "</span></h2>";

            // If it's In Transit, inject the employee contact info!
            if (newStatus.equalsIgnoreCase("intransit") && employeeName != null) {
                htmlContent += "<p>Your package has been picked up and is on its way!</p>"
                             + "<b>Assigned Driver Details:</b><br>"
                             + "<ul>"
                             + "<li><b>Driver Name:</b> " + employeeName + "</li>"
                             + "<li><b>Driver Phone:</b> " + employeePhone + "</li>"
                             + "</ul>"
                             + "<p>Feel free to contact your driver if you need to coordinate the drop-off.</p>";
            }

            htmlContent += "<br><p>Thank you,<br>The DOKO Team</p>";

            message.setContent(htmlContent, "text/html");
            Transport.send(message);
            System.out.println("Update email sent successfully to " + receiverEmail);

        } catch (Exception e) {
            System.out.println("Failed to send update email: " + e.getMessage());
        }
    }
}