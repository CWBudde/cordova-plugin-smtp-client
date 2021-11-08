package com.cordova.smtp.client;

import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

public class SMTPClient extends CordovaPlugin {
    public final String ACTION_SEND_EMAIL = "cordovaSendMail";

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
        Log.i(TAG, "Running executeInternal(), action: " + action + ", rawArgs: " + rawArgs);

        if (ACTION_SEND_EMAIL.equals(action)) {
            threadHelper(new SMTPFunction() {
                @Override
                public void run(JSONArray args, CallbackContext callback) throws Exception {
                    sendEmail(args, callback);
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
     * 
     * 
     * @param args
     * @param callback
     */
    private void sendEmail(JSONArray args, CallbackContext callback) {
        try {
            JSONObject smtpSettings = new JSONObject(args.getString(0));
            String user = smtpSettings.getString("user");
            String password = smtpSettings.getString("password");
            String fromEmail = smtpSettings.getString("fromEmail");
            JSONArray toEmailsJSONArray = smtpSettings.getJSONArray("toEmails");
            String[] toEmails = new String[toEmailsJSONArray.length()];
            for (int i = 0; i < toEmailsJSONArray.length(); i++) {
                toEmails[i] = toEmailsJSONArray.getString(i);
            }
            String host = smtpSettings.getString("host");
            int port = smtpSettings.getInt("port");
            boolean auth = smtpSettings.getBoolean("auth");
            int encryption = smtpSettings.getInt("encryption");
            String subject = smtpSettings.getString("subject");
            String body = smtpSettings.getString("body");

            Mail mail = new Mail(user, password);
            mail.setFromEmail(fromEmail);
            mail.setToEmails(toEmails);
            mail.setHost(host);
            mail.setPort(port);
            mail.setAuth(auth);
            mail.setEncryption(encryption);
            mail.setSubject(subject);
            mail.setBody(body);
            
            mail.send();
            callback.success("Message sent");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            callback.error("An error occurred while trying to send the email");
        }
    }
}
