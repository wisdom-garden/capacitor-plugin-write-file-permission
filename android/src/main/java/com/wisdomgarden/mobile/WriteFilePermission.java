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
    private static final String PERMISSION_DENIED_ERROR = "Unable to do this operation, user denied permission request";
    private String permissionName = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private boolean useManagerExternalStorage = false;

    @Override
    public void load() {
        super.load();
        // only check 1 permission in 3 permissions
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // for android 13
            permissionName = Manifest.permission.READ_MEDIA_IMAGES;
        }
        // for cn
        this.useManagerExternalStorage = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R && android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU && this.hasDefinedPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
    }

    private boolean hasPermission() {
        if (this.useManagerExternalStorage && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R && android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) {
            return Environment.isExternalStorageManager();
        } else {
            return hasPermission(permissionName);
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
        boolean result = this.hasPermission();
        if (result) {
            this.onGranted(call);
            return;
        }
        saveCall(call);
        if (this.useManagerExternalStorage) {
            pluginRequestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE}, FILESYSTEM_REQUEST_WRITE_FILE_PERMISSIONS);
        } else {
            // android 13 request 3 permissions
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                pluginRequestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO}, FILESYSTEM_REQUEST_WRITE_FILE_PERMISSIONS);
            } else {
                pluginRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, FILESYSTEM_REQUEST_WRITE_FILE_PERMISSIONS);
            }
        }
    }

    @PluginMethod
    public void requestPostNotificationPermission(PluginCall call) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            saveCall(call);
            pluginRequestPermission(Manifest.permission.POST_NOTIFICATIONS, FILESYSTEM_REQUEST_WRITE_FILE_PERMISSIONS);
        } else {
            this.onDenied(call);
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
            String permission = permissions[i];
            if (result == PackageManager.PERMISSION_DENIED) {
                Logger.debug(getLogTag(), "User denied permission: " + permission);
                savedCall.reject(PERMISSION_DENIED_ERROR);
                this.freeSavedCall();

                if (this.useManagerExternalStorage && permission.equals(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
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

    private void onDenied(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("result", false);
        call.resolve(ret);
    }
}
