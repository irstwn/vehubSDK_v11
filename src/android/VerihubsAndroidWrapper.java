package com.verihubs.android.plugin.liveness;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.verihubs.msdk_bca.Verihubs;
import static com.verihubs.msdk_bca.VerihubsType.CREATEUSER_CODE;
import static com.verihubs.msdk_bca.VerihubsType.LIVENESS_CODE;
import com.verihubs.msdk_bca.VerihubsString;

import static com.verihubs.msdk_bca.VerihubsType.RESULT_ACTIVE_FAIL;
import static com.verihubs.msdk_bca.VerihubsType.RESULT_PASSIVE_FAIL;

public class VerihubsAndroidWrapper extends CordovaPlugin {
  
    private static int RESULT_OK = -1;
    private static int RESULT_CANCELED = 0;
    private static int MODE_PRIVATE = 0;
    private static CallbackContext callback;
    private Verihubs obj;
    private JSONObject string_parameters;

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) {
        
        this.callback = callbackContext;

        if(action.equals("initClass")){
            obj = new Verihubs(cordova.getActivity());

            PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
            pluginResult.setKeepCallback(true);
            this.callback.sendPluginResult(pluginResult);
            this.callback.success();
            return true;
        }

        if(action.equals("verifyLiveness")){
            int instructions_count;
            int timeout;
            VerihubsString stringObj = new VerihubsString();
            try{
                instructions_count = args.getInt(0);
                timeout = args.getInt(1);
                string_parameters = args.getJSONObject(2);
                if(string_parameters != null){
                    if(!string_parameters.getString("see_straight").equals("")){
                        stringObj.setString_instruction_head_look_straight(string_parameters.getString("see_straight"));
                    }
                    if(!string_parameters.getString("close_eyes").equals("")){
                        stringObj.setString_instruction_both_eye_close(string_parameters.getString("close_eyes"));
                    }
                    if(!string_parameters.getString("tilt_right").equals("")){
                        stringObj.setString_instruction_head_tilt_right(string_parameters.getString("tilt_right"));
                    }
                    if(!string_parameters.getString("tilt_left").equals("")){
                        stringObj.setString_instruction_head_tilt_left(string_parameters.getString("tilt_left"));
                    }
                    if(!string_parameters.getString("open_mouth").equals("")){
                        stringObj.setString_instruction_mouth_open(string_parameters.getString("open_mouth"));
                    }
                    if(!string_parameters.getString("see_left").equals("")){
                        stringObj.setString_instruction_head_look_left(string_parameters.getString("see_left"));
                    }
                    if(!string_parameters.getString("see_below").equals("")){
                        stringObj.setString_instruction_head_look_down(string_parameters.getString("see_below"));
                    }
                    if(!string_parameters.getString("see_above").equals("")){
                        stringObj.setString_instruction_head_look_up(string_parameters.getString("see_above"));
                    }
                    if(!string_parameters.getString("see_right").equals("")){
                        stringObj.setString_instruction_head_look_right(string_parameters.getString("see_right"));
                    }
                }
            }
            catch(JSONException e){
                callbackContext.error("Error encountered: " + e.getMessage());
                return false;
            }

            cordova.setActivityResultCallback(this);
            obj.verifyLiveness(instructions_count, timeout, stringObj);

            PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
            pluginResult.setKeepCallback(true);
            this.callback.sendPluginResult(pluginResult);
            return true;
        }

        if(action.equals("getVersion")){
            PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
            pluginResult.setKeepCallback(true);
            this.callback.sendPluginResult(pluginResult);
            try{
                JSONObject jsonResult = new JSONObject();
                jsonResult.put("version", "1.2.2");    
                this.callback.success(jsonResult);
            }catch(JSONException e){
                this.callback.error("Error encountered: " + e.getMessage());
            }
        }

        callbackContext.error("\"" + action + "\" is not a recognized action.");
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LIVENESS_CODE)
        {
            if(resultCode == RESULT_OK || resultCode == RESULT_PASSIVE_FAIL || resultCode == RESULT_ACTIVE_FAIL || resultCode == RESULT_CANCELED)
            {
                int status = data.getIntExtra("status", 500);
                int total_instruction = data.getIntExtra("total_instruction", 0);
                SharedPreferences sp = cordova.getActivity().getSharedPreferences("verihubs-storage", MODE_PRIVATE);
                try{
                        JSONObject jsonResult = new JSONObject();
                        jsonResult.put("status", status);
                        jsonResult.put("total_instruction", total_instruction);
                        
                        for(int i=1; i<=total_instruction; ++i){
                                String encoded = sp.getString("image" + (i-1), "");
                                jsonResult.put("base64String_" + i, encoded);
                                String instruction = data.getStringExtra("instruction" + i);
                                jsonResult.put("instruction" + i, instruction);
                                int encoded_length = sp.getInt("length" + (i-1), 0);
                                jsonResult.put("base64StringLength_" + i, encoded.length());
                                if(encoded.length() != encoded_length){
                                    Toast.makeText(cordova.getActivity(), "Length not same", Toast.LENGTH_SHORT).show();
                                }
                        }
                        obj.clean(total_instruction);
                        this.callback.success(jsonResult);
                }
                catch(JSONException e){
                        this.callback.error("Error encountered: " + e.getMessage());
                }
            }
        }
    }
}