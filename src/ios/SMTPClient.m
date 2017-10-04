#import "SMTPClient.h"
#import "SKPSMTPMessage.h"
#import "NSData+Base64Additions.h"

@implementation SMTPClient

- (void)cordovaSendMail:(CDVInvokedUrlCommand*)command
{
	self.pluginCallbackId = command.callbackId;
 
    NSString *args = [command.arguments objectAtIndex:0];
    NSData *objectData = [args dataUsingEncoding:NSUTF8StringEncoding];
	NSError *jsonError;
    NSDictionary *json = [NSJSONSerialization JSONObjectWithData:objectData
                          options:NSJSONReadingMutableContainers
                          error:&jsonError];

    NSString *from = [json objectForKey:@"emailFrom"];
    NSString *to = [json objectForKey:@"emailTo"];
	NSString *bcc = [json objectForKey:@"bcc"];
    NSString *smtpServer = [json objectForKey:@"smtp"];
    NSString *smtpUser = [json objectForKey:@"smtpUserName"];
    NSString *smtpPassword = [json objectForKey:@"smtpPassword"];
	NSString *textBody = [json objectForKey:@"textBody"];
    
    SKPSMTPMessage *message = [[SKPSMTPMessage alloc] init];
    
    message.fromEmail = from;
    message.toEmail = to;
	if (bcc == (id)[NSNull null] || bcc.length == 0 ){
		message.bccEmail = nil;
	}
	else{
		message.bccEmail = bcc;		
	}

    message.relayHost = smtpServer;
    //message.requiresAuth = [json objectForKey:@"smtpRequiresAuth"];
	message.requiresAuth = true;
    
    if (message.requiresAuth) {
        message.login = smtpUser;
        message.pass = smtpPassword;
    }
    
    message.wantsSecure = true; // smtp.gmail.com doesn't work without TLS!
    
    message.subject = [json objectForKey:@"subject"];
    
    // Only do this for self-signed certs, test only
    // testMsg.validateSSLChain = NO;

    message.delegate = self;
    
	
    NSDictionary *plainPart = [NSDictionary dictionaryWithObjectsAndKeys:@"text/plain; charset=UTF-8",kSKPSMTPPartContentTypeKey,
                               textBody,kSKPSMTPPartMessageKey,@"8bit",kSKPSMTPPartContentTransferEncodingKey,nil];
    
    NSMutableArray *partsToSend = [NSMutableArray arrayWithObjects:plainPart,nil];
    
    NSArray *files = [json objectForKey:@"attachmentsInBase64Format"];

    NSError *error = nil;
	
	NSRegularExpression *regex = [NSRegularExpression regularExpressionWithPattern:@"\/(.*)\;" options:NSRegularExpressionCaseInsensitive error:&error];

    
    for (NSString *file in files) {
		
       	NSArray* imageDataSplitByComma = [file componentsSeparatedByString:@","];
	
		NSTextCheckingResult *match = [regex firstMatchInString:file
		                                     options:0
		                                     range:NSMakeRange(0, [file length])];
        
		NSMutableString *fileName = [NSMutableString stringWithString:@"attachment."];
		if (match) {
		    [fileName appendString:[file substringWithRange:[match rangeAtIndex:1]]];
		}
		else{
		    [fileName appendString:@"jpg"];
		}
        
		NSMutableString *attachedFilename = [NSMutableString stringWithString:@"text/directory;\r\n\tx-unix-mode=0644;\r\n\tname=\""] ;
		[attachedFilename appendString:fileName];
        
		NSData *fileData = [NSData dataFromBase64String:[imageDataSplitByComma objectAtIndex:1]];
        
		NSMutableString *attachementString = [NSMutableString stringWithString:@"attachment;\r\n\tfilename=\""];
		[attachementString appendString:fileName];
		[attachementString appendString:@"\""];

        
		NSDictionary *filePart = [NSDictionary dictionaryWithObjectsAndKeys:attachedFilename,        kSKPSMTPPartContentTypeKey,
		                          attachementString,kSKPSMTPPartContentDispositionKey,[fileData encodeBase64ForData],kSKPSMTPPartMessageKey,@"base64",kSKPSMTPPartContentTransferEncodingKey,nil];
        
		[partsToSend addObject:filePart];
    }
    
    message.parts = partsToSend;

    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
       [message send];
    });
}

- (void)messageSent:(SKPSMTPMessage *)message
{
    //NSLog(@"delegate - message sent");
	
    NSDictionary *jsonObj = [ [NSDictionary alloc]
                             initWithObjectsAndKeys :
                             @"", @"errorMessage",
                             @"true", @"success",
							 @"0", @"errorCode",
                             nil ];
	
	CDVPluginResult *pluginResult = [ CDVPluginResult
	                                     resultWithStatus    : CDVCommandStatus_OK
	                                     messageAsString : jsonObj
	                                     ];
	
	[self.commandDelegate sendPluginResult:pluginResult callbackId:self.pluginCallbackId];
}

- (void)messageFailed:(SKPSMTPMessage *)message error:(NSError *)error
{
    
    NSString* errorMessage = [NSString stringWithFormat:@"Darn! Error!\n%i: %@\n%@", [error code], [error localizedDescription], [error localizedRecoverySuggestion]];
    
    
    NSDictionary *jsonObj = [ [NSDictionary alloc]
                              initWithObjectsAndKeys :
                              errorMessage , @"errorMessage",
                              @"false", @"success",
                              [error code],@"errorCode",
                              nil ];
	CDVPluginResult *pluginResult = [ CDVPluginResult
	                                  resultWithStatus : CDVCommandStatus_OK
	                                  messageAsString : jsonObj];			  
	
	[self.commandDelegate sendPluginResult:pluginResult callbackId:self.pluginCallbackId];
	
    //self.textView.text = [NSString stringWithFormat:@"Darn! Error: %@, %@", [error code], [error localizedDescription]];
    //self.textView.text = [NSString stringWithFormat:@"Darn! Error!\n%i: %@\n%@", [error code], [error localizedDescription], [error localizedRecoverySuggestion]];
    //[message release];
    
    //NSLog(@"delegate - error(%d): %@", [error code], [error localizedDescription]);
}

@end