package com.cordova.smtp.client;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;

public class Mail {

    // Object properties
    private String fromEmail;
    private String[] toEmails;

    private String host;
    private String port;
    
    private boolean auth;
    private String user;
    private String password;

    private int encryption; // O: None, 1: SSL, 2: TLS

    private String subject;
    private String body;
    
    // Getters and setters
    public String getFromEmail() {
        return this.fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String[] getToEmails() {
        return this.toEmails;
    }

    public void setToEmails(String[] toEmails) {
        this.toEmails = toEmails;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return Integer.parseInt(this.port);
    }

    public void setPort(int port) {
        this.port = Integer.toString(port);
    }

    public boolean isAuth() {
        return this.auth;
    }

    public boolean getAuth() {
        return this.auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getEncryption() {
        return this.encryption;
    }

    public void setEncryption(int encryption) {
        this.encryption = encryption;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    // Constructors
    public Mail() {
        this.fromEmail = "";

        this.host = "";
        this.port = "25";
        
        this.auth = false;
        this.user = "";
        this.password = "";

        // O: None, 1: SSL, 2: TLS
        this.encryption = 0;
    }

    public Mail(String user, String password) {
        this();

        this.user = user;
        this.password = password;
    }

    // Methods
    public void send() throws Exception {
        MimeMessage msg = new MimeMessage(this.getSession());
        
        // Set message headers
        msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
        msg.addHeader("format", "flowed");
        msg.addHeader("Content-Transfer-Encoding", "8bit");

        msg.setFrom(new InternetAddress(this.fromEmail));
        msg.setSubject(this.subject, "UTF-8");
        msg.setText(this.body, "UTF-8");
        msg.setSentDate(new Date());

        if (this.toEmails != null && this.toEmails.length > 0) {
            InternetAddress[] addressesTo = new InternetAddress[this.toEmails.length];
            for (int i = 0; i < this.toEmails.length; i++) {
                addressesTo[i] = new InternetAddress(this.toEmails[i]);
            }
            msg.setRecipients(Message.RecipientType.TO, addressesTo);
        }
        
        Transport.send(msg);  
    }

    /**
     * 
     * 
     * @return
     */
    private Session getSession() throws Exception {
        // No authentication
        if (!this.auth) {
            Properties props = System.getProperties();
            props.put("mail.smtp.host", this.host);
            return Session.getInstance(props, null);
        } 

        // Authentication
        Properties props = new Properties();
        props.put("mail.smtp.host", this.host); // SMTP Host
        props.put("mail.smtp.port", this.port); // SMTP Port
        props.put("mail.smtp.auth", "true"); // Enabling SMTP Authentication
        final String usr = this.user;
        final String psw = this.password;
        Authenticator auth = new Authenticator() {
            // Override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usr, psw);
            }
        };
        if (this.encryption == 2) {
            // TLS Authentication
            props.put("mail.smtp.starttls.enable", "true"); // Enable StartTLS
            return Session.getInstance(props, auth);
        } else if (encryption == 1) {
            // SSL Authentication            
            props.put("mail.smtp.socketFactory.port", this.port); // SSL Port
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // SSL factory class
            return Session.getDefaultInstance(props, auth);
        }

        throw new Exception("Session could not be obtained");
    }
}
