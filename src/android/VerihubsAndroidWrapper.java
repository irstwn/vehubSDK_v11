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
            int[] custom_instructions;
            boolean[] attributes_check;
            JSONArray json_custom_instructions;
            JSONArray json_attributes_check;
            VerihubsString stringObj = new VerihubsString();
            try{
                instructions_count = args.getInt(0);
                timeout = args.getInt(1);
                json_custom_instructions = args.getJSONArray(2);
                json_attributes_check = args.getJSONArray(3);
                custom_instructions = new int[json_custom_instructions.length()];
                for(int i = 0; i < json_custom_instructions.length(); ++i){
                    custom_instructions[i] = json_custom_instructions.getInt(i);
                }
                attributes_check = new boolean[json_attributes_check.length()];
                for(int i = 0; i < json_attributes_check.length(); ++i){
                    attributes_check[i] = json_attributes_check.getBoolean(i);
                }
                stringObj.setString_instruction_head_look_straight("Arahkan wajah ke depan");
                stringObj.setString_instruction_both_eye_close("Tutup mata selama 2 detik");
                stringObj.setString_instruction_head_tilt_right("Miringkan kepala ke kanan");
                stringObj.setString_instruction_head_tilt_left("Miringkan kepala ke kiri");
                stringObj.setString_instruction_head_look_down("Tundukkan kepala ke bawah");
                stringObj.setString_instruction_head_look_up("Arahkan wajah ke atas");
                stringObj.setString_instruction_head_look_right("Tengok ke kanan");
                stringObj.setString_instruction_head_look_left("Tengok ke kiri");
                stringObj.setString_instruction_mouth_open("Buka mulut");
                stringObj.setString_instruction_remove_mask("Mohon lepaskan masker");
                stringObj.setString_instruction_remove_sunglasses("Mohon lepaskan kacamata");
                stringObj.setString_follow_instruction("Posisikan wajah ke frame");
            }
            catch(JSONException e){
                callbackContext.error("Error encountered: " + e.getMessage());
                return false;
            }

            cordova.setActivityResultCallback(this);
            obj.verifyLiveness(instructions_count, custom_instructions, attributes_check, timeout, stringObj);

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
                jsonResult.put("version", "1.3.3");    
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
        if(data == null){
            this.callback.error("Null error detected");
        }else

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
