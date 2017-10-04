# Cordova SMTP Client

Basic SMTP client cordova plugin for editing and sending email messages.

## Using

Install iOS and/or Android platform

    cordova platform add ios
    cordova platform add android

Install the plugin using any plugman compatible cli

    $ cordova plugin add https://github.com/CWBudde/cordova-plugin-smtp-client.git

On your javascript call use a code similar to this.

	var mailSettings = {
	    emailFrom: "emailFrom@domain.com",
	    emailTo: "emailTo@domain.com",
	    smtp: "smtp-mail.domain.com",
	    smtpUserName: "authuser@domain.com",
	    smtpPassword: "password",
	    attachments: ["attchament1","attchament2"],
	    subject: "email subject",
		textBody: "write something within the boddy of the email"
	};
	            
	var success = function(message) {
		alert(message);
	}
	
	var failure = function(message) {
		alert("Error sending the email");
	}			
				
	smtpClient.sendMail(mailSettings, success, failure);

The attachments is an array of strings where when using IOS the files needs to be in DATA_URI format and when Android should be the path of the file.
	
The return object "message" has the following structure

	{
	  success: bool,
		errorCode: int,
		errorMessage: string	    
	}

## Copyright

The library was originally written by albernazf (https://github.com/albernazf/cordova-smtp-client) and later modified by Nelson Medina Humberto (https://github.com/nelsonhumberto/cordova-smtp-client/). On iOS it makes use of the skpsmtpmessage library, which was originally written by Ian Baird. A recent fork can be found here: https://github.com/jetseven/skpsmtpmessage  
