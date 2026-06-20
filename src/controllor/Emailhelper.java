package controllor; // Make sure this matches your folder name

import java.util.Properties;
import java.util.Random;

// These are the ONLY imports you need for JavaMail
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Emailhelper {

    // Generates a random 6-character password
    public static String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) { 
            int index = (int) (rnd.nextFloat() * chars.length());
            salt.append(chars.charAt(index));
        }
        return salt.toString();
    }

    // Sends the email
    public static boolean sendEmail(String toEmail, String generatedPassword,String name) {
        
        // TODO: Put your actual Gmail and 16-digit App Password here
        final String fromEmail = "sthanischal5060@gmail.com"; // enter your email 
        final String password = "zxuo xzci pzfo xreq";  // enter your email key

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); 
        props.put("mail.smtp.port", "587"); 
        props.put("mail.smtp.auth", "true"); 
        props.put("mail.smtp.starttls.enable", "true"); 

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        
        Session session = Session.getInstance(props, auth);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromEmail));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            msg.setSubject("Your DOKO App Verification Password");
            msg.setText("Dear " + name + ",\n\nWelcome to DOKO!\n\nYour generated verification password / OTP  is: " + generatedPassword + "\n\nPlease enter this password in the app to complete your registration.");

            
            Transport.send(msg);
            return true; 
            
        } catch (Exception e) {
            e.printStackTrace();
            return false; 
        }
    }

//    public static void main (String[] args){
//        // Because generatePassword() is a "static" method, you don't even 
//        // need to write 'new Emailhelper()'. You can call it directly:
//        String pass = Emailhelper.generatePassword();
//        System.out.println("Test Password Generated: " + pass);
//    }
}