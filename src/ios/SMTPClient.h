//
//  SMTPClient.h
//  CustomPlugin
//
//  Created by Fernando Albernaz on 5/10/15.
//
//

#import <Cordova/CDV.h>

@interface SMTPClient : CDVPlugin

- (void) cordovaSendMail:(CDVInvokedUrlCommand*)command;

@property (nonatomic,strong) NSString* pluginCallbackId;

@end