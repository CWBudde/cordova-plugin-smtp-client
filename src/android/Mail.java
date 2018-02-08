package com.cordova.smtp.client;

import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Mail extends javax.mail.Authenticator {
    private String _user;
    private String _pass;

    private String[] _to;
    private String[] _cc;
    private String _from;

    private String _port;
    private String _sport;

    private String _host;

    private String _subject;
    private String _body;

    private boolean _auth;
    private boolean _ssl;

    private boolean _debuggable;

    private Multipart _multipart;

    public Mail() {
        _host = ""; // default smtp server
        _port = "587"; // default smtp port
        _sport = "587"; // default socketfactory port

        _user = ""; // username
        _pass = ""; // password
        _from = ""; // email sent from
        _subject = ""; // email subject
        _body = ""; // email body
        _ssl = false;

        _debuggable = false; // debug mode on or off - default off
        _auth = true; // smtp authentication - default on

        _multipart = new MimeMultipart();

        // There is something wrong with MailCap, javamail can not find a handler for the multipart/mixed part, so this bit needs to be added.
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
    }

    public Mail(String user, String pass) {
        this();

        _user = user;
        _pass = pass;
    }

    public boolean send() throws Exception {
        Properties props = _setProperties();

        if (!_user.equals("") && !_pass.equals("") && _to.length > 0 && !_from.equals("") && !_subject.equals("") && !_body.equals("")) {
            Session session = Session.getInstance(props, this);

            MimeMessage msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(_from));

            InternetAddress[] addressTo = new InternetAddress[_to.length];
            for (int i = 0; i < _to.length; i++) {
                addressTo[i] = new InternetAddress(_to[i]);
            }
            msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);

			if((_cc != null)){
				InternetAddress[] addressCC = new InternetAddress[_cc.length]; 
				for (int i = 0; i < _cc.length; i++) { 
					addressCC[i] = new InternetAddress(_cc[i]); 
				}
				msg.setRecipients(MimeMessage.RecipientType.CC, addressCC);
			}
			
            msg.setSubject(_subject);
            msg.setSentDate(new Date());

            // setup message body
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(_body);
            _multipart.addBodyPart(messageBodyPart);

            // Put parts in message
            // msg.setContent(_multipart);
			msg.setContent(_body,"text/html; charset=utf-8"); 

            // send email
            Transport.send(msg);

            return true;
        } else {
            return false;
        }
    }

    public void addAttachment(String filename) throws Exception {
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);

        _multipart.addBodyPart(messageBodyPart);
    }

    public String[] get_to() {
        return _to;
    }

    public void set_to(String[] _to) {
        this._to = _to;
    }

	public String[] get_cc() {
		return _cc;
	}

	public void set_cc(String[] _cc) {
		this._cc = _cc;
	}

    public void set_ssl(boolean _ssl) {
        this._ssl = _ssl;
    }

    public String get_from() {
        return _from;
    }

    public void set_from(String _from) {
        this._from = _from;
    }

    public void set_auth(boolean _auth) {
        this._auth = _auth;
    }

    public void set_port(int _port) {
        this._port = Integer.toString(_port);
    }

    public void set_sport(int _sport) {
        this._sport = Integer.toString(_sport);
    }

    public void set_host(String _host) {
        this._host = _host;
    }

    public String get_body() {
        return _body;
    }

    public void set_body(String _body) {
        this._body = _body;
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(_user, _pass);
    }

    private Properties _setProperties() {
        Properties props = new Properties();

        props.put("mail.smtp.host", _host);
        props.put("mail.smtp.port", _port);
        if(_auth){
            props.put("mail.smtp.auth", "true");
            if(_ssl){
                props.put("mail.smtp.socketFactory.port", _sport);
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            }
            else {
                props.put("mail.smtp.starttls.enable", "true");
            }
        }

        return props;
    }

    public String get_subject() {
        return _subject;
    }

    public void set_subject(String _subject) {
        this._subject = _subject;
    }

    // more of the getters and setters
}