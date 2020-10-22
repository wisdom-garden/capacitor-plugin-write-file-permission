package com.wisdomgarden.mobile;

import android.Manifest;
import android.content.pm.PackageManager;
import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

@NativePlugin(requestCodes = { WriteFilePermission.FILESYSTEM_REQUEST_WRITE_FILE_PERMISSIONS })
public class WriteFilePermission extends Plugin {

    public static final int FILESYSTEM_REQUEST_WRITE_FILE_PERMISSIONS = 9527;
    private static final String PERMISSION_DENIED_ERROR = "Unable to do file operation, user denied permission request";

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", value);
        call.success(ret);
    }

    @PluginMethod
    public void check(PluginCall call) {
        String value = call.getString("permissionName");
        boolean result = hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        JSObject ret = new JSObject();
        ret.put("result", result);
        call.resolve(ret);
    }

    @PluginMethod
    public void request(PluginCall call) {
        saveCall(call);
        String value = call.getString("permissionName");
        boolean result = hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result) {
            JSObject ret = new JSObject();
            ret.put("result", true);
            call.resolve(ret);
            this.freeSavedCall();
            return;
        }
        pluginRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, FILESYSTEM_REQUEST_WRITE_FILE_PERMISSIONS);
    }

    @Override
    protected void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.handleRequestPermissionsResult(requestCode, permissions, grantResults);

        Logger.debug(getLogTag(), "handling request perms result");

        if (getSavedCall() == null) {
            Logger.debug(getLogTag(), "No stored plugin call for permissions request result");
            return;
        }

        PluginCall savedCall = getSavedCall();

        for (int i = 0; i < grantResults.length; i++) {
            int result = grantResults[i];
            String perm = permissions[i];
            if (result == PackageManager.PERMISSION_DENIED) {
                Logger.debug(getLogTag(), "User denied storage permission: " + perm);
                savedCall.reject(PERMISSION_DENIED_ERROR);
                this.freeSavedCall();
                return;
            }
        }

        if (requestCode == WriteFilePermission.FILESYSTEM_REQUEST_WRITE_FILE_PERMISSIONS) {
            this.onGranted(savedCall);
        }
        this.freeSavedCall();
    }

    private void onGranted(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("result", true);
        call.resolve(ret);
    }
}
