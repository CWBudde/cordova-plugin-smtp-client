# Cordova SMTP Client

Basic SMTP client cordova plugin for editing and sending email messages.

## Installation

Install iOS and/or Android platform

    cordova platform add ios
    cordova platform add android

Install the plugin using any plugman compatible cli

    $ cordova plugin add https://github.com/CWBudde/cordova-plugin-smtp-client.git

## Usage

On Javascript, use code that is similar to the following.

	var mailSettings = {
	    emailFrom: "emailFrom@domain.com",
	    emailTo: "emailTo@domain.com",
	    smtp: "smtp-mail.domain.com",
	    smtpUserName: "authuser@domain.com",
	    smtpPassword: "password",
	    attachments: ["attachment1", "attachment2"],
	    subject: "email subject",
	    textBody: "write something within the body of the email"
	};
	            
	var success = function(message) {
		alert(message);
	}
	
	var failure = function(message) {
		alert("Error sending the email");
	}			
				
	smtpClient.sendMail(mailSettings, success, failure);

### Attachments

The attachments is an array of strings.

On the iOS platform, it must use a [DATA_URI format](doc/attachments.md).

On the Android platform, it must be the path to the file.

### Return type
	
The return object "message" has the following structure

	{
		success: boolean,
		errorCode: number,
		errorMessage: string	    
	}

## Copyright

The library was originally written by albernazf ([cordova-smtp-client](https://github.com/albernazf/cordova-smtp-client)) and later modified by Nelson Medina Humberto ([cordova-smtp-client](https://github.com/nelsonhumberto/cordova-smtp-client/)).

On iOS it makes use of the skpsmtpmessage library, which was originally written by Ian Baird. A recent fork can be found on ([skpsmtpmessage](https://github.com/jetseven/skpsmtpmessage)).
