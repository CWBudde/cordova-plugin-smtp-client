package com.cordova.smtp.client;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
//import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class SMTPClient extends CordovaPlugin {
    public final String ACTION_SEND_EMAIL = "cordovaSendMail";
    private final SMTPClient context = this;

    @Override
    public boolean execute(String action, final JSONArray arg1, final CallbackContext callbackContext) {
//PluginResult result = new PluginResult(Status.INVALID_ACTION);
        if (action.equals(ACTION_SEND_EMAIL)) {
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            final Handler mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {
                    // This is where you do your work in the UI thread.
                    // Your worker tells you in the message what to do.

                    /*if (message.what == 0) {
                        Toast.makeText(cordova.getActivity(),
                                "Email was sent successfully.",
                                Toast.LENGTH_LONG).show();
                    } else if (message.what == 1){
                        Toast.makeText(cordova.getActivity(),
                                "Email was not sent.", Toast.LENGTH_LONG)
                                .show();
                    }
                    else{
                        Toast.makeText(cordova.getActivity(),
                                "There was a problem sending the email. " + message.obj,
                                Toast.LENGTH_LONG).show();
                    }*/
                }
            };
            this.cordova.getThreadPool().execute (new Runnable() {
                public void run() {
                    try {
//String message = arg1.getString(0);
                        JSONObject json = new JSONObject(arg1.getString(0));

                        String state = Environment.getExternalStorageState();
                        context.sendEmailViaGmail(json);
                        Message message = mHandler.obtainMessage(0, null);
                        message.sendToTarget();
                        callbackContext.success();
//                        return true;
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        Message message = mHandler.obtainMessage(2, ex);
                        message.sendToTarget();
                        callbackContext.error(ex.getMessage());
//                        return false;
                    } catch (Exception e) {
// TODO Auto-generated catch block
                        e.printStackTrace();
                        Message message = mHandler.obtainMessage(2, e);
                        message.sendToTarget();
                        callbackContext.error(e.getMessage());
                    }
                }
            });
        };
        return true;
    }

    private void sendEmailViaGmail(JSONObject json) throws Exception {

        Mail m = new Mail(json.getString("smtpUserName"), json.getString("smtpPassword"));
        String[] toArr = json.getString("emailTo").split(",");
        String emailCC = json.optString("emailCC");
        String[] ccArr = (emailCC.isEmpty()) ? null : emailCC.split(",");
        m.set_to(toArr);
        m.set_cc(ccArr);
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
