package com.wisdomgarden.mobile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;

import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

@NativePlugin(requestCodes = {WriteFilePermission.FILESYSTEM_REQUEST_WRITE_FILE_PERMISSIONS})
public class WriteFilePermission extends Plugin {

    public static final int FILESYSTEM_REQUEST_WRITE_FILE_PERMISSIONS = 9527;
    private static final String PERMISSION_DENIED_ERROR = "Unable to do file operation, user denied permission request";
    private static final String PERMISSION_NAME = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private boolean userManagerExternalStorage = false;

    @Override
    public void load() {
        super.load();
        this.userManagerExternalStorage = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R;
    }


    private boolean hasPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            return hasPermission(PERMISSION_NAME);
        }
    }

    @PluginMethod
    public void check(PluginCall call) {
        boolean result = this.hasPermission();

        JSObject ret = new JSObject();
        ret.put("result", result);
        call.resolve(ret);
    }

    @PluginMethod
    public void request(PluginCall call) {
        saveCall(call);
        boolean result = this.hasPermission();
        if (result) {
            JSObject ret = new JSObject();
            ret.put("result", true);
            call.resolve(ret);
            this.freeSavedCall();
            return;
        }
        if (this.userManagerExternalStorage) {
            pluginRequestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE}, FILESYSTEM_REQUEST_WRITE_FILE_PERMISSIONS);
        } else {
            pluginRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, FILESYSTEM_REQUEST_WRITE_FILE_PERMISSIONS);
        }

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

                if (this.userManagerExternalStorage && perm.equals(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
                    try {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + getContext().getPackageName()));
                        getContext().startActivity(intent);
                    } catch (Exception ex) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        getContext().startActivity(intent);
                    }
                }

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
