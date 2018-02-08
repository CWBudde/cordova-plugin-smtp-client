package com.cordova.smtp.client;

import android.os.Environment;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class SMTPClient extends CordovaPlugin {
    public final String ACTION_SEND_EMAIL = "cordovaSendMail";

    @Override
    public boolean execute(String action, JSONArray arg1, CallbackContext callbackContext) {
//PluginResult result = new PluginResult(Status.INVALID_ACTION);
        if (action.equals(ACTION_SEND_EMAIL)) {
            try {
//String message = arg1.getString(0);
                JSONObject json = new JSONObject(arg1.getString(0));

                String state = Environment.getExternalStorageState();
                this.sendEmailViaGmail(json);
                callbackContext.success();
                return true;
            } catch (JSONException ex) {
                callbackContext.error(ex.getMessage());
                return false;
            } catch (Exception e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
    }

    private void sendEmailViaGmail(JSONObject json) throws Exception {

        Mail m = new Mail(json.getString("smtpUserName"), json.getString("smtpPassword"));
        String[] toArr = {json.getString("emailTo")};
        m.set_to(toArr);
        m.set_host(json.getString("smtp"));
        m.set_from(json.getString("emailFrom"));
        m.set_body(json.getString("textBody"));
        m.set_subject(json.getString("subject"));

        JSONArray attachments = json.getJSONArray("attachments");
        if(attachments != null){
            for(int i=0; i < attachments.length(); i++){
                String fileFullName = attachments.getString(i);
                if(fileFullName.contains(":")){
                    fileFullName = fileFullName.split(":")[1];
                }
                m.addAttachment(fileFullName);
            }
        }

        boolean sendFlag = m.send();

    }

}