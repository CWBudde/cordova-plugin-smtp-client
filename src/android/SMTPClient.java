package com.cordova.smtp.client;

import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
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
    public boolean execute(String action, final JSONArray rawArgs, final CallbackContext callback) {
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
     * 
     * 
     * @param args
     * @param callback
     * @throws JSONException
     */
    private void sendEmail(JSONArray args, CallbackContext callback) throws JSONException {
        callback.success("Message sent");
    }



    // /**
    //  * 
    //  * 
    //  * @param json
    //  * @throws Exception
    //  */
    // private void sendEmailViaGmail(JSONObject json) throws Exception {
    //     Mail m = new Mail(json.getString("smtpUserName"), json.getString("smtpPassword"));
    //     String[] toArr = json.getString("emailTo").split(",");
    //     String emailCC = json.optString("emailCC");
    //     String[] ccArr = (emailCC.isEmpty()) ? null : emailCC.split(",");
    //     m.set_to(toArr);
    //     m.set_cc(ccArr);
    //     m.set_host(json.getString("smtp"));
    //     m.set_from(json.getString("emailFrom"));
    //     m.set_body(json.getString("textBody"));
    //     m.set_subject(json.getString("subject"));

    //     JSONArray attachments = json.getJSONArray("attachments");
    //     if(attachments != null){
    //         for(int i=0; i < attachments.length(); i++){
    //             String fileFullName = attachments.getString(i);
    //             if(fileFullName.contains(":")){
    //                 fileFullName = fileFullName.split(":")[1];
    //             }
    //             m.addAttachment(fileFullName);
    //         }
    //     }
    //     boolean sendFlag = m.send();
    // }

}