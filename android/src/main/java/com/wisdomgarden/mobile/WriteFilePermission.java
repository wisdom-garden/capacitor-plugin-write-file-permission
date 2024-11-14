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

    // sdk 30 | android version 11
    private static final int ANDROID_VERSION_R = android.os.Build.VERSION_CODES.R;

    // sdk 33 | android version 13
    private static final int ANDROID_VERSION_TIRAMISU = android.os.Build.VERSION_CODES.TIRAMISU;

    // sdk 34 | android version 14
    private static final int ANDROID_VERSION_UPSIDE_DOWN_CAKE = android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE;

    private String permissionName = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private boolean useManagerExternalStorage = false;

    @Override
    public void load() {
        super.load();
        determinePermissionName();
        determineExternalStorageManagerUsage();
    }

    private void determinePermissionName() {
        // for android 13
        // only check 1 permission in 3 permissions
        if (AndroidVersionUtils.isGreaterThanOrEqualTo(ANDROID_VERSION_TIRAMISU)) {
            permissionName = Manifest.permission.READ_MEDIA_IMAGES;
        }
    }

    private void determineExternalStorageManagerUsage() {
        if (AndroidVersionUtils.isBetween(ANDROID_VERSION_R, ANDROID_VERSION_TIRAMISU, true)) {
            useManagerExternalStorage = hasDefinedPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        }
    }

    private boolean checkPermission() {
        // return true if android version is greater than or equal to android 13
        if (AndroidVersionUtils.isGreaterThanOrEqualTo(ANDROID_VERSION_TIRAMISU)) {
            return true;
        }
        if (this.useManagerExternalStorage) {
            return Environment.isExternalStorageManager();
        }
        return hasPermission(permissionName);
    }

    @PluginMethod
    public void check(PluginCall call) {
        boolean result = this.checkPermission();

        if (result) {
            this.onGranted(call);
        } else {
            this.onDenied(call);
        }
    }

    @PluginMethod
    public void request(PluginCall call) {
        boolean result = this.checkPermission();
        if (result) {
            this.onGranted(call);
            return;
        }
        saveCall(call);
        if (this.useManagerExternalStorage) {
            pluginRequestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE}, FILESYSTEM_REQUEST_WRITE_FILE_PERMISSIONS);
            return;
        }
        // android 13 request 3 permissions
        if (AndroidVersionUtils.isGreaterThanOrEqualTo((ANDROID_VERSION_TIRAMISU))) {
            pluginRequestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO}, FILESYSTEM_REQUEST_WRITE_FILE_PERMISSIONS);
            return;
        }
        pluginRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, FILESYSTEM_REQUEST_WRITE_FILE_PERMISSIONS);
    }

    @PluginMethod
    public void requestPostNotificationPermission(PluginCall call) {
        if (AndroidVersionUtils.isGreaterThanOrEqualTo(ANDROID_VERSION_TIRAMISU)) {
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
