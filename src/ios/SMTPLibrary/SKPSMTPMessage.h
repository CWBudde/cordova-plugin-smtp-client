//
//  SKPSMTPMessage.h
//
//  Created by Ian Baird on 10/28/08.
//
//  Copyright (c) 2008 Skorpiostech, Inc. All rights reserved.
//
//  Permission is hereby granted, free of charge, to any person
//  obtaining a copy of this software and associated documentation
//  files (the "Software"), to deal in the Software without
//  restriction, including without limitation the rights to use,
//  copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the
//  Software is furnished to do so, subject to the following
//  conditions:
//
//  The above copyright notice and this permission notice shall be
//  included in all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
//  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
//  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
//  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
//  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
//  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
//  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
//  OTHER DEALINGS IN THE SOFTWARE.
//

#import <CFNetwork/CFNetwork.h>

typedef enum
{
    kSKPSMTPIdle = 0,
    kSKPSMTPConnecting,
    kSKPSMTPWaitingEHLOReply,
    kSKPSMTPWaitingTLSReply,
    kSKPSMTPWaitingLOGINUsernameReply,
    kSKPSMTPWaitingLOGINPasswordReply,
    kSKPSMTPWaitingAuthSuccess,
    kSKPSMTPWaitingFromReply,
    kSKPSMTPWaitingToReply,
    kSKPSMTPWaitingForEnterMail,
    kSKPSMTPWaitingSendSuccess,
    kSKPSMTPWaitingQuitReply,
    kSKPSMTPMessageSent
} SKPSMTPState;

// Message part keys
extern NSString *const kSKPSMTPPartContentDispositionKey;
extern NSString *const kSKPSMTPPartContentTypeKey;
extern NSString *const kSKPSMTPPartMessageKey;
extern NSString *const kSKPSMTPPartContentTransferEncodingKey;

// Error message codes
#define kSKPSMPTErrorConnectionTimeout -5
#define kSKPSMTPErrorConnectionFailed -3
#define kSKPSMTPErrorConnectionInterrupted -4
#define kSKPSMTPErrorUnsupportedLogin -2
#define kSKPSMTPErrorTLSFail -1
#define kSKPSMTPErrorNonExistentDomain 1
#define kSKPSMTPErrorInvalidUserPass 535
#define kSKPSMTPErrorInvalidMessage 550
#define kSKPSMTPErrorNoRelay 530

@class SKPSMTPMessage;

typedef void (^SKPSMTPMessageCompletionHandler)(SKPSMTPMessage *message, NSError *error);

@protocol SKPSMTPMessageDelegate
@required

-(void)messageSent:(SKPSMTPMessage *)message;
-(void)messageFailed:(SKPSMTPMessage *)message error:(NSError *)error;

@end

@interface SKPSMTPMessage : NSObject <NSCopying>

@property(nonatomic, retain) NSString *login;
@property(nonatomic, retain) NSString *pass;
@property(nonatomic, retain) NSString *relayHost;
@property(nonatomic, retain) NSArray *relayPorts;
@property(nonatomic, assign) BOOL requiresAuth;
@property(nonatomic, assign) BOOL wantsSecure;
@property(nonatomic, assign) BOOL validateSSLChain;

@property(nonatomic, retain) NSString *subject;
@property(nonatomic, retain) NSString *fromEmail;
@property(nonatomic, retain) NSString *toEmail;
@property(nonatomic, retain) NSString *ccEmail;
@property(nonatomic, retain) NSString *bccEmail;
@property(nonatomic, retain) NSArray *parts;

@property(nonatomic, assign) NSTimeInterval connectTimeout;

@property(nonatomic, assign) id <SKPSMTPMessageDelegate> delegate;

- (BOOL)send;
- (BOOL)sendWithCompletionHandler:(SKPSMTPMessageCompletionHandler)handler;

@end
