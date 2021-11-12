package com.cordova.smtp.client;

import android.util.Log;
import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SMTPClient extends CordovaPlugin {
    public final String ACTION_SEND_EMAIL = "cordovaSendMail";
    public final String ACTION_TEST_CONNECTION = "cordovaTestConnection";
    
    private static final String TAG = "SMTPClient";
    private CallbackContext callback;
    private String action;
    private String rawArgs;

    private interface SMTPFunction {
        void run(JSONArray args, CallbackContext callback) throws Exception;
    }

    @Override
    public boolean execute(String action, String rawArgs, CallbackContext callback) {
        this.callback = callback;
        this.action = action;
        this.rawArgs = rawArgs;

        return executeInternal(action, rawArgs, callback);
    }

    private boolean executeInternal(String action, String rawArgs, CallbackContext callback) {
        Log.i(TAG, "Running executeInternal(), action: " + action);

        if (ACTION_SEND_EMAIL.equals(action)) {
            threadHelper(new SMTPFunction() {
                @Override
                public void run(JSONArray args, CallbackContext callback) throws Exception {
                    sendMail(args, callback);
                }
            }, rawArgs, callback);
            return true;
        }

        if (ACTION_TEST_CONNECTION.equals(action)) {
            threadHelper(new SMTPFunction() {
                @Override
                public void run(JSONArray args, CallbackContext callback) throws Exception {
                    testConnection(args, callback);
                }
            }, rawArgs, callback);
            return true;
        }

        return false;
    }

    /*
     * Helper to execute functions async and handle the result codes.
     */
    private void threadHelper(final SMTPFunction f, final String rawArgs, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray args = new JSONArray(rawArgs);
                    f.run(args, callbackContext);
                } catch (Exception e) {
                    logError(callbackContext, "Got unknown error in SMTPClient plugin", e);
                }
            }
        });
    }

    /**
     * Helper function to log to Logcat and log back to plugin result.
     */
    private void logError(final CallbackContext callbackContext, final String msg, final Exception e) {
        Log.e(TAG, msg, e);
        callbackContext.error(msg + ": " + e.getMessage());
    }


    /**
     * Sends the email based on the arguments passed as a parameter.
     */
    private void sendMail(JSONArray args, CallbackContext callback) {
        try {
            JSONObject smtpSettings = new JSONObject(args.getString(0));
            Mail mail = this.getMailObject(smtpSettings, false);
            mail.send();
            callback.success("Email sent");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            callback.error("An error occurred while trying to send the email");
        }
    }

    /**
     * Allows to test an SMTP connection.
     */
    private void testConnection(JSONArray args, CallbackContext callback) {
        try {
            JSONObject smtpSettings = new JSONObject(args.getString(0));
            Mail mail = this.getMailObject(smtpSettings, true);
            mail.testConnection();
            callback.success("Successful connection");
        } catch (AuthenticationFailedException e) {
            Log.e(TAG, e.getMessage(), e);
            callback.error("AuthenticationFailedException");
        } catch (MessagingException e) {
            Log.e(TAG, e.getMessage(), e);
            callback.error("MessagingException");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            callback.error("An error occurred while trying to test the connection");
        }
    }

    /**
     * Creates an object of type Mail and assigns its attributes based on the smtpSettings's values.
     */
    private Mail getMailObject(JSONObject smtpSettings, boolean testingConnection) throws JSONException {
        String user = smtpSettings.getString("user");
        String password = smtpSettings.getString("password");
        String host = smtpSettings.getString("host");
        int port = smtpSettings.getInt("port");
        boolean auth = smtpSettings.getBoolean("auth");
        int encryption = smtpSettings.getInt("encryption");

        Mail mail = new Mail(user, password);
        mail.setHost(host);
        mail.setPort(port);
        mail.setAuth(auth);
        mail.setEncryption(encryption);
        
        if (!testingConnection) {
            String fromEmail = smtpSettings.getString("fromEmail");
            JSONArray toEmailsJSONArray = smtpSettings.getJSONArray("toEmails");
            String[] toEmails = new String[toEmailsJSONArray.length()];
            for (int i = 0; i < toEmailsJSONArray.length(); i++) {
                toEmails[i] = toEmailsJSONArray.getString(i);
            }
            String subject = smtpSettings.getString("subject");
            String body = smtpSettings.getString("body");
            JSONArray attachmentsJSONArray = smtpSettings.getJSONArray("attachments");
            Attachment[] attachments = new Attachment[attachmentsJSONArray.length()];
            for (int i = 0; i < attachmentsJSONArray.length(); i++) {
                JSONObject attachmentJSONObject = attachmentsJSONArray.getJSONObject(i);
                Attachment attachment = new Attachment(attachmentJSONObject.getString("path"), attachmentJSONObject.getString("name"));
                attachments[i] = attachment;
            }
            mail.setFromEmail(fromEmail);
            mail.setToEmails(toEmails);
            mail.setSubject(subject);
            mail.setBody(body);
            mail.setAttachments(attachments);
        }

        return mail;
    }
}
